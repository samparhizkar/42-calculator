package com.sepidsa.fortytwocalculator.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightScheme = lightColorScheme(
    primary = VoidLightPrimary,
    onPrimary = VoidLightOnPrimary,
    primaryContainer = VoidLightPrimaryContainer,
    onPrimaryContainer = VoidLightOnPrimaryContainer,
    secondary = VoidLightSecondary,
    onSecondary = VoidLightOnSecondary,
    secondaryContainer = VoidLightSecondaryContainer,
    onSecondaryContainer = VoidLightOnSecondaryContainer,
    background = VoidLightBackground,
    onBackground = VoidLightOnBackground,
    surface = VoidLightSurface,
    onSurface = VoidLightOnSurface,
    surfaceVariant = VoidLightSurfaceVariant,
    onSurfaceVariant = VoidLightOnSurfaceVariant,
    outline = VoidLightOutline,
    error = VoidLightError,
    onError = VoidLightOnError,
)

private val DarkScheme = darkColorScheme(
    primary = VoidDarkPrimary,
    onPrimary = VoidDarkOnPrimary,
    primaryContainer = VoidDarkPrimaryContainer,
    onPrimaryContainer = VoidDarkOnPrimaryContainer,
    secondary = VoidDarkSecondary,
    onSecondary = VoidDarkOnSecondary,
    secondaryContainer = VoidDarkSecondaryContainer,
    onSecondaryContainer = VoidDarkOnSecondaryContainer,
    background = VoidDarkBackground,
    onBackground = VoidDarkOnBackground,
    surface = VoidDarkSurface,
    onSurface = VoidDarkOnSurface,
    surfaceVariant = VoidDarkSurfaceVariant,
    onSurfaceVariant = VoidDarkOnSurfaceVariant,
    outline = VoidDarkOutline,
    error = VoidDarkError,
    onError = VoidDarkOnError,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) {
                dynamicDarkColorScheme(LocalContext.current)
            } else {
                dynamicLightColorScheme(LocalContext.current)
            }
        }

        darkTheme -> DarkScheme
        else -> LightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
