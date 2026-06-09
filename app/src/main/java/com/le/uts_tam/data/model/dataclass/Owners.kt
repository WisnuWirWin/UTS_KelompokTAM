package com.le.uts_tam.data.model.dataclass

import com.google.gson.annotations.SerializedName

data class Owners(
    @SerializedName("owner") val owner: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("no_hp") val noHp: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    val firebaseKey: String? = null
)