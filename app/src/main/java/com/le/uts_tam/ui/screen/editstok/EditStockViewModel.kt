package com.le.uts_tam.ui.screen.editstok

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.model.dataclass.Items
import com.le.uts_tam.data.repository.FirebaseRepository
import kotlinx.coroutines.launch

class EditStockViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    var idItems by mutableStateOf("")
    var name by mutableStateOf("")
    var price by mutableStateOf("")
    var stock by mutableStateOf("")

    var hargaBeli by mutableStateOf("")
    var minStok by mutableStateOf("")
    var kategori by mutableStateOf("Semua")
    var supplier by mutableStateOf("")

    fun setInitialData(item: Items?) {
        item?.let {
            idItems = it.id ?: ""
            name = it.name ?: ""
            price = it.price ?: ""
            stock = it.stock ?: ""
        }
    }

    fun saveChanges(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val item = Items(
                    id = idItems,
                    name = name,
                    price = price,
                    stock = stock
                )
                repository.addItem(item)
                onSuccess()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
