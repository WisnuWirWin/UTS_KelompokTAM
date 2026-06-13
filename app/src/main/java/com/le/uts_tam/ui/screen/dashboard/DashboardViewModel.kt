package com.le.uts_tam.ui.screen.dashboard

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.local.AppDatabase
import com.le.uts_tam.data.model.dataclass.Items
import com.le.uts_tam.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel(private val ownerId: String, database: AppDatabase) : ViewModel() {
    private val repository = FirebaseRepository(ownerId, database)

    var ownerName by mutableStateOf("...")
        private set
    var profileImageUrl by mutableStateOf("")
        private set
    var totalItems by mutableStateOf(0)
        private set
    var lowStockItemsCount by mutableStateOf(0)
        private set
    var totalCustomers by mutableStateOf(0)
        private set
    var lowStockList by mutableStateOf<List<Items>>(emptyList())
        private set

    var totalIncomeToday by mutableLongStateOf(0L)
        private set
    var transactionCountToday by mutableStateOf(0)
        private set
    var serviceCountToday by mutableStateOf(0)
        private set
    var sparepartIncomeToday by mutableLongStateOf(0L)
        private set
    var monthlyIncome by mutableLongStateOf(0L)
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        Log.d("DashboardVM", "DashboardViewModel created for ownerId: $ownerId")
        observeDashboardData()
    }

    private fun observeDashboardData() {
        viewModelScope.launch {
            isLoading = true
            combine(
                repository.getOwner(),
                repository.getItems(),
                repository.getCustomers(),
                repository.getTransactions()
            ) { owner, items, customers, transactions ->
                Log.d("DashboardVM", "Data received for ownerId: $ownerId. Owner: ${owner?.username}")

                ownerName = owner?.owner ?: owner?.username ?: "Pengguna"
                profileImageUrl = owner?.imageUrl ?: ""
                
                totalItems = items.size
                lowStockList = items.filter { (it.stock?.toIntOrNull() ?: 0) < 3 }
                lowStockItemsCount = lowStockList.size
                
                totalCustomers = customers.size

                calculateFinanceData(transactions)
                
                isLoading = false
            }.collect {}
        }
    }

    private fun calculateFinanceData(transactions: List<Map<String, Any>>) {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val todayStr = sdf.format(Date())
        val currentMonth = if (todayStr.length >= 3) todayStr.substring(3) else "" // MM-yyyy

        var incomeToday = 0L
        var countToday = 0
        var servicesToday = 0
        var sparepartsToday = 0L
        var incomeMonth = 0L

        transactions.forEach { data ->
            val date = data["date"] as? String ?: ""
            val totalRaw = data["totalPrice"] as? Number ?: 0
            val total = totalRaw.toLong()

            if (currentMonth.isNotEmpty() && date.endsWith(currentMonth)) {
                incomeMonth += total
            }

            if (date == todayStr) {
                incomeToday += total
                countToday++
                
                @Suppress("UNCHECKED_CAST")
                val items = data["items"] as? List<Map<String, Any>>
                items?.forEach { item ->
                    val name = (item["name"] as? String)?.lowercase() ?: ""
                    if (name.contains("servis") || name.contains("jasa")) {
                        servicesToday++
                    } else {
                        val price = (item["price"] as? Number ?: 0).toLong()
                        val qty = (item["quantity"] as? Number ?: 1).toLong()
                        sparepartsToday += price * qty
                    }
                }
            }
        }

        totalIncomeToday = incomeToday
        transactionCountToday = countToday
        serviceCountToday = servicesToday
        sparepartIncomeToday = sparepartsToday
        monthlyIncome = incomeMonth
    }
}
