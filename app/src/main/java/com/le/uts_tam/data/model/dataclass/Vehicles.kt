package com.le.uts_tam.data.model.dataclass

import com.google.gson.annotations.SerializedName

data class Vehicles(
    @SerializedName("id_vehicle") val idVehicle: String? = null,
    @SerializedName("number_plate") val numberPlate: String? = null,
    @SerializedName("id_customers") val idCustomers: String? = null,
    @SerializedName("brand") val brand: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("vehicle_year") val vehicleYear: String? = null,
    @SerializedName("color") val color: String? = null
)