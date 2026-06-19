package com.le.uts_tam.data.repository

import androidx.room.withTransaction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.le.uts_tam.data.local.AppDatabase
import com.le.uts_tam.data.model.dataclass.Customers
import com.le.uts_tam.data.model.dataclass.Items
import com.le.uts_tam.data.model.dataclass.Owners
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseRepository(private val ownerId: String? = null, databaseInstance: AppDatabase? = null) {
    private val database = FirebaseDatabase.getInstance("https://uas-tam-6107e-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val localDb = databaseInstance
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private val allOwnersRef = database.getReference("owners_list")
    private val ownerDataRef = ownerId?.let { database.getReference("data").child(it) }
    private val customersRef = ownerDataRef?.child("customers")
    private val itemsRef = ownerDataRef?.child("items")
    private val profileRef = ownerDataRef?.child("profile")
    private val transactionsRef = ownerDataRef?.child("transactions")

    init {
        if (ownerId != null) {
            startSyncing()
        }
    }

    private fun startSyncing() {
        customersRef?.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    repositoryScope.launch {
                        val list = snapshot.children.mapNotNull {
                            it.getValue(Customers::class.java)?.copy(firebaseKey = it.key ?: "")
                        }
                        localDb?.withTransaction {
                            localDb.customerDao().deleteAll()
                            localDb.customerDao().upsertCustomers(list)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            },
        )

        itemsRef?.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    repositoryScope.launch {
                        val list = snapshot.children.mapNotNull {
                            it.getValue(Items::class.java)?.copy(firebaseKey = it.key ?: "")
                        }
                        localDb?.withTransaction {
                            localDb.itemDao().deleteAll()
                            localDb.itemDao().upsertItems(list)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            },
        )

        profileRef?.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    repositoryScope.launch {
                        val owner = snapshot.getValue(Owners::class.java)?.copy(firebaseKey = snapshot.key ?: "")
                        if (owner != null) {
                            localDb?.withTransaction {
                                localDb.ownerDao().deleteAll()
                                localDb.ownerDao().upsertOwner(owner)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            },
        )
    }

    fun getOwnersForLogin(): Flow<List<Owners>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull {
                    it.getValue(Owners::class.java)?.copy(firebaseKey = it.key ?: "")
                }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        allOwnersRef.addValueEventListener(listener)
        awaitClose { allOwnersRef.removeEventListener(listener) }
    }

    suspend fun registerNewOwner(owner: Owners) {
        val key = allOwnersRef.push().key ?: return
        val ownerWithKey = owner.copy(firebaseKey = key)
        allOwnersRef.child(key).setValue(ownerWithKey).await()
        database.getReference("data").child(key).child("profile").setValue(ownerWithKey).await()
    }

    fun getCustomers(): Flow<List<Customers>> {
        return localDb?.customerDao()?.getAllCustomers() ?: callbackFlow { trySend(emptyList()); awaitClose() }
    }

    suspend fun addCustomer(customer: Customers) {
        val ref = customersRef?.push() ?: return
        val key = ref.key ?: return
        val customerWithKey = customer.copy(firebaseKey = key)
        localDb?.customerDao()?.upsertCustomer(customerWithKey)
        ref.setValue(customerWithKey).await()
    }

    suspend fun updateCustomer(key: String, customer: Customers) {
        val customerWithKey = customer.copy(firebaseKey = key)
        localDb?.customerDao()?.upsertCustomer(customerWithKey)
        customersRef?.child(key)?.setValue(customerWithKey)?.await()
    }

    suspend fun deleteCustomer(key: String) {
        localDb?.customerDao()?.deleteByFirebaseKey(key)
        customersRef?.child(key)?.removeValue()?.await()
    }

    fun getItems(): Flow<List<Items>> {
        return localDb?.itemDao()?.getAllItems() ?: callbackFlow { trySend(emptyList()); awaitClose() }
    }

    suspend fun addItem(item: Items) {
        val ref = itemsRef?.push() ?: return
        val key = ref.key ?: return
        val itemWithKey = item.copy(firebaseKey = key)
        localDb?.itemDao()?.upsertItem(itemWithKey)
        ref.setValue(itemWithKey).await()
    }

    suspend fun updateItem(key: String, item: Items) {
        val itemWithKey = item.copy(firebaseKey = key)
        localDb?.itemDao()?.upsertItem(itemWithKey)
        itemsRef?.child(key)?.setValue(itemWithKey)?.await()
    }

    suspend fun deleteItem(key: String) {
        localDb?.itemDao()?.deleteByFirebaseKey(key)
        itemsRef?.child(key)?.removeValue()?.await()
    }

    fun getOwner(): Flow<Owners?> {
        return localDb?.ownerDao()?.getOwner() ?: callbackFlow { trySend(null); awaitClose() }
    }

    suspend fun updateOwner(owner: Owners) {
        val ownerId = this.ownerId ?: return
        val ownerWithKey = owner.copy(firebaseKey = ownerId)
        localDb?.ownerDao()?.upsertOwner(ownerWithKey)
        allOwnersRef.child(ownerId).setValue(ownerWithKey).await()
        profileRef?.setValue(ownerWithKey)?.await()
    }

    fun getTransactions(): Flow<List<Map<String, Any>>> = callbackFlow {
        if (transactionsRef == null) { trySend(emptyList()); return@callbackFlow }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { 
                    @Suppress("UNCHECKED_CAST")
                    val data = it.value as? Map<String, Any>
                    data?.plus("firebaseKey" to (it.key ?: ""))
                }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        transactionsRef.addValueEventListener(listener)
        awaitClose { transactionsRef.removeEventListener(listener) }
    }

    suspend fun addTransaction(transaction: Map<String, Any>) {
        transactionsRef?.push()?.setValue(transaction)?.await()
    }
}
