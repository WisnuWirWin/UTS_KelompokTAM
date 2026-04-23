package com.le.uts_tam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class JasaServis(val nama: String, val isSelected: Boolean = false)
data class Barang(val nama: String, val harga: Int, var jumlah: Int)

val DarkBackground = Color(0xFF121212)
val DarkCard = Color(0xFF1E1E1E)
val PrimaryOrange = Color(0xFFFF5722)
val TextGrey = Color(0xFFB0B0B0)
val TextWhite = Color(0xFFFFFFFF)
val TextYellow = Color(0xFFFFEB3B)

@Composable
fun Kasir(
    onBack: () -> Unit,
    onPrintNota: () -> Unit
) {
    val jasaServisList = listOf(
        JasaServis("Servis Rutin", isSelected = true),
        JasaServis("Ganti Oli"),
        JasaServis("Tune Up"),
        JasaServis("Rem"),
        JasaServis("Aki")
    )

    val keranjangList = listOf(
        Barang("Oli Mesin 10W-40", 45000, 2),
        Barang("Filter Udara CB150R", 35000, 1)
    )

    val totalJasa = 75000
    val totalBarang = keranjangList.sumOf { it.harga * it.jumlah }
    val totalBayar = totalJasa + totalBarang

    Scaffold(
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            HeaderKasir(onBack = onBack)

            Box(modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Red, Color(0xFFFFEB3B), Color.Transparent)
                    )
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PILIH JASA SERVIS",
                color = TextGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                items(jasaServisList) { jasa ->
                    JasaServisChip(jasa = jasa, onClick = {})
                }
            }

            Text(
                text = "KERANJANG BARANG",
                color = TextGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(keranjangList) { barang ->
                    CartItemCard(barang = barang)
                }
                item { ButtonTambahBarang() }
            }

            FooterTotalAndPay(
                totalBayar = totalBayar, 
                totalJasa = totalJasa,
                onPrintClick = onPrintNota
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun HeaderKasir(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint = TextWhite,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "KASIR",
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        Text(
            text = "TRX-20250422-013",
            color = PrimaryOrange,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun JasaServisChip(
    jasa: JasaServis,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (jasa.isSelected) Color.Transparent else DarkCard,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .then(
                if (jasa.isSelected) Modifier.border(
                    2.dp,
                    PrimaryOrange,
                    RoundedCornerShape(12.dp)
                ) else Modifier
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = jasa.nama,
                color = if (jasa.isSelected) PrimaryOrange else TextGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CartItemCard(barang: Barang) {
    Surface(
        color = DarkCard,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = barang.nama,
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Rp ${"%,d".format(barang.harga)} / pcs",
                    color = TextGrey,
                    fontSize = 12.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF262626), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("-", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = barang.jumlah.toString(),
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(PrimaryOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", color = DarkBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
@Composable
fun ButtonTambahBarang() {
    Surface(
        onClick = { /* Handle Tambah Barang */ },
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, PrimaryOrange, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "+",
                tint = PrimaryOrange,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "TAMBAH BARANG",
                color = PrimaryOrange,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FooterTotalAndPay(totalBayar: Int, totalJasa: Int, onPrintClick: () -> Unit) {
    Column {
        Surface(
            color = DarkCard,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "TOTAL BAYAR", color = TextGrey, fontSize = 12.sp)
                    Text(
                        text = "RP ${"%,d".format(totalBayar)}",
                        color = TextYellow,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Jasa Servis", color = TextGrey, fontSize = 12.sp)
                    Text(
                        text = "Rp ${"%,d".format(totalJasa)}",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Surface(
            onClick = onPrintClick,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFFE53935), PrimaryOrange)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "CETAK NOTA & SELESAI",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

