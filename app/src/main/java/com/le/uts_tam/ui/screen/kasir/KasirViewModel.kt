package com.le.uts_tam.ui.screen.kasir

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.model.dataclass.Items
import com.le.uts_tam.data.remote.retrofit.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class KasirViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<Items>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _cartItems = MutableStateFlow<Map<String, Pair<Items, Int>>>(emptyMap())

    val cartItems: StateFlow<List<Pair<Items, Int>>> = _cartItems
        .map { it.values.toList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val totalBayar: StateFlow<Int> = _cartItems
        .map { map ->
            map.values.sumOf { (item, qty) ->
                val cleanPrice = item.price?.replace(Regex("[^0-9]"), "") ?: ""
                val price = cleanPrice.toIntOrNull() ?: 0
                price * qty
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val filteredItems: StateFlow<List<Items>> = combine(_items, _searchQuery) { items, query ->
        if (query.isEmpty()) emptyList()
        else items.filter { it.name?.contains(query, ignoreCase = true) == true }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchItems()
    }

    private fun fetchItems() {
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

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun addToCart(item: Items) {
        val id = item.id ?: return
        val currentMap = _cartItems.value.toMutableMap()
        val currentQty = currentMap[id]?.second ?: 0

        currentMap[id] = Pair(item, currentQty + 1)
        _cartItems.value = currentMap
        _searchQuery.value = ""
    }

    fun updateQty(item: Items, delta: Int) {
        val id = item.id ?: return
        val currentMap = _cartItems.value.toMutableMap()
        val currentQty = currentMap[id]?.second ?: 0
        val newQty = currentQty + delta

        if (newQty <= 0) {
            currentMap.remove(id)
        } else {
            currentMap[id] = Pair(item, newQty)
        }
        _cartItems.value = currentMap
    }
}