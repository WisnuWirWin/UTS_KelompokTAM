package com.le.uts_tam.ui.screen.editstok

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.local.AppDatabase
import com.le.uts_tam.data.model.dataclass.Items
import com.le.uts_tam.data.repository.FirebaseRepository
import com.le.uts_tam.utils.FormatUtils
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
    var nameError by mutableStateOf<String?>(null)
    var priceError by mutableStateOf<String?>(null)
    var stockError by mutableStateOf<String?>(null)

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
        clearErrors()
    }
    
    private fun clearErrors() {
        nameError = null
        priceError = null
        stockError = null
    }

    fun saveChanges(onSuccess: (String) -> Unit) {
        clearErrors()
        var hasError = false
        if (name.isBlank()) {
            nameError = "Nama barang tidak boleh kosong"
            hasError = true
        }
        if (price.isBlank()) {
            priceError = "Harga jual tidak boleh kosong"
            hasError = true
        }
        if (stock.isBlank()) {
            stockError = "Stok tidak boleh kosong"
            hasError = true
        }
        if (hasError) return
        viewModelScope.launch {
            try {
                val item = Items(
                    firebaseKey = firebaseKey ?: "",
                    id = idItems,
                    name = name,
                    price = FormatUtils.cleanNumber(price),
                    purchasePrice = FormatUtils.cleanNumber(hargaBeli),
                    stock = FormatUtils.cleanNumber(stock),
                )
                val currentName = name
                firebaseKey?.let { key ->
                    repository.updateItem(key, item)
                    onSuccess("Barang '$currentName' berhasil diperbarui")
                } ?: run {
                    repository.addItem(item)
                    onSuccess("Barang '$currentName' berhasil ditambahkan")
                }
            } catch (_: Exception) {}
        }
    }
}
