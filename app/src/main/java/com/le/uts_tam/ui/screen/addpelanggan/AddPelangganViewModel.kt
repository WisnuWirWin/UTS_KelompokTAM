package com.le.uts_tam.ui.screen.addpelanggan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.local.AppDatabase
import com.le.uts_tam.data.model.dataclass.Customers
import com.le.uts_tam.data.repository.FirebaseRepository
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

    fun saveCustomer(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val customer = Customers(
                    firebaseKey = firebaseKey ?: "",
                    name = namaLengkap,
                    noHp = nomorTelepon,
                    address = alamat,
                    plateNumber = nomorPlat,
                    motorBrand = merkMotor,
                    motorModel = tipeModel,
                    motorYear = tahun,
                    motorColor = warna,
                    complaint = catatan,
                )
                
                if (firebaseKey != null) {
                    repository.updateCustomer(firebaseKey!!, customer)
                } else {
                    repository.addCustomer(customer)
                }
                onSuccess()
            } catch (_: Exception) {
            }
        }
    }
}
