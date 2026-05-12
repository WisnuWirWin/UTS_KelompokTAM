package com.le.uts_tam.data.model.dataclass

import com.google.gson.annotations.SerializedName

data class Customers(
    @SerializedName("id_customers") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("no_hp") val noHp: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("complaint") val complaint: String? = null
)
