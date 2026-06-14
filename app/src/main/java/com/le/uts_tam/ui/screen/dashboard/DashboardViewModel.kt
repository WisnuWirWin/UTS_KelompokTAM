package com.le.uts_tam.ui.screen.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

class DashboardViewModel(ownerId: String, database: AppDatabase) : ViewModel() {
    private val repository = FirebaseRepository(ownerId, database)

    var ownerName by mutableStateOf("...")
    var profileImageUrl by mutableStateOf("")
    var totalItems by mutableIntStateOf(0)
    var lowStockItemsCount by mutableIntStateOf(0)
    var totalCustomers by mutableIntStateOf(0)
    var lowStockList by mutableStateOf<List<Items>>(emptyList())

    var totalIncomeToday by mutableLongStateOf(0L)
    var transactionCountToday by mutableIntStateOf(0)
    var serviceCountToday by mutableIntStateOf(0)
    var sparepartIncomeToday by mutableLongStateOf(0L)
    var monthlyIncome by mutableLongStateOf(0L)

    var isLoading by mutableStateOf(false)

    init {
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
                ownerName = owner?.owner ?: owner?.username ?: "Pengguna"
                profileImageUrl = owner?.imageUrl ?: ""
                
                totalItems = items.size
                lowStockList = items.filter { (it.stock?.toIntOrNull() ?: 0) < 3 }
                lowStockItemsCount = lowStockList.size
                totalCustomers = customers.size

                calculateFinance(transactions)
                
                isLoading = false
            }.collect {}
        }
    }

    private fun calculateFinance(transactions: List<Map<String, Any>>) {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val todayStr = sdf.format(Date())
        val currentMonth = todayStr.substring(3)

        var incomeToday = 0L
        var countToday = 0
        var servicesToday = 0
        var sparepartsToday = 0L
        var incomeMonth = 0L

        transactions.forEach { data ->
            val date = data["date"] as? String ?: ""
            val total = (data["totalPrice"] as? Number ?: 0).toLong()

            if (date.endsWith(currentMonth)) incomeMonth += total
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
                        val qty = (item["qty"] as? Number ?: 1).toLong()
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
