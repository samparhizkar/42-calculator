package com.sepidsa.fortytwocalculator.ui.calculator

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sepidsa.fortytwocalculator.ExpressionEvaluator
import com.sepidsa.fortytwocalculator.NumberToWordsConverter
import com.sepidsa.fortytwocalculator.data.SettingsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Stack
import kotlin.math.abs

data class CalculatorUiState(
    val expression: String = "",
    val result: String = "0",
    val rawResult: Double = 0.0,
    val decimalFraction: String = "",
    val translatedResult: String = "",
    val isError: Boolean = false,
    val memory: Double = 0.0,
    val angleMode: Boolean = true,
    val clearMode: ClearButtonMode = ClearButtonMode.Clear,
    val inverseMode: Boolean = false,
    val arcMode: Boolean = false,
    val isCalculationPerformed: Boolean = false,
    val language: Int = 0,
    val activeOperator: String? = null,
)

enum class ClearButtonMode {
    Clear,
    Backspace,
}

sealed class CalculatorUiEvent {
    object CalculationPerformed : CalculatorUiEvent()
    data class PlaySound(val soundType: SoundType) : CalculatorUiEvent()
    object ErrorOccurred : CalculatorUiEvent()
}

enum class SoundType {
    Numeric,
    Operator,
    Execute,
    Clear,
    Backspace,
    Error
}

class CalculatorViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(
        CalculatorUiState(
            angleMode = settingsRepository.isDegree,
            language = settingsRepository.language,
        ),
    )
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<CalculatorUiEvent>()
    val uiEvents: SharedFlow<CalculatorUiEvent> = _uiEvents.asSharedFlow()

    private val _newLogEntry = MutableSharedFlow<Triple<String, String, String>>()
    val newLogEntry: SharedFlow<Triple<String, String, String>> = _newLogEntry.asSharedFlow()

    private val expressionBuffer = StringBuilder()
    private val buttonsStack = Stack<String>()
    private var justPressedExecuteButton = true

    fun onButtonPressed(buttonValue: String) {
        when (buttonValue) {
            "C" -> performClear()
            "MC" -> performMc()
            "MR" -> performMr()
            "M+" -> performMPlus()
            "M-" -> performMMinus()
            "⌫" -> performBackspace()
            "=" -> performEquals()
            else -> handleInput(buttonValue)
        }
    }

    fun setAngleMode(isDegree: Boolean) {
        settingsRepository.isDegree = isDegree
        _uiState.update { it.copy(angleMode = isDegree) }
        updateTranslation()
    }

    fun setLanguage(language: Int) {
        settingsRepository.language = language
        _uiState.update { it.copy(language = language) }
        updateTranslation()
    }

    fun setInverseMode(inverse: Boolean) {
        _uiState.update { it.copy(inverseMode = inverse) }
    }

    fun setArcMode(arc: Boolean) {
        _uiState.update { it.copy(arcMode = arc) }
    }

    fun setClearMode(mode: ClearButtonMode) {
        _uiState.update { it.copy(clearMode = mode) }
    }

    private fun performClear() {
        expressionBuffer.clear()
        buttonsStack.clear()
        justPressedExecuteButton = true
        _uiState.update {
            it.copy(
                expression = "",
                result = "0",
                rawResult = 0.0,
                decimalFraction = "",
                translatedResult = "",
                isError = false,
                clearMode = ClearButtonMode.Clear,
                isCalculationPerformed = false,
                activeOperator = null,
            )
        }
        viewModelScope.launch {
            _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Clear))
        }
    }

    private fun performMc() {
        _uiState.update { it.copy(memory = 0.0) }
        viewModelScope.launch {
            _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Clear))
        }
    }

    private fun performMr() {
        addNumberToCalculation(_uiState.value.memory.toString())
        viewModelScope.launch {
            _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Clear))
        }
    }

    private fun performMPlus() {
        val currentResult = currentResultDouble()
        val newMemory = _uiState.value.memory + currentResult
        _uiState.update {
            it.copy(
                memory = newMemory,
                isCalculationPerformed = true,
            )
        }
        viewModelScope.launch {
            _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Operator))
        }
    }

    private fun performMMinus() {
        val currentResult = currentResultDouble()
        val newMemory = _uiState.value.memory - currentResult
        _uiState.update {
            it.copy(
                memory = newMemory,
                isCalculationPerformed = true,
            )
        }
        viewModelScope.launch {
            _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Operator))
        }
    }

    private fun performBackspace() {
        if (buttonsStack.isEmpty()) return
        buttonsStack.pop()
        expressionBuffer.clear()
        buttonsStack.forEach { expressionBuffer.append(it) }

        if (expressionBuffer.isEmpty()) {
            performClear()
        } else {
            calculateResult(null)
            _uiState.update { it.copy(isCalculationPerformed = false) }
        }
        viewModelScope.launch {
            _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Backspace))
        }
    }

    private fun performEquals() {
        if (expressionBuffer.isEmpty()) return
        val finalExpression = expressionBuffer.toString()
        val resultStatus = calculateResult(null)
        if (resultStatus == RESULT_SUCCESS) {
            val state = _uiState.value
            _uiState.update { it.copy(isCalculationPerformed = true) }
            updateTranslation()
            viewModelScope.launch {
                _newLogEntry.emit(Triple(finalExpression, state.result, state.translatedResult))
                _uiEvents.emit(CalculatorUiEvent.CalculationPerformed)
                _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Execute))
            }
            justPressedExecuteButton = true
        } else {
            viewModelScope.launch {
                _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Error))
            }
        }
    }

    private fun handleInput(input: String) {
        if (shouldResetFor(input)) {
            expressionBuffer.clear()
            buttonsStack.clear()
        }
        justPressedExecuteButton = false

        if (preventCommonErrors(input)) return
        if (fixSuccessiveOperators(input)) return
        if (input == "." && fixDoublePoints()) return

        val resultStatus = calculateResult(input)
        if (resultStatus != RESULT_FATAL) {
            val sound = if (isOperator(input)) SoundType.Operator else SoundType.Numeric
            viewModelScope.launch {
                _uiEvents.emit(CalculatorUiEvent.PlaySound(sound))
            }
        }
    }

    private fun shouldResetFor(input: String): Boolean {
        return justPressedExecuteButton && !isOperator(input)
    }

    private fun preventCommonErrors(input: String): Boolean {
        if (expressionBuffer.isEmpty() && (input == "×" || input == "÷" || input == "%" || input == "!" || input == "^")) {
            return true
        }
        return false
    }

    private fun fixSuccessiveOperators(input: String): Boolean {
        if (expressionBuffer.isNotEmpty() && isOperator(input) && isOperator(buttonsStack.peek())) {
            if (!isTrigonometric(input) && !isTrigonometric(buttonsStack.peek())) {
                buttonsStack.pop()
                buttonsStack.push(input)
                expressionBuffer.setLength(expressionBuffer.length - 1)
                expressionBuffer.append(input)
                updateExpressionDisplay()
                return true
            }
        }
        return false
    }

    private fun fixDoublePoints(): Boolean {
        val lastPart = expressionBuffer.split(Regex("[+−×÷]")).last()
        return lastPart.contains(".")
    }

    fun addNumberToCalculation(number: String) {
        val cleanNumber = number.replace(",", "")
        if (justPressedExecuteButton) {
            expressionBuffer.clear()
            buttonsStack.clear()
            justPressedExecuteButton = false
        }
        appendToExpression(cleanNumber)
        calculateResult(null)
    }

    private fun extractDecimalFraction(value: String): String {
        val parts = value.split(".")
        return if (parts.size > 1) "." + parts[1] else ""
    }

    private fun appendToExpression(value: String) {
        expressionBuffer.append(value)
        buttonsStack.push(value)
        updateExpressionDisplay()
    }

    private fun calculateResult(input: String?): Int {
        var candidate = expressionBuffer.toString()
        if (input != null) {
            candidate += input
        }

        return try {
            val evaluator = ExpressionEvaluator(_uiState.value.angleMode)
            val raw = evaluator.evaluateRaw(candidate.replace(",", ""))
            
            val result = evaluator.formatResult(raw)
            expressionBuffer.clear()
            expressionBuffer.append(candidate)
            if (input != null) {
                buttonsStack.push(input)
            }
            _uiState.update {
                it.copy(
                    expression = formatExpression(expressionBuffer.toString()),
                    result = result,
                    rawResult = raw,
                    decimalFraction = extractDecimalFraction(result),
                    isError = false,
                )
            }
            RESULT_SUCCESS
        } catch (_: Exception) {
            if (input != null) {
                expressionBuffer.append(input)
                buttonsStack.push(input)
                updateExpressionDisplay()
            }
            RESULT_FATAL
        }
    }

    private fun updateExpressionDisplay() {
        _uiState.update {
            it.copy(
                expression = formatExpression(expressionBuffer.toString()),
                isError = false,
            )
        }
    }

    private fun currentResultDouble(): Double {
        return _uiState.value.result.replace(",", "").toDoubleOrNull() ?: 0.0
    }

    private fun formatExpression(value: String): String {
        return value.replace(
            "(?<!\\.\\d{0,6})\\d+?(?=(?:\\d{3})+(?:\\D|$))".toRegex(),
            "$0,",
        )
    }

    private fun resolveClearMode(): ClearButtonMode {
        return if (buttonsStack.size <= 1) {
            ClearButtonMode.Clear
        } else {
            ClearButtonMode.Backspace
        }
    }

    private fun isOperator(value: String): Boolean {
        return value in listOf("+", "−", "×", "÷", "(", ")", "%", "^", "√", "!")
    }

    private fun isTrigonometric(value: String): Boolean {
        return value.endsWith("(")
    }

    private fun updateTranslation() {
        val state = _uiState.value
        if (!state.isCalculationPerformed) {
            _uiState.update { it.copy(translatedResult = state.expression) }
            return
        }

        val (localeCode, negativePrefix) = when (state.language) {
            LANGUAGE_ENGLISH -> "en" to "minus "
            LANGUAGE_FRENCH -> "fr" to "moins "
            LANGUAGE_ARABIC -> "ar" to "ناقص "
            LANGUAGE_PERSIAN -> "fa" to "منفی "
            else -> null to ""
        }

        val translation = localeCode?.let { locale ->
            val isNegative = state.rawResult < -0.0000009
            val absoluteResult = abs(state.rawResult).toBigDecimal().toPlainString()
            val words = NumberToWordsConverter.convert(absoluteResult, locale)
            
            if (isNegative && words.isNotEmpty()) "$negativePrefix$words" else words
        } ?: ""

        _uiState.update { it.copy(translatedResult = translation) }
    }

    private companion object {
        const val LANGUAGE_PERSIAN = 0
        const val LANGUAGE_ENGLISH = 1
        const val LANGUAGE_FRENCH = 2
        const val LANGUAGE_ARABIC = 3

        const val RESULT_SUCCESS = 0
        const val RESULT_ERROR = 1
        const val RESULT_FATAL = 2
    }
}
