package com.le.uts_tam

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Inventaris(onBack: () -> Unit = {}, onAddItem: () -> Unit = {}) {
    val customFontFamily = FontFamily(Font(R.font.poppins))
    val categories = listOf("SEMUA", "OLI & CAIRAN", "FILTER", "REM")
    var selectedCategory by remember { mutableStateOf("SEMUA") }

    val inventoryItems = listOf(
        InventoryItem("Oli Mesin 10W-40", "OL-001", "Yamalube", "Rp 45.000", 2, "LOW", Icons.Default.Build),
        InventoryItem("Filter Udara CB150R", "FA-022", "Honda", "Rp 35.000", 1, "LOW", Icons.Default.Info),
        InventoryItem("Kampas Rem Depan", "BR-031", "FBW", "Rp 55.000", 12, "OK", Icons.Default.Settings),
        InventoryItem("Aki Kering 5Ah", "AK-008", "GS Astra", "Rp 175.000", 8, "OK", Icons.Default.ShoppingCart),
        InventoryItem("Busi NGK CPR8EA", "BU-015", "NGK", "Rp 28.000", 25, "OK", Icons.Default.Build)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "INVENTARIS",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontFamily = customFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFFF8A00), Color(0xFFFF4D00))
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onAddItem() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF9575CD),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Cari suku cadang...",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontFamily = customFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) Color(0xFFFF6D00) else Color.Transparent,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .then(
                                if (!isSelected) Modifier.background(
                                    Color(0xFF1E1E1E),
                                    RoundedCornerShape(20.dp)
                                ) else Modifier
                            )
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.White else Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = customFontFamily
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 30.dp)
            ) {
                items(inventoryItems) { item ->
                    InventoryCard(item, customFontFamily)
                }
            }
        }
    }
}

@Composable
fun InventoryCard(item: InventoryItem, fontFamily: FontFamily) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF262626), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily
                )
                Text(
                    text = "SKU: ${item.sku} - ${item.brand}",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontFamily = fontFamily
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = item.price,
                    color = Color(0xFFFFA000),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily
                )
                Text(
                    text = item.stock.toString(),
                    color = Color(0xFFFFA000),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily
                )

                Spacer(modifier = Modifier.height(4.dp))

                val badgeColor = if (item.status == "LOW") Color(0xFFEF5350) else Color(0xFF66BB6A)
                Box(
                    modifier = Modifier
                        .background(badgeColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.status,
                        color = badgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily
                    )
                }
            }
        }
    }
}

data class InventoryItem(
    val name: String,
    val sku: String,
    val brand: String,
    val price: String,
    val stock: Int,
    val status: String,
    val icon: ImageVector
)
