package com.example.atvidadedm.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atvidadedm.TravelApplication
import com.example.atvidadedm.data.model.TripType
import com.example.atvidadedm.ui.theme.LocalThemeVersion
import com.example.atvidadedm.ui.theme.ThemeVersion
import com.example.atvidadedm.ui.viewmodel.TripFormViewModel
import com.example.atvidadedm.ui.viewmodel.TripFormViewModelFactory
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTripScreen(
    currentUserId: Long,
    tripId: Long? = null,
    onBack: () -> Unit,
    onSaved: (Long?) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as TravelApplication
    val snackbarHostState = remember { SnackbarHostState() }
    val version = LocalThemeVersion.current
    
    val viewModel: TripFormViewModel = viewModel(
        factory = remember(currentUserId, tripId) {
            TripFormViewModelFactory(
                tripRepository = application.tripRepository,
                tripDestinationRepository = application.tripDestinationRepository,
                userId = currentUserId,
                tripId = tripId
            )
        }
    )
    val uiState by viewModel.uiState.collectAsState()
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val formatter = remember {
        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
    }

    LaunchedEffect(uiState.feedbackMessage) {
        uiState.feedbackMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onFeedbackMessageShown()
        }
    }

    LaunchedEffect(uiState.saveCompleted) {
        if (uiState.saveCompleted) {
            val savedTripId = uiState.savedTripId
            viewModel.onSaveHandled()
            onSaved(savedTripId)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { innerPadding ->

        if (uiState.isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (uiState.isEditMode) "Editar Viagem" else "Nova Viagem",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Preencha as informações da sua viagem",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(28.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {

                        Text(
                            text = "Destino",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        TripInputField(
                            label = "Destino Inicial",
                            value = uiState.destination,
                            onValueChange = viewModel::onDestinationChange,
                            version = version
                        )

                        TripInputField(
                            label = "Destino Final",
                            value = uiState.finalDestination,
                            onValueChange = viewModel::onFinalDestinationChange,
                            version = version
                        )

                        Text(
                            text = "Período",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.weight(1f) // O Box só cuida do tamanho agora
                            ) {
                                TripInputField(
                                    label = "Data Inicial",
                                    value = uiState.startDate?.let { formatDate(it, formatter) } ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = true,
                                    version = version,
                                    modifier = Modifier.clickable { showStartDatePicker = true } // O clique fica EXCLUSIVAMENTE aqui
                                )
                            }

                            Box(
                                modifier = Modifier.weight(1f) // O Box só cuida do tamanho agora
                            ) {
                                TripInputField(
                                    label = "Data Final",
                                    value = uiState.endDate?.let { formatDate(it, formatter) } ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = true,
                                    version = version,
                                    modifier = Modifier.clickable { showEndDatePicker = true } // O clique fica EXCLUSIVAMENTE aqui
                                )
                            }
                        }

                        Text(
                            text = "Tipo de Viagem",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            FilterChip(
                                selected = uiState.type == TripType.LAZER,
                                onClick = {
                                    viewModel.onTypeChange(TripType.LAZER)
                                },
                                label = { Text("Lazer") }
                            )

                            FilterChip(
                                selected = uiState.type == TripType.NEGOCIOS,
                                onClick = {
                                    viewModel.onTypeChange(TripType.NEGOCIOS)
                                },
                                label = { Text("Negócios") }
                            )
                        }

                        Text(
                            text = "Informações",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        TripInputField(
                            label = "Orçamento (R$)",
                            value = uiState.budget,
                            onValueChange = viewModel::onBudgetChange,
                            keyboardType = KeyboardType.Decimal,
                            version = version
                        )

                        TripInputField(
                            label = "Observações",
                            value = uiState.comments,
                            onValueChange = viewModel::onCommentsChange,
                            singleLine = false,
                            version = version
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            // 💡 Mudança aqui: usamos chaves para passar o context local para a função
                            onClick = { viewModel.saveTrip(context) },
                            enabled = !uiState.isSaving,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text(
                                if (uiState.isSaving)
                                    "Salvando..."
                                else
                                    "Confirmar Viagem"
                            )
                        }

                        TextButton(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }

    if (showStartDatePicker) {
        val datePickerState = androidx.compose.material3.rememberDatePickerState(initialSelectedDateMillis = uiState.startDate)
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let(viewModel::onStartDateSelected); showStartDatePicker = false }) { Text("OK") } }
        ) { DatePicker(state = datePickerState) }
    }
    if (showEndDatePicker) {
        val datePickerState = androidx.compose.material3.rememberDatePickerState(initialSelectedDateMillis = uiState.endDate)
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let(viewModel::onEndDateSelected); showEndDatePicker = false }) { Text("OK") } }
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
private fun TripInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    version: ThemeVersion,
    modifier: Modifier = Modifier, // Adicione essa linha
    readOnly: Boolean = false,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    if (version == ThemeVersion.VERSION_1) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = modifier.fillMaxWidth(), // Aplique o modifier aqui
            readOnly = readOnly,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = singleLine,
            shape = RoundedCornerShape(8.dp)
        )
    } else {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = singleLine,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }
}

private fun formatDate(millis: Long, formatter: DateTimeFormatter): String {
    return Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().format(formatter)
}
