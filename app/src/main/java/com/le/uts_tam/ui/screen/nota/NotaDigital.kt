package com.le.uts_tam.ui.screen.nota

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.le.uts_tam.ui.screen.riwayat.HistoryItem

@Composable
fun NotaDigital(
    onBack: () -> Unit,
    transaction: HistoryItem? = null
) {
    var showPrintDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
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
                if (transaction != null) {
                    ReceiptCard(transaction)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Data Transaksi Tidak Ditemukan", color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (transaction != null) {
                    QRCodeSection(transaction.trxId)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            ActionButtons(onPrintClick = { showPrintDialog = true })
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showPrintDialog && transaction != null) {
        PrintReceiptDialog(
            transaction = transaction,
            onDismiss = { showPrintDialog = false }
        )
    }
}

@Composable
fun PrintReceiptDialog(transaction: HistoryItem, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .width(350.dp)
                    .padding(16.dp)
                    .clickable(enabled = false) {}, // Prevent dismiss on card click
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Receipt Header
                    Text(
                        text = "TAM MOTOR",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Jl. Raya Natar No.12, Lampung Selatan",
                        color = Color.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Telp: 0857-6494-8010",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    DashedDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    // Trx Info
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = transaction.trxId, fontSize = 11.sp, color = Color.Black, fontFamily = FontFamily.Monospace)
                        Text(text = "${transaction.tgl}/${transaction.bln} ${transaction.jam}", fontSize = 11.sp, color = Color.Black, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Kasir: Admin", fontSize = 11.sp, color = Color.Black, fontFamily = FontFamily.Monospace)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    DashedDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    // Customer Info
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Pelanggan: ${transaction.customer.name ?: "Umum"}", fontSize = 12.sp, color = Color.Black, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Plat     : ${transaction.vehicle.numberPlate ?: "-"}", fontSize = 12.sp, color = Color.Black, fontFamily = FontFamily.Monospace)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Items Table
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Item", weight = Modifier.weight(1f))
                        Text("Qty", weight = Modifier.width(40.dp), textAlign = TextAlign.Center)
                        Text("Total", weight = Modifier.width(80.dp), textAlign = TextAlign.End)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    DashedDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    // Transaction list (Using layanan summary)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = transaction.layanan, 
                            color = Color.Black, 
                            fontSize = 12.sp, 
                            modifier = Modifier.weight(1f),
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "1", 
                            color = Color.Black, 
                            fontSize = 12.sp, 
                            modifier = Modifier.width(40.dp), 
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = transaction.totalHarga, 
                            color = Color.Black, 
                            fontSize = 12.sp, 
                            modifier = Modifier.width(80.dp), 
                            textAlign = TextAlign.End,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    DashedDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    // Totals
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "TOTAL", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "Rp ${transaction.totalHarga}", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // QR Code Fake Implementation for UI
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.White)
                            .border(1.dp, Color.Black)
                            .padding(8.dp)
                    ) {
                        // QR Code Grid pattern mock
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val size = 10
                            val cellSize = size.toFloat()
                            for (i in 0..8) {
                                for (j in 0..8) {
                                    if ((i + j) % 2 == 0) {
                                        drawRect(
                                            color = Color.Black,
                                            topLeft = Offset(i * cellSize * 1.2f, j * cellSize * 1.2f),
                                            size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = transaction.trxId,
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "*** TERIMA KASIH ***",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Barang yang sudah dibeli tidak dapat ditukar/dikembalikan",
                        color = Color.Black,
                        fontSize = 8.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun Text(text: String, weight: Modifier, textAlign: TextAlign = TextAlign.Start) {
    Text(
        text = text,
        modifier = weight,
        textAlign = textAlign,
        color = Color.Black,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
    )
}

@Composable
fun DashedDivider() {
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = Color.Black,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
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
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "NOTA DIGITAL",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Surface(
            color = Color(0xFF1B5E20),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "LUNAS",
                color = Color(0xFF4CAF50),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun ReceiptCard(transaction: HistoryItem) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TAM MOTOR",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Jl. Raya Natar No.12, Lampung Selatan",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        Text(" 0857-6494-8010", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(transaction.trxId, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelMedium)
                    Text("${transaction.tgl} ${transaction.bln}, ${transaction.jam}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            // Customer Info
            Column {
                Text("PELANGGAN:", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                Text(transaction.customer.name ?: "Umum", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(transaction.vehicle.numberPlate ?: "-", color = MaterialTheme.colorScheme.primary)
                Text(transaction.vehicle.brand ?: "", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))

            // Items List
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(transaction.layanan, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
                Text("Rp ${transaction.totalHarga}", color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("TOTAL BAYAR", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = "RP ${transaction.totalHarga}",
                    color = Color(0xFFFFEB3B),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun QRCodeSection(trxId: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
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
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Share, null, tint = Color.Black)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("QR Code Nota", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("Scan untuk verifikasi - $trxId", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ActionButtons(onPrintClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = { },
            modifier = Modifier.weight(1f).height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Share, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("WHATSAPP")
        }

        Button(
            onClick = onPrintClick,
            modifier = Modifier.weight(1f).height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CETAK NOTA", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
