package com.le.uts_tam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import com.le.uts_tam.ui.screen.editstok.EditStockViewModel
import com.le.uts_tam.ui.theme.UTS_TAMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }

            UTS_TAMTheme(darkTheme = isDarkTheme) {
                var currentScreen by remember { mutableStateOf("login") }
                var selectedItemForEdit by remember { mutableStateOf<com.le.uts_tam.data.model.dataclass.Items?>(null) }
                var selectedCustomerForEdit by remember { mutableStateOf<com.le.uts_tam.data.model.dataclass.Customers?>(null) }
                var selectedTransactionForNota by remember { mutableStateOf<com.le.uts_tam.ui.screen.riwayat.HistoryItem?>(null) }

                when (currentScreen) {
                    "login" -> Login(onLoginSuccess = { currentScreen = "dashboard" })
                    "dashboard" -> Dashboard(
                        onPelangganClick = { currentScreen = "pelanggan" },
                        onProfileClick = { currentScreen = "profil" },
                        onKasirClick = { currentScreen = "kasir" },
                        onRiwayatClick = { currentScreen = "riwayat" },
                        onStokClick = { currentScreen = "inventaris" },
                        onLaporanClick = { currentScreen = "laporan" }
                    )
                    "pelanggan" -> Pelanggan(
                        onBack = { currentScreen = "dashboard" },
                        onAddPelanggan = {
                            selectedCustomerForEdit = null
                            currentScreen = "add_pelanggan"
                        },
                        onEditPelanggan = { customer ->
                            selectedCustomerForEdit = customer
                            currentScreen = "add_pelanggan"
                        }
                    )
                    "add_pelanggan" -> {
                        val addPelangganViewModel: com.le.uts_tam.ui.screen.addpelanggan.AddPelangganViewModel = viewModel()
                        LaunchedEffect(selectedCustomerForEdit) {
                            addPelangganViewModel.setInitialData(selectedCustomerForEdit)
                        }
                        AddPelanggan(
                            onBack = { currentScreen = "pelanggan" },
                            onConfirm = { currentScreen = "pelanggan" },
                            viewModel = addPelangganViewModel
                        )
                    }
                    "profil" -> Profil(
                        onBack = { currentScreen = "dashboard" },
                        onLogout = { currentScreen = "login" },
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = it }
                    )
                    "kasir" -> Kasir(
                        onBack = { currentScreen = "dashboard" },
                        onPrintNota = {
                            // This would ideally set the last transaction
                            currentScreen = "riwayat"
                        }
                    )
                    "nota_digital" -> NotaDigital(
                        onBack = { currentScreen = "riwayat" },
                        transaction = selectedTransactionForNota
                    )
                    "riwayat" -> Riwayat(
                        onBack = { currentScreen = "dashboard" },
                        onKasirClick = { currentScreen = "kasir" },
                        onRiwayatClick = { currentScreen = "riwayat" },
                        onStokClick = { currentScreen = "inventaris" },
                        onLaporanClick = { currentScreen = "laporan" },
                        onTransactionClick = { transaction ->
                            selectedTransactionForNota = transaction
                            currentScreen = "nota_digital"
                        }
                    )
                    "inventaris" -> Inventaris(
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
                        onLaporanClick = { currentScreen = "laporan" }
                    )
                    "edit_stock" -> {
                        val editViewModel: EditStockViewModel = viewModel()
                        LaunchedEffect(selectedItemForEdit) {
                            editViewModel.setInitialData(selectedItemForEdit)
                        }
                        EditStock(
                            onBack = { currentScreen = "inventaris" },
                            viewModel = editViewModel
                        )
                    }
                    "laporan" -> Laporan(
                        onBack = { currentScreen = "dashboard" },
                        onKasirClick = { currentScreen = "kasir" },
                        onRiwayatClick = { currentScreen = "riwayat" },
                        onStokClick = { currentScreen = "inventaris" },
                        onLaporanClick = { currentScreen = "laporan" }
                    )
                }
            }
        }
    }
}
