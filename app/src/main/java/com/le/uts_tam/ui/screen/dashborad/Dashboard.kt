package com.le.uts_tam.ui.screen.dashborad

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.le.uts_tam.R

@Composable
fun Dashboard(
    onPelangganClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onKasirClick: () -> Unit = {},
    onRiwayatClick: () -> Unit = {},
    onStokClick: () -> Unit = {},
    onLaporanClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            // Nav Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.clickable { onKasirClick() }) {
                        QuickActionButton(Icons.Default.ShoppingCart, "Kasir")
                    }
                    Box(modifier = Modifier.clickable { onRiwayatClick() }) {
                        QuickActionButton(Icons.Default.Email, "Riwayat")
                    }
                    Box(modifier = Modifier.clickable { onStokClick() }) {
                        QuickActionButton(Icons.AutoMirrored.Filled.List, "Stok")
                    }
                    Box(modifier = Modifier.clickable { onLaporanClick() }) {
                        QuickActionButton(Icons.Default.Edit, "Laporan")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            //Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column (modifier = Modifier.padding(top = 50.dp)) {
                    Text(
                        text = "SELAMAT PAGI,", 
                        color = MaterialTheme.colorScheme.onSurfaceVariant, 
                        style = MaterialTheme.typography.labelMedium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Pak ", 
                            color = MaterialTheme.colorScheme.onBackground, 
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            text = "Arli ", 
                            color = MaterialTheme.colorScheme.secondary, 
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(text = "👋", fontSize = 24.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(top = 50.dp)
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(painter = painterResource(R.drawable.profile), contentDescription = "Profile")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //Total Pendapatan
            Card(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "TOTAL PENDAPATAN HARI INI", 
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f), 
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "RP 4.750.000", 
                        color = MaterialTheme.colorScheme.onPrimaryContainer, 
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SummaryText("Transaksi", "12")
                        SummaryText("Servis", "8")
                        SummaryText("Sparepart", "Rp 1.2jt")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            //Statistik
            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard("JENIS STOK", "47", Modifier.weight(1f), onClick = onStokClick)
                Spacer(modifier = Modifier.width(16.dp))
                StatCard("STOK HAMPIR HABIS", "3", Modifier.weight(1f), MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard("PELANGGAN", "156", Modifier.weight(1f), onClick = onPelangganClick)
                Spacer(modifier = Modifier.width(16.dp))
                StatCard("BULAN INI", "RP 68JT", Modifier.weight(1f), MaterialTheme.colorScheme.secondary)
            }

            Spacer(modifier = Modifier.height(24.dp))

            //Low-Stock Alert
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LOW-STOCK ALERT", 
                            color = MaterialTheme.colorScheme.secondary, 
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    AlertRow("Oli Mesin 10W-40", "2 PCS")
                    AlertRow("Filter Udara CB150R", "1 PCS")
                    AlertRow("Kampas Rem Depan", "3 PCS")
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun QuickActionButton(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun SummaryText(label: String, value: String) {
    Row {
        Text(text = "$label: ", color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
        Text(text = value, color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Bottom) {
            Text(text = value, color = valueColor, style = MaterialTheme.typography.headlineSmall)
            Text(text = title, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun AlertRow(name: String, qty: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = name, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
        Text(text = qty, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
    }
}
