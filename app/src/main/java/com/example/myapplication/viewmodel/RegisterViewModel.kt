package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val errorMessage: String = "",
    val fieldErrors: Map<String, String> = emptyMap()
)

class RegisterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name, errorMessage = "") }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = "") }
    }

    fun updatePhone(phone: String) {
        _uiState.update { it.copy(phone = phone, errorMessage = "") }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = "") }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, errorMessage = "") }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun validateRegistration(): Boolean {
        val currentState = _uiState.value
        val errors = mutableMapOf<String, String>()

        if (currentState.name.isBlank()) {
            errors["name"] = "Nome é obrigatório"
        }

        if (currentState.email.isBlank()) {
            errors["email"] = "E-mail é obrigatório"
        } else if (!isValidEmail(currentState.email)) {
            errors["email"] = "E-mail inválido"
        }

        if (currentState.phone.isBlank()) {
            errors["phone"] = "Telefone é obrigatório"
        } else if (!isValidPhone(currentState.phone)) {
            errors["phone"] = "Telefone inválido"
        }

        if (currentState.password.isBlank()) {
            errors["password"] = "Senha é obrigatória"
        } else if (currentState.password.length < 6) {
            errors["password"] = "Senha deve ter pelo menos 6 caracteres"
        }

        if (currentState.confirmPassword.isBlank()) {
            errors["confirmPassword"] = "Confirmação de senha é obrigatória"
        } else if (currentState.password != currentState.confirmPassword) {
            errors["confirmPassword"] = "As senhas não coincidem"
        }

        _uiState.update { it.copy(fieldErrors = errors) }
        return errors.isEmpty()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPhone(phone: String): Boolean {
        return phone.replace(Regex("[^0-9]"), "").length >= 10
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = "", fieldErrors = emptyMap()) }
    }
}

