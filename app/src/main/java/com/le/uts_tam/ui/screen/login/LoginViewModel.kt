package com.le.uts_tam.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.le.uts_tam.data.local.AppDatabase
import com.le.uts_tam.data.model.dataclass.Owners
import com.le.uts_tam.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginViewModel(private val database: AppDatabase) : ViewModel() {
    private val repository = FirebaseRepository(null, database)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun login(usernameInput: String, passwordInput: String, onSuccess: (Owners) -> Unit) {
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
                val owners = repository.getOwnersForLogin().first()
                val matchedOwner = owners.find {
                    it.username == u && it.password == p
                }
                if (matchedOwner != null) {
                    database.ownerDao().deleteAll()
                    database.ownerDao().upsertOwner(matchedOwner)
                    onSuccess(matchedOwner)
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

    fun register(user: String, pass: String, name: String, onSuccess: () -> Unit) {
        if (user.isEmpty() || pass.isEmpty() || name.isEmpty()) {
            errorMessage = "Semua field harus diisi"
            return
        }
        viewModelScope.launch {
            isLoading = true
            try {
                repository.registerNewOwner(Owners(owner = name, username = user, password = pass))
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Gagal daftar: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
