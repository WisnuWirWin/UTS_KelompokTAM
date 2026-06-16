package com.le.uts_tam.ui.screen.nota

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.le.uts_tam.ui.screen.profil.ProfilViewModel
import com.le.uts_tam.ui.screen.riwayat.HistoryItem
import com.le.uts_tam.utils.BluetoothPrinterManager
import kotlinx.coroutines.launch
import kotlin.random.Random

@SuppressLint("UnrememberedMutableState")
@Composable
fun NotaDigital(
    onBack: () -> Unit,
    transaction: HistoryItem? = null,
    profilViewModel: ProfilViewModel = viewModel(),
    printerManager: BluetoothPrinterManager? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val shopInfo by profilViewModel.uiState.collectAsState()
    var showPrintDialog by remember { mutableStateOf(false) }
    val isPrinterConnected by printerManager?.isConnected?.collectAsState() ?: mutableStateOf(false)

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
                    ReceiptCard(
                        transaction = transaction,
                        shopName = shopInfo.ownerName.ifEmpty { "TAM MOTOR" },
                        shopAddress = shopInfo.address.ifEmpty { "Jl. Raya Natar No.12, Lampung Selatan" },
                        shopPhone = shopInfo.phone.ifEmpty { "0857-6494-8010" }
                    )
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
            ActionButtons(
                onPrintClick = { showPrintDialog = true },
                onBluetoothPrintClick = {
                    if (isPrinterConnected && transaction != null && printerManager != null) {
                        scope.launch {
                            val receiptText = buildReceiptString(
                                transaction,
                                shopInfo.ownerName.ifEmpty { "TAM MOTOR" },
                                shopInfo.address.ifEmpty { "Jl. Raya Natar" },
                                shopInfo.phone.ifEmpty { "-" }
                            )
                            val success = printerManager.printReceipt(receiptText)
                            if (success) Toast.makeText(context, "Mencetak nota...", Toast.LENGTH_SHORT).show()
                            else Toast.makeText(context, "Gagal mencetak", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Printer belum terhubung. Atur di Profil.", Toast.LENGTH_LONG).show()
                    }
                },
                onWhatsAppShare = {
                    if (transaction != null) {
                        val message = buildWhatsAppMessage(
                            transaction,
                            shopInfo.ownerName.ifEmpty { "TAM MOTOR" }
                        )
                        val phoneNumber = transaction.customer.noHp?.replace(Regex("[^0-9]"), "") ?: ""
                        
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            val url = "https://api.whatsapp.com/send?phone=62$phoneNumber&text=${Uri.encode(message)}"
                            data = Uri.parse(url)
                        }
                        context.startActivity(intent)
                    }
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showPrintDialog && transaction != null) {
        PrintReceiptDialog(
            transaction = transaction,
            shopName = shopInfo.ownerName.ifEmpty { "TAM MOTOR" },
            shopAddress = shopInfo.address.ifEmpty { "Jl. Raya Natar No.12, Lampung Selatan" },
            shopPhone = shopInfo.phone.ifEmpty { "0857-6494-8010" },
            onDismiss = { showPrintDialog = false }
        )
    }
}

private fun buildReceiptString(
    transaction: HistoryItem,
    shopName: String,
    shopAddress: String,
    shopPhone: String
): String {
    val builder = StringBuilder()
    builder.append("${shopName.uppercase()}\n")
    builder.append("$shopAddress\n")
    builder.append("Telp: $shopPhone\n")
    builder.append("--------------------------------\n")
    builder.append("${transaction.trxId}\n")
    builder.append("${transaction.tgl}/${transaction.bln} ${transaction.jam}\n")
    builder.append("--------------------------------\n")
    builder.append("Pelanggan: ${transaction.customer.name ?: "Umum"}\n")
    builder.append("Plat     : ${transaction.vehicle.numberPlate ?: "-"}\n")
    builder.append("--------------------------------\n")
    builder.append("${transaction.layanan.take(20).padEnd(20)} ${transaction.totalHarga.padStart(10)}\n")
    builder.append("--------------------------------\n")
    builder.append("TOTAL: Rp ${transaction.totalHarga}\n")
    builder.append("\n      TERIMA KASIH      \n\n\n")
    return builder.toString()
}

private fun buildWhatsAppMessage(transaction: HistoryItem, shopName: String): String {
    return """
        *NOTA DIGITAL - $shopName*
        
        ID Transaksi: ${transaction.trxId}
        Tanggal: ${transaction.tgl} ${transaction.bln} ${transaction.jam}
        
        Pelanggan: ${transaction.customer.name ?: "Umum"}
        Kendaraan: ${transaction.vehicle.brand ?: "-"} (${transaction.vehicle.numberPlate ?: "-"})
        
        *Rincian:*
        ${transaction.layanan}
        
        *TOTAL: Rp ${transaction.totalHarga}*
        Status: LUNAS
        
        Terima kasih telah mempercayai layanan kami!
    """.trimIndent()
}

@Composable
fun ActionButtons(
    onPrintClick: () -> Unit = {},
    onBluetoothPrintClick: () -> Unit = {},
    onWhatsAppShare: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBluetoothPrintClick,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Print, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("PRINTER")
            }

            OutlinedButton(
                onClick = onWhatsAppShare,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, Color(0xFF25D366))
            ) {
                Icon(Icons.Default.Share, null, tint = Color(0xFF25D366))
                Spacer(modifier = Modifier.width(8.dp))
                Text("WHATSAPP", color = Color(0xFF25D366))
            }
        }

        Button(
            onClick = onPrintClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
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
                    Text("LIHAT NOTA", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
fun PrintReceiptDialog(
    transaction: HistoryItem,
    shopName: String,
    shopAddress: String,
    shopPhone: String,
    onDismiss: () -> Unit
) {
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
                    .clickable(enabled = false) {}, 
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = shopName.uppercase(),
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = shopAddress,
                        color = Color.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Telp: $shopPhone",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    DashedDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ReceiptText(text = transaction.trxId, modifier = Modifier)
                        ReceiptText(text = "${transaction.tgl}/${transaction.bln} ${transaction.jam}", modifier = Modifier)
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ReceiptText(text = "Kasir: Admin", modifier = Modifier)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    DashedDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        ReceiptText(text = "Pelanggan: ${transaction.customer.name ?: "Umum"}", modifier = Modifier)
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ReceiptText(text = "Plat     : ${transaction.vehicle.numberPlate ?: "-"}", modifier = Modifier)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ReceiptText("Item", modifier = Modifier.weight(1f))
                        ReceiptText("Qty", modifier = Modifier.width(40.dp), textAlign = TextAlign.Center)
                        ReceiptText("Total", modifier = Modifier.width(80.dp), textAlign = TextAlign.End)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    DashedDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ReceiptText(
                            text = transaction.layanan, 
                            modifier = Modifier.weight(1f)
                        )
                        ReceiptText(
                            text = "1", 
                            modifier = Modifier.width(40.dp), 
                            textAlign = TextAlign.Center
                        )
                        ReceiptText(
                            text = transaction.totalHarga, 
                            modifier = Modifier.width(80.dp), 
                            textAlign = TextAlign.End
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    DashedDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ReceiptText(text = "TOTAL", modifier = Modifier)
                        ReceiptText(text = "Rp ${transaction.totalHarga}", modifier = Modifier)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    QRVisualizer(seed = transaction.trxId, sizeDp = 100)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    ReceiptText(
                        text = transaction.trxId,
                        modifier = Modifier
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
fun QRVisualizer(seed: String, sizeDp: Int, tintColor: Color = Color.Black) {
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .background(Color.White)
            .border(1.dp, tintColor.copy(alpha = 0.2f))
            .padding(4.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val dots = 15
            val cellSize = size.width / dots
            val random = Random(seed.hashCode())

            drawCornerPattern(0, 0, cellSize)
            drawCornerPattern(dots - 5, 0, cellSize)
            drawCornerPattern(0, dots - 5, cellSize)

            for (i in 0 until dots) {
                for (j in 0 until dots) {
                    val isInTopLeft = i < 5 && j < 5
                    val isInTopRight = i > dots - 6 && j < 5
                    val isInBottomLeft = i < 5 && j > dots - 6
                    
                    if (!isInTopLeft && !isInTopRight && !isInBottomLeft) {
                        if (random.nextBoolean()) {
                            drawRect(
                                color = tintColor,
                                topLeft = Offset(i * cellSize, j * cellSize),
                                size = Size(cellSize * 0.9f, cellSize * 0.9f)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun DrawScope.drawCornerPattern(x: Int, y: Int, cellSize: Float) {
    for (i in 0 until 5) {
        for (j in 0 until 5) {
            val isBorder = i == 0 || i == 4 || j == 0 || j == 4
            val isCenter = i == 2 && j == 2
            if (isBorder || isCenter) {
                drawRect(
                    color = Color.Black,
                    topLeft = Offset((x + i) * cellSize, (y + j) * cellSize),
                    size = Size(cellSize * 0.9f, cellSize * 0.9f)
                )
            }
        }
    }
}

@Composable
fun ReceiptText(text: String, modifier: Modifier, textAlign: TextAlign = TextAlign.Start) {
    Text(
        text = text,
        modifier = modifier,
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
fun ReceiptCard(
    transaction: HistoryItem,
    shopName: String,
    shopAddress: String,
    shopPhone: String
) {
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
                        text = shopName.uppercase(),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = shopAddress,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        Text(" $shopPhone", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
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

            Column {
                Text("PELANGGAN:", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                Text(transaction.customer.name ?: "Umum", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(transaction.vehicle.numberPlate ?: "-", color = MaterialTheme.colorScheme.primary)
                Text(transaction.vehicle.brand ?: "", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))

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
            QRVisualizer(seed = trxId, sizeDp = 60, tintColor = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("QR Code Nota", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("Scan untuk verifikasi - $trxId", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
