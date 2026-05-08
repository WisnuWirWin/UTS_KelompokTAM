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
import androidx.compose.ui.unit.dp

@Composable
fun Pelanggan(onBack: () -> Unit = {}, onAddPelanggan: () -> Unit = {}) {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Back", 
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PELANGGAN",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onAddPelanggan,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                    .size(40.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.onPrimary)
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
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            placeholder = { 
                Text(
                    text = "Cari nama atau no. plat...", 
                    color = MaterialTheme.colorScheme.onSurfaceVariant, 
                    style = MaterialTheme.typography.bodyMedium
                ) 
            },
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Default.Search, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                ) 
            }
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
            )
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
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomerCard(
            plate = "BE 2210 YA",
            name = "Athallah",
            phone = "0823-9988-1122",
            motor = "Suzuki GSX 150R 2021",
            history = listOf(
                ServiceHistory("Rem Depan + Kampas", "21 Apr 2025", "Rp 280rb")
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomerCard(
            plate = "F 5509 TT",
            name = "Dzackiy",
            phone = "0821-8943-2121",
            motor = "Honda Beat 2024",
            history = listOf(
                ServiceHistory("Ganti shock + Filter + Aki", "12 Jan 2025", "Rp 400rb"),
                ServiceHistory("Ganti Rem Depan", "12 Jan 2025", "Rp 100rb")
            )
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
    history: List<ServiceHistory>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Plate Number Box
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = plate,
                        color = Color.Black, // Keeping black for high contrast on yellow
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = name, 
                        color = MaterialTheme.colorScheme.onSurface, 
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Call, 
                            contentDescription = null, 
                            tint = MaterialTheme.colorScheme.primary, 
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = phone, 
                            color = MaterialTheme.colorScheme.onSurfaceVariant, 
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = motor, 
                        color = MaterialTheme.colorScheme.secondary, 
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "RIWAYAT SERVIS",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall
            )

            history.forEach { item ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(
                            text = item.name, 
                            color = MaterialTheme.colorScheme.onSurface, 
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = item.date, 
                            color = MaterialTheme.colorScheme.onSurfaceVariant, 
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Text(
                        text = item.price, 
                        color = MaterialTheme.colorScheme.secondary, 
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
