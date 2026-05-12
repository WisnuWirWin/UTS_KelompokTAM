package com.le.uts_tam.repository

import com.le.uts_tam.data.model.dataclass.Owners
import com.le.uts_tam.data.remote.retrofit.RetrofitClient

class AuthRepository {
    suspend fun getOwners(): List<Owners> {
        return try {
            val response = RetrofitClient.instance.getOwners()

            // Filter data agar tidak ada yang null sebelum dikembalikan ke ViewModel
            response.filterNotNull()
        } catch (e: Exception) {
            emptyList()
        }
    }
}