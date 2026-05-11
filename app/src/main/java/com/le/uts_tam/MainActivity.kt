package com.le.uts_tam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.le.uts_tam.ui.screen.dashborad.Dashboard
import com.le.uts_tam.ui.screen.login.Login
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
                        onAddPelanggan = { currentScreen = "add_pelanggan" }
                    )
                    "add_pelanggan" -> AddPelanggan(
                        onBack = { currentScreen = "pelanggan" },
                        onConfirm = { currentScreen = "pelanggan" }
                    )
                    "profil" -> Profil(
                        onBack = { currentScreen = "dashboard" },
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = it }
                    )
                    "kasir" -> Kasir(
                        onBack = { currentScreen = "dashboard" },
                        onPrintNota = { currentScreen = "nota_digital" }
                    )
                    "nota_digital" -> NotaDigital(onBack = { currentScreen = "kasir" })
                    "riwayat" -> Riwayat(onBack = { currentScreen = "dashboard" })
                    "inventaris" -> Inventaris(
                        onBack = { currentScreen = "dashboard" },
                        onAddItem = { currentScreen = "edit_stock" }
                    )
                    "edit_stock" -> EditStock(onBack = { currentScreen = "inventaris" })
                    "laporan" -> Laporan(onBack = { currentScreen = "dashboard" })
                }
            }
        }
    }
}
