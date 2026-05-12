package com.le.uts_tam.repository

import com.le.uts_tam.data.model.dataclass.Customers
import com.le.uts_tam.data.model.dataclass.Items
import com.le.uts_tam.data.model.dataclass.Owners
import com.le.uts_tam.data.remote.retrofit.RetrofitClient

class DashboardRepository {
    suspend fun getOwners(): List<Owners> {
        return try {
            RetrofitClient.instance.getOwners().filterNotNull()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getItems(): List<Items> {
        return try {
            RetrofitClient.instance.getItems().filterNotNull()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCustomers(): List<Customers> {
        return try {
            RetrofitClient.instance.getCustomers().filterNotNull()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
