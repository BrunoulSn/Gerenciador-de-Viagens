package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val errorMessage: String = ""
)

class LoginViewModel : ViewModel() {
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
}

