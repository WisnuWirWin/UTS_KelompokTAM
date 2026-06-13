package com.le.uts_tam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.le.uts_tam.data.local.AppDatabase
import com.le.uts_tam.ui.screen.addpelanggan.AddPelanggan
import com.le.uts_tam.ui.screen.dashboard.Dashboard
import com.le.uts_tam.ui.screen.editstok.EditStock
import com.le.uts_tam.ui.screen.inventaris.Inventaris
import com.le.uts_tam.ui.screen.kasir.Kasir
import com.le.uts_tam.ui.screen.laporan.Laporan
import com.le.uts_tam.ui.screen.login.Login
import com.le.uts_tam.ui.screen.nota.NotaDigital
import com.le.uts_tam.ui.screen.pelanggan.Pelanggan
import com.le.uts_tam.ui.screen.profil.Profil
import com.le.uts_tam.ui.screen.riwayat.Riwayat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.le.uts_tam.ui.screen.dashboard.DashboardViewModel
import com.le.uts_tam.ui.screen.pelanggan.viewmodel.PelangganViewModel
import com.le.uts_tam.ui.screen.kasir.KasirViewModel
import com.le.uts_tam.ui.screen.riwayat.RiwayatViewModel
import com.le.uts_tam.ui.screen.profil.viewmodel.ProfilViewModel
import com.le.uts_tam.ui.screen.addpelanggan.AddPelangganViewModel
import com.le.uts_tam.ui.screen.editstok.EditStockViewModel
import com.le.uts_tam.ui.screen.inventaris.InventarisViewModel
import com.le.uts_tam.ui.screen.laporan.LaporanViewModel
import com.le.uts_tam.ui.theme.UTS_TAMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.getDatabase(this)
        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }

            UTS_TAMTheme(darkTheme = isDarkTheme) {
                var currentScreen by remember { mutableStateOf("login") }
                var loggedInOwnerId by remember { mutableStateOf<String?>(null) }
                
                var selectedItemForEdit by remember { mutableStateOf<com.le.uts_tam.data.model.dataclass.Items?>(null) }
                var selectedCustomerForEdit by remember { mutableStateOf<com.le.uts_tam.data.model.dataclass.Customers?>(null) }
                var selectedTransactionForNota by remember { mutableStateOf<com.le.uts_tam.ui.screen.riwayat.HistoryItem?>(null) }

                @Suppress("UNCHECKED_CAST")
                class ScopedViewModelFactory(private val ownerId: String, private val db: AppDatabase) : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        val viewModel = when {
                            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(ownerId, db)
                            modelClass.isAssignableFrom(PelangganViewModel::class.java) -> PelangganViewModel(ownerId, db)
                            modelClass.isAssignableFrom(KasirViewModel::class.java) -> KasirViewModel(ownerId, db)
                            modelClass.isAssignableFrom(RiwayatViewModel::class.java) -> RiwayatViewModel(ownerId, db)
                            modelClass.isAssignableFrom(ProfilViewModel::class.java) -> ProfilViewModel(ownerId, db)
                            modelClass.isAssignableFrom(AddPelangganViewModel::class.java) -> AddPelangganViewModel(ownerId, db)
                            modelClass.isAssignableFrom(EditStockViewModel::class.java) -> EditStockViewModel(ownerId, db)
                            modelClass.isAssignableFrom(InventarisViewModel::class.java) -> InventarisViewModel(ownerId, db)
                            modelClass.isAssignableFrom(LaporanViewModel::class.java) -> LaporanViewModel(ownerId, db)
                            else -> throw IllegalArgumentException("Unknown ViewModel class")
                        }
                        return viewModel as T
                    }
                }

                when (currentScreen) {
                    "login" -> Login(
                        onLoginSuccess = { owner -> 
                            loggedInOwnerId = owner.firebaseKey
                            currentScreen = "dashboard" 
                        },
                        database = database
                    )
                    
                    "dashboard" -> {
                        val ownerId = loggedInOwnerId ?: return@UTS_TAMTheme
                        Dashboard(
                            onPelangganClick = { currentScreen = "pelanggan" },
                            onProfileClick = { currentScreen = "profil" },
                            onKasirClick = { currentScreen = "kasir" },
                            onRiwayatClick = { currentScreen = "riwayat" },
                            onStokClick = { currentScreen = "inventaris" },
                            onLaporanClick = { currentScreen = "laporan" },
                            viewModel = viewModel(
                                key = "dashboard_$ownerId",
                                factory = ScopedViewModelFactory(ownerId, database)
                            )
                        )
                    }
                    
                    "pelanggan" -> {
                        val ownerId = loggedInOwnerId ?: return@UTS_TAMTheme
                        Pelanggan(
                            onBack = { currentScreen = "dashboard" },
                            onAddPelanggan = { 
                                selectedCustomerForEdit = null
                                currentScreen = "add_pelanggan" 
                            },
                            onEditPelanggan = { customer ->
                                selectedCustomerForEdit = customer
                                currentScreen = "add_pelanggan"
                            },
                            viewModel = viewModel(
                                key = "pelanggan_$ownerId",
                                factory = ScopedViewModelFactory(ownerId, database)
                            )
                        )
                    }
                    
                    "add_pelanggan" -> {
                        val ownerId = loggedInOwnerId ?: return@UTS_TAMTheme
                        val addViewModel: AddPelangganViewModel = viewModel(
                            key = "add_pelanggan_$ownerId",
                            factory = ScopedViewModelFactory(ownerId, database)
                        )
                        LaunchedEffect(selectedCustomerForEdit) {
                            addViewModel.setInitialData(selectedCustomerForEdit)
                        }
                        AddPelanggan(
                            onBack = { currentScreen = "pelanggan" },
                            onConfirm = { currentScreen = "pelanggan" },
                            viewModel = addViewModel
                        )
                    }
                    
                    "profil" -> {
                        val ownerId = loggedInOwnerId ?: return@UTS_TAMTheme
                        Profil(
                            onBack = { currentScreen = "dashboard" },
                            onLogout = { 
                                loggedInOwnerId = null
                                currentScreen = "login" 
                            },
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = { isDarkTheme = it },
                            viewModel = viewModel(
                                key = "profil_$ownerId",
                                factory = ScopedViewModelFactory(ownerId, database)
                            )
                        )
                    }
                    
                    "kasir" -> {
                        val ownerId = loggedInOwnerId ?: return@UTS_TAMTheme
                        Kasir(
                            onBack = { currentScreen = "dashboard" },
                            onPrintNota = { currentScreen = "riwayat" },
                            viewModel = viewModel(
                                key = "kasir_$ownerId",
                                factory = ScopedViewModelFactory(ownerId, database)
                            )
                        )
                    }
                    
                    "nota_digital" -> {
                        val ownerId = loggedInOwnerId ?: return@UTS_TAMTheme
                        NotaDigital(
                            onBack = { currentScreen = "riwayat" },
                            transaction = selectedTransactionForNota,
                            profilViewModel = viewModel(
                                key = "profil_nota_$ownerId",
                                factory = ScopedViewModelFactory(ownerId, database)
                            )
                        )
                    }
                    
                    "riwayat" -> {
                        val ownerId = loggedInOwnerId ?: return@UTS_TAMTheme
                        Riwayat(
                            onBack = { currentScreen = "dashboard" },
                            onKasirClick = { currentScreen = "kasir" },
                            onRiwayatClick = { currentScreen = "riwayat" },
                            onStokClick = { currentScreen = "inventaris" },
                            onLaporanClick = { currentScreen = "laporan" },
                            onTransactionClick = { transaction ->
                                selectedTransactionForNota = transaction
                                currentScreen = "nota_digital"
                            },
                            viewModel = viewModel(
                                key = "riwayat_$ownerId",
                                factory = ScopedViewModelFactory(ownerId, database)
                            )
                        )
                    }
                    
                    "inventaris" -> {
                        val ownerId = loggedInOwnerId ?: return@UTS_TAMTheme
                        Inventaris(
                            onBack = { currentScreen = "dashboard" },
                            onAddItem = { 
                                selectedItemForEdit = null
                                currentScreen = "edit_stock" 
                            },
                            onEditItem = { item ->
                                selectedItemForEdit = item
                                currentScreen = "edit_stock"
                            },
                            onKasirClick = { currentScreen = "kasir" },
                            onRiwayatClick = { currentScreen = "riwayat" },
                            onStokClick = { currentScreen = "inventaris" },
                            onLaporanClick = { currentScreen = "laporan" },
                            viewModel = viewModel(
                                key = "inventaris_$ownerId",
                                factory = ScopedViewModelFactory(ownerId, database)
                            )
                        )
                    }
                    
                    "edit_stock" -> {
                        val ownerId = loggedInOwnerId ?: return@UTS_TAMTheme
                        val editViewModel: EditStockViewModel = viewModel(
                            key = "edit_stock_$ownerId",
                            factory = ScopedViewModelFactory(ownerId, database)
                        )
                        LaunchedEffect(selectedItemForEdit) {
                            editViewModel.setInitialData(selectedItemForEdit)
                        }
                        EditStock(
                            onBack = { currentScreen = "inventaris" },
                            viewModel = editViewModel
                        )
                    }

                    "laporan" -> {
                        val ownerId = loggedInOwnerId ?: return@UTS_TAMTheme
                        Laporan(
                            onBack = { currentScreen = "dashboard" },
                            onKasirClick = { currentScreen = "kasir" },
                            onRiwayatClick = { currentScreen = "riwayat" },
                            onStokClick = { currentScreen = "inventaris" },
                            onLaporanClick = { currentScreen = "laporan" },
                            viewModel = viewModel(
                                key = "laporan_$ownerId",
                                factory = ScopedViewModelFactory(ownerId, database)
                            )
                        )
                    }
                }
            }
        }
    }
}
