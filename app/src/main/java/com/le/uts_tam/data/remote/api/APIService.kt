package com.le.uts_tam.data.remote.api

import retrofit2.http.GET

interface APIService {
    @GET("customers.json")
    suspend fun getCustomers(): List<Customers>

    @GET("items.json")
    suspend fun getItems(): List<Items>

    @GET("vehicle.json")
    suspend fun getVehicles(): List<Vehicles>

    @GET("owner.json")
    suspend fun getOwners(): List<Owners>
}