package com.example.atvidadedm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atvidadedm.data.TripDestinationRepository
import com.example.atvidadedm.data.TripRepository
import com.example.atvidadedm.data.TripSaveResult
import com.example.atvidadedm.data.model.TripType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.content.Context
import android.location.Geocoder

// Exemplo de uso dentro do ViewModel ou Repository passando o contexto:
fun obterCoordenadasDoDestino(context: Context, nomeDoDestino: String) {
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val enderecos = geocoder.getFromLocationName(nomeDoDestino, 1)

        if (!enderecos.isNullOrEmpty()) {
            val latitude = enderecos[0].latitude
            val longitude = enderecos[0].longitude
            // Agora você tem os números para passar para o tripRepository.saveTrip(...)!
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
private val tripFormDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(
    "dd/MM/yyyy",
    Locale.Builder().setLanguage("pt").setRegion("BR").build()
)

data class TripFormUiState(
    val tripId: Long? = null,
    val destination: String = "",
    val type: TripType = TripType.LAZER,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val budget: String = "",
    val comments: String = "",
    val finalDestination: String = "",
    val destinationError: String? = null,
    val startDateError: String? = null,
    val endDateError: String? = null,
    val budgetError: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val feedbackMessage: String? = null,
    val saveCompleted: Boolean = false,
    val savedTripId: Long? = null,
    val itinerary: String? = null
) {
    val isEditMode: Boolean
        get() = tripId != null && tripId > 0
}

class TripFormViewModel(
    private val tripRepository: TripRepository,
    private val tripDestinationRepository: TripDestinationRepository,
    private val userId: Long,
    private val tripId: Long?
) : ViewModel() {
    private val _uiState = MutableStateFlow(TripFormUiState(isLoading = tripId != null && tripId > 0))
    val uiState: StateFlow<TripFormUiState> = _uiState.asStateFlow()

    init {
        if (tripId != null && tripId > 0) {
            loadTrip(tripId)
        }
    }

    fun onDestinationChange(destination: String) {
        _uiState.update { it.copy(destination = destination, destinationError = null) }
    }

    fun onFinalDestinationChange(destination: String) {
        _uiState.update { it.copy(finalDestination = destination, destinationError = null) }
    }

    fun onTypeChange(type: TripType) {
        _uiState.update { it.copy(type = type) }
    }

    fun onStartDateSelected(date: Long) {
        _uiState.update { it.copy(startDate = date, startDateError = null) }
    }

    fun onEndDateSelected(date: Long) {
        _uiState.update { it.copy(endDate = date, endDateError = null) }
    }

    fun onBudgetChange(budget: String) {
        val normalized = budget.filter { it.isDigit() || it == ',' || it == '.' }
        _uiState.update { it.copy(budget = normalized, budgetError = null) }
    }

    fun onCommentsChange(comments: String) {
        _uiState.update { it.copy(comments = comments) }
    }

    fun saveTrip(context: Context) {
        if (!validate()) {
            return
        }

        val state = _uiState.value
        val parsedBudget = state.budget.replace(',', '.').toDoubleOrNull() ?: 0.0
        val initialDestination = state.destination.trim()
        val finalDestination = state.finalDestination.trim()
        val orderedDestinationNames = buildOrderedDestinationNames(initialDestination, finalDestination)
        val existingItinerary = if (state.isEditMode) state.itinerary else null

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, feedbackMessage = null) }

            var geoLatitude = 0.0
            var geoLongitude = 0.0

            // =========================================================================
            // O CÓDIGO DO GEOCORDER ATUALIZADO ENTRA EXATAMENTE AQUI:
            // =========================================================================
            try {
                val geocoder = Geocoder(context, java.util.Locale.getDefault())

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    // Versão moderna (Android 13 ou superior) usando o Callback oficial assíncrono
                    kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
                        geocoder.getFromLocationName(finalDestination, 1, object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<android.location.Address>) {
                                if (addresses.isNotEmpty()) {
                                    geoLatitude = addresses[0].latitude
                                    geoLongitude = addresses[0].longitude
                                }
                                if (continuation.isActive) continuation.resume(Unit) {}
                            }

                            override fun onError(errorMessage: String?) {
                                if (continuation.isActive) continuation.resume(Unit) {}
                            }
                        })
                    }
                } else {
                    // Versão antiga (Android 12 ou inferior) - Seu código original com supressão de aviso
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocationName(finalDestination, 1)
                    if (!addresses.isNullOrEmpty()) {
                        geoLatitude = addresses[0].latitude
                        geoLongitude = addresses[0].longitude
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // =========================================================================

            // 3. PASSA AS COORDENADAS PARA O REPOSITÓRIO
            val saveResult = tripRepository.saveTrip(
                tripId = state.tripId,
                destination = finalDestination,
                type = state.type,
                startDate = state.startDate!!,
                endDate = state.endDate!!,
                budget = parsedBudget,
                comments = state.comments,
                itinerary = existingItinerary,
                userId = userId,
                latitude = geoLatitude,
                longitude = geoLongitude
            )

            when (saveResult) {
                is TripSaveResult.Created -> {
                    tripDestinationRepository.replaceTripDestinations(
                        tripId = saveResult.id,
                        destinationsInOrder = orderedDestinationNames,
                        finalDestination = finalDestination
                    )
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            feedbackMessage = if (existingItinerary.isNullOrBlank()) {
                                "Viagem cadastrada com sucesso. O roteiro será gerado em seguida."
                            } else {
                                "Viagem cadastrada com sucesso!"
                            },
                            saveCompleted = true,
                            savedTripId = saveResult.id
                        )
                    }
                }

                TripSaveResult.Updated -> {
                    state.tripId?.let { updatedTripId ->
                        tripDestinationRepository.replaceTripDestinations(
                            tripId = updatedTripId,
                            destinationsInOrder = orderedDestinationNames,
                            finalDestination = finalDestination
                        )
                    }
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            feedbackMessage = "Viagem atualizada com sucesso!",
                            saveCompleted = true,
                            savedTripId = state.tripId
                        )
                    }
                }

                TripSaveResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            feedbackMessage = "Não foi possível salvar a viagem"
                        )
                    }
                }
            }
        }
    }

    fun onSaveHandled() {
        _uiState.update { it.copy(saveCompleted = false) }
    }

    fun onFeedbackMessageShown() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    private fun loadTrip(tripId: Long) {
        viewModelScope.launch {
            val trip = tripRepository.getTripById(tripId)
            if (trip == null || trip.userId != userId) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        feedbackMessage = "Viagem não encontrada"
                    )
                }
            } else {
                val destinations = tripDestinationRepository.getTripDestinations(trip.id)
                val orderedNames = destinations.sortedBy { it.orderIndex }.map { it.name }
                val inferredInitial = orderedNames.firstOrNull() ?: trip.destination
                val inferredFinal = destinations.firstOrNull { it.isFinal }?.name
                    ?: orderedNames.lastOrNull()
                    ?: trip.destination

                _uiState.update {
                    it.copy(
                        tripId = trip.id,
                            destination = inferredInitial,
                        finalDestination = inferredFinal,
                        type = TripType.fromStorage(trip.type),
                        startDate = trip.startDate,
                        endDate = trip.endDate,
                        budget = trip.budget.toString(),
                        comments = trip.comments,
                            itinerary = trip.itinerary,
                        isLoading = false
                    )
                }
            }
        }
    }


    private fun buildOrderedDestinationNames(
        initialDestination: String,
        finalDestination: String
    ): List<String> {
        val result = linkedSetOf<String>()
        if (initialDestination.isNotBlank()) result.add(initialDestination)
        if (finalDestination.isNotBlank()) result.add(finalDestination)

        return result.toList()
    }


    private fun validate(): Boolean {
        val state = _uiState.value

        // 1. Criamos variáveis locais para acumular os erros
        var currentDestinationError: String? = null
        var currentStartDateError: String? = null
        var currentEndDateError: String? = null
        var currentBudgetError: String? = null
        var currentFeedbackMessage: String? = null
        var isValid = true

        // 2. Validação de Destinos
        val hasInitialDestination = state.destination.isNotBlank()
        val hasFinalDestination = state.finalDestination.isNotBlank()

        if (!hasInitialDestination || !hasFinalDestination) {
            currentDestinationError = "Informe destino inicial e destino final"
            currentFeedbackMessage = "Preencha os destinos corretamente."
            isValid = false
        } else if (state.destination.trim().equals(state.finalDestination.trim(), ignoreCase = true)) {
            currentDestinationError = "Destino inicial e final devem ser diferentes"
            currentFeedbackMessage = "Destino inicial e final não podem ser iguais."
            isValid = false
        }

        // 3. Validação de Datas
        if (state.startDate == null) {
            currentStartDateError = "Data inicial é obrigatória"
            currentFeedbackMessage = currentFeedbackMessage ?: "Informe as datas do período."
            isValid = false
        }

        if (state.endDate == null) {
            currentEndDateError = "Data final é obrigatória"
            currentFeedbackMessage = currentFeedbackMessage ?: "Informe as datas do período."
            isValid = false
        }

        // Converte os milissegundos brutos em datas locais UTC antes de comparar
        val dataInicialLocal = state.startDate?.let {
            Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
        }
        val dataFinalLocal = state.endDate?.let {
            Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
        }

        if (dataInicialLocal != null && dataFinalLocal != null && dataFinalLocal.isBefore(dataInicialLocal)) {
            currentEndDateError = "A data final deve ser maior ou igual à inicial"
            currentFeedbackMessage = "A data final não pode ser anterior à data inicial."
            isValid = false
        }

        // 4. Validação de Orçamento
        val parsedBudget = state.budget.replace(',', '.').toDoubleOrNull()
        if (state.budget.isBlank()) {
            currentBudgetError = "Orçamento é obrigatório"
            currentFeedbackMessage = currentFeedbackMessage ?: "Informe o orçamento."
            isValid = false
        } else if (parsedBudget == null || parsedBudget <= 0.0) {
            currentBudgetError = "Informe um orçamento válido"
            currentFeedbackMessage = currentFeedbackMessage ?: "Orçamento digitado é inválido."
            isValid = false
        }

        // 5. ATUALIZAÇÃO ÚNICA: Enviamos tudo de uma vez para a UI
        _uiState.update {
            it.copy(
                destinationError = currentDestinationError,
                startDateError = currentStartDateError,
                endDateError = currentEndDateError,
                budgetError = currentBudgetError,
                feedbackMessage = currentFeedbackMessage // Isso vai disparar o seu Snackbar!
            )
        }

        return isValid
    }

    private fun Long.toFormattedDate(): String {
        return Instant.ofEpochMilli(this)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
            .format(tripFormDateFormatter)
    }
}
