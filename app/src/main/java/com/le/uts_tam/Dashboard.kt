package com.le.uts_tam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Dashboard() {
    val scrollState = rememberScrollState()
    val customFontFamily = FontFamily(
        Font(R.font.poppins),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        //Header
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column (modifier = Modifier
                .padding(top = 50.dp)) {
                Text(
                    text = "SELAMAT PAGI,",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontFamily = customFontFamily,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Pak ",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontFamily = customFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Budi ",
                        color = Color(0xFFFFA000),
                        fontSize = 24.sp,
                        fontFamily = customFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "👋", fontSize = 24.sp)
                }
            }
            
            //Profil
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE53935)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "B",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = customFontFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        //Kartu Total Pendapatan
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFB71C1C))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "TOTAL PENDAPATAN HARI INI",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontFamily = customFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "RP 4.750.000",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontFamily = customFontFamily,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryText("Transaksi", "12")
                    SummaryText("Servis", "8")
                    SummaryText("Sparepart", "Rp 1.2jt")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        //Grid Statistik
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard(
                title = "JENIS STOK",
                value = "47",
                fontFamily = customFontFamily,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatCard(
                title = "STOK HAMPIR HABIS",
                value = "3",
                valueColor = Color.Red,
                fontFamily = customFontFamily,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard(
                title = "PELANGGAN",
                value = "156",
                fontFamily = customFontFamily,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatCard(
                title = "BULAN INI",
                value = "RP 68JT",
                valueColor = Color(0xFFFFA000),
                fontFamily = customFontFamily,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        //Low-Stock Alert
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFFFA000), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFFA000),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LOW-STOCK ALERT",
                        color = Color(0xFFFFA000),
                        fontSize = 12.sp,
                        fontFamily = customFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                AlertRow("Oli Mesin 10W-40", "2 PCS")
                AlertRow("Filter Udara CB150R", "1 PCS")
                AlertRow("Kampas Rem Depan", "3 PCS")
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun SummaryText(label: String, value: String) {
    Row {
        Text(text = "$label: ", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
        Text(text = value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatCard(
    title: String, 
    value: String, 
    fontFamily: FontFamily,
    modifier: Modifier = Modifier, 
    valueColor: Color = Color.White
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = value,
                color = valueColor,
                fontSize = 24.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = Color.Gray,
                fontSize = 10.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AlertRow(name: String, qty: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = name, color = Color.White, fontSize = 13.sp)
        Text(text = qty, color = Color.Red, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
