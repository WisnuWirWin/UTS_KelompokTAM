package com.le.uts_tam.data.local.dao

import androidx.room.*
import com.le.uts_tam.data.model.dataclass.Customers
import kotlinx.coroutines.flow.Flow

@Dao
@JvmSuppressWildcards
interface CustomerDao {
    @Query("SELECT * FROM customers")
    fun getAllCustomers(): Flow<List<Customers>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCustomer(customer: Customers): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCustomers(customers: List<Customers>): List<Long>

    @Query("DELETE FROM customers WHERE firebaseKey = :key")
    suspend fun deleteByFirebaseKey(key: String): Int

    @Delete
    suspend fun deleteCustomer(customer: Customers): Int

    @Query("DELETE FROM customers")
    suspend fun deleteAll(): Int

    @Query("SELECT * FROM customers WHERE firebaseKey IS NULL")
    suspend fun getUnsyncedCustomers(): List<Customers>
}
