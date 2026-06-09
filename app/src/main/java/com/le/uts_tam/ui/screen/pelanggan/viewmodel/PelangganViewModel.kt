package com.le.uts_tam.ui.screen.pelanggan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PelangganUIState(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val plate: String = "",
    val motor: String = "",
    val complaint: String = ""
)

class PelangganViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    
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
                        id = customer.id ?: "",
                        name = customer.name ?: "Tanpa Nama",
                        phone = customer.noHp ?: "-",
                        plate = "-", // For now, since vehicles might be separate
                        motor = "-",
                        complaint = customer.complaint ?: "-"
                    )
                }
                _isLoading.value = false
            }
        }
    }

    fun fetchData() {
        observeCustomers()
    }
}
