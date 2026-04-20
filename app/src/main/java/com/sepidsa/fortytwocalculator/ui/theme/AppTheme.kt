package com.sepidsa.fortytwocalculator.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Local composition for key color override (paid feature)
val LocalKeyColorOverride = staticCompositionLocalOf<Color?> { null }

/**
 * Main theme composable that reads from ThemePreferences.
 * Supports seed-based theming, dynamic color, and appearance modes.
 */
@Composable
fun AppTheme(
    themePreferences: ThemePreferences? = null,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val prefs = remember { themePreferences ?: ThemePreferences(context) }

    // Determine if dark theme should be used
    val darkTheme = when (prefs.appearanceMode) {
        AppearanceMode.LIGHT -> false
        AppearanceMode.DARK -> true
        AppearanceMode.SYSTEM -> isSystemInDarkTheme()
    }

    // Get the color scheme based on preferences
    val colorScheme = rememberColorScheme(prefs, darkTheme, context)

    // Get key color override if set
    val keyColorOverride = prefs.keyColorOverride?.let { Color(it) }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
    ) {
        CompositionLocalProvider(
            LocalKeyColorOverride provides keyColorOverride
        ) {
            content()
        }
    }
}

/**
 * Preview theme that allows specifying exact parameters for the preview card
 */
@Composable
fun PreviewTheme(
    seedPreset: SeedPreset = SeedPreset.TEAL,
    customSeedColor: Int? = null,
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    keyColorOverride: Int? = null,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    val colorScheme = remember(seedPreset, customSeedColor, darkTheme, dynamicColor) {
        getColorSchemeForPreview(
            context = context,
            seedPreset = seedPreset,
            customSeedColor = customSeedColor,
            darkTheme = darkTheme,
            dynamicColor = dynamicColor
        )
    }

    val keyColor = keyColorOverride?.let { Color(it) }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
    ) {
        CompositionLocalProvider(
            LocalKeyColorOverride provides keyColor
        ) {
            content()
        }
    }
}

@Composable
private fun rememberColorScheme(
    prefs: ThemePreferences,
    darkTheme: Boolean,
    context: Context
): ColorScheme {
    return remember(
        prefs.seedPreset,
        prefs.customSeedColor,
        prefs.isDynamicColorEnabled,
        darkTheme
    ) {
        when {
            prefs.isDynamicColorEnabled -> {
                if (darkTheme) {
                    dynamicDarkColorScheme(context)
                } else {
                    dynamicLightColorScheme(context)
                }
            }

            prefs.seedPreset == SeedPreset.CUSTOM -> {
                // For custom, fall back to teal preset
                // TODO: Generate custom scheme from seed color using material-color-utilities
                if (darkTheme) darkColorSchemeFor(SeedPreset.TEAL)
                else lightColorSchemeFor(SeedPreset.TEAL)
            }

            else -> {
                if (darkTheme) darkColorSchemeFor(prefs.seedPreset)
                else lightColorSchemeFor(prefs.seedPreset)
            }
        }
    }
}

private fun getColorSchemeForPreview(
    context: Context,
    seedPreset: SeedPreset,
    customSeedColor: Int?,
    darkTheme: Boolean,
    dynamicColor: Boolean,
): ColorScheme {
    return when {
        dynamicColor -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        seedPreset == SeedPreset.CUSTOM -> {
            // For custom, fall back to teal preset
            if (darkTheme) darkColorSchemeFor(SeedPreset.TEAL)
            else lightColorSchemeFor(SeedPreset.TEAL)
        }

        else -> {
            if (darkTheme) darkColorSchemeFor(seedPreset)
            else lightColorSchemeFor(seedPreset)
        }
    }
}

/**
 * Get the brand color for the current theme
 */
@Composable
fun getBrandColor(): Color {
    // Brand color is always the teal seed
    return VoidBrand
}

/**
 * Get the appropriate key background color based on role and theme
 */
@Composable
fun getKeyBackgroundColor(isOperator: Boolean = false, isEquals: Boolean = false): Color {
    val keyOverride = LocalKeyColorOverride.current

    return when {
        isEquals -> VoidBrand
        keyOverride != null -> keyOverride
        isOperator -> VoidKeyOpBg
        else -> VoidKeyDigitBg
    }
}
