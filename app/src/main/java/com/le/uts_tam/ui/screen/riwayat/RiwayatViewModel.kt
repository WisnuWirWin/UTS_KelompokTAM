package com.le.uts_tam.ui.screen.riwayat

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.model.dataclass.Customers
import com.le.uts_tam.data.model.dataclass.Vehicles
import com.le.uts_tam.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RiwayatViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _allHistory = MutableStateFlow<List<HistoryItem>>(emptyList())

    private val _selectedFilter = MutableStateFlow("HARI INI")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        fetchTransactions()
    }

    private fun fetchTransactions() {
        viewModelScope.launch {
            repository.getTransactions().collect { transactions ->
                _allHistory.value = transactions.map { data ->
                    val dateStr = data["date"] as? String ?: ""
                    val tgl = dateStr.split("-").firstOrNull() ?: ""
                    val blnNum = dateStr.split("-").getOrNull(1) ?: ""
                    val bln = when(blnNum) {
                        "01" -> "JAN"
                        "02" -> "FEB"
                        "03" -> "MAR"
                        "04" -> "APR"
                        "05" -> "MEI"
                        "06" -> "JUN"
                        "07" -> "JUL"
                        "08" -> "AGU"
                        "09" -> "SEP"
                        "10" -> "OKT"
                        "11" -> "NOV"
                        "12" -> "DES"
                        else -> "..."
                    }

                    val totalRaw = data["totalPrice"]
                    val totalFormatted = if (totalRaw is Number) {
                        if (totalRaw.toLong() >= 1000) "${totalRaw.toLong() / 1000}K"
                        else totalRaw.toString()
                    } else "0"

                    val items = data["items"] as? List<Map<String, Any>>
                    val layanan = items?.joinToString(", ") { it["name"] as? String ?: "" } ?: "-"
                    
                    val brand = data["motorBrand"] as? String ?: ""
                    val model = data["motorModel"] as? String ?: ""
                    val motorDisplay = "$brand $model".trim().ifEmpty { "-" }

                    HistoryItem(
                        trxId = data["trxId"] as? String ?: "TRX-...",
                        tgl = tgl,
                        bln = bln,
                        jam = data["time"] as? String ?: "00:00",
                        customer = Customers(name = data["customerName"] as? String),
                        vehicle = Vehicles(
                            brand = motorDisplay,
                            numberPlate = data["customerPlate"] as? String
                        ),
                        layanan = layanan,
                        totalHarga = totalFormatted,
                        status = data["status"] as? String ?: "LUNAS",
                        statusColor = if ((data["status"] as? String) == "BON") Color(0xFFFFA000) else Color(0xFF4CAF50),
                        kategori = determineCategory(dateStr)
                    )
                }.sortedByDescending { it.trxId }
            }
        }
    }

    private fun determineCategory(dateStr: String): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val today = sdf.format(Date())
        if (dateStr == today) return "HARI INI"

        // Simple logic for Week/Month for UTS
        return "MINGGU INI"
    }

    val filteredHistory: StateFlow<List<HistoryItem>> = combine(
        _allHistory,
        _selectedFilter,
        _searchQuery
    ) { items: List<HistoryItem>, filter: String, query: String ->
        items.filter { item ->
            val matchFilter = if (filter == "BULAN INI") true else item.kategori == filter
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
