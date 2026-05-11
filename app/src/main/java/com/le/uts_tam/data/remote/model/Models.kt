package com.le.uts_tam.data.remote.model

import com.google.gson.annotations.SerializedName

data class Customers(
    @SerializedName("id_customers") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("no_hp") val noHp: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("complaint") val complaint: String? = null
)

data class Items(
    @SerializedName("id_items") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("price") val price: String? = null,
    @SerializedName("stock") val stock: String? = null
)

data class Vehicles(
    @SerializedName("id_vehicle") val idVehicle: String? = null,
    @SerializedName("number_plate") val numberPlate: String? = null,
    @SerializedName("id_customers") val idCustomers: String? = null,
    @SerializedName("brand") val brand: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("vehicle_year") val vehicleYear: String? = null,
    @SerializedName("color") val color: String? = null
)

data class Owners(
    @SerializedName("owner") val owner: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("no_hp") val noHp: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null
)
