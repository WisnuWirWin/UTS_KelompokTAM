package com.le.uts_tam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.le.uts_tam.ui.theme.UTS_TAMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UTS_TAMTheme {
                var currentScreen by remember { mutableStateOf("login") }

                if (currentScreen == "login") {
                    Login(onLoginSuccess = {
                        currentScreen = "dashboard"
                    })
                } else {
                    Dashboard()
                }
            }
        }
    }
}
