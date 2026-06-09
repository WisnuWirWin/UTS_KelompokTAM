package com.le.uts_tam.ui.screen.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.model.dataclass.Items
import com.le.uts_tam.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DashboardViewModel(ownerId: String) : ViewModel() {
    private val repository = FirebaseRepository(ownerId)

    var ownerName by mutableStateOf("...")
    var profileImageUrl by mutableStateOf("")
    var totalItems by mutableStateOf(0)
    var lowStockItemsCount by mutableStateOf(0)
    var totalCustomers by mutableStateOf(0)
    var lowStockList by mutableStateOf<List<Items>>(emptyList())
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
                repository.getCustomers()
            ) { owner, items, customers ->
                ownerName = owner?.owner ?: "Owner"
                profileImageUrl = owner?.imageUrl ?: ""
                
                totalItems = items.size
                lowStockList = items.filter { (it.stock?.toIntOrNull() ?: 0) < 3 }
                lowStockItemsCount = lowStockList.size
                
                totalCustomers = customers.size
                
                isLoading = false
            }.collect {}
        }
    }
}
