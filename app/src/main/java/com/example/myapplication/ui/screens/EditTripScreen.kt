package com.example.myapplication.ui.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.Trip
import com.example.myapplication.viewmodel.CreateTripViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RememberReturnType")
@Composable
fun EditTripScreen(
    trip: Trip,
    onTripUpdated: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: CreateTripViewModel = viewModel(factory = CreateTripViewModelFactory(LocalContext.current))
) {
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    var expandedType by remember { mutableStateOf(false) }

    // Initialize form with trip data
    remember {
        viewModel.updateDestination(trip.destination)
        viewModel.updateType(trip.type)
        viewModel.updateStartDate(trip.startDate)
        viewModel.updateEndDate(trip.endDate)
        viewModel.updateBudget(trip.budget.toString())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Editar Viagem",
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (uiState.errorMessage.isNotEmpty()) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        OutlinedTextField(
            value = uiState.destination,
            onValueChange = { viewModel.updateDestination(it) },
            label = { Text("Destino") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true,
            isError = uiState.fieldErrors.containsKey("destination"),
            enabled = !uiState.isLoading
        )

        ExposedDropdownMenuBox(
            expanded = expandedType,
            onExpandedChange = { expandedType = !expandedType },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = uiState.type,
                onValueChange = {},
                label = { Text("Tipo") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                enabled = !uiState.isLoading
            )
            DropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                DropdownMenuItem(
                    text = { Text("Lazer") },
                    onClick = {
                        viewModel.updateType("Lazer")
                        expandedType = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Negócios") },
                    onClick = {
                        viewModel.updateType("Negócios")
                        expandedType = false
                    }
                )
            }
        }

        OutlinedTextField(
            value = if (uiState.startDate == 0L) "" else SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(uiState.startDate)),
            onValueChange = {},
            label = { Text("Data de Início") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            readOnly = true,
            enabled = !uiState.isLoading
        )
        TextButton(
            onClick = {
                val calendar = Calendar.getInstance()
                if (uiState.startDate > 0) {
                    calendar.timeInMillis = uiState.startDate
                }
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        viewModel.updateStartDate(calendar.timeInMillis)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text("Selecionar Data de Início")
        }

        OutlinedTextField(
            value = if (uiState.endDate == 0L) "" else SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(uiState.endDate)),
            onValueChange = {},
            label = { Text("Data de Fim") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            readOnly = true,
            enabled = !uiState.isLoading
        )
        TextButton(
            onClick = {
                val calendar = Calendar.getInstance()
                if (uiState.endDate > 0) {
                    calendar.timeInMillis = uiState.endDate
                }
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        viewModel.updateEndDate(calendar.timeInMillis)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text("Selecionar Data de Fim")
        }

        OutlinedTextField(
            value = uiState.budget,
            onValueChange = { viewModel.updateBudget(it) },
            label = { Text("Orçamento (R\$)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            isError = uiState.fieldErrors.containsKey("budget"),
            enabled = !uiState.isLoading
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (viewModel.validateTrip()) {
                    val updatedTrip = trip.copy(
                        destination = uiState.destination,
                        type = uiState.type,
                        startDate = uiState.startDate,
                        endDate = uiState.endDate,
                        budget = uiState.budget.toDouble()
                    )
                    // TODO: Call update method
                    onTripUpdated()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.height(24.dp))
            } else {
                Text("Atualizar Viagem")
            }
        }

        TextButton(onClick = onBackClick, modifier = Modifier.padding(top = 12.dp), enabled = !uiState.isLoading) {
            Text("Cancelar")
        }
    }
}

