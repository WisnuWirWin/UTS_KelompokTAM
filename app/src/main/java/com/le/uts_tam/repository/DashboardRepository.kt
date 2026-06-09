package com.le.uts_tam.repository

import com.le.uts_tam.data.model.dataclass.Customers
import com.le.uts_tam.data.model.dataclass.Items
import com.le.uts_tam.data.model.dataclass.Owners
import com.le.uts_tam.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.first

class DashboardRepository {
    private val firebaseRepository = FirebaseRepository()

    suspend fun getOwners(): List<Owners> {
        return try {
            firebaseRepository.getOwners().first()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getItems(): List<Items> {
        return try {
            firebaseRepository.getItems().first()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCustomers(): List<Customers> {
        return try {
            firebaseRepository.getCustomers().first()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
