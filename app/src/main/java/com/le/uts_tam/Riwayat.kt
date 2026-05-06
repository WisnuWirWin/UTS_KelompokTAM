package com.le.uts_tam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HistoryItem(
    val tgl: String,
    val bln: String,
    val trxId: String,
    val nama: String,
    val motor: String,
    val plat: String,
    val layanan: String,
    val harga: String,
    val jam: String,
    val status: String,
    val statusColor: Color
)

@Composable
fun Riwayat(onBack: () -> Unit) {
    val customFontFamily = FontFamily(Font(R.font.poppins))

    val listRiwayat = listOf(
        HistoryItem("22", "APR", "TRX-20250422-013", "Wisnu Wira Winata", "Honda CB150R", "B 4821 XZ", "Servis Rutin + Ganti Oli + Filter", "200RB", "10:25", "LUNAS", Color(0xFF4CAF50)),
        HistoryItem("22", "APR", "TRX-20250422-012", "Kamila Putri Hasan", "Yamaha NMAX", "B 7734 KC", "Tune Up + Busi + Aki", "450RB", "09:10", "LUNAS", Color(0xFF4CAF50)),
        HistoryItem("21", "APR", "TRX-20250421-011", "Athallah", "Suzuki GSX", "D 2210 YA", "Rem Depan + Kampas", "280RB", "15:42", "BON", Color(0xFFFFA000)),
        HistoryItem("21", "APR", "TRX-20250421-010", "Miqdad Dzackiy Arroyan", "Honda Beat", "F 5509 TT", "Ganti Oli + Filter Udara", "120RB", "11:30", "LUNAS", Color(0xFF4CAF50))
    )

    Scaffold(
        containerColor = Color(0xFF121212)

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(25.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "RIWAYAT",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontFamily = customFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(10.dp))
                            .clickable { /* Search action */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_search),
                            contentDescription = null,
                            tint = Color(0xFF03A9F4),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(
                                Color.Red,
                                Color.Yellow,
                                Color.Transparent
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TabItem("HARI INI", true, customFontFamily)
                TabItem("MINGGU INI", false, customFontFamily)
                TabItem("BULAN INI", false, customFontFamily)
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(listRiwayat) { item ->
                    HistoryCard(item, customFontFamily)
                }
            }
        }
    }
}

@Composable
fun TabItem(label: String, isSelected: Boolean, fontFamily: FontFamily) {
    Surface(
        color = if (isSelected) Color(0xFFFF6F00) else Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.border(1.dp, if (isSelected) Color.Transparent else Color.Gray, RoundedCornerShape(20.dp))
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun HistoryCard(item: HistoryItem, fontFamily: FontFamily) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFF262626), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(item.tgl, color = Color(0xFFFF6F00), fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                    Text(item.bln, color = Color.Gray, fontSize = 10.sp, fontFamily = fontFamily)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.trxId, color = Color.Gray, fontSize = 10.sp, fontFamily = fontFamily)
                Text(item.nama, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                Row {
                    Text(item.motor, color = Color.Gray, fontSize = 12.sp, fontFamily = fontFamily)
                    Text(" • ", color = Color.Gray)
                    Text(item.plat, color = Color.Yellow, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.layanan, color = Color.Gray.copy(alpha = 0.6f), fontSize = 11.sp, fontFamily = fontFamily)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(item.harga, color = Color.Yellow, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, fontFamily = fontFamily)
                Text(item.jam, color = Color.Gray, fontSize = 10.sp, fontFamily = fontFamily)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = item.statusColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.status,
                        color = item.statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = fontFamily,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}