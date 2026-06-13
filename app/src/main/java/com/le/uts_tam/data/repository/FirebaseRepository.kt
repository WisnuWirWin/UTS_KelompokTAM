package com.le.uts_tam.data.repository

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

    // Global path for all owners (users)
    private val allOwnersRef = database.getReference("owners_list")
    
    // Scoped paths for a specific owner's data
    private val ownerDataRef = if (ownerId != null) database.getReference("data").child(ownerId) else null
    
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
        // Sync Customers
        customersRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                repositoryScope.launch {
                    val list = snapshot.children.mapNotNull { 
                        it.getValue(Customers::class.java)?.copy(firebaseKey = it.key) 
                    }
                    localDb?.customerDao()?.upsertCustomers(list)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Sync Items
        itemsRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                repositoryScope.launch {
                    val list = snapshot.children.mapNotNull { 
                        it.getValue(Items::class.java)?.copy(firebaseKey = it.key) 
                    }
                    localDb?.itemDao()?.upsertItems(list)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Sync Profile
        profileRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                repositoryScope.launch {
                    val owner = snapshot.getValue(Owners::class.java)?.copy(firebaseKey = snapshot.key)
                    if (owner != null) {
                        localDb?.ownerDao()?.upsertOwner(owner)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // --- Login & Registration (Global) ---
    fun getOwnersForLogin(): Flow<List<Owners>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull {
                    it.getValue(Owners::class.java)?.copy(firebaseKey = it.key)
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
        allOwnersRef.child(key).setValue(owner).await()
        
        // Initialize profile data for the new owner
        database.getReference("data").child(key).child("profile").setValue(owner).await()
    }

    // --- Customers (Scoped) ---
    fun getCustomers(): Flow<List<Customers>> {
        return localDb?.customerDao()?.getAllCustomers() ?: callbackFlow { trySend(emptyList()); awaitClose() }
    }

    suspend fun addCustomer(customer: Customers) {
        // Save to Room first (as unsynced)
        localDb?.customerDao()?.upsertCustomer(customer)
        // Then push to Firebase
        val ref = customersRef?.push()
        if (ref != null) {
            ref.setValue(customer).await()
            // Room will be updated by the listener once Firebase confirms
        }
    }

    suspend fun updateCustomer(key: String, customer: Customers) {
        // Update local first
        localDb?.customerDao()?.upsertCustomer(customer.copy(firebaseKey = key))
        // Update Firebase
        customersRef?.child(key)?.setValue(customer)?.await()
    }

    suspend fun deleteCustomer(key: String) {
        // Delete local
        localDb?.customerDao()?.deleteByFirebaseKey(key)
        // Delete Firebase
        customersRef?.child(key)?.removeValue()?.await()
    }

    // --- Items / Stock (Scoped) ---
    fun getItems(): Flow<List<Items>> {
        return localDb?.itemDao()?.getAllItems() ?: callbackFlow { trySend(emptyList()); awaitClose() }
    }

    suspend fun addItem(item: Items) {
        localDb?.itemDao()?.upsertItem(item)
        val ref = itemsRef?.push()
        if (ref != null) {
            ref.setValue(item).await()
        }
    }

    suspend fun updateItem(key: String, item: Items) {
        localDb?.itemDao()?.upsertItem(item.copy(firebaseKey = key))
        itemsRef?.child(key)?.setValue(item)?.await()
    }

    suspend fun deleteItem(key: String) {
        localDb?.itemDao()?.deleteByFirebaseKey(key)
        itemsRef?.child(key)?.removeValue()?.await()
    }

    // --- Owner / Profile (Scoped) ---
    fun getOwner(): Flow<Owners?> {
        return localDb?.ownerDao()?.getOwner() ?: callbackFlow { trySend(null); awaitClose() }
    }

    suspend fun updateOwner(owner: Owners) {
        localDb?.ownerDao()?.upsertOwner(owner)
        profileRef?.setValue(owner)?.await()
    }

    // --- Transactions (Scoped) ---
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
