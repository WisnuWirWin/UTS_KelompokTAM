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

class FirebaseRepository(private val ownerId: String? = null) {
    private val database = FirebaseDatabase.getInstance("https://uas-tam-6107e-default-rtdb.asia-southeast1.firebasedatabase.app")
    
    // Global path for all owners (users)
    private val allOwnersRef = database.getReference("owners_list")
    
    // Scoped paths for a specific owner's data
    private val ownerDataRef = if (ownerId != null) database.getReference("data").child(ownerId) else null
    
    private val customersRef = ownerDataRef?.child("customers")
    private val itemsRef = ownerDataRef?.child("items")
    private val profileRef = ownerDataRef?.child("profile")
    private val transactionsRef = ownerDataRef?.child("transactions")

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
    fun getCustomers(): Flow<List<Customers>> = callbackFlow {
        if (customersRef == null) { trySend(emptyList()); return@callbackFlow }
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
        customersRef?.push()?.setValue(customer)?.await()
    }

    suspend fun updateCustomer(key: String, customer: Customers) {
        customersRef?.child(key)?.setValue(customer)?.await()
    }

    suspend fun deleteCustomer(key: String) {
        customersRef?.child(key)?.removeValue()?.await()
    }

    // --- Items / Stock (Scoped) ---
    fun getItems(): Flow<List<Items>> = callbackFlow {
        if (itemsRef == null) { trySend(emptyList()); return@callbackFlow }
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
        itemsRef?.push()?.setValue(item)?.await()
    }

    suspend fun updateItem(key: String, item: Items) {
        itemsRef?.child(key)?.setValue(item)?.await()
    }

    suspend fun deleteItem(key: String) {
        itemsRef?.child(key)?.removeValue()?.await()
    }

    // --- Owner / Profile (Scoped) ---
    fun getOwner(): Flow<Owners?> = callbackFlow {
        if (profileRef == null) { trySend(null); return@callbackFlow }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val owner = snapshot.getValue(Owners::class.java)
                trySend(owner)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        profileRef.addValueEventListener(listener)
        awaitClose { profileRef.removeEventListener(listener) }
    }

    suspend fun updateOwner(owner: Owners) {
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
