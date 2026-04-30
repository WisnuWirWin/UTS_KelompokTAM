package com.le.uts_tam

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Laporan(
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("HARIAN") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "LAPORAN",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineMedium,
                        letterSpacing = 1.sp
                    )
                }
                
                IconButton(
                    onClick = { /* Handle Export */ },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Export",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Custom Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(4.dp)
            ) {
                val tabs = listOf("HARIAN", "MINGGUAN", "BULANAN")
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chart Section
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PENDAPATAN 7 HARI TERAKHIR (RP)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Simple Custom Bar Chart
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val barData = listOf(0.4f, 0.7f, 0.5f, 0.8f, 0.3f, 0.75f, 1.0f)
                        barData.forEachIndexed { index, heightMultiplier ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .fillMaxHeight(heightMultiplier)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(
                                            if (index == 6) Brush.verticalGradient(listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.tertiary))
                                            else Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary))
                                        )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = (16 + index).toString(),
                                    color = if (index == 6) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .padding(12.dp)
            ) {
                Text(text = "TANGGAL", modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                Text(text = "PENDAPATAN", modifier = Modifier.weight(1.5f), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                Text(text = "LABA BERSIH", modifier = Modifier.weight(1.5f), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
            }

            // Table Content
            val reportItems = listOf(
                ReportItem("22 Apr", "Rp 4.750.000", "Rp 1.820.000", true),
                ReportItem("21 Apr", "Rp 3.200.000", "Rp 1.150.000"),
                ReportItem("20 Apr", "Rp 1.800.000", "Rp 620.000"),
                ReportItem("19 Apr", "Rp 5.100.000", "Rp 2.040.000"),
                ReportItem("18 Apr", "Rp 2.400.000", "Rp 890.000")
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                items(reportItems) { item ->
                    ReportRow(item)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), thickness = 0.5.dp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Total
            Surface(
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL BULAN INI",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "RP 68.500.000",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ReportRow(item: ReportItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (item.isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.date,
            modifier = Modifier.weight(1f),
            color = if (item.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = item.income,
            modifier = Modifier.weight(1.5f),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = item.profit,
            modifier = Modifier.weight(1.5f),
            color = Color(0xFF4CAF50), // Keeping green for profit success
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

data class ReportItem(val date: String, val income: String, val profit: String, val isToday: Boolean = false)
