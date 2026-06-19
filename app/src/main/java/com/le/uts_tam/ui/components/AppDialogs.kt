package com.le.uts_tam.ui.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.le.uts_tam.ui.screen.riwayat.HistoryItem
import com.le.uts_tam.utils.QRUtils

@Composable
fun RegisterDialog(
    onDismiss: () -> Unit,
    onRegister: (String, String, String) -> Unit,
) {
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    
    var userError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Daftar Akun Baru", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                
                TextField(
                    value = name, 
                    onValueChange = { 
                        name = it
                        if (it.isNotBlank()) nameError = null
                    }, 
                    label = { Text("Nama Pemilik") }, 
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it) } }
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = user, 
                    onValueChange = { 
                        user = it
                        if (it.isNotBlank()) userError = null
                    }, 
                    label = { Text("Username") }, 
                    modifier = Modifier.fillMaxWidth(),
                    isError = userError != null,
                    supportingText = { userError?.let { Text(it) } }
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = pass, 
                    onValueChange = { 
                        pass = it
                        if (it.length >= 6) passError = null
                    }, 
                    label = { Text("Password") }, 
                    modifier = Modifier.fillMaxWidth(), 
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passError != null,
                    supportingText = { passError?.let { Text(it) } }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Batal") }
                    Button(
                        onClick = { 
                            var hasError = false
                            if (name.isBlank()) {
                                nameError = "Nama wajib diisi"
                                hasError = true
                            }
                            if (user.isBlank()) {
                                userError = "Username wajib diisi"
                                hasError = true
                            }
                            if (pass.length < 6) {
                                passError = "Password minimal 6 karakter"
                                hasError = true
                            }
                            
                            if (!hasError) {
                                onRegister(user, pass, name)
                            }
                        }
                    ) { Text("Daftar") }
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    initialName: String,
    initialAddress: String,
    initialPhone: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var address by remember { mutableStateOf(initialAddress) }
    var phone by remember { mutableStateOf(initialPhone) }
    
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Konfirmasi Perubahan") },
            text = { Text("Apakah Anda yakin ingin menyimpan perubahan profil ini?") },
            confirmButton = {
                Button(onClick = {
                    onConfirm(name, address, phone)
                    showConfirmDialog = false
                }) { Text("Ya, Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Batal") }
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Ubah Profil Bengkel", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(24.dp))
                
                TextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        nameError = if (it.isBlank()) "Nama tidak boleh kosong" else null
                    },
                    label = { Text("Nama Pemilik") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it) } },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                TextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Alamat") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                TextField(
                    value = phone,
                    onValueChange = { 
                        phone = it
                        phoneError = if (it.length < 10) "Nomor telepon tidak valid" else null
                    },
                    label = { Text("No. Telepon") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = phoneError != null,
                    supportingText = { phoneError?.let { Text(it) } },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                    Button(
                        onClick = {
                            if ((name.isNotBlank()) && (phone.length >= 10)) {
                                showConfirmDialog = true
                            }
                        },
                        enabled = (nameError == null) && (phoneError == null) && (name.isNotBlank()) && (phone.length >= 10)
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Konfirmasi Ganti Password") },
            text = { Text("Apakah Anda yakin ingin mengubah password akun Anda?") },
            confirmButton = {
                Button(onClick = {
                    onConfirm(newPassword)
                    showConfirmDialog = false
                }) { Text("Ya, Ubah") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Batal") }
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Ubah Password", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                
                TextField(
                    value = newPassword,
                    onValueChange = { 
                        newPassword = it 
                        error = null
                    },
                    label = { Text("Password Baru") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = confirmPassword,
                    onValueChange = { 
                        confirmPassword = it 
                        error = null
                    },
                    label = { Text("Konfirmasi Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Batal") }
                    Button(onClick = {
                        if (newPassword.length < 6) {
                            error = "Password minimal 6 karakter"
                        } else if (newPassword != confirmPassword) {
                            error = "Password tidak cocok"
                        } else {
                            showConfirmDialog = true
                        }
                    }) { Text("Simpan") }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun PrinterSelectionDialog(
    pairedDevices: List<BluetoothDevice>,
    isConnected: Boolean,
    onDeviceClick: (BluetoothDevice) -> Unit,
    onDisconnect: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Pilih Printer Bluetooth", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                
                if (pairedDevices.isEmpty()) {
                    Text("Tidak ada perangkat Bluetooth yang terpasang.", color = Color.Gray)
                } else {
                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(pairedDevices) { device ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onDeviceClick(device) }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Print, null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    val deviceName = device.name ?: "Unknown Device"
                                    @SuppressLint("MissingPermission")
                                    val deviceAddress = device.address
                                    Text(deviceName, style = MaterialTheme.typography.bodyLarge)
                                    Text(deviceAddress, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                            }
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    if (isConnected) {
                        TextButton(onClick = onDisconnect) {
                            Text("Putus Koneksi", color = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Tutup")
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Hapus")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun ItemQRCodeDialog(
    itemName: String,
    itemId: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "QR CODE BARANG",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = itemName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                val qrBitmap = remember(itemId) {
                    QRUtils.generateQRCode(itemId)
                }

                qrBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.White)
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "ID: $itemId",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("TUTUP")
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
fun SaveConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String = "Konfirmasi Simpan",
    message: String = "Apakah Anda yakin ingin menyimpan perubahan ini?"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Ya, Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun InfoDetailDialog(
    title: String,
    content: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        text = { 
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            ) 
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("TUTUP")
            }
        }
    )
}
