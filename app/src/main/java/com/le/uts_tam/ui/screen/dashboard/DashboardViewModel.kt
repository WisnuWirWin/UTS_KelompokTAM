package com.le.uts_tam.ui.screen.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.model.dataclass.Items
import com.le.uts_tam.repository.DashboardRepository
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val repository = DashboardRepository()

    var ownerName by mutableStateOf("...")
    var profileImageUrl by mutableStateOf("")
    var totalItems by mutableStateOf(0)
    var lowStockItemsCount by mutableStateOf(0)
    var totalCustomers by mutableStateOf(0)
    var lowStockList by mutableStateOf<List<Items>>(emptyList())
    var isLoading by mutableStateOf(false)

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            isLoading = true
            try {
                val owners = repository.getOwners()
                if (owners.isNotEmpty()) {
                    ownerName = owners[0].owner ?: "Owner"
                    profileImageUrl = owners[0].imageUrl ?: ""
                }

                val items = repository.getItems()
                totalItems = items.size
                lowStockList = items.filter { (it.stock?.toIntOrNull() ?: 0) < 3 }
                lowStockItemsCount = lowStockList.size

                val customers = repository.getCustomers()
                totalCustomers = customers.size

            } catch (e: Exception) {
            } finally {
                isLoading = false
            }
        }
    }
}
