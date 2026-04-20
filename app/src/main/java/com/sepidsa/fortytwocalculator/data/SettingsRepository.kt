package com.sepidsa.fortytwocalculator.data

import android.content.Context
import android.content.SharedPreferences

class SettingsRepository(private val context: Context) {

    private val anglePrefs: SharedPreferences = context.getSharedPreferences("angleMode", Context.MODE_PRIVATE)
    private val langPrefs: SharedPreferences = context.getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE)
    private val themePrefs: SharedPreferences = context.getSharedPreferences("THEME", Context.MODE_PRIVATE)
    private val volumePrefs: SharedPreferences = context.getSharedPreferences("volumeState", Context.MODE_PRIVATE)
    private val typographyPrefs: SharedPreferences = context.getSharedPreferences("typography", Context.MODE_PRIVATE)
    private val appPrefs: SharedPreferences = context.getSharedPreferences("APP", Context.MODE_PRIVATE)

    var isDegree: Boolean
        get() = anglePrefs.getBoolean("isDeg", true)
        set(value) = anglePrefs.edit().putBoolean("isDeg", value).apply()

    var language: Int
        get() = langPrefs.getInt("LANGUAGE", 0)
        set(value) = langPrefs.edit().putInt("LANGUAGE", value).apply()

    var accentColor: Int
        get() = themePrefs.getInt("ACCENT_COLOR_CODE", android.graphics.Color.parseColor("#009688"))
        set(value) = themePrefs.edit().putInt("ACCENT_COLOR_CODE", value).apply()

    var keypadBackgroundColor: Int
        get() = themePrefs.getInt("KEYPAD_BACKGROUND_COLOR_CODE", android.graphics.Color.WHITE)
        set(value) = themePrefs.edit().putInt("KEYPAD_BACKGROUND_COLOR_CODE", value).apply()

    var isClassicTheme: Boolean
        get() = themePrefs.getBoolean("CLASSIC_THEME", false)
        set(value) = themePrefs.edit().putBoolean("CLASSIC_THEME", value).apply()

    var dialpadFont: Int
        get() = typographyPrefs.getInt("DIALPAD_FONT", 0)
        set(value) = typographyPrefs.edit().putInt("DIALPAD_FONT", value).apply()

    var hasPopulatedConstantDatabase: Boolean
        get() = appPrefs.getBoolean("hasPopulatedConstantDatabase", false)
        set(value) = appPrefs.edit().putBoolean("hasPopulatedConstantDatabase", value).apply()
}
