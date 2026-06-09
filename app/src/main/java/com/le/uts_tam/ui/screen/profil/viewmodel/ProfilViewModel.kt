package com.le.uts_tam.ui.screen.profil.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.model.dataclass.Owners
import com.le.uts_tam.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfilUIState(
    val ownerName: String = "BENGKEL PAK ARLI",
    val businessType: String = "BENGKEL MOTOR",
    val address: String = "Rejomulyo, Kec. Jati Agung, Kab. Lampung Selatan",
    val phone: String = "0857-6494-8010",
    val imageUrl: String = ""
)

class ProfilViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    
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
                        ownerName = owner.owner ?: "BENGKEL PAK ARLI",
                        businessType = "BENGKEL MOTOR",
                        address = owner.address ?: "Rejomulyo, Kec. Jati Agung, Kab. Lampung Selatan",
                        phone = owner.noHp ?: "0857-6494-8010",
                        imageUrl = owner.imageUrl ?: ""
                    )
                }
                _isLoading.value = false
            }
        }
    }

    fun updateOwnerName(newName: String) {
        saveChanges(_uiState.value.copy(ownerName = newName))
    }

    fun updateAddress(newAddress: String) {
        saveChanges(_uiState.value.copy(address = newAddress))
    }

    fun updatePhone(newPhone: String) {
        saveChanges(_uiState.value.copy(phone = newPhone))
    }

    private fun saveChanges(newState: ProfilUIState) {
        _uiState.value = newState
        viewModelScope.launch {
            try {
                repository.updateOwner(
                    Owners(
                        owner = newState.ownerName,
                        address = newState.address,
                        noHp = newState.phone,
                        imageUrl = newState.imageUrl
                    )
                )
            } catch (e: Exception) {
                _error.value = "Gagal menyimpan perubahan: ${e.message}"
            }
        }
    }
}
