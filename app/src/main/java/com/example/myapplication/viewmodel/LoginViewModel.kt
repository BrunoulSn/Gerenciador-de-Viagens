package com.example.myapplication.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.CurrentUser
import com.example.myapplication.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val errorMessage: String = "",
    val isLoading: Boolean = false
)

class LoginViewModel(private val appContext: Context) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = "") }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = "") }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun validateLogin(): Boolean {
        val currentState = _uiState.value
        return when {
            currentState.email.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "E-mail é obrigatório") }
                false
            }

            currentState.password.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Senha é obrigatória") }
                false
            }

            !isValidEmail(currentState.email) -> {
                _uiState.update { it.copy(errorMessage = "E-mail inválido") }
                false
            }

            else -> true
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = "") }
    }

    fun login(onSuccess: () -> Unit) {
        if (!validateLogin()) {
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                if (appContext != null) {
                    val database = AppContainer.getDatabase(appContext)
                    val userDao = database.userDao()
                    val currentState = _uiState.value

                    // Buscar usuário pelo email
                    val user = userDao.getUserByEmail(currentState.email)

                    if (user == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Usuário não encontrado"
                            )
                        }
                        return@launch
                    }

                    // Verificar senha
                    if (user.password != currentState.password) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Senha incorreta"
                            )
                        }
                        return@launch
                    }

                    // Login bem-sucedido
                    CurrentUser.userId = user.id
                    CurrentUser.userName = user.name
                    CurrentUser.userEmail = user.email

                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao fazer login: ${e.message}"
                    )
                }
            }
        }
    }
}


