package com.le.uts_tam.data.model.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "items")
data class Items(
    @PrimaryKey(autoGenerate = true)
    val idInt: Int = 0,
    @SerializedName("id_items") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("price") val price: String? = null,
    @SerializedName("purchase_price") val purchasePrice: String? = null,
    @SerializedName("stock") val stock: String? = null,
    val firebaseKey: String? = null,
    val lastSync: Long = 0
)
