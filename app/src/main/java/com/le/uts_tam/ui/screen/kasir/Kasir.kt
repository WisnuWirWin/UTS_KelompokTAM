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
import com.le.uts_tam.ui.components.SaveConfirmationDialog
import com.le.uts_tam.utils.FormatUtils

@Composable
fun Kasir(
    onBack: () -> Unit,
    onPrintNota: () -> Unit,
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

    val showScanner = remember { mutableStateOf(value = false) }
    var showConfirmDialog by remember { mutableStateOf(value = false) }

    if (showConfirmDialog) {
        SaveConfirmationDialog(
            onConfirm = {
                showConfirmDialog = false
                viewModel.processPayment {
                    onPrintNota()
                }
            },
            onDismiss = { showConfirmDialog = false },
            title = "Konfirmasi Pembayaran",
            message = "Apakah Anda yakin ingin memproses pembayaran sebesar RP ${"%,d".format(totalBayar)}?"
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showScanner.value = true
        } else {
            Toast.makeText(context, "Izin kamera diperlukan untuk scan QR", Toast.LENGTH_SHORT).show()
        }
    }

    if (showScanner.value) {
        Box(modifier = Modifier.fillMaxSize()) {
            QRScanner(
                onScan = { code ->
                    if (viewModel.addToCartByQr(code)) {
                        showScanner.value = false
                        Toast.makeText(context, "Barang berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    }
                },
                onClose = { showScanner.value = false },
            )
            
            // Overlay Close Button
            IconButton(
                onClick = { showScanner.value = false },
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close Scanner", tint = Color.White)
            }
        }
    } else {
        Scaffold(containerColor = Color(0xFF121212)) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                        }
                        Text("KASIR", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("TRX-${System.currentTimeMillis() / 100000}", color = Color(0xFFFF5722), fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(listOf(Color.Red, Color.Yellow, Color.Transparent)),
                        ),
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Customer Selector
                Text("PELANGGAN", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                if (selectedCustomer == null) {
                    OutlinedTextField(
                        value = customerSearchQuery,
                        onValueChange = { viewModel.onCustomerSearchChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Cari Pelanggan...", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFFF5722)) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1E1E1E),
                            unfocusedContainerColor = Color(0xFF1E1E1E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFF5722),
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    if (customerSearchQuery.isNotEmpty() && filteredCustomers.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF262626)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column {
                                filteredCustomers.take(3).forEach { customer ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.selectCustomer(customer) }
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(customer.name ?: "", color = Color.White)
                                        Text(customer.plateNumber ?: "-", color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFFF5722))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(selectedCustomer?.name ?: "", color = Color.White, fontWeight = FontWeight.Bold)
                                    Text(selectedCustomer?.plateNumber ?: "-", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                            IconButton(onClick = { viewModel.selectCustomer(null) }) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Item Search with QR Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Cari barang/jasa...", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFFF5722)) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1E1E1E),
                            unfocusedContainerColor = Color(0xFF1E1E1E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFF5722),
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                showScanner.value = true
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.size(56.dp).background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                    ) {
                        Icon(Icons.Default.Build, contentDescription = "Scan QR", tint = Color(0xFFFF5722))
                    }
                }

                if (searchQuery.isNotEmpty() && filteredItems.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF262626)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column {
                            filteredItems.take(4).forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.addToCart(item)
                                            viewModel.onSearchQueryChange("")
                                        }
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(item.name ?: "", color = Color.White)
                                    Text("Rp ${item.price}", color = Color(0xFFFFEB3B))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("KERANJANG BARANG", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
                ) {
                    items(cartItems.toList()) { (item, qty) ->
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
                                    IconButton(onClick = { viewModel.updateQty(item, -1) }) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                                    }
                                    Text(text = qty.toString(), color = Color.White, fontWeight = FontWeight.Bold)
                                    Box(
                                        modifier = Modifier.size(30.dp).background(Color(0xFFFF5722), CircleShape)
                                            .clickable { viewModel.updateQty(item, 1) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TOTAL BAYAR", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                text = FormatUtils.formatCurrency(totalBayar.toLong()).uppercase(),
                                color = Color(0xFFFFEB3B),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        }
                        Button(
                            onClick = { 
                                showConfirmDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                            shape = RoundedCornerShape(12.dp),
                            enabled = cartItems.isNotEmpty()
                        ) {
                            Text("BAYAR", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
