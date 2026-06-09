package com.le.uts_tam.ui.screen.riwayat

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.model.dataclass.Customers
import com.le.uts_tam.data.model.dataclass.Vehicles
import com.le.uts_tam.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RiwayatViewModel(ownerId: String) : ViewModel() {
    private val repository = FirebaseRepository(ownerId)
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
                    val bln = getMonthName(blnNum)

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

    private fun getMonthName(num: String): String = when(num) {
        "01" -> "JAN"; "02" -> "FEB"; "03" -> "MAR"; "04" -> "APR"
        "05" -> "MEI"; "06" -> "JUN"; "07" -> "JUL"; "08" -> "AGU"
        "09" -> "SEP"; "10" -> "OKT"; "11" -> "NOV"; "12" -> "DES"
        else -> "..."
    }

    private fun determineCategory(dateStr: String): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val now = Calendar.getInstance()
        val todayStr = sdf.format(now.time)

        if (dateStr == todayStr) return "HARI INI"

        try {
            val date = sdf.parse(dateStr) ?: return "LAINNYA"
            val itemCal = Calendar.getInstance().apply { time = date }

            if (itemCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                itemCal.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)) {
                return "MINGGU INI"
            }

            if (itemCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                itemCal.get(Calendar.MONTH) == now.get(Calendar.MONTH)) {
                return "BULAN INI"
            }
        } catch (e: Exception) {}

        return "LAINNYA"
    }

    val filteredHistory: StateFlow<List<HistoryItem>> = combine(
        _allHistory, _selectedFilter, _searchQuery
    ) { items, filter, query ->
        items.filter { item ->
            val matchFilter = when (filter) {
                "HARI INI" -> item.kategori == "HARI INI"
                "MINGGU INI" -> item.kategori == "HARI INI" || item.kategori == "MINGGU INI"
                "BULAN INI" -> item.kategori == "HARI INI" || item.kategori == "MINGGU INI" || item.kategori == "BULAN INI"
                else -> true
            }
            val matchQuery = item.customer.name?.contains(query, ignoreCase = true) == true ||
                    item.vehicle.numberPlate?.contains(query, ignoreCase = true) == true
            matchFilter && (query.isEmpty() || matchQuery)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(filter: String) { _selectedFilter.value = filter }
    fun onSearchQueryChanged(newQuery: String) { _searchQuery.value = newQuery }
}