package com.sepidsa.fortytwocalculator

import org.mariuszgromada.math.mxparser.Expression as MXExpression
import org.mariuszgromada.math.mxparser.mXparser
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class ExpressionEvaluator(private val isDegMode: Boolean) {

    /**
     * Sanitizes the input string and evaluates it using mXparser.
     * Returns a formatted string ready for the UI.
     */
    fun evaluateToFormattedString(input: String): String {
        val result = evaluateRaw(input)
        return formatResult(result)
    }

    /**
     * Evaluates the input string and returns the raw Double result.
     */
    fun evaluateRaw(input: String): Double {
        val sanitizedInput = sanitize(input)
        val mXExpression = MXExpression(sanitizedInput)

        if (isDegMode) {
            mXparser.setDegreesMode()
        } else {
            mXparser.setRadiansMode()
        }

        return mXExpression.calculate()
    }

    private fun sanitize(input: String): String {
        return input
            .replace("−", "-")
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", "pi")
    }

    fun formatResult(value: Double): String {
        if (value.isNaN()) return "Error"
        if (value.isInfinite()) return "∞"
        val symbols = DecimalFormatSymbols(Locale.US)
        val absValue = Math.abs(value)
        if (absValue != 0.0 && (absValue >= 1e12 || absValue < 1e-9)) {
            return String.format(Locale.US, "%.7e", value)
                .replace("e+0", "e")
                .replace("e+", "e")
        }
        return DecimalFormat("0.##########", symbols).format(value)
    }
}
