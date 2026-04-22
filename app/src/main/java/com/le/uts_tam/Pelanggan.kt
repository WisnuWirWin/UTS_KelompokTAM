package com.le.uts_tam

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Pelanggan(onBack: () -> Unit = {}) {
    val scrollState = rememberScrollState()
    val customFontFamily = FontFamily(
        Font(R.font.poppins),
    )
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PELANGGAN",
                color = Color.White,
                fontSize = 28.sp,
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .background(Color(0xFFE53935), RoundedCornerShape(12.dp))
                    .size(40.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E1E1E),
                unfocusedContainerColor = Color(0xFF1E1E1E),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            placeholder = { Text("Cari nama atau no. plat...", color = Color.Gray, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Customer Cards
        CustomerCard(
            plate = "BE 4821 XZ",
            name = "Wisnu",
            phone = "0812-3456-7890",
            motor = "Honda CB150R 2022",
            history = listOf(
                ServiceHistory("Ganti Oli + Servis Rutin", "22 Apr 2025", "Rp 200rb"),
                ServiceHistory("Tune Up + Filter Udara", "12 Mar 2025", "Rp 175rb")
            ),
            customFontFamily = customFontFamily
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomerCard(
            plate = "BE 7734 KC",
            name = "Kamila",
            phone = "0857-6543-2100",
            motor = "Yamaha NMAX 155 2023",
            history = listOf(
                ServiceHistory("Tune Up + Busi + Aki", "22 Apr 2025", "Rp 450rb"),
                ServiceHistory("Ganti Rem Belakang", "01 Feb 2025", "Rp 130rb")
            ),
            customFontFamily = customFontFamily
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomerCard(
            plate = "BE 2210 YA",
            name = "Athallah",
            phone = "0823-9988-1122",
            motor = "Suzuki GSX 150R 2021",
            history = listOf(
                ServiceHistory("Rem Depan + Kampas", "21 Apr 2025", "Rp 280rb")
            ),
            customFontFamily = customFontFamily
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

data class ServiceHistory(val name: String, val date: String, val price: String)

@Composable
fun CustomerCard(
    plate: String,
    name: String,
    phone: String,
    motor: String,
    history: List<ServiceHistory>,
    customFontFamily: FontFamily
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Plate Number Box
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFFD600), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = plate,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = customFontFamily
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(text = name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = customFontFamily)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = phone, color = Color.Gray, fontSize = 12.sp, fontFamily = customFontFamily)
                    }
                    Text(text = motor, color = Color(0xFFFFA000), fontSize = 12.sp, fontFamily = customFontFamily)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "RIWAYAT SERVIS",
                color = Color.Gray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = customFontFamily
            )

            history.forEach { item ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = item.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium, fontFamily = customFontFamily)
                        Text(text = item.date, color = Color.Gray, fontSize = 11.sp, fontFamily = customFontFamily)
                    }
                    Text(text = item.price, color = Color(0xFFFFA000), fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = customFontFamily)
                }
            }
        }
    }
}
