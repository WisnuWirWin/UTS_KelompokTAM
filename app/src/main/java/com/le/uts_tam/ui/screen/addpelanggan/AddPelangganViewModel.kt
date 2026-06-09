package com.le.uts_tam.ui.screen.addpelanggan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.model.dataclass.Customers
import com.le.uts_tam.data.repository.FirebaseRepository
import kotlinx.coroutines.launch

class AddPelangganViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    fun saveCustomer(
        name: String,
        phone: String,
        address: String,
        complaint: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val customer = Customers(
                    name = name,
                    noHp = phone,
                    address = address,
                    complaint = complaint
                )
                repository.addCustomer(customer)
                onSuccess()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
