package com.le.uts_tam.ui.screen.pelanggan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.local.AppDatabase
import com.le.uts_tam.data.model.dataclass.PelangganUIState
import com.le.uts_tam.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PelangganViewModel(ownerId: String, database: AppDatabase) : ViewModel() {
    private val repository = FirebaseRepository(ownerId, database)
    private val _uiState = MutableStateFlow<List<PelangganUIState>>(emptyList())
    val uiState: StateFlow<List<PelangganUIState>> = _uiState.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        observeCustomers()
    }

    private fun observeCustomers() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getCustomers().collect { customers ->
                _uiState.value = customers.map { customer ->
                    PelangganUIState(
                        firebaseKey = customer.firebaseKey,
                        id = customer.id ?: "",
                        name = customer.name ?: "Tanpa Nama",
                        phone = customer.noHp ?: "-",
                        address = customer.address ?: "",
                        plate = customer.plateNumber ?: "-",
                        motorBrand = customer.motorBrand ?: "",
                        motorModel = customer.motorModel ?: "",
                        motorYear = customer.motorYear ?: "",
                        motorColor = customer.motorColor ?: "",
                        complaint = customer.complaint ?: "-",
                        motorDisplay = "${customer.motorBrand ?: ""} ${customer.motorModel ?: ""}".trim()
                            .ifEmpty { "-" }
                    )
                }
                _isLoading.value = false
            }
        }
    }

    fun deleteCustomer(key: String) {
        viewModelScope.launch {
            try {
                repository.deleteCustomer(key)
            } catch (e: Exception) {
                _error.value = "Gagal menghapus pelanggan: ${e.message}"
            }
        }
    }

    fun fetchData() {
        observeCustomers()
    }
}
