package com.le.uts_tam

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Login(onLoginSuccess: () -> Unit) {
    val customFontFamily = FontFamily(
        Font(R.font.poppins),
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "KELOLA",
            color = Color.White,
            fontSize = 40.sp,
            fontFamily = customFontFamily,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "BENGKEL",
            color = Color(0xFFE53935),
            fontSize = 40.sp,
            fontFamily = customFontFamily,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "LEBIH CEPAT",
            color = Color.White,
            fontSize = 40.sp,
            fontFamily = customFontFamily,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Platform manajemen servis kendaraan terpadu untuk bengkel modern.",
            color = Color.Gray,
            fontSize = 14.sp,
            fontFamily = customFontFamily
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "USERNAME / EMAIL",
            color = Color.Gray,
            fontSize = 12.sp,
            fontFamily = customFontFamily,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E1E1E),
                unfocusedContainerColor = Color(0xFF1E1E1E),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            placeholder = { Text("Masukan Username", color = Color.DarkGray) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "PASSWORD",
            color = Color.Gray,
            fontSize = 12.sp,
            fontFamily = customFontFamily,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E1E1E),
                unfocusedContainerColor = Color(0xFF1E1E1E),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            placeholder = { Text("Masukan Password", color = Color.DarkGray) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onLoginSuccess() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFE53935), Color(0xFFFF7043))
                    ),
                    shape = RoundedCornerShape(12.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues()
        ) {
            Text(
                text = "MASUK SEKARANG",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                fontFamily = customFontFamily
            )
        }
    }
}
