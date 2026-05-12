package com.le.uts_tam.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.model.dataclass.Owners
import com.le.uts_tam.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun login(
        usernameInput: String,
        passwordInput: String,
        onSuccess: () -> Unit
    ) {
        val u = usernameInput.trim()
        val p = passwordInput.trim()

        if (u.isEmpty() || p.isEmpty()) {
            errorMessage = "Username dan Password tidak boleh kosong"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val owners = repository.getOwners()

                if (owners.isEmpty()) {
                    errorMessage = "Gagal mengambil data dari server"
                    return@launch
                }

                val matchedOwner = owners.find {
                    it.username?.equals(u, ignoreCase = true) == true &&
                            it.password == p
                }

                if (matchedOwner != null) {
                    onSuccess()
                } else {
                    errorMessage = "Username atau Password salah"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
