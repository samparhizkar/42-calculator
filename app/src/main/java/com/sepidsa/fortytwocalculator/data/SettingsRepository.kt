package com.sepidsa.fortytwocalculator.data

import android.content.Context
import android.content.SharedPreferences

class SettingsRepository(private val context: Context) {

    private val anglePrefs: SharedPreferences = context.getSharedPreferences("angleMode", Context.MODE_PRIVATE)
    private val langPrefs: SharedPreferences = context.getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE)
    private val volumePrefs: SharedPreferences = context.getSharedPreferences("volumeState", Context.MODE_PRIVATE)
    private val typographyPrefs: SharedPreferences = context.getSharedPreferences("typography", Context.MODE_PRIVATE)
    private val appPrefs: SharedPreferences = context.getSharedPreferences("APP", Context.MODE_PRIVATE)

    var isDegree: Boolean
        get() = anglePrefs.getBoolean("isDeg", true)
        set(value) = anglePrefs.edit().putBoolean("isDeg", value).apply()

    var language: Int
        get() = langPrefs.getInt("LANGUAGE", 0)
        set(value) = langPrefs.edit().putInt("LANGUAGE", value).apply()

    var hasPopulatedConstantDatabase: Boolean
        get() = appPrefs.getBoolean("hasPopulatedConstantDatabase", false)
        set(value) = appPrefs.edit().putBoolean("hasPopulatedConstantDatabase", value).apply()
}
