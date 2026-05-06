package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Trip
import com.example.myapplication.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateTripUiState(
    val destination: String = "",
    val type: String = "Lazer",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val budget: String = "",
    val errorMessage: String = "",
    val fieldErrors: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false
)

class CreateTripViewModel(private val appContext: android.content.Context? = null) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateTripUiState())
    val uiState: StateFlow<CreateTripUiState> = _uiState.asStateFlow()

    fun updateDestination(destination: String) {
        _uiState.update { it.copy(destination = destination, errorMessage = "") }
    }

    fun updateType(type: String) {
        _uiState.update { it.copy(type = type, errorMessage = "") }
    }

    fun updateStartDate(date: Long) {
        _uiState.update { it.copy(startDate = date, errorMessage = "") }
    }

    fun updateEndDate(date: Long) {
        _uiState.update { it.copy(endDate = date, errorMessage = "") }
    }

    fun updateBudget(budget: String) {
        _uiState.update { it.copy(budget = budget, errorMessage = "") }
    }

    fun validateTrip(): Boolean {
        val currentState = _uiState.value
        val errors = mutableMapOf<String, String>()

        if (currentState.destination.isBlank()) {
            errors["destination"] = "Destino é obrigatório"
        }
        if (currentState.startDate == 0L) {
            errors["startDate"] = "Data de início é obrigatória"
        }
        if (currentState.endDate == 0L) {
            errors["endDate"] = "Data de fim é obrigatória"
        }
        if (currentState.startDate > 0 && currentState.endDate > 0 && currentState.startDate > currentState.endDate) {
            errors["endDate"] = "Data de fim deve ser após data de início"
        }
        if (currentState.budget.isBlank()) {
            errors["budget"] = "Orçamento é obrigatório"
        } else if (currentState.budget.toDoubleOrNull() == null || currentState.budget.toDouble() <= 0) {
            errors["budget"] = "Orçamento deve ser um valor positivo"
        }

        _uiState.update { it.copy(fieldErrors = errors) }
        return errors.isEmpty()
    }

    fun createTrip(userId: Int, onSuccess: () -> Unit) {
        if (!validateTrip()) {
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                if (appContext != null) {
                    val database = AppContainer.getDatabase(appContext)
                    val tripDao = database.tripDao()
                    val currentState = _uiState.value

                    val newTrip = Trip(
                        userId = userId,
                        destination = currentState.destination,
                        type = currentState.type,
                        startDate = currentState.startDate,
                        endDate = currentState.endDate,
                        budget = currentState.budget.toDouble()
                    )

                    tripDao.insert(newTrip)
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao criar viagem: ${e.message}"
                    )
                }
            }
        }
    }
}

