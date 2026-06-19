package com.le.uts_tam.ui.screen.kasir

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.le.uts_tam.data.model.dataclass.Customers
import com.le.uts_tam.data.model.dataclass.Items
import com.le.uts_tam.ui.components.SaveConfirmationDialog
import com.le.uts_tam.utils.FormatUtils

@Composable
fun Kasir(
    onBack: () -> Unit,
    onPrintNota: () -> Unit,
    onAddCustomerClick: () -> Unit = {},
    viewModel: KasirViewModel = viewModel(),
) {
    val context = LocalContext.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val customerSearchQuery by viewModel.customerSearchQuery.collectAsState()
    val filteredItems by viewModel.filteredItems.collectAsState()
    val filteredCustomers by viewModel.filteredCustomers.collectAsState()
    val selectedCustomer by viewModel.selectedCustomer.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val totalBayar by viewModel.totalBayar.collectAsState()

    val showScanner = remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showCart by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        PaymentConfirmationDialog(
            totalAmount = totalBayar,
            onConfirm = {
                showConfirmDialog = false
                viewModel.processPayment { onPrintNota() }
            },
            onDismiss = { showConfirmDialog = false }
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) showScanner.value = true
        else Toast.makeText(context, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
    }

    if (showScanner.value) {
        ScannerView(
            onScan = { code ->
                if (viewModel.addToCartByQr(code)) {
                    showScanner.value = false
                    Toast.makeText(context, "Berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                }
            },
            onClose = { showScanner.value = false }
        )
    } else {
        Scaffold(containerColor = Color(0xFF121212)) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
                KasirHeader(onBack = onBack)
                
                Spacer(modifier = Modifier.height(20.dp))

                CustomerSection(
                    selectedCustomer = selectedCustomer,
                    searchQuery = customerSearchQuery,
                    customers = filteredCustomers,
                    onSearchChange = viewModel::onCustomerSearchChange,
                    onSelect = viewModel::selectCustomer,
                    onAddClick = onAddCustomerClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProductSearchSection(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    onScanClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            showScanner.value = true
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                CatalogCartToggle(
                    isCartVisible = showCart,
                    cartSize = cartItems.size,
                    onToggle = { showCart = it }
                )

                if (showCart) {
                    CartList(
                        items = cartItems,
                        onUpdateQty = viewModel::updateQty,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    ProductCatalog(
                        items = filteredItems,
                        onAddToCart = viewModel::addToCart,
                        modifier = Modifier.weight(1f)
                    )
                }

                PaymentFooter(
                    totalAmount = totalBayar.toLong(),
                    isPayEnabled = cartItems.isNotEmpty(),
                    onPayClick = { showConfirmDialog = true }
                )
            }
        }
    }
}

@Composable
private fun KasirHeader(onBack: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
                Text("KASIR", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            }
            Text("TRX-${System.currentTimeMillis() / 100000}", color = Color(0xFFFF5722), fontSize = 12.sp)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Brush.horizontalGradient(listOf(Color.Red, Color.Yellow, Color.Transparent))),
        )
    }
}

@Composable
private fun CustomerSection(
    selectedCustomer: Customers?,
    searchQuery: String,
    customers: List<Customers>,
    onSearchChange: (String) -> Unit,
    onSelect: (Customers?) -> Unit,
    onAddClick: () -> Unit
) {
    Text("PELANGGAN", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
    
    if (selectedCustomer == null) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Cari Pelanggan...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFFFF5722)) },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = kasirTextFieldColors()
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                ) {
                    Icon(Icons.Default.Add, "Tambah Pelanggan", tint = Color(0xFFFF5722))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(customers) { customer ->
                    CustomerCard(customer = customer, onClick = { onSelect(customer) })
                }
            }
        }
    } else {
        SelectedCustomerCard(customer = selectedCustomer, onDeselect = { onSelect(null) })
    }
}

