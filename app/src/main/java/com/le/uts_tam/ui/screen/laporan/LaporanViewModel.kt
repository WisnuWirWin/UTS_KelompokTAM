package com.le.uts_tam.ui.screen.laporan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class LaporanViewModel(ownerId: String) : ViewModel() {
    private val repository = FirebaseRepository(ownerId)

    private val _transactions = MutableStateFlow<List<Map<String, Any>>>(emptyList())

    private val _selectedTab = MutableStateFlow("HARIAN")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchTransactions()
    }

    private fun fetchTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getTransactions().collect { data ->
                _transactions.value = data
                _isLoading.value = false
            }
        }
    }

    fun setSelectedTab(tab: String) {
        _selectedTab.value = tab
    }

    val reportData = combine(_transactions, _selectedTab) { transactions, tab ->
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val today = Calendar.getInstance()

        val filteredData = mutableMapOf<String, Long>()
        val profitData = mutableMapOf<String, Long>()

        transactions.forEach { trx ->
            val dateStr = trx["date"] as? String ?: ""
            val total = (trx["totalPrice"] as? Number ?: 0).toLong()

            val profit = (total * 0.4).toLong()

            when (tab) {
                "HARIAN" -> {
                    filteredData[dateStr] = (filteredData[dateStr] ?: 0) + total
                    profitData[dateStr] = (profitData[dateStr] ?: 0) + profit
                }
                "MINGGUAN" -> {
                    // Group by week (e.g., "W1", "W2" of current month)
                    try {
                        val date = sdf.parse(dateStr)
                        val cal = Calendar.getInstance().apply { time = date!! }
                        if (cal.get(Calendar.MONTH) == today.get(Calendar.MONTH)) {
                            val week = "W${cal.get(Calendar.WEEK_OF_MONTH)}"
                            filteredData[week] = (filteredData[week] ?: 0) + total
                            profitData[week] = (profitData[week] ?: 0) + profit
                        }
                    } catch (e: Exception) {}
                }
                "BULAN INI" -> {
                    val month = dateStr.substring(3) // MM-yyyy
                    filteredData[month] = (filteredData[month] ?: 0) + total
                    profitData[month] = (profitData[month] ?: 0) + profit
                }
            }
        }

        val labels = mutableListOf<String>()
        val chartValues = mutableListOf<Float>()
        val reportItems = mutableListOf<ReportItem>()
        var totalEstimasi = 0L

        if (tab == "HARIAN") {
            for (i in 6 downTo 0) {
                val cal = Calendar.getInstance().apply { add(Calendar.DATE, -i) }
                val label = SimpleDateFormat("dd", Locale.getDefault()).format(cal.time)
                val fullDate = sdf.format(cal.time)

                val income = filteredData[fullDate] ?: 0L
                val profit = profitData[fullDate] ?: 0L

                labels.add(label)
                chartValues.add(income / 1000000f)

                if (income > 0) {
                    reportItems.add(0, ReportItem(
                        date = SimpleDateFormat("dd MMM", Locale.getDefault()).format(cal.time),
                        income = "Rp ${formatNumber(income)}",
                        profit = "Rp ${formatNumber(profit)}",
                        isToday = i == 0
                    ))
                }
                totalEstimasi += income
            }
        } else if (tab == "MINGGUAN") {
            for (i in 1..5) {
                val week = "W$i"
                val income = filteredData[week] ?: 0L
                val profit = profitData[week] ?: 0L
                labels.add(week)
                chartValues.add(income / 1000000f)
                if (income > 0) {
                    reportItems.add(ReportItem("Minggu $i", "Rp ${formatNumber(income)}", "Rp ${formatNumber(profit)}"))
                }
                totalEstimasi += income
            }
        } else {
            val months = listOf("JAN", "FEB", "MAR", "APR", "MEI", "JUN", "JUL", "AGU", "SEP", "OKT", "NOV", "DES")
            val year = today.get(Calendar.YEAR)
            months.forEachIndexed { index, m ->
                val key = "%02d-%d".format(index + 1, year)
                val income = filteredData[key] ?: 0L
                val profit = profitData[key] ?: 0L
                labels.add(m)
                chartValues.add(income / 1000000f)
                if (income > 0) {
                    reportItems.add(ReportItem(m, "Rp ${formatNumber(income)}", "Rp ${formatNumber(profit)}"))
                }
                totalEstimasi += income
            }
        }

        ReportState(labels, chartValues, reportItems, "Rp ${formatNumber(totalEstimasi)}")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReportState())

    private fun formatNumber(num: Long): String {
        return java.text.NumberFormat.getInstance(Locale("id", "ID")).format(num)
    }
}

data class ReportState(
    val labels: List<String> = emptyList(),
    val chartData: List<Float> = emptyList(),
    val items: List<ReportItem> = emptyList(),
    val totalEstimasi: String = "Rp 0"
)
