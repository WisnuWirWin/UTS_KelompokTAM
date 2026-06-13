package com.le.uts_tam.data.local.dao

import androidx.room.*
import com.le.uts_tam.data.model.dataclass.Owners
import kotlinx.coroutines.flow.Flow

@Dao
@JvmSuppressWildcards
interface OwnerDao {
    @Query("SELECT * FROM owners LIMIT 1")
    fun getOwner(): Flow<Owners?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOwner(owner: Owners): Long

    @Query("DELETE FROM owners")
    suspend fun deleteAll(): Int
}
