package com.le.uts_tam.data.local.dao

import androidx.room.*
import com.le.uts_tam.data.model.dataclass.Items
import kotlinx.coroutines.flow.Flow

@Dao
@JvmSuppressWildcards
interface ItemDao {
    @Query("SELECT * FROM items")
    fun getAllItems(): Flow<List<Items>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertItem(item: Items): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertItems(items: List<Items>): List<Long>

    @Query("DELETE FROM items WHERE firebaseKey = :key")
    suspend fun deleteByFirebaseKey(key: String): Int

    @Query("DELETE FROM items")
    suspend fun deleteAll(): Int

    @Query("SELECT * FROM items WHERE firebaseKey IS NULL")
    suspend fun getUnsyncedItems(): List<Items>
}
