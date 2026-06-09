package com.le.uts_tam.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.le.uts_tam.data.model.dataclass.Customers
import com.le.uts_tam.data.model.dataclass.Items
import com.le.uts_tam.data.model.dataclass.Owners
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val database = FirebaseDatabase.getInstance("https://uas-tam-6107e-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val customersRef = database.getReference("customers")
    private val itemsRef = database.getReference("items")
    private val ownerRef = database.getReference("owner")
    private val transactionsRef = database.getReference("transactions")

    // --- Customers ---
    fun getCustomers(): Flow<List<Customers>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { 
                    it.getValue(Customers::class.java)?.copy(firebaseKey = it.key) 
                }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        customersRef.addValueEventListener(listener)
        awaitClose { customersRef.removeEventListener(listener) }
    }

    suspend fun addCustomer(customer: Customers) {
        val key = customersRef.push().key ?: return
        customersRef.child(key).setValue(customer).await()
    }

    suspend fun updateCustomer(key: String, customer: Customers) {
        customersRef.child(key).setValue(customer).await()
    }

    suspend fun deleteCustomer(key: String) {
        customersRef.child(key).removeValue().await()
    }

    // --- Items / Stock ---
    fun getItems(): Flow<List<Items>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { 
                    val item = it.getValue(Items::class.java)
                    item?.copy(firebaseKey = it.key) 
                }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        itemsRef.addValueEventListener(listener)
        awaitClose { itemsRef.removeEventListener(listener) }
    }

    suspend fun addItem(item: Items) {
        val key = itemsRef.push().key ?: return
        itemsRef.child(key).setValue(item).await()
    }

    suspend fun updateItem(key: String, item: Items) {
        itemsRef.child(key).setValue(item).await()
    }

    suspend fun deleteItem(key: String) {
        itemsRef.child(key).removeValue().await()
    }

    // --- Owner / Profile ---
    fun getOwners(): Flow<List<Owners>> = callbackFlow {
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
        ownerRef.addValueEventListener(listener)
        awaitClose { ownerRef.removeEventListener(listener) }
    }

    fun getOwner(): Flow<Owners?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // If the root is a list, take the first child, otherwise take value
                val owner = if (snapshot.hasChildren()) {
                    snapshot.children.firstOrNull()?.let {
                        it.getValue(Owners::class.java)?.copy(firebaseKey = it.key)
                    }
                } else {
                    snapshot.getValue(Owners::class.java)
                }
                trySend(owner)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ownerRef.addValueEventListener(listener)
        awaitClose { ownerRef.removeEventListener(listener) }
    }

    suspend fun registerOwner(owner: Owners) {
        val key = ownerRef.push().key ?: return
        ownerRef.child(key).setValue(owner).await()
    }

    suspend fun updateOwner(key: String, owner: Owners) {
        ownerRef.child(key).setValue(owner).await()
    }

    suspend fun deleteOwner(key: String) {
        ownerRef.child(key).removeValue().await()
    }

    // --- Transactions ---
    fun getTransactions(): Flow<List<Map<String, Any>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { 
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
        val key = transactionsRef.push().key ?: return
        transactionsRef.child(key).setValue(transaction).await()
    }
}
