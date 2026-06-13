package com.le.uts_tam.ui.screen.inventaris

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.local.AppDatabase
import com.le.uts_tam.data.model.dataclass.Items
import com.le.uts_tam.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InventarisViewModel(ownerId: String, database: AppDatabase) : ViewModel() {
    private val repository = FirebaseRepository(ownerId, database)
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

    fun deleteItem(item: Items) {
        viewModelScope.launch {
            item.firebaseKey?.let { key ->
                repository.deleteItem(key)
            }
        }
    }

    fun fetchItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getItems()
                    .catch { e ->
                        _isLoading.value = false
                        // You could add an error state flow here if needed
                    }
                    .collect { items ->
                        _items.value = items
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }
}
