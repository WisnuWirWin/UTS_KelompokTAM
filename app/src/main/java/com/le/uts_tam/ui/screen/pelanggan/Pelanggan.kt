package com.le.uts_tam.ui.screen.pelanggan

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.le.uts_tam.data.model.dataclass.Customers
import com.le.uts_tam.data.model.dataclass.PelangganUIState

@Composable
fun Pelanggan(
    onBack: () -> Unit = {},
    onAddPelanggan: () -> Unit = {},
    onEditPelanggan: (Customers) -> Unit = {},
    viewModel: PelangganViewModel = viewModel(),
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
    
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val filteredList = uiState.filter {
        it.name.contains(searchQuery, ignoreCase = true) || 
        it.plate.contains(searchQuery, ignoreCase = true)
    }

    var selectedCustomerForDelete by remember { mutableStateOf<PelangganUIState?>(null) }

    // Dialog Konfirmasi Hapus
    selectedCustomerForDelete?.let { customer ->
        AlertDialog(
            onDismissRequest = { selectedCustomerForDelete = null },
            title = { Text("Hapus Pelanggan") },
            text = { Text("Apakah Anda yakin ingin menghapus data pelanggan '${customer.name}' beserta riwayatnya?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCustomer(customer.firebaseKey)
                        Toast.makeText(context, "Data pelanggan '${customer.name}' berhasil dihapus", Toast.LENGTH_SHORT).show()
                        selectedCustomerForDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCustomerForDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp),
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Back", 
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PELANGGAN",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick = onAddPelanggan,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                    .size(40.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            ),
            placeholder = { 
                Text(
                    text = "Cari nama atau no. plat...", 
                    color = MaterialTheme.colorScheme.onSurfaceVariant, 
                    style = MaterialTheme.typography.bodyMedium,
                ) 
            },
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Default.Search, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                ) 
            },
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = error ?: "Unknown Error", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.fetchData() }) {
                    Text("Coba Lagi")
                }
            }
        } else {
            filteredList.forEach { customer ->
                CustomerCard(
                    plate = customer.plate,
                    name = customer.name,
                    phone = customer.phone,
                    motor = customer.motorDisplay,
                    history = listOf(
                        ServiceHistory(customer.complaint, "Hari ini", "-"),
                    ),
                    onEdit = {
                        onEditPelanggan(
                            Customers(
                                firebaseKey = customer.firebaseKey,
                                id = customer.id,
                                name = customer.name,
                                noHp = customer.phone,
                                address = customer.address,
                                plateNumber = customer.plate,
                                motorBrand = customer.motorBrand,
                                motorModel = customer.motorModel,
                                motorYear = customer.motorYear,
                                motorColor = customer.motorColor,
                                complaint = customer.complaint
                            )
                        )
                    },
                    onDelete = {
                        selectedCustomerForDelete = customer
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

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
    history: List<ServiceHistory>,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = plate,
                        color = Color.Black,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name, 
                        color = MaterialTheme.colorScheme.onSurface, 
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Call, 
                            contentDescription = null, 
                            tint = MaterialTheme.colorScheme.primary, 
                            modifier = Modifier.size(12.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = phone, 
                            color = MaterialTheme.colorScheme.onSurfaceVariant, 
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Text(
                        text = motor, 
                        color = MaterialTheme.colorScheme.secondary, 
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "RIWAYAT SERVIS",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )

            history.forEach { item ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(
                            text = item.name, 
                            color = MaterialTheme.colorScheme.onSurface, 
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = item.date, 
                            color = MaterialTheme.colorScheme.onSurfaceVariant, 
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    Text(
                        text = item.price, 
                        color = MaterialTheme.colorScheme.secondary, 
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}
