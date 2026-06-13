package com.le.uts_tam.data.model.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "vehicles")
data class Vehicles(
    @PrimaryKey(autoGenerate = true)
    val idInt: Int = 0,
    @SerializedName("id_vehicle") val idVehicle: String? = null,
    @SerializedName("number_plate") val numberPlate: String? = null,
    @SerializedName("id_customers") val idCustomers: String? = null,
    @SerializedName("brand") val brand: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("vehicle_year") val vehicleYear: String? = null,
    @SerializedName("color") val color: String? = null,
    val firebaseKey: String? = null,
    val lastSync: Long = 0
)
