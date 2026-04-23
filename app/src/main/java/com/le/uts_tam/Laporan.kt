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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Laporan(
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("HARIAN") }

    Scaffold(
        containerColor = DarkBackground
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
                            .background(DarkCard, RoundedCornerShape(12.dp))
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "LAPORAN",
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                
                IconButton(
                    onClick = { /* Handle Export */ },
                    modifier = Modifier
                        .background(DarkCard, RoundedCornerShape(12.dp))
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Export",
                        tint = TextWhite,
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
                    .background(DarkCard)
                    .padding(4.dp)
            ) {
                val tabs = listOf("HARIAN", "MINGGUAN", "BULANAN")
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PrimaryOrange else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) TextWhite else TextGrey,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chart Section
            Surface(
                color = DarkCard,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PENDAPATAN 7 HARI TERAKHIR (RP)",
                        color = TextGrey,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
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
                                            if (index == 6) Brush.verticalGradient(listOf(Color(0xFFFFA000), Color(0xFFFFEB3B)))
                                            else Brush.verticalGradient(listOf(Color(0xFFB71C1C), PrimaryOrange))
                                        )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = (16 + index).toString(),
                                    color = if (index == 6) TextYellow else TextGrey,
                                    fontSize = 10.sp,
                                    fontWeight = if (index == 6) FontWeight.Bold else FontWeight.Normal
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
                    .background(Color(0xFF262626), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .padding(12.dp)
            ) {
                Text(text = "TANGGAL", modifier = Modifier.weight(1f), color = TextGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = "PENDAPATAN", modifier = Modifier.weight(1.5f), color = TextGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = "LABA BERSIH", modifier = Modifier.weight(1.5f), color = TextGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
                    .background(DarkCard)
            ) {
                items(reportItems) { item ->
                    ReportRow(item)
                    HorizontalDivider(color = Color(0xFF333333), thickness = 0.5.dp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Total
            Surface(
                color = Color(0xFF1E1E00), // Very dark yellow hint
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333300)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL BULAN INI",
                        color = TextGrey,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "RP 68.500.000",
                        color = TextYellow,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
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
            .background(if (item.isToday) Color(0xFF262210) else Color.Transparent)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.date,
            modifier = Modifier.weight(1f),
            color = if (item.isToday) PrimaryOrange else TextWhite,
            fontSize = 13.sp,
            fontWeight = if (item.isToday) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = item.income,
            modifier = Modifier.weight(1.5f),
            color = TextWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = item.profit,
            modifier = Modifier.weight(1.5f),
            color = Color(0xFF4CAF50),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

data class ReportItem(val date: String, val income: String, val profit: String, val isToday: Boolean = false)
