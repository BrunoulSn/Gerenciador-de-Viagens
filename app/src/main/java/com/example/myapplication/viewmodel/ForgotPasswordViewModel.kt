package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ForgotPasswordUiState(
    val email: String = "",
    val errorMessage: String = "",
    val successMessage: String = "",
    val isSubmitted: Boolean = false
)

class ForgotPasswordViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = "", successMessage = "") }
    }

    fun validateAndSubmit(): Boolean {
        val currentState = _uiState.value
        return when {
            currentState.email.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "E-mail é obrigatório") }
                false
            }
            !isValidEmail(currentState.email) -> {
                _uiState.update { it.copy(errorMessage = "E-mail inválido") }
                false
            }
            else -> {
                _uiState.update {
                    it.copy(
                        successMessage = "Instruções de recuperação foram enviadas para o seu e-mail",
                        errorMessage = "",
                        isSubmitted = true
                    )
                }
                true
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = "", successMessage = "") }
    }

    fun resetForm() {
        _uiState.value = ForgotPasswordUiState()
    }
}

