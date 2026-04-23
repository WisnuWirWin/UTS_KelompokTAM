package com.le.uts_tam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas

@Composable
fun EditStock(onBack: () -> Unit = {}) {
    val customFontFamily = FontFamily(Font(R.font.poppins))
    val scrollState = rememberScrollState()

    var namaBarang by remember { mutableStateOf("Kampas Rem Belakang") }
    var kodeSku by remember { mutableStateOf("BR-044") }
    var hargaBeli by remember { mutableStateOf("Rp 38.000") }
    var hargaJual by remember { mutableStateOf("Rp 55.000") }
    var stok by remember { mutableStateOf("15") }
    var minStok by remember { mutableStateOf("5") }
    var kategori by remember { mutableStateOf("Rem & Kopling") }
    var supplier by remember { mutableStateOf("FBW Indonesia") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(10.dp))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "TAMBAH BARANG",
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = customFontFamily,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Red, Color.Yellow, Color.Transparent)
                    )
                )
        )

        Spacer(modifier = Modifier.height(24.dp))

        val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clickable { /* Upload action */ },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = Color.Gray.copy(alpha = 0.5f),
                    style = stroke,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_camera),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "TAP UNTUK UPLOAD FOTO",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontFamily = customFontFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        EditField("NAMA BARANG", namaBarang, { namaBarang = it }, customFontFamily, isHighlighted = true)
        EditField("KODE SKU", kodeSku, { kodeSku = it }, customFontFamily)
        
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                EditField("HARGA BELI", hargaBeli, { hargaBeli = it }, customFontFamily)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                EditField("HARGA JUAL", hargaJual, { hargaJual = it }, customFontFamily)
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                EditField("STOK", stok, { stok = it }, customFontFamily)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                EditField("MIN. STOK", minStok, { minStok = it }, customFontFamily)
            }
        }

        EditField("KATEGORI", kategori, { kategori = it }, customFontFamily, isDropdown = true)
        EditField("SUPPLIER / MERK", supplier, { supplier = it }, customFontFamily)

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00))
        ) {
            Text("SIMPAN PERUBAHAN", fontFamily = customFontFamily, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    fontFamily: FontFamily,
    isHighlighted: Boolean = false,
    isDropdown: Boolean = false
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF1E1E1E), RoundedCornerShape(14.dp))
                .then(
                    if (isHighlighted) Modifier.border(1.dp, Color(0xFFFF6D00), RoundedCornerShape(14.dp))
                    else Modifier
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = fontFamily
                )
                if (isDropdown) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                }
            }
        }
    }
}
