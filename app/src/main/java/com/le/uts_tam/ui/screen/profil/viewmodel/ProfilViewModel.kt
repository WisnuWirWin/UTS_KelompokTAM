package com.le.uts_tam.ui.screen.profil.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.remote.model.Owners
import com.le.uts_tam.data.remote.retrofit.RetrofitClient
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
    private val _uiState = MutableStateFlow(ProfilUIState())
    val uiState: StateFlow<ProfilUIState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchProfilData()
    }

    fun fetchProfilData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response: List<Owners?> = RetrofitClient.instance.getOwners() ?: emptyList()
                val owner = response.filterNotNull().firstOrNull()
                
                if (owner != null) {
                    _uiState.value = ProfilUIState(
                        ownerName = owner.owner ?: "BENGKEL PAK ARLI",
                        businessType = "BENGKEL MOTOR",
                        address = owner.address ?: "Lampung",
                        phone = owner.noHp ?: "0857-6494-8010",
                        imageUrl = owner.imageUrl ?: ""
                    )
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal mengambil data profil"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
