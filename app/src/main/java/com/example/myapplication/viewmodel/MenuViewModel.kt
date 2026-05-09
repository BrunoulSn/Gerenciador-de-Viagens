package com.example.myapplication.viewmodel

import android.Manifest
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.CurrentUser
import com.example.myapplication.data.model.Trip
import com.example.myapplication.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.location.Geocoder
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import java.util.Locale

data class MenuUiState(
    val isLoading: Boolean = false,
    val currentTrip: Trip? = null,
    val errorMessage: String = ""
)

class MenuViewModel(private val appContext: Context) : ViewModel() {
    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun checkLocationAndFindTrip() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val result = googleApiAvailability.isGooglePlayServicesAvailable(appContext)
        if (result != ConnectionResult.SUCCESS) {
            _uiState.update { it.copy(errorMessage = "Google Play Services não disponível") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = "") }

        try {
            val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appContext)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    getCityFromLocation(location.latitude, location.longitude)
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Não foi possível obter a localização") }
                }
            }.addOnFailureListener {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao obter localização: ${it}") }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Erro: ${e.message}") }
        }
    }

    private fun getCityFromLocation(lat: Double, lng: Double) {
        try {
            val geocoder = Geocoder(appContext, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val city = addresses[0].locality ?: addresses[0].subAdminArea ?: "Cidade desconhecida"
                findTripByCity(city)
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Não foi possível obter a cidade") }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao obter cidade: ${e.message}") }
        }
    }

    private fun findTripByCity(city: String) {
        viewModelScope.launch {
            try {
                val database = AppContainer.getDatabase(appContext)
                val tripDao = database.tripDao()
                val currentDate = System.currentTimeMillis()
                val userId = CurrentUser.userId ?: return@launch

                val trip = tripDao.getCurrentTripByCity(userId, city, currentDate)
                _uiState.update { it.copy(isLoading = false, currentTrip = trip) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao buscar viagem: ${e.message}") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = "") }
    }

    fun setError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }
}
