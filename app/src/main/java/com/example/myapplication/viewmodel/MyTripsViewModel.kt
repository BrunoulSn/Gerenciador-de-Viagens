package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Trip
import com.example.myapplication.di.AppContainer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyTripsUiState(
    val trips: List<Trip> = emptyList(),
    val errorMessage: String = "",
    val isLoading: Boolean = true,
    val editingTrip: Trip? = null
)

class MyTripsViewModel(private val appContext: android.content.Context? = null) : ViewModel() {
    private val _uiState = MutableStateFlow(MyTripsUiState())
    val uiState: StateFlow<MyTripsUiState> = _uiState.asStateFlow()

    private var tripsFlow: Flow<List<Trip>>? = null

    fun loadTrips(userId: Int) {
        viewModelScope.launch {
            try {
                if (appContext != null) {
                    val database = AppContainer.getDatabase(appContext)
                    val tripDao = database.tripDao()
                    
                    tripDao.getTripsByUserId(userId).collect { trips ->
                        _uiState.update { it.copy(trips = trips, isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar viagens: ${e.message}"
                    )
                }
            }
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            try {
                if (appContext != null) {
                    val database = AppContainer.getDatabase(appContext)
                    val tripDao = database.tripDao()
                    tripDao.delete(trip)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Erro ao deletar viagem: ${e.message}")
                }
            }
        }
    }

    fun updateTrip(trip: Trip, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                if (appContext != null) {
                    val database = AppContainer.getDatabase(appContext)
                    val tripDao = database.tripDao()
                    tripDao.update(trip)
                    _uiState.update { it.copy(editingTrip = null) }
                    onSuccess()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Erro ao atualizar viagem: ${e.message}")
                }
            }
        }
    }

    fun setEditingTrip(trip: Trip?) {
        _uiState.update { it.copy(editingTrip = trip) }
    }
}

