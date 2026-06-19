package com.le.uts_tam.ui.screen.nota

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.le.uts_tam.ui.components.PrintReceiptDialog
import com.le.uts_tam.ui.components.QRVisualizer
import com.le.uts_tam.ui.screen.profil.ProfilViewModel
import com.le.uts_tam.ui.screen.riwayat.HistoryItem
import com.le.uts_tam.utils.BluetoothPrinterManager
import kotlinx.coroutines.launch

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
                        shopName = shopInfo.ownerName.ifEmpty { "SmartBengkel" },
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
                                shopInfo.ownerName.ifEmpty { "SmartBengkel" },
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
                            shopInfo.ownerName.ifEmpty { "SmartBengkel" }
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
            shopName = shopInfo.ownerName.ifEmpty { "SmartBengkel" },
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
                Icon(Icons.Default.Refresh, null)
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
                Text(transaction.totalHarga, color = MaterialTheme.colorScheme.primary)
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
                    text = transaction.totalHarga.uppercase(),
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
            QRVisualizer(seed = trxId, sizeDp = 60)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("QR Code Nota", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("Scan untuk verifikasi - $trxId", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
