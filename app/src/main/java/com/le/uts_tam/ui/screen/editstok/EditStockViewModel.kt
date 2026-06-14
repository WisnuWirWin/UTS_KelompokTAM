package com.le.uts_tam.ui.screen.editstok

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.local.AppDatabase
import com.le.uts_tam.data.model.dataclass.Items
import com.le.uts_tam.data.repository.FirebaseRepository
import kotlinx.coroutines.launch

class EditStockViewModel(ownerId: String, database: AppDatabase) : ViewModel() {
    private val repository = FirebaseRepository(ownerId, database)

    var firebaseKey by mutableStateOf<String?>(null)
    var idItems by mutableStateOf("")
    var name by mutableStateOf("")
    var price by mutableStateOf("")
    var stock by mutableStateOf("")

    var hargaBeli by mutableStateOf("")
    var minStok by mutableStateOf("")
    var kategori by mutableStateOf("Semua")
    var supplier by mutableStateOf("")

    fun setInitialData(item: Items?) {
        if (item != null) {
            firebaseKey = item.firebaseKey
            idItems = item.id ?: ""
            name = item.name ?: ""
            price = item.price ?: ""
            stock = item.stock ?: ""
            hargaBeli = item.purchasePrice ?: ""
        } else {
            firebaseKey = null
            idItems = ""
            name = ""
            price = ""
            stock = ""
            hargaBeli = ""
            minStok = ""
            kategori = "Semua"
            supplier = ""
        }
    }

    fun saveChanges(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val item = Items(
                    firebaseKey = firebaseKey ?: "",
                    id = idItems,
                    name = name,
                    price = price,
                    purchasePrice = hargaBeli,
                    stock = stock,
                )
                
                firebaseKey?.let { key ->
                    repository.updateItem(key, item)
                } ?: run {
                    repository.addItem(item)
                }

                onSuccess()
            } catch (e: Exception) {
            }
        }
    }
}
