package com.sepidsa.fortytwocalculator

import android.icu.text.RuleBasedNumberFormat
import java.util.Locale

object NumberToWordsConverter {
    /**
     * Converts a number string to its word representation in the specified locale.
     * Uses Android's native ICU library for grammatically correct translations.
     *
     * @param number The number string (supports decimals and large values).
     * @param localeCode ISO language code (e.g., "en", "fr", "ar", "fa").
     * @return The spellout string, or an empty string if input is invalid.
     */
    fun convert(number: String, localeCode: String): String {
        if (number.isBlank()) return ""

        val locale = Locale.forLanguageTag(localeCode)
        val formatter = try {
            RuleBasedNumberFormat(locale, RuleBasedNumberFormat.SPELLOUT)
        } catch (e: Exception) {
            // Fallback if the specific locale or rule set is not supported
            return ""
        }

        return try {
            val cleanNumber = number.trim()
            // ICU RuleBasedNumberFormat handles both Long and Double.
            // For extreme precision with integers, try Long if possible.
            if (!cleanNumber.contains(".") && cleanNumber.length <= 18) {
                val longValue = cleanNumber.toLong()
                formatter.format(longValue)
            } else {
                val doubleValue = cleanNumber.toDouble()
                formatter.format(doubleValue)
            }
        } catch (e: Exception) {
            ""
        }
    }
}
