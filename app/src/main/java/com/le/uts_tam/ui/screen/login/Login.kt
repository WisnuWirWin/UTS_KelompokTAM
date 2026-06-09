package com.le.uts_tam.ui.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Login(onLoginSuccess: () -> Unit) {
    val viewModel: LoginViewModel = viewModel()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var showRegisterDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "KELOLA",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = "BENGKEL",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = "LEBIH CEPAT",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(40.dp))

        viewModel.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Text(
            text = "USERNAME / EMAIL",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = username,
            onValueChange = { 
                username = it
                viewModel.errorMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            placeholder = { Text("Masukan Username", color = MaterialTheme.colorScheme.outline) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "PASSWORD",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { 
                password = it
                viewModel.errorMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            placeholder = { Text("Masukan Password", color = MaterialTheme.colorScheme.outline) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { 
                viewModel.login(username, password, onLoginSuccess)
            },
            enabled = !viewModel.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues()
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "MASUK SEKARANG",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Belum punya akun? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "Daftar",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { showRegisterDialog = true }
            )
        }
    }

    if (showRegisterDialog) {
        RegisterDialog(
            onDismiss = { showRegisterDialog = false },
            onRegister = { user, pass, name ->
                viewModel.register(user, pass, name) {
                    showRegisterDialog = false
                }
            }
        )
    }
}

@Composable
fun RegisterDialog(onDismiss: () -> Unit, onRegister: (String, String, String) -> Unit) {
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Daftar Akun Baru", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = name, onValueChange = { name = it }, label = { Text("Nama Pemilik") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = user, onValueChange = { user = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = pass, onValueChange = { pass = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Batal") }
                    Button(onClick = { onRegister(user, pass, name) }) { Text("Daftar") }
                }
            }
        }
    }
}
