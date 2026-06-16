package com.le.uts_tam.ui.screen.profil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.local.AppDatabase
import com.le.uts_tam.data.model.dataclass.Owners
import com.le.uts_tam.data.model.dataclass.ProfilUIState
import com.le.uts_tam.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfilViewModel(ownerId: String, database: AppDatabase) : ViewModel() {
    private val repository = FirebaseRepository(ownerId, database)
    private val _uiState = MutableStateFlow(ProfilUIState())
    val uiState: StateFlow<ProfilUIState> = _uiState.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        observeProfilData()
    }

    private fun observeProfilData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getOwner().collect { owner ->
                if (owner != null) {
                    _uiState.value = ProfilUIState(
                        firebaseKey = owner.firebaseKey,
                        ownerName = owner.owner ?: "",
                        businessType = "BENGKEL MOTOR",
                        address = owner.address ?: "",
                        phone = owner.noHp ?: "",
                        imageUrl = owner.imageUrl ?: "",
                        username = owner.username ?: "",
                        password = owner.password ?: ""
                    )
                }
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(name: String, address: String, phone: String, onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.firebaseKey.isEmpty()) {
            _error.value = "Data profil tidak ditemukan"
            return
        }
        viewModelScope.launch {
            try {
                repository.updateOwner(
                    Owners(
                        firebaseKey = currentState.firebaseKey,
                        owner = name,
                        address = address,
                        noHp = phone,
                        imageUrl = currentState.imageUrl,
                        username = currentState.username,
                        password = currentState.password
                    )
                )
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Gagal menyimpan perubahan: ${e.message}"
            }
        }
    }
}
