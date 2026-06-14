package com.le.uts_tam.ui.screen.editstok

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.le.uts_tam.R

@Composable
fun EditStock(
    onBack: () -> Unit = {},
    viewModel: EditStockViewModel = viewModel()
) {
    val customFontFamily = FontFamily(Font(R.font.poppins))
    val scrollState = rememberScrollState()

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
            val isEditMode = viewModel.firebaseKey != null
            Text(
                if (isEditMode) "EDIT BARANG" else "TAMBAH BARANG",
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
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = Color.Gray.copy(alpha = 0.5f),
                    style = stroke,
                    cornerRadius = CornerRadius(16.dp.toPx())
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

        EditField(
            label = "NAMA BARANG",
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            fontFamily = customFontFamily,
            isHighlighted = true
        )
        
        EditField(
            label = "ID / SKU",
            value = viewModel.idItems,
            onValueChange = { viewModel.idItems = it },
            fontFamily = customFontFamily
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                EditField(
                    label = "HARGA BELI",
                    value = viewModel.hargaBeli,
                    onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.hargaBeli = it },
                    fontFamily = customFontFamily,
                    keyboardType = KeyboardType.Number
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                EditField(
                    label = "HARGA JUAL",
                    value = viewModel.price,
                    onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.price = it },
                    fontFamily = customFontFamily,
                    keyboardType = KeyboardType.Number
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                EditField(
                    label = "STOK",
                    value = viewModel.stock,
                    onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.stock = it },
                    fontFamily = customFontFamily,
                    keyboardType = KeyboardType.Number
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                EditField(
                    label = "MIN. STOK",
                    value = viewModel.minStok,
                    onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.minStok = it },
                    fontFamily = customFontFamily,
                    keyboardType = KeyboardType.Number
                )
            }
        }

        EditField(
            label = "KATEGORI",
            value = viewModel.kategori,
            onValueChange = { viewModel.kategori = it },
            fontFamily = customFontFamily,
            isDropdown = true
        )

        EditField(
            label = "SUPPLIER / MERK",
            value = viewModel.supplier,
            onValueChange = { viewModel.supplier = it },
            fontFamily = customFontFamily
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                viewModel.saveChanges {
                    onBack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00))
        ) {
            val isEditMode = viewModel.firebaseKey != null
            Text(
                if (isEditMode) "SIMPAN PERUBAHAN" else "TAMBAH KE STOK", 
                fontFamily = customFontFamily, 
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    fontFamily: FontFamily,
    isHighlighted: Boolean = false,
    isDropdown: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("Sparepart", "Jasa/Servis", "Oli", "Ban", "Lain-lain")

    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        if (isDropdown) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(14.dp))
                        .then(
                            if (isHighlighted) Modifier.border(1.dp, Color(0xFFFF6D00), RoundedCornerShape(14.dp))
                            else Modifier
                        )
                        .padding(horizontal = 16.dp)
                        .menuAnchor(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = value.ifEmpty { "Pilih Kategori" },
                            color = if (value.isEmpty()) Color.Gray else Color.White,
                            fontSize = 14.sp,
                            fontFamily = fontFamily,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.rotate(if (expanded) 180f else 0f)
                        )
                    }
                }
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color(0xFF1E1E1E))
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    text = category,
                                    color = Color.White,
                                    fontFamily = fontFamily
                                ) 
                            },
                            onClick = {
                                onValueChange(category)
                                expanded = false
                            },
                            modifier = Modifier.background(Color(0xFF1E1E1E))
                        )
                    }
                }
            }
        } else {
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
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = fontFamily
                    ),
                    cursorBrush = SolidColor(Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
                )
            }
        }
    }
}
