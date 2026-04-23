package com.le.uts_tam

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotaDigital(
    onBack: () -> Unit
) {
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
            
            HeaderNota(onBack = onBack)

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                ReceiptCard()
                Spacer(modifier = Modifier.height(16.dp))
                QRCodeSection()
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            ActionButtons()
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun HeaderNota(onBack: () -> Unit) {
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
                text = "NOTA DIGITAL",
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Surface(
            color = Color(0xFF1B5E20),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "LUNAS",
                color = Color(0xFF4CAF50),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun ReceiptCard() {
    Surface(
        color = DarkCard,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Workshop Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "BENGKEL MAJU JAYA",
                        color = PrimaryOrange,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Jl. Raya Natar No.12, Lampung Selatan",
                        color = TextGrey,
                        fontSize = 12.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Color(0xFFE91E63),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "0821-XXXX-XXXX",
                            color = TextGrey,
                            fontSize = 12.sp
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "TRX-20250422-013",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "22 Apr 2025, 10:25",
                        color = TextGrey,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFF333333), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Customer Info
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoColumn("PELANGGAN", "Budi Santoso", Modifier.weight(1f))
                InfoColumn("NO. PLAT", "B 4821 XZ", Modifier.weight(1f))
                InfoColumn("KENDARAAN", "CB150R", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFF333333), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Items
            val items = listOf(
                ReceiptItem("Jasa Servis Rutin", 1, 50000),
                ReceiptItem("Jasa Ganti Oli", 1, 25000),
                ReceiptItem("Oli Mesin 10W-40", 2, 90000),
                ReceiptItem("Filter Udara CB150R", 1, 35000)
            )

            items.forEach { item ->
                ReceiptItemRow(item)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFF333333), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Totals
            TotalRow("Subtotal", "Rp 200.000", TextGrey)
            Spacer(modifier = Modifier.height(8.dp))
            TotalRow("Diskon", "- Rp 0", Color.Red)
            
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TOTAL BAYAR",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "RP 200.000",
                    color = TextYellow,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun InfoColumn(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, color = TextGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = TextYellow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ReceiptItemRow(item: ReceiptItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.name,
            color = TextWhite,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${item.quantity}x",
            color = TextGrey,
            fontSize = 14.sp,
            modifier = Modifier.width(40.dp)
        )
        Text(
            text = "Rp ${"%,d".format(item.price)}",
            color = PrimaryOrange,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TotalRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextGrey, fontSize = 14.sp)
        Text(text = value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun QRCodeSection() {
    Surface(
        color = DarkCard,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(60.dp)
            ) {
                // Placeholder for QR Code
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "QR Code Nota",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Scan untuk verifikasi - TRX-013",
                    color = TextGrey,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ActionButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // WhatsApp Button
        OutlinedButton(
            onClick = { /* Handle WhatsApp */ },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, PrimaryOrange)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = PrimaryOrange
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "WHATSAPP",
                color = PrimaryOrange,
                fontWeight = FontWeight.Bold
            )
        }

        // Print Button
        Button(
            onClick = { /* Handle Print */ },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFFE53935), PrimaryOrange)),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info, // Replaced Print with Info for now
                        contentDescription = null,
                        tint = TextWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CETAK NOTA",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

data class ReceiptItem(val name: String, val quantity: Int, val price: Int)
