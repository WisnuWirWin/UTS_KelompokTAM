package com.le.uts_tam.data.remote.api

import com.le.uts_tam.data.remote.model.Customers
import com.le.uts_tam.data.remote.model.Items
import com.le.uts_tam.data.remote.model.Owners
import com.le.uts_tam.data.remote.model.Vehicles
import retrofit2.http.GET

interface APIService {
    @GET("customers.json")
    suspend fun getCustomers(): List<Customers?>

    @GET("items.json")
    suspend fun getItems(): List<Items?>

    @GET("vehicle.json")
    suspend fun getVehicles(): List<Vehicles?>

    @GET("owner.json")
    suspend fun getOwners(): List<Owners?>
}
