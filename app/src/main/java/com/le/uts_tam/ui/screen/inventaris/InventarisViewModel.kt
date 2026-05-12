package com.le.uts_tam.ui.screen.inventaris

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.remote.model.Items
import com.le.uts_tam.data.remote.retrofit.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InventarisViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<Items>>(emptyList())
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("SEMUA")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val filteredItems: StateFlow<List<Items>> = combine(_items, _searchQuery, _selectedCategory) { items, query, category ->
        items.filter { item ->
            val matchesQuery = item.name?.contains(query, ignoreCase = true) == true || 
                               item.id?.contains(query, ignoreCase = true) == true
            
            val matchesCategory = when (category) {
                "SEMUA" -> true
                "OLI & CAIRAN" -> item.name?.contains("oli", ignoreCase = true) == true || item.name?.contains("oil", ignoreCase = true) == true
                "FILTER" -> item.name?.contains("filter", ignoreCase = true) == true
                "REM" -> item.name?.contains("rem", ignoreCase = true) == true || item.name?.contains("brake", ignoreCase = true) == true
                else -> true
            }

            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchItems()
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategoryChange(newCategory: String) {
        _selectedCategory.value = newCategory
    }

    fun fetchItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.instance.getItems()
                _items.value = response.filterNotNull()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
