package com.example.atvidadedm.ui.screen

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.produceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atvidadedm.TravelApplication
import com.example.atvidadedm.data.PhotoCaptureTarget
import com.example.atvidadedm.ui.theme.LocalThemeVersion
import com.example.atvidadedm.ui.theme.ThemeVersion
import com.example.atvidadedm.ui.viewmodel.TripPhotosViewModel
import com.example.atvidadedm.ui.viewmodel.TripPhotosViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun TripPhotosScreen(
    tripId: Long,
    onOpenRoteiro: (Long) -> Unit,
    onOpenPhotos: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as TravelApplication
    val version = LocalThemeVersion.current
    val viewModel: TripPhotosViewModel = viewModel(
        factory = remember(tripId) {
            TripPhotosViewModelFactory(
                tripRepository = application.tripRepository,
                tripPhotoRepository = application.tripPhotoRepository,
                tripId = tripId
            )
        }
    )
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingCameraTarget by remember { mutableStateOf<PhotoCaptureTarget?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let(viewModel::addGalleryPhoto)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val pendingTarget = pendingCameraTarget
        if (success && pendingTarget != null) {
            viewModel.addCapturedPhoto(pendingTarget.uri)
        } else if (pendingTarget != null) {
            viewModel.discardCameraTarget(pendingTarget)
        }
        pendingCameraTarget = null
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onMessageShown()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            TripBottomBar(
                selectedDestination = TripBottomBarDestination.PHOTOS,
                showPhotoTab = true,
                onOpenRoteiro = { onOpenRoteiro(tripId) },
                onOpenPhotos = onOpenPhotos
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            uiState.trip == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.message ?: "Ops! Não conseguimos localizar esta viagem.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            else -> {
                val trip = uiState.trip ?: return@Scaffold
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(if (version == ThemeVersion.VERSION_1) 16.dp else 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (version == ThemeVersion.VERSION_2) {
                        Text(
                            text = "Minhas Memórias",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground,
                            letterSpacing = (-0.5).sp
                        )
                    }

                    // Card Header muito mais limpo e direto ao ponto
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 16.dp else 28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (version == ThemeVersion.VERSION_1)
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            else
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (version == ThemeVersion.VERSION_1) "Álbum de Memórias" else trip.destination,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (version == ThemeVersion.VERSION_1) {
                                Text(
                                    text = trip.destination,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "Registre cada segundo da sua jornada.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Botões de Ação Modernizados (Estilo Pill e Tonal)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier
                                .weight(1f)
                                .height(if (version == ThemeVersion.VERSION_1) 50.dp else 56.dp),
                            shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 12.dp else 28.dp),
                            enabled = !uiState.isSaving
                        ) {
                            Icon(Icons.Rounded.Collections, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.size(8.dp))
                            Text("Abrir Galeria", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                val target = viewModel.createCameraCaptureTarget()
                                pendingCameraTarget = target
                                cameraLauncher.launch(target.uri)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(if (version == ThemeVersion.VERSION_1) 50.dp else 56.dp),
                            shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 12.dp else 28.dp),
                            enabled = !uiState.isSaving,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Rounded.AddAPhoto, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.size(8.dp))
                            Text("Nova Foto", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Estado vazio mais polido e sutil
                    if (uiState.photos.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sua galeria está vazia.\nQue tal adicionar a primeira foto?",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(32.dp)
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(if (version == ThemeVersion.VERSION_1) 2 else 3),
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            verticalArrangement = Arrangement.spacedBy(if (version == ThemeVersion.VERSION_1) 12.dp else 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(if (version == ThemeVersion.VERSION_1) 12.dp else 8.dp)
                        ) {
                            items(
                                items = uiState.photos,
                                key = { it.id }
                            ) { photo ->
                                PhotoGridItem(photoUri = photo.photoUri, version = version)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoGridItem(photoUri: String, version: ThemeVersion) {
    val imageBitmap by rememberPhotoBitmap(photoUri)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 16.dp else 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (version == ThemeVersion.VERSION_1) 3.dp else 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap!!,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (version == ThemeVersion.VERSION_1) 160.dp else 130.dp)
                    .clip(RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 16.dp else 8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (version == ThemeVersion.VERSION_1) 160.dp else 130.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            }
        }
    }
}

@Composable
private fun rememberPhotoBitmap(photoUri: String): androidx.compose.runtime.State<ImageBitmap?> {
    val context = LocalContext.current
    return produceState<ImageBitmap?>(initialValue = null, key1 = photoUri) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(photoUri.toUri())?.use { input ->
                    BitmapFactory.decodeStream(input)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }
}