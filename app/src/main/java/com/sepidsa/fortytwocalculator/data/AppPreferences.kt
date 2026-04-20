package com.sepidsa.fortytwocalculator.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Single source of truth for all app preferences.
 * Consolidates the previously scattered SharedPrefs buckets
 * (LanguagePreference, volumeState, angleMode, typography, APP)
 * into one "app_prefs" bucket, with migration from legacy keys.
 */
class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Legacy buckets — read-only, used for migration
    private val legacyLangPrefs =
        context.getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE)
    private val legacyAnglePrefs =
        context.getSharedPreferences("angleMode", Context.MODE_PRIVATE)
    private val legacyVolumePrefs =
        context.getSharedPreferences("volumeState", Context.MODE_PRIVATE)
    private val legacyTypographyPrefs =
        context.getSharedPreferences("typography", Context.MODE_PRIVATE)
    private val legacyAppPrefs =
        context.getSharedPreferences("APP", Context.MODE_PRIVATE)

    init {
        if (!prefs.getBoolean(KEY_MIGRATION_DONE, false)) {
            performMigration()
        }
    }

    // ── Language ──────────────────────────────────────────────────────────────

    /** Language code: 0 = Persian, 1 = English, 2 = French, 3 = Arabic */
    var language: Int
        get() = prefs.getInt(KEY_LANGUAGE, LANG_PERSIAN)
        set(value) = prefs.edit { putInt(KEY_LANGUAGE, value) }

    // ── Number style ──────────────────────────────────────────────────────────

    /** Number style: 0 = Western (1 2 3), 1 = Arabic-Indic (١ ٢ ٣) */
    var numberStyle: Int
        get() = prefs.getInt(KEY_NUMBER_STYLE, NUMBER_STYLE_WESTERN)
        set(value) = prefs.edit { putInt(KEY_NUMBER_STYLE, value) }

    /** Whether number style should be shown (only for Persian/Arabic languages) */
    val shouldShowNumberStyle: Boolean
        get() = language == LANG_PERSIAN || language == LANG_ARABIC

    // ── Angle mode ────────────────────────────────────────────────────────────

    /** Default angle mode: true = DEG, false = RAD */
    var isDegree: Boolean
        get() = prefs.getBoolean(KEY_IS_DEG, true)
        set(value) = prefs.edit { putBoolean(KEY_IS_DEG, value) }

    // ── Key haptics ───────────────────────────────────────────────────────────

    /** Key haptics enabled */
    var keyHapticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_KEY_HAPTICS, true)
        set(value) = prefs.edit { putBoolean(KEY_KEY_HAPTICS, value) }

    // ── History auto-clear ────────────────────────────────────────────────────

    /** Auto-clear period for history. Values: 0 = Never, 1 = 30 days, 2 = 90 days, 3 = 1 year */
    var autoClearHistory: Int
        get() = prefs.getInt(KEY_AUTO_CLEAR_HISTORY, AUTO_CLEAR_NEVER)
        set(value) = prefs.edit { putInt(KEY_AUTO_CLEAR_HISTORY, value) }

    /** Auto-clear period in days, or null for Never */
    val autoClearDays: Int?
        get() = when (autoClearHistory) {
            AUTO_CLEAR_30_DAYS -> 30
            AUTO_CLEAR_90_DAYS -> 90
            AUTO_CLEAR_1_YEAR -> 365
            else -> null
        }

    // ── App metadata ──────────────────────────────────────────────────────────

    var hasPopulatedConstantDatabase: Boolean
        get() = prefs.getBoolean(KEY_HAS_POPULATED_CONST_DB, false)
        set(value) = prefs.edit { putBoolean(KEY_HAS_POPULATED_CONST_DB, value) }

    // ── Migration ─────────────────────────────────────────────────────────────

    private fun performMigration() {
        prefs.edit {
            // Language
            putInt(KEY_LANGUAGE, legacyLangPrefs.getInt("LANGUAGE", LANG_PERSIAN))

            // Angle mode
            putBoolean(KEY_IS_DEG, legacyAnglePrefs.getBoolean("isDeg", true))

            // Constant database flag
            putBoolean(
                KEY_HAS_POPULATED_CONST_DB,
                legacyAppPrefs.getBoolean("hasPopulatedConstantDatabase", false)
            )

            // Obsolete keys that are intentionally NOT migrated:
            // - hasVolume (volumeState) → sound follows system ringer mode
            // - hasViewedTour (APP) → onboarding tour removed
            // - hasViewedGoGoldNotif (APP) → premium removed
            // - is_retro_theme_selected (THEME) → retro theme removed

            markMigrationDone()
        }
    }

    private fun SharedPreferences.Editor.markMigrationDone() {
        putBoolean(KEY_MIGRATION_DONE, true)
    }

    companion object {
        const val PREFS_NAME = "app_prefs"

        // Keys
        private const val KEY_MIGRATION_DONE = "migration_done_v2"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_NUMBER_STYLE = "number_style"
        private const val KEY_IS_DEG = "is_deg"
        private const val KEY_KEY_HAPTICS = "key_haptics"
        private const val KEY_AUTO_CLEAR_HISTORY = "auto_clear_history"
        private const val KEY_HAS_POPULATED_CONST_DB = "has_populated_constant_database"

        // Language constants
        const val LANG_PERSIAN = 0
        const val LANG_ENGLISH = 1
        const val LANG_FRENCH = 2
        const val LANG_ARABIC = 3

        // Number style constants
        const val NUMBER_STYLE_WESTERN = 0
        const val NUMBER_STYLE_ARABIC_INDIC = 1

        // Auto-clear constants
        const val AUTO_CLEAR_NEVER = 0
        const val AUTO_CLEAR_30_DAYS = 1
        const val AUTO_CLEAR_90_DAYS = 2
        const val AUTO_CLEAR_1_YEAR = 3
    }
}
