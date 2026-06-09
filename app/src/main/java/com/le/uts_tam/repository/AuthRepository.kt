package com.le.uts_tam.repository

import com.le.uts_tam.data.model.dataclass.Owners
import com.le.uts_tam.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.first

class AuthRepository {
    private val firebaseRepository = FirebaseRepository()

    suspend fun getOwners(): List<Owners> {
        return try {
            firebaseRepository.getOwners().first()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
