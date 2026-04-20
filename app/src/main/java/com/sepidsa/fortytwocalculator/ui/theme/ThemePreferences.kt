package com.sepidsa.fortytwocalculator.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

/**
 * Typed SharedPreferences wrapper for theme settings.
 * Handles migration from legacy color storage to seed-based theming.
 */
class ThemePreferences(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Legacy keys for migration
    private companion object {
        const val PREFS_NAME = "theme_prefs"

        // New keys
        const val KEY_SEED_PRESET = "seed_preset"
        const val KEY_CUSTOM_SEED_COLOR = "custom_seed_color"
        const val KEY_APPEARANCE_MODE = "appearance_mode"
        const val KEY_DYNAMIC_COLOR = "dynamic_color"
        const val KEY_KEY_COLOR_OVERRIDE = "key_color_override"
        const val KEY_MIGRATION_DONE = "migration_done"

        // Legacy keys (for migration)
        const val LEGACY_THEME_PREFS = "THEME"
        const val LEGACY_ACCENT_COLOR = "ACCENT_COLOR_CODE"
        const val LEGACY_KEYPAD_COLOR = "KEYPAD_BACKGROUND_COLOR_CODE"
        const val LEGACY_CLASSIC_THEME = "IS_CLASSIC_THEME"

        // Default colors
        const val DEFAULT_TEAL = 0xFF1ABC9C.toInt()
        const val DEFAULT_DARK_TEAL = 0xFF009688.toInt()
    }

    init {
        if (!prefs.getBoolean(KEY_MIGRATION_DONE, false)) {
            performMigration()
        }
    }

    /**
     * Seed preset selection - the main theme color
     */
    var seedPreset: SeedPreset
        get() {
            val name = prefs.getString(KEY_SEED_PRESET, SeedPreset.TEAL.name)
            return try {
                SeedPreset.valueOf(name!!)
            } catch (_: Exception) {
                SeedPreset.TEAL
            }
        }
        set(value) {
            prefs.edit { putString(KEY_SEED_PRESET, value.name) }
        }

    /**
     * Custom seed color value (used when seedPreset is CUSTOM)
     */
    var customSeedColor: Int
        get() = prefs.getInt(KEY_CUSTOM_SEED_COLOR, DEFAULT_TEAL)
        set(value) {
            prefs.edit { putInt(KEY_CUSTOM_SEED_COLOR, value) }
        }

    /**
     * Appearance mode - Light, Dark, or System
     */
    var appearanceMode: AppearanceMode
        get() {
            val name = prefs.getString(KEY_APPEARANCE_MODE, AppearanceMode.SYSTEM.name)
            return try {
                AppearanceMode.valueOf(name!!)
            } catch (_: Exception) {
                AppearanceMode.SYSTEM
            }
        }
        set(value) {
            prefs.edit { putString(KEY_APPEARANCE_MODE, value.name) }
            applyAppearanceMode(value)
        }

    /**
     * Dynamic color enabled (Android 12+ only)
     */
    var isDynamicColorEnabled: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_COLOR, false)
        set(value) {
            prefs.edit { putBoolean(KEY_DYNAMIC_COLOR, value) }
        }

    /**
     * Key color override for paid feature (null if not set)
     * This overrides colorSecondaryContainer for operator/digit keys
     */
    var keyColorOverride: Int?
        get() {
            val color = prefs.getInt(KEY_KEY_COLOR_OVERRIDE, -1)
            return if (color == -1) null else color
        }
        set(value) {
            if (value == null) {
                prefs.edit { remove(KEY_KEY_COLOR_OVERRIDE) }
            } else {
                prefs.edit { putInt(KEY_KEY_COLOR_OVERRIDE, value) }
            }
        }

    /**
     * Apply the appearance mode to the app
     */
    fun applyAppearanceMode(mode: AppearanceMode = appearanceMode) {
        val nightMode = when (mode) {
            AppearanceMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            AppearanceMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            AppearanceMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    /**
     * Get the effective seed color based on current settings
     */
    fun getEffectiveSeedColor(): Int {
        return when (seedPreset) {
            SeedPreset.TEAL -> SeedPreset.TEAL.seedColor
            SeedPreset.BLUE -> SeedPreset.BLUE.seedColor
            SeedPreset.GREEN -> SeedPreset.GREEN.seedColor
            SeedPreset.PURPLE -> SeedPreset.PURPLE.seedColor
            SeedPreset.ORANGE -> SeedPreset.ORANGE.seedColor
            SeedPreset.PINK -> SeedPreset.PINK.seedColor
            SeedPreset.CUSTOM -> customSeedColor
        }
    }

    /**
     * Reset all theme settings to defaults
     */
    fun resetToDefaults() {
        prefs.edit {
            putString(KEY_SEED_PRESET, SeedPreset.TEAL.name)
            putInt(KEY_CUSTOM_SEED_COLOR, DEFAULT_TEAL)
            putString(KEY_APPEARANCE_MODE, AppearanceMode.SYSTEM.name)
            putBoolean(KEY_DYNAMIC_COLOR, false)
            remove(KEY_KEY_COLOR_OVERRIDE)
        }
        applyAppearanceMode(AppearanceMode.SYSTEM)
    }

    /**
     * Migrate from legacy color storage to seed-based theming
     */
    private fun performMigration() {
        val legacyPrefs = context.getSharedPreferences(LEGACY_THEME_PREFS, Context.MODE_PRIVATE)

        // Migrate accent color to nearest preset
        val legacyAccent = legacyPrefs.getInt(LEGACY_ACCENT_COLOR, DEFAULT_DARK_TEAL)
        val mappedPreset = mapLegacyColorToPreset(legacyAccent)
        seedPreset = mappedPreset

        // If not a standard preset, store as custom
        if (mappedPreset == SeedPreset.CUSTOM) {
            customSeedColor = legacyAccent
        }

        // Migrate keypad color to key color override
        val legacyKeypadColor = legacyPrefs.getInt(LEGACY_KEYPAD_COLOR, android.graphics.Color.WHITE)
        if (legacyKeypadColor != android.graphics.Color.WHITE) {
            keyColorOverride = legacyKeypadColor
        }

        // Mark migration as done
        prefs.edit {
            putBoolean(KEY_MIGRATION_DONE, true)
        }
    }

    /**
     * Map a legacy accent color to the nearest preset
     */
    private fun mapLegacyColorToPreset(color: Int): SeedPreset {
        // Normalize the color (remove alpha)
        val rgb = color and 0xFFFFFF

        // Check each preset's seed color with some tolerance
        return when {
            isColorNear(rgb, SeedPreset.TEAL.seedColor) -> SeedPreset.TEAL
            isColorNear(rgb, 0x009688) -> SeedPreset.TEAL // Legacy dark teal maps to teal
            isColorNear(rgb, 0x004D40) -> SeedPreset.TEAL // Legacy darker teal maps to teal
            isColorNear(rgb, SeedPreset.BLUE.seedColor) -> SeedPreset.BLUE
            isColorNear(rgb, 0x29B6F6) -> SeedPreset.BLUE // Light blue
            isColorNear(rgb, 0x01579B) -> SeedPreset.BLUE // Dark blue
            isColorNear(rgb, SeedPreset.GREEN.seedColor) -> SeedPreset.GREEN
            isColorNear(rgb, SeedPreset.PURPLE.seedColor) -> SeedPreset.PURPLE
            isColorNear(rgb, 0x7C4DFF) -> SeedPreset.PURPLE // Deep purple
            isColorNear(rgb, 0xBA68C8) -> SeedPreset.PURPLE // Light purple
            isColorNear(rgb, SeedPreset.ORANGE.seedColor) -> SeedPreset.ORANGE
            isColorNear(rgb, 0xFF6E40) -> SeedPreset.ORANGE // Deep orange
            isColorNear(rgb, 0xFFC107) -> SeedPreset.ORANGE // Amber
            isColorNear(rgb, SeedPreset.PINK.seedColor) -> SeedPreset.PINK
            isColorNear(rgb, 0xF06292) -> SeedPreset.PINK // Pink
            isColorNear(rgb, 0xD81B60) -> SeedPreset.PINK // Dark pink
            isColorNear(rgb, 0xC51162) -> SeedPreset.PINK // Pink accent
            else -> SeedPreset.CUSTOM
        }
    }

    /**
     * Check if two colors are "near" each other (within hue tolerance)
     */
    private fun isColorNear(color1: Int, color2: Int, tolerance: Float = 30f): Boolean {
        val hsv1 = FloatArray(3)
        val hsv2 = FloatArray(3)

        android.graphics.Color.colorToHSV(color1 or 0xFF000000.toInt(), hsv1)
        android.graphics.Color.colorToHSV(color2 or 0xFF000000.toInt(), hsv2)

        // Compare hue (0-360)
        val hueDiff = kotlin.math.abs(hsv1[0] - hsv2[0])
        val hueDistance = kotlin.math.min(hueDiff, 360f - hueDiff)

        return hueDistance < tolerance
    }
}

/**
 * Preset seed colors for theme selection
 */
enum class SeedPreset(val seedColor: Int) {
    TEAL(0xFF1ABC9C.toInt()),
    BLUE(0xFF2563EB.toInt()),
    GREEN(0xFF16A34A.toInt()),
    PURPLE(0xFF7C3AED.toInt()),
    ORANGE(0xFFEA580C.toInt()),
    PINK(0xFFDB2777.toInt()),
    CUSTOM(0xFF1ABC9C.toInt()) // Default to teal, actual value stored separately
}

/**
 * Appearance mode options
 */
enum class AppearanceMode {
    LIGHT,
    DARK,
    SYSTEM
}
