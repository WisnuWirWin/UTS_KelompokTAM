package com.le.uts_tam.ui.screen.pelanggan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.remote.model.Customers
import com.le.uts_tam.data.remote.model.Vehicles
import com.le.uts_tam.data.remote.retrofit.RetrofitClient
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
    private val _uiState = MutableStateFlow<List<PelangganUIState>>(emptyList())
    val uiState: StateFlow<List<PelangganUIState>> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val customersResponse: List<Customers?> = RetrofitClient.instance.getCustomers() ?: emptyList()
                val vehiclesResponse: List<Vehicles?> = RetrofitClient.instance.getVehicles() ?: emptyList()

                val combinedData = customersResponse.filterNotNull().map { customer ->
                    val vehicle = vehiclesResponse.filterNotNull().find { it.idCustomers == customer.id }
                    
                    PelangganUIState(
                        id = customer.id ?: "",
                        name = customer.name ?: "Tanpa Nama",
                        phone = customer.noHp ?: "-",
                        plate = vehicle?.numberPlate ?: "-",
                        motor = "${vehicle?.brand ?: ""} ${vehicle?.type ?: ""} ${vehicle?.vehicleYear ?: ""}".trim().ifEmpty { "-" },
                        complaint = customer.complaint ?: "-"
                    )
                }

                _uiState.value = combinedData
            } catch (e: Exception) {
                _error.value = e.message ?: "Terjadi kesalahan saat mengambil data"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
