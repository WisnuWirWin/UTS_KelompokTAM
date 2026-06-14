package com.le.uts_tam.data.model.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "items")
data class Items(
    @PrimaryKey
    val firebaseKey: String = "",
    @SerializedName("id_items") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("price") val price: String? = null,
    @SerializedName("purchase_price") val purchasePrice: String? = null,
    @SerializedName("stock") val stock: String? = null,
    val lastSync: Long = 0,
)
