package com.example.atvidadedm.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// --- VERSÃO 1: OCEAN BLUE ---
private val V1_DarkColorScheme = darkColorScheme(
    primary = V1_Dark_Primary,
    secondary = V1_Dark_Secondary,
    tertiary = V1_Dark_Tertiary,
    background = V1_Dark_Background,
    surface = V1_Dark_Surface,
    onPrimary = Color.White
)

private val V1_LightColorScheme = lightColorScheme(
    primary = V1_Primary,
    secondary = V1_Secondary,
    tertiary = V1_Tertiary,
    background = V1_Background,
    surface = V1_Surface,
    onPrimary = V1_OnPrimary
)

// --- VERSÃO 2: SUNSET & LAVENDER ---
private val V2_DarkColorScheme = darkColorScheme(
    primary = V2_Dark_Primary,
    secondary = V2_Dark_Secondary,
    tertiary = V2_Dark_Tertiary,
    background = V2_Dark_Background,
    surface = V2_Dark_Surface,
    onPrimary = Color.Black
)

private val V2_LightColorScheme = lightColorScheme(
    primary = V2_Primary,
    secondary = V2_Secondary,
    tertiary = V2_Tertiary,
    background = V2_Background,
    surface = V2_Surface,
    onPrimary = V2_OnPrimary
)

enum class ThemeVersion { VERSION_1, VERSION_2 }

val LocalThemeVersion = staticCompositionLocalOf { ThemeVersion.VERSION_1 }

@Composable
fun AtvidadeDMTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    version: ThemeVersion = ThemeVersion.VERSION_1,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        version == ThemeVersion.VERSION_1 -> {
            if (darkTheme) V1_DarkColorScheme else V1_LightColorScheme
        }
        else -> {
            if (darkTheme) V2_DarkColorScheme else V2_LightColorScheme
        }
    }

    CompositionLocalProvider(LocalThemeVersion provides version) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
