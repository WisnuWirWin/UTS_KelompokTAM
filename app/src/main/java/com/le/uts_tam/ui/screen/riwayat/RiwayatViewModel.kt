package com.le.uts_tam.ui.screen.riwayat

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.model.dataclass.Customers
import com.le.uts_tam.data.model.dataclass.Vehicles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class RiwayatViewModel : ViewModel() {

    private val _allHistory = MutableStateFlow(
        listOf(
            HistoryItem(
                trxId = "TRX-20250422-013",
                tgl = "22",
                bln = "APR",
                jam = "10:25",
                customer = Customers(name = "Wisnu Wira Winata"),
                vehicle = Vehicles(brand = "Honda CB150R", numberPlate = "B 4821 XZ"),
                layanan = "Servis Rutin + Ganti Oli",
                totalHarga = "200RB",
                status = "LUNAS",
                statusColor = Color(0xFF4CAF50),
                kategori = "HARI INI"
            ),
            HistoryItem(
                trxId = "TRX-20250422-012",
                tgl = "22",
                bln = "APR",
                jam = "09:10",
                customer = Customers(name = "Kamila Putri Hasan"),
                vehicle = Vehicles(brand = "Yamaha NMAX", numberPlate = "B 7734 KC"),
                layanan = "Tune Up + Busi",
                totalHarga = "450RB",
                status = "LUNAS",
                statusColor = Color(0xFF4CAF50),
                kategori = "HARI INI"
            ),
            HistoryItem(
                trxId = "TRX-20250421-011",
                tgl = "21",
                bln = "APR",
                jam = "15:42",
                customer = Customers(name = "Athallah"),
                vehicle = Vehicles(brand = "Suzuki GSX", numberPlate = "D 2210 YA"),
                layanan = "Rem Depan + Kampas",
                totalHarga = "280RB",
                status = "BON",
                statusColor = Color(0xFFFFA000),
                kategori = "MINGGU INI"
            ),
            HistoryItem(
                trxId = "TRX-20250415-009",
                tgl = "15",
                bln = "APR",
                jam = "14:00",
                customer = Customers(name = "Dzaki"),
                vehicle = Vehicles(brand = "Honda Vario", numberPlate = "A 1234 BC"),
                layanan = "Ganti Ban Luar",
                totalHarga = "300RB",
                status = "LUNAS",
                statusColor = Color(0xFF4CAF50),
                kategori = "BULAN INI"
            ),

        )
    )

    private val _selectedFilter = MutableStateFlow("HARI INI")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredHistory: StateFlow<List<HistoryItem>> = combine(
        _allHistory,
        _selectedFilter,
        _searchQuery
    ) { items: List<HistoryItem>, filter: String, query: String ->
        items.filter { item ->
            val matchFilter = item.kategori == filter
            val matchQuery = item.customer.name?.contains(query, ignoreCase = true) == true ||
                    item.vehicle.numberPlate?.contains(query, ignoreCase = true) == true
            matchFilter && (query.isEmpty() || matchQuery)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }
}