@Composable
private fun CustomerCard(customer: Customers, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(customer.name ?: "", color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(customer.plateNumber ?: "-", color = Color.Gray, fontSize = 10.sp)
        }
    }
}

@Composable
private fun SelectedCustomerCard(customer: Customers, onDeselect: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, tint = Color(0xFFFF5722))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(customer.name ?: "", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(customer.plateNumber ?: "-", color = Color.Gray, fontSize = 12.sp)
                }
            }
            IconButton(onClick = onDeselect) {
                Icon(Icons.Default.Close, null, tint = Color.Red)
            }
        }
    }
}

@Composable
private fun ProductSearchSection(
    query: String,
    onQueryChange: (String) -> Unit,
    onScanClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Cari barang/jasa...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFFFF5722)) },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = kasirTextFieldColors()
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onScanClick,
            modifier = Modifier
                .size(56.dp)
                .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
        ) {
            Icon(Icons.Default.Search, "Scan QR Barang", tint = Color(0xFFFF5722))
        }
    }
}

@Composable
private fun CatalogCartToggle(
    isCartVisible: Boolean,
    cartSize: Int,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isCartVisible) "KERANJANG ($cartSize)" else "KATALOG BARANG",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = { onToggle(!isCartVisible) }) {
            Text(
                text = if (isCartVisible) "LIHAT KATALOG" else "LIHAT KERANJANG",
                color = Color(0xFFFF5722),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ProductCatalog(
    items: List<Items>,
    onAddToCart: (Items) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
    ) {
        items(items) { item ->
            ProductItemCard(item = item, onClick = { onAddToCart(item) })
        }
    }
}

@Composable
private fun ProductItemCard(item: Items, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name ?: "", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Stok: ${item.stock ?: "0"}", color = Color.Gray, fontSize = 11.sp)
            }
            Text("Rp ${item.price}", color = Color(0xFFFFEB3B), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CartList(
    items: List<Pair<Items, Int>>,
    onUpdateQty: (Items, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
    ) {
        if (items.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Keranjang masih kosong", color = Color.Gray)
                }
            }
        }
        items(items) { (item, qty) ->
            CartItemCard(item = item, qty = qty, onUpdateQty = onUpdateQty)
        }
    }
}

@Composable
private fun CartItemCard(item: Items, qty: Int, onUpdateQty: (Items, Int) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name ?: "", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Rp ${item.price}", color = Color.Gray, fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = { onUpdateQty(item, -1) }) {
                    Icon(Icons.Default.Close, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                }
                Text(text = qty.toString(), color = Color.White, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color(0xFFFF5722), CircleShape)
                        .clickable { onUpdateQty(item, 1) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun PaymentFooter(
    totalAmount: Long,
    isPayEnabled: Boolean,
    onPayClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("TOTAL BAYAR", color = Color.Gray, fontSize = 12.sp)
                Text(
                    text = FormatUtils.formatCurrency(totalAmount).uppercase(),
                    color = Color(0xFFFFEB3B),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Button(
                onClick = onPayClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                shape = RoundedCornerShape(12.dp),
                enabled = isPayEnabled
            ) {
                Text("BAYAR", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PaymentConfirmationDialog(
    totalAmount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    SaveConfirmationDialog(
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        title = "Konfirmasi Pembayaran",
        message = "Apakah Anda yakin ingin memproses pembayaran sebesar RP ${"%,d".format(totalAmount)}?"
    )
}

@Composable
private fun ScannerView(
    onScan: (String) -> Unit,
    onClose: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        QRScanner(onScan = onScan, onClose = onClose)
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.Default.Close, "Close Scanner", tint = Color.White)
        }
    }
}

@Composable
private fun kasirTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color(0xFF1E1E1E),
    unfocusedContainerColor = Color(0xFF1E1E1E),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = Color(0xFFFF5722),
    unfocusedBorderColor = Color.Transparent
)
