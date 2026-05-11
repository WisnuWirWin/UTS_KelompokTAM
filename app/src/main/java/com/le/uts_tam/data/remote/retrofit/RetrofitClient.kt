package com.le.uts_tam.data.remote.retrofit

import com.google.gson.GsonBuilder
import com.le.uts_tam.data.remote.api.APIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://gist.githubusercontent.com/WisnuWirWin/8a628f3fb09ab7f7a1dfb7ffe847e8e7/raw/"
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    val instance: APIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(APIService::class.java)
    }
}