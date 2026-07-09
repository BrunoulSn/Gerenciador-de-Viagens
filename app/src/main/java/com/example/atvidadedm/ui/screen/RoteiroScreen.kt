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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.atvidadedm.ui.viewmodel.RoteiroViewModel
import com.example.atvidadedm.ui.viewmodel.RoteiroViewModelFactory
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoteiroScreen(
    currentUserId: Long,
    tripId: Long,
    onOpenRoteiro: (Long) -> Unit,
    onOpenPhotos: (Long) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as TravelApplication
    val version = LocalThemeVersion.current
    
    val viewModel: RoteiroViewModel = viewModel(
        factory = remember(currentUserId, tripId) {
            RoteiroViewModelFactory(
                tripRepository = application.tripRepository,
                tripDestinationRepository = application.tripDestinationRepository,
                geminiRepository = application.geminiRepository,
                userId = currentUserId,
                tripId = tripId.takeIf { it > 0 }
            )
        }
    )
    val uiState by viewModel.uiState.collectAsState()
    val formatter = remember {
        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            TripBottomBar(
                selectedDestination = TripBottomBarDestination.ROUTEIRO,
                showPhotoTab = true,
                onOpenRoteiro = { onOpenRoteiro(tripId) },
                onOpenPhotos = { onOpenPhotos(tripId) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (version == ThemeVersion.VERSION_1) "Roteiro da Viagem" else "MEU ROTEIRO",
                style = if (version == ThemeVersion.VERSION_1) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = if (version == ThemeVersion.VERSION_2) 2.sp else 0.sp
            )

            if (uiState.isLoadingTrip) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            // Card de Dados
            Card(
                shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 12.dp else 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = if (version == ThemeVersion.VERSION_1) 2.dp else 6.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(if (version == ThemeVersion.VERSION_1) 16.dp else 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Informações Base",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    RoteiroInputField(label = "Origem", value = uiState.initialDestination, version = version)
                    RoteiroInputField(label = "Destino", value = uiState.finalDestination, version = version)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = uiState.type == TripType.LAZER,
                            onClick = { viewModel.onTypeChange(TripType.LAZER) },
                            label = { Text("Lazer") },
                            shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 8.dp else 16.dp)
                        )
                        FilterChip(
                            selected = uiState.type == TripType.NEGOCIOS,
                            onClick = { viewModel.onTypeChange(TripType.NEGOCIOS) },
                            label = { Text("Negócios") },
                            shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 8.dp else 16.dp)
                        )
                    }

                    RoteiroInputField(label = "Interesses", value = uiState.interests, onValueChange = viewModel::onInterestsChange, version = version)
                    RoteiroInputField(label = "Estilo", value = uiState.travelStyle, onValueChange = viewModel::onTravelStyleChange, version = version)

                    Button(
                        onClick = viewModel::generateItinerary,
                        enabled = !uiState.isGenerating,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 8.dp else 25.dp)
                    ) {
                        Text(if (uiState.isGenerating) "Criando Magia..." else "Gerar com IA")
                    }
                }
            }

            // Card de Resultado
            Card(
                shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 12.dp else 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (version == ThemeVersion.VERSION_1) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (version == ThemeVersion.VERSION_1) 2.dp else 0.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Roteiro Sugerido",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    if (!uiState.itinerary.isNullOrBlank()) {
                        SelectionContainer {
                            Text(
                                text = uiState.itinerary.orEmpty(),
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 22.sp
                            )
                        }
                    } else {
                        Text(
                            text = "Seu roteiro aparecerá aqui após a geração.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun RoteiroInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    version: ThemeVersion
) {
    if (version == ThemeVersion.VERSION_1) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )
    } else {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
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
