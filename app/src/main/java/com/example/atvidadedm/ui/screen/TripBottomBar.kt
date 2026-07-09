package com.example.atvidadedm.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.atvidadedm.ui.theme.LocalThemeVersion
import com.example.atvidadedm.ui.theme.ThemeVersion

public enum class TripBottomBarDestination {
    ROUTEIRO,
    PHOTOS
}

@Composable
fun TripBottomBar(
    selectedDestination: TripBottomBarDestination? = null,
    enableRoteiroTab: Boolean = true,
    showPhotoTab: Boolean,
    onOpenRoteiro: () -> Unit,
    onOpenPhotos: () -> Unit
) {
    val version = LocalThemeVersion.current
    
    NavigationBar(
        containerColor = if (version == ThemeVersion.VERSION_1) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
        tonalElevation = if (version == ThemeVersion.VERSION_1) 8.dp else 0.dp
    ) {
        NavigationBarItem(
            selected = selectedDestination == TripBottomBarDestination.ROUTEIRO,
            enabled = enableRoteiroTab,
            onClick = onOpenRoteiro,
            icon = {
                Icon(
                    imageVector = if (version == ThemeVersion.VERSION_1) Icons.Default.Route else Icons.Default.Map,
                    contentDescription = "Roteiro"
                )
            },
            label = { Text("Roteiro") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = if (version == ThemeVersion.VERSION_2) Color.Transparent else MaterialTheme.colorScheme.secondaryContainer
            )
        )
        NavigationBarItem(
            selected = selectedDestination == TripBottomBarDestination.PHOTOS,
            enabled = showPhotoTab,
            onClick = onOpenPhotos,
            icon = {
                Icon(
                    imageVector = if (version == ThemeVersion.VERSION_1) Icons.Default.PhotoLibrary else Icons.Default.CameraAlt,
                    contentDescription = "Fotos"
                )
            },
            label = { Text("Fotos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = if (version == ThemeVersion.VERSION_2) Color.Transparent else MaterialTheme.colorScheme.secondaryContainer
            )
        )
    }
}
