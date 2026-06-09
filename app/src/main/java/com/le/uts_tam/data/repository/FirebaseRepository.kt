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
    private val database = FirebaseDatabase.getInstance()
    private val customersRef = database.getReference("customers")
    private val itemsRef = database.getReference("items")
    private val ownerRef = database.getReference("owner")

    // --- Customers ---
    fun getCustomers(): Flow<List<Customers>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Customers::class.java) }
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

    // --- Items / Stock ---
    fun getItems(): Flow<List<Items>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Items::class.java) }
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

    // --- Owner / Profile ---
    fun getOwner(): Flow<Owners?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Assuming single owner object or the first one
                val owner = snapshot.children.firstOrNull()?.getValue(Owners::class.java)
                    ?: snapshot.getValue(Owners::class.java)
                trySend(owner)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ownerRef.addValueEventListener(listener)
        awaitClose { ownerRef.removeEventListener(listener) }
    }

    suspend fun updateOwner(owner: Owners) {
        // If owner is a single object, we can use setValue
        ownerRef.setValue(owner).await()
    }
}
