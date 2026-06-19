package com.le.uts_tam.ui.screen.addpelanggan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.local.AppDatabase
import com.le.uts_tam.data.model.dataclass.Customers
import com.le.uts_tam.data.repository.FirebaseRepository
import com.le.uts_tam.utils.FormatUtils
import kotlinx.coroutines.launch

class AddPelangganViewModel(ownerId: String, database: AppDatabase) : ViewModel() {
    private val repository = FirebaseRepository(ownerId, database)
    var firebaseKey by mutableStateOf<String?>(null)
    var namaLengkap by mutableStateOf("")
    var nomorTelepon by mutableStateOf("")
    var alamat by mutableStateOf("")
    var nomorPlat by mutableStateOf("")
    var merkMotor by mutableStateOf("")
    var tipeModel by mutableStateOf("")
    var tahun by mutableStateOf("")
    var warna by mutableStateOf("")
    var catatan by mutableStateOf("")
    var nameError by mutableStateOf<String?>(null)
    var phoneError by mutableStateOf<String?>(null)
    var plateError by mutableStateOf<String?>(null)

    fun setInitialData(customer: Customers?) {
        if (customer != null) {
            firebaseKey = customer.firebaseKey
            namaLengkap = customer.name ?: ""
            nomorTelepon = customer.noHp ?: ""
            alamat = customer.address ?: ""
            nomorPlat = customer.plateNumber ?: ""
            merkMotor = customer.motorBrand ?: ""
            tipeModel = customer.motorModel ?: ""
            tahun = customer.motorYear ?: ""
            warna = customer.motorColor ?: ""
            catatan = customer.complaint ?: ""
        } else {
            resetFields()
        }
        clearErrors()
    }

    private fun resetFields() {
        firebaseKey = null
        namaLengkap = ""
        nomorTelepon = ""
        alamat = ""
        nomorPlat = ""
        merkMotor = ""
        tipeModel = ""
        tahun = ""
        warna = ""
        catatan = ""
    }
    
    private fun clearErrors() {
        nameError = null
        phoneError = null
        plateError = null
    }

    fun saveCustomer(onSuccess: () -> Unit) {
        clearErrors()
        var hasError = false
        if (namaLengkap.isBlank()) {
            nameError = "Nama lengkap wajib diisi"
            hasError = true
        }
        if (nomorTelepon.isBlank() || nomorTelepon.length < 10) {
            phoneError = "Nomor telepon tidak valid"
            hasError = true
        }
        if (nomorPlat.isBlank()) {
            plateError = "Nomor plat wajib diisi"
            hasError = true
        }
        if (hasError) return
        viewModelScope.launch {
            try {
                val customer = Customers(
                    firebaseKey = firebaseKey ?: "",
                    name = namaLengkap,
                    noHp = FormatUtils.cleanNumber(nomorTelepon),
                    address = alamat,
                    plateNumber = nomorPlat,
                    motorBrand = merkMotor,
                    motorModel = tipeModel,
                    motorYear = FormatUtils.cleanNumber(tahun),
                    motorColor = warna,
                    complaint = catatan
                )
                firebaseKey?.let { key ->
                    repository.updateCustomer(key, customer)
                } ?: run {
                    repository.addCustomer(customer)
                }
                onSuccess()
            } catch (_: Exception) {}
        }
    }
}
