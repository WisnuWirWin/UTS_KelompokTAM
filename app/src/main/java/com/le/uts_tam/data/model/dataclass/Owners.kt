package com.le.uts_tam.data.model.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "owners")
data class Owners(
    @PrimaryKey(autoGenerate = true)
    val idInt: Int = 0,
    @SerializedName("owner") val owner: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("no_hp") val noHp: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    val firebaseKey: String? = null,
    val lastSync: Long = 0
)
