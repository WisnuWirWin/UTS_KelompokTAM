package com.le.uts_tam.data.model.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "customers")
data class Customers(
    @PrimaryKey(autoGenerate = true)
    val idInt: Int = 0,
    @SerializedName("id_customers") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("no_hp") val noHp: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("plate_number") val plateNumber: String? = null,
    @SerializedName("motor_brand") val motorBrand: String? = null,
    @SerializedName("motor_model") val motorModel: String? = null,
    @SerializedName("motor_year") val motorYear: String? = null,
    @SerializedName("motor_color") val motorColor: String? = null,
    @SerializedName("complaint") val complaint: String? = null,
    val firebaseKey: String? = null,
    val lastSync: Long = 0
)
