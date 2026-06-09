package com.le.uts_tam.ui.screen.kasir

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.model.dataclass.Customers
import com.le.uts_tam.data.model.dataclass.Items
import com.le.uts_tam.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class KasirViewModel(ownerId: String) : ViewModel() {
    private val repository = FirebaseRepository(ownerId)

    private val _items = MutableStateFlow<List<Items>>(emptyList())
    private val _customers = MutableStateFlow<List<Customers>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _customerSearchQuery = MutableStateFlow("")
    val customerSearchQuery: StateFlow<String> = _customerSearchQuery

    private val _selectedCustomer = MutableStateFlow<Customers?>(null)
    val selectedCustomer: StateFlow<Customers?> = _selectedCustomer

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

    val filteredCustomers: StateFlow<List<Customers>> = combine(_customers, _customerSearchQuery) { customers, query ->
        if (query.isEmpty()) emptyList()
        else customers.filter { it.name?.contains(query, ignoreCase = true) == true }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _isLoading.value = true
            launch {
                repository.getItems().collect { _items.value = it }
            }
            launch {
                repository.getCustomers().collect { _customers.value = it }
            }
            _isLoading.value = false
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCustomerSearchChange(newQuery: String) {
        _customerSearchQuery.value = newQuery
    }

    fun selectCustomer(customer: Customers?) {
        _selectedCustomer.value = customer
        _customerSearchQuery.value = ""
    }

    fun addToCart(item: Items) {
        val id = item.firebaseKey ?: item.id ?: return
        val currentMap = _cartItems.value.toMutableMap()
        val currentQty = currentMap[id]?.second ?: 0

        currentMap[id] = Pair(item, currentQty + 1)
        _cartItems.value = currentMap
        _searchQuery.value = ""
    }

    fun updateQty(item: Items, delta: Int) {
        val id = item.firebaseKey ?: item.id ?: return
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

    fun processPayment(onSuccess: () -> Unit) {
        val customer = _selectedCustomer.value
        val items = _cartItems.value.values.toList()
        val total = totalBayar.value

        if (items.isEmpty()) return

        viewModelScope.launch {
            try {
                val sdfDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                val now = Date()

                val transaction = mapOf(
                    "trxId" to "TRX-${System.currentTimeMillis()}",
                    "date" to sdfDate.format(now),
                    "time" to sdfTime.format(now),
                    "customerId" to (customer?.firebaseKey ?: "anon"),
                    "customerName" to (customer?.name ?: "Umum"),
                    "customerPlate" to (customer?.plateNumber ?: "-"),
                    "motorBrand" to (customer?.motorBrand ?: ""),
                    "motorModel" to (customer?.motorModel ?: ""),
                    "totalPrice" to total,
                    "items" to items.map { (item, qty) ->
                        mapOf(
                            "itemId" to (item.firebaseKey ?: item.id),
                            "name" to item.name,
                            "price" to item.price,
                            "purchasePrice" to item.purchasePrice,
                            "qty" to qty
                        )
                    },
                    "status" to "LUNAS"
                )

                repository.addTransaction(transaction)
                _cartItems.value = emptyMap()
                _selectedCustomer.value = null
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
