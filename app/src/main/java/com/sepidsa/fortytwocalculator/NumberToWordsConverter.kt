package com.sepidsa.fortytwocalculator

import java.util.Locale

object NumberToWordsConverter {
    fun convert(number: String, localeCode: String): String {
        if (number.isBlank()) return ""
        return try {
            val locale = Locale.forLanguageTag(localeCode)
            val cls = Class.forName("android.icu.text.RuleBasedNumberFormat")
            val spelloutConst = cls.getField("SPELLOUT").getInt(null)
            val formatter = cls.getConstructor(Locale::class.java, Int::class.java)
                .newInstance(locale, spelloutConst)
            val formatMethod = cls.getMethod("format", Any::class.java)
            val cleanNumber = number.trim()
            if (!cleanNumber.contains(".") && cleanNumber.length <= 18) {
                formatMethod.invoke(formatter, cleanNumber.toLong()) as? String ?: ""
            } else {
                formatMethod.invoke(formatter, cleanNumber.toDouble()) as? String ?: ""
            }
        } catch (_: Exception) {
            ""
        }
    }
}
