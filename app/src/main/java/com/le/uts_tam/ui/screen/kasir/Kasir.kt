package com.le.uts_tam.ui.screen.kasir

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.le.uts_tam.data.model.dataclass.Items

@Composable
fun Kasir(
    onBack: () -> Unit,
    onPrintNota: () -> Unit, // Tambahkan ini agar tidak error di MainActivity
    viewModel: KasirViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredItems by viewModel.filteredItems.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val totalBayar by viewModel.totalBayar.collectAsState()

    Scaffold(containerColor = Color(0xFF121212)) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp)) // Dikurangi agar tidak terlalu turun

            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        // Menggunakan AutoMirrored agar tidak deprecated
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                    Text("KASIR", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                }
                Text("TRX-20250422-013", color = Color(0xFFFF5722), fontSize = 12.sp)
            }

            // Divider Garis Gradasi
            Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(
                Brush.horizontalGradient(listOf(Color.Red, Color.Yellow, Color.Transparent))
            ))

            Spacer(modifier = Modifier.height(20.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
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

            // Dropdown Hasil Pencarian
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
                                        viewModel.onSearchQueryChange("") // Reset search setelah pilih
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

            // Daftar Keranjang
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
                                // Tombol Kurang / Hapus
                                IconButton(onClick = { viewModel.updateQty(item, -1) }) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                                }
                                Text("$qty", color = Color.White, fontWeight = FontWeight.Bold)
                                // Tombol Tambah
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

            // Bottom Section: Total & Tombol Bayar
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
                        Text("RP ${"%,d".format(totalBayar)}", color = Color(0xFFFFEB3B), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Button(
                        onClick = { onPrintNota() }, // Pindah ke layar Nota Digital
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = cartItems.isNotEmpty() // Tombol hanya aktif jika ada belanjaan
                    ) {
                        Text("BAYAR", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}