package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.CurrentUser
import com.example.myapplication.data.model.Trip
import com.example.myapplication.viewmodel.MyTripsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyTripsScreen(
    onBackClick: () -> Unit,
    onEditTrip: (Trip) -> Unit,
    viewModel: MyTripsViewModel = viewModel(factory = MyTripsViewModelFactory(LocalContext.current))
) {
    val uiState = viewModel.uiState.collectAsState().value
    var selectedTripForEdit by remember { mutableStateOf<Trip?>(null) }

    LaunchedEffect(Unit) {
        CurrentUser.userId?.let { userId ->
            viewModel.loadTrips(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Minhas Viagens",
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 16.dp),
            fontWeight = FontWeight.Bold
        )

        if (uiState.errorMessage.isNotEmpty()) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.trips.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhuma viagem cadastrada", fontSize = 16.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(uiState.trips) { trip ->
                    TripItem(
                        trip = trip,
                        onDelete = { viewModel.deleteTrip(trip) },
                        onEdit = { selectedTripForEdit = trip; onEditTrip(trip) }
                    )
                }
            }
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onBackClick, modifier = Modifier.fillMaxWidth()) {
            Text("Voltar")
        }
    }
}

@Composable
fun TripItem(trip: Trip, onDelete: () -> Unit, onEdit: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable() { }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trip.destination,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tipo: ${trip.type}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "${dateFormat.format(Date(trip.startDate))} até ${dateFormat.format(Date(trip.endDate))}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Orçamento: R\$ ${String.format("%.2f", trip.budget)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Deletar viagem", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun MyTripsViewModelFactory(context: android.content.Context) = object : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return MyTripsViewModel(context) as T
    }
}

