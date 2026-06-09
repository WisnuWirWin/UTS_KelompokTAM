package com.le.uts_tam.data.model.dataclass

import com.google.gson.annotations.SerializedName

data class Items(
    @SerializedName("id_items") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("price") val price: String? = null,
    @SerializedName("purchase_price") val purchasePrice: String? = null,
    @SerializedName("stock") val stock: String? = null,
    val firebaseKey: String? = null
)
