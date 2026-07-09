package com.example.atvidadedm.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Scaffold
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atvidadedm.TravelApplication
import com.example.atvidadedm.data.local.TripEntity
import com.example.atvidadedm.data.local.UserEntity
import com.example.atvidadedm.data.model.TripType
import com.example.atvidadedm.ui.theme.LocalThemeVersion
import com.example.atvidadedm.ui.theme.ThemeVersion
import com.example.atvidadedm.ui.viewmodel.HomeViewModel
import com.example.atvidadedm.ui.viewmodel.HomeViewModelFactory
import com.example.atvidadedm.ui.viewmodel.MapDestinationPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentUser: UserEntity,
    onOpenRoteiro: (Long) -> Unit,
    onOpenPhotos: (Long) -> Unit,
    onOpenPhotosFallback: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as TravelApplication
    val version = LocalThemeVersion.current
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            tripRepository = app.tripRepository,
            tripDestinationRepository = app.tripDestinationRepository,
            locationRepository = app.locationRepository,
            userId = currentUser.id
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    val activeTrip = uiState.activeTrip
    val isCurrentDaySelection = uiState.selectedTripId == null
    var isMapExpanded by remember { mutableStateOf(false) }
    var isTripSelectorExpanded by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.onPermissionResult(granted)
    }

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            viewModel.onPermissionResult(true)
        } else {
            viewModel.refreshTripData()
            viewModel.markPermissionRequested()
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    LaunchedEffect(Unit) {
        try {
            Configuration.getInstance().apply {
                userAgentValue = "AtvidadeDM"
                cacheMapTileCount = 500
                tileDownloadThreads = 5
            }
        } catch (_: Exception) {}
    }

    Scaffold(
        bottomBar = {
            TripBottomBar(
                selectedDestination = null,
                enableRoteiroTab = activeTrip != null,
                showPhotoTab = activeTrip != null,
                onOpenRoteiro = { activeTrip?.let { onOpenRoteiro(it.id) } ?: onOpenPhotosFallback() },
                onOpenPhotos = { activeTrip?.let { onOpenPhotos(it.id) } ?: onOpenPhotosFallback() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(if (version == ThemeVersion.VERSION_1) 24.dp else 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = if (version == ThemeVersion.VERSION_1) Alignment.CenterHorizontally else Alignment.Start
        ) {
            Text(
                text = "Olá, ${currentUser.name}!",
                style = if (version == ThemeVersion.VERSION_1) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            
            if (version == ThemeVersion.VERSION_1) {
                Text(
                    text = "Acesse suas viagens e roteiros abaixo.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            if (uiState.availableTrips.isNotEmpty()) {
                ExposedDropdownMenuBox(
                    expanded = isTripSelectorExpanded,
                    onExpandedChange = { isTripSelectorExpanded = !isTripSelectorExpanded }
                ) {
                    val selectedTripLabel = activeTrip?.let {
                        "${it.destination} (${formatTripPeriod(it.startDate, it.endDate)})"
                    } ?: "Viagem atual do dia"

                    OutlinedTextField(
                        value = selectedTripLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Selecionar viagem") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTripSelectorExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 8.dp else 24.dp)
                    )

                    DropdownMenu(
                        expanded = isTripSelectorExpanded,
                        onDismissRequest = { isTripSelectorExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Viagem atual do dia") },
                            onClick = {
                                isTripSelectorExpanded = false
                                viewModel.onTripSelectionChange(null)
                            }
                        )
                        uiState.availableTrips.forEach { trip ->
                            DropdownMenuItem(
                                text = { Text("${trip.destination} (${formatTripPeriod(trip.startDate, trip.endDate)})") },
                                onClick = {
                                    isTripSelectorExpanded = false
                                    viewModel.onTripSelectionChange(trip.id)
                                }
                            )
                        }
                    }
                }
            }

            if (activeTrip != null) {
                ActiveTripCard(activeTrip, version)
                ItineraryPreviewCard(
                    itinerary = activeTrip.itinerary,
                    version = version,
                    onOpenRoteiro = { onOpenRoteiro(activeTrip.id) }
                )
                CurrentTripMapCard(
                    trip = activeTrip,
                    currentCity = uiState.currentCity,
                    latitude = uiState.mapLatitude,
                    longitude = uiState.mapLongitude,
                    isMapLoading = uiState.isMapLoading,
                    mapLabel = uiState.mapDestinationLabel,
                    mapPoints = uiState.mapPoints,
                    tripDestinations = uiState.tripDestinations,
                    version = version,
                    onExpandMap = { isMapExpanded = true }
                )
            }
        }
    }

    if (isMapExpanded) {
        ExpandedMapDialog(
            latitude = uiState.mapLatitude,
            longitude = uiState.mapLongitude,
            destination = uiState.mapDestinationLabel ?: activeTrip?.destination ?: "Viagem atual",
            mapPoints = uiState.mapPoints,
            onDismiss = { isMapExpanded = false }
        )
    }
}

@Composable
private fun ActiveTripCard(trip: TripEntity, version: ThemeVersion) {
    val tripType = TripType.fromStorage(trip.type)
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()) }
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 12.dp else 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (version == ThemeVersion.VERSION_1) 2.dp else 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Viagem: ${trip.destination}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Período: ${formatDate(trip.startDate, dateFormatter)} - ${formatDate(trip.endDate, dateFormatter)}")
            Text("Tipo: ${tripType.label}")
            Text("Orçamento: ${currencyFormatter.format(trip.budget)}", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun ItineraryPreviewCard(itinerary: String?, version: ThemeVersion, onOpenRoteiro: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 12.dp else 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (version == ThemeVersion.VERSION_1) 2.dp else 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Roteiro", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                text = itinerary?.take(150)?.plus("...") ?: "Gere seu roteiro personalizado agora!",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = onOpenRoteiro,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 8.dp else 20.dp)
            ) {
                Text("Ver Detalhes")
            }
        }
    }
}

@Composable
private fun CurrentTripMapCard(
    trip: TripEntity?,
    currentCity: String?,
    latitude: Double?,
    longitude: Double?,
    isMapLoading: Boolean,
    mapLabel: String?,
    mapPoints: List<MapDestinationPoint>,
    tripDestinations: List<String>,
    version: ThemeVersion,
    onExpandMap: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 12.dp else 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (version == ThemeVersion.VERSION_1) 2.dp else 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Mapa de Destino", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            if (latitude != null && longitude != null) {
                Box(modifier = Modifier.height(180.dp).fillMaxWidth()) {
                    OsmTripMapView(
                        latitude = latitude,
                        longitude = longitude,
                        destination = mapLabel ?: trip?.destination ?: "Destino",
                        mapPoints = mapPoints,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Button(
                    onClick = onExpandMap,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 8.dp else 20.dp)
                ) {
                    Text("Abrir Mapa Interativo")
                }
            } else if (isMapLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

@Composable
private fun ExpandedMapDialog(
    latitude: Double?,
    longitude: Double?,
    destination: String,
    mapPoints: List<MapDestinationPoint>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (latitude != null && longitude != null) {
                OsmTripMapView(latitude = latitude, longitude = longitude, destination = destination, mapPoints = mapPoints, modifier = Modifier.fillMaxSize())
            }
            IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar")
            }
        }
    }
}

@Composable
private fun OsmTripMapView(latitude: Double, longitude: Double, destination: String, mapPoints: List<MapDestinationPoint> = emptyList(), modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember { MapView(context).apply { setTileSource(TileSourceFactory.MAPNIK); setMultiTouchControls(true); controller.setZoom(13.0) } }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) mapView.onResume()
            else if (event == Lifecycle.Event.ON_PAUSE) mapView.onPause()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer); mapView.onDetach() }
    }

    LaunchedEffect(latitude, longitude, mapPoints) {
        val geoPoint = GeoPoint(latitude, longitude)
        mapView.controller.setCenter(geoPoint)
        mapView.overlays.clear()
        if (mapPoints.isNotEmpty()) {
            mapPoints.forEach { point ->
                val marker = Marker(mapView)
                marker.position = GeoPoint(point.latitude, point.longitude)
                marker.title = point.name
                mapView.overlays.add(marker)
            }
        } else {
            val marker = Marker(mapView)
            marker.position = geoPoint
            marker.title = destination
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    AndroidView(factory = { mapView }, modifier = modifier)
}

private fun formatDate(millis: Long, formatter: DateTimeFormatter): String {
    return Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().format(formatter)
}

private fun formatTripPeriod(startMillis: Long, endMillis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return "${formatDate(startMillis, formatter)} - ${formatDate(endMillis, formatter)}"
}
