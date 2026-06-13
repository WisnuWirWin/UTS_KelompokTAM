package com.le.uts_tam.data.local.dao

import androidx.room.*
import com.le.uts_tam.data.model.dataclass.Vehicles
import kotlinx.coroutines.flow.Flow

@Dao
@JvmSuppressWildcards
interface VehicleDao {
    @Query("SELECT * FROM vehicles")
    fun getAllVehicles(): Flow<List<Vehicles>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVehicle(vehicle: Vehicles): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVehicles(vehicles: List<Vehicles>): List<Long>

    @Query("DELETE FROM vehicles WHERE firebaseKey = :key")
    suspend fun deleteByFirebaseKey(key: String): Int

    @Query("SELECT * FROM vehicles WHERE idCustomers = :customerId")
    fun getVehiclesByCustomerId(customerId: String): Flow<List<Vehicles>>

    @Query("DELETE FROM vehicles")
    suspend fun deleteAll(): Int
}
