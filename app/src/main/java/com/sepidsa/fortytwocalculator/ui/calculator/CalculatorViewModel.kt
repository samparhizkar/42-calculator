package com.sepidsa.fortytwocalculator.ui.calculator

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sepidsa.fortytwocalculator.Expression
import com.sepidsa.fortytwocalculator.NumberConverterArabic
import com.sepidsa.fortytwocalculator.NumberConverterFrench
import com.sepidsa.fortytwocalculator.NumberConverterFrenchPartII
import com.sepidsa.fortytwocalculator.NumberConverterPersianPartII
import com.sepidsa.fortytwocalculator.NumberConveterAmerican
import com.sepidsa.fortytwocalculator.NumberConveterAmericanPartII
import com.sepidsa.fortytwocalculator.NumberConveterPersianPartI
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.Stack

data class CalculatorUiState(
    val expression: String = "",
    val result: String = "0",
    val rawResult: BigDecimal = BigDecimal.ZERO,
    val decimalFraction: String = "",
    val translatedResult: String = "",
    val isError: Boolean = false,
    val memory: BigDecimal = BigDecimal.ZERO,
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

    private val appContext = application.applicationContext

    private val _uiState = MutableStateFlow(
        CalculatorUiState(
            angleMode = loadAngleMode(),
            language = loadLanguage(),
        ),
    )
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<CalculatorUiEvent>()
    val uiEvents: SharedFlow<CalculatorUiEvent> = _uiEvents.asSharedFlow()

    private val _newLogEntry = MutableSharedFlow<Pair<String, String>>()
    val newLogEntry: SharedFlow<Pair<String, String>> = _newLogEntry.asSharedFlow()

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
        _uiState.value = _uiState.value.copy(angleMode = isDegree)
        updateTranslation()
    }

    fun setLanguage(language: Int) {
        _uiState.value = _uiState.value.copy(language = language)
        updateTranslation()
    }

    fun setInverseMode(inverse: Boolean) {
        _uiState.value = _uiState.value.copy(inverseMode = inverse)
    }

    fun setArcMode(arc: Boolean) {
        _uiState.value = _uiState.value.copy(arcMode = arc)
    }

    fun setClearMode(mode: ClearButtonMode) {
        _uiState.value = _uiState.value.copy(clearMode = mode)
    }

    private fun performClear() {
        expressionBuffer.clear()
        buttonsStack.clear()
        justPressedExecuteButton = true
        _uiState.value = _uiState.value.copy(
            expression = "",
            result = "0",
            rawResult = BigDecimal.ZERO,
            decimalFraction = "",
            translatedResult = "",
            isError = false,
            clearMode = ClearButtonMode.Clear,
            isCalculationPerformed = false,
            activeOperator = null,
        )
        viewModelScope.launch {
            _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Clear))
        }
    }

    private fun performMc() {
        _uiState.value = _uiState.value.copy(memory = BigDecimal.ZERO)
        viewModelScope.launch {
            _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Clear))
        }
    }

    private fun performMr() {
        addNumberToCalculation(_uiState.value.memory.toPlainString())
        viewModelScope.launch {
            _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Clear))
        }
    }

    private fun performMPlus() {
        val currentResult = currentResultDecimal()
        val newMemory = _uiState.value.memory.add(currentResult)
        buttonsStack.clear()
        buttonsStack.push(_uiState.value.result.replace(",", ""))
        justPressedExecuteButton = true
        _uiState.value = _uiState.value.copy(
            memory = newMemory,
            clearMode = resolveClearMode(),
            isCalculationPerformed = true,
        )
        viewModelScope.launch {
            _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Clear))
            _uiEvents.emit(CalculatorUiEvent.CalculationPerformed)
        }
    }

    private fun performMMinus() {
        val currentResult = currentResultDecimal()
        val newMemory = _uiState.value.memory.subtract(currentResult)
        buttonsStack.clear()
        buttonsStack.push(_uiState.value.result.replace(",", ""))
        justPressedExecuteButton = true
        _uiState.value = _uiState.value.copy(
            memory = newMemory,
            clearMode = resolveClearMode(),
            isCalculationPerformed = true,
        )
        viewModelScope.launch {
            _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Clear))
            _uiEvents.emit(CalculatorUiEvent.CalculationPerformed)
        }
    }

    private fun performBackspace() {
        if (buttonsStack.isEmpty()) {
            return
        }

        _uiState.value = _uiState.value.copy(activeOperator = null)

        val lastButton = buttonsStack.pop()
        val newLength = (expressionBuffer.length - lastButton.length).coerceAtLeast(0)
        expressionBuffer.setLength(newLength)

        if (expressionBuffer.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                expression = "",
                result = "0",
                rawResult = BigDecimal.ZERO,
                decimalFraction = "",
                translatedResult = "",
                isError = false,
                clearMode = resolveClearMode(),
            )
        } else if (buttonsStack.isNotEmpty() && isOperator(buttonsStack.peek())) {
            updateExpressionDisplay()
        } else if (calculateResult(null) != RESULT_FATAL) {
            updateResultDisplay()
        }

        _uiState.value = _uiState.value.copy(clearMode = resolveClearMode())
        updateTranslation()
        viewModelScope.launch {
            _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Backspace))
        }
    }

    private fun performEquals() {
        if (expressionBuffer.isEmpty()) {
            return
        }

        if (calculateResult(null) == RESULT_SUCCESS) {
            justPressedExecuteButton = true
            _uiState.value = _uiState.value.copy(isCalculationPerformed = true, activeOperator = null)
            updateTranslation()
            viewModelScope.launch {
                _newLogEntry.emit(_uiState.value.expression to _uiState.value.result)
                _uiEvents.emit(CalculatorUiEvent.CalculationPerformed)
                _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Execute))
            }
        }
    }

    private fun handleInput(buttonValue: String) {
        if (justPressedExecuteButton && shouldResetFor(buttonValue)) {
            expressionBuffer.clear()
            buttonsStack.clear()
        }
        justPressedExecuteButton = false

        if (preventCommonErrors(buttonValue)) {
            _uiState.value = _uiState.value.copy(isError = true)
            viewModelScope.launch {
                _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Error))
                _uiEvents.emit(CalculatorUiEvent.ErrorOccurred)
            }
            return
        }

        val isOp = isOperator(buttonValue) || isTrigonometric(buttonValue)
        if (isOp) {
            appendToExpression(buttonValue)
            buttonsStack.push(buttonValue)
            updateExpressionDisplay()
            val newActiveOp = if (buttonValue in listOf("+", "−", "×", "÷")) buttonValue else null
            _uiState.value = _uiState.value.copy(activeOperator = newActiveOp)
            viewModelScope.launch {
                _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Operator))
            }
        } else {
            val result = calculateResult(buttonValue)
            if (result != RESULT_FATAL) {
                updateResultDisplay()
                _uiState.value = _uiState.value.copy(activeOperator = null)
                viewModelScope.launch {
                    _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Numeric))
                }
            } else {
                appendToExpression(buttonValue)
                buttonsStack.push(buttonValue)
                updateExpressionDisplay()
                viewModelScope.launch {
                    _uiEvents.emit(CalculatorUiEvent.PlaySound(SoundType.Operator))
                }
            }
        }

        _uiState.value = _uiState.value.copy(
            clearMode = resolveClearMode(),
            isCalculationPerformed = false,
        )
        updateTranslation()
    }

    private fun shouldResetFor(buttonValue: String): Boolean {
        return buttonValue.firstOrNull()?.isDigit() == true ||
            buttonValue == "π" ||
            buttonValue == "e"
    }

    private fun preventCommonErrors(buttonValue: String): Boolean {
        if (expressionBuffer.isEmpty()) {
            return buttonValue in listOf("+", "÷", "×", ")", "%", "−")
        }

        if (fixSuccessiveOperators(buttonValue)) {
            return true
        }

        if (buttonValue == ".") {
            return fixDoublePoints()
        }

        return false
    }

    private fun fixSuccessiveOperators(input: String): Boolean {
        if (expressionBuffer.length <= 1) {
            return false
        }

        val lastChar = expressionBuffer.last()
        if ((lastChar == '×' || lastChar == '÷') && input == "−") {
            return false
        }

        return isOperator(input) && isOperator(lastChar.toString())
    }

    private fun fixDoublePoints(): Boolean {
        var legalStart = -1
        for (index in expressionBuffer.indices) {
            val char = expressionBuffer[index]
            if (!char.isDigit() && char != '.') {
                legalStart = index
            }
        }
        val lastDot = expressionBuffer.lastIndexOf('.')
        return lastDot > legalStart
    }

    fun addNumberToCalculation(inputString: String) {
        val currentExpression = expressionBuffer.toString()
        if (currentExpression.isNotEmpty() && isOperator(currentExpression.last().toString())) {
            expressionBuffer.append(inputString)
            val raw = Expression(expressionBuffer.toString(), _uiState.value.angleMode, appContext).evaluate() ?: BigDecimal.ZERO
            val result = formatNumber(raw)
            buttonsStack.addAll(inputString.map(Char::toString))
            _uiState.value = _uiState.value.copy(
                expression = formatExpression(expressionBuffer.toString()),
                result = result,
                rawResult = raw,
                decimalFraction = extractDecimalFraction(result),
                isError = false,
                clearMode = resolveClearMode(),
            )
        } else {
            val raw = BigDecimal(inputString.replace(",", ""))
            val formattedResult = formatNumber(raw)
            buttonsStack.clear()
            buttonsStack.addAll(formattedResult.replace(",", "").map(Char::toString))
            justPressedExecuteButton = true
            expressionBuffer.clear()
            expressionBuffer.append(formattedResult.replace(",", ""))
            _uiState.value = _uiState.value.copy(
                expression = formatExpression(expressionBuffer.toString()),
                result = formattedResult,
                rawResult = raw,
                decimalFraction = extractDecimalFraction(formattedResult),
                isError = false,
                clearMode = resolveClearMode(),
            )
        }
        updateTranslation()
    }

    private fun extractDecimalFraction(result: String): String {
        val index = result.indexOf('.')
        return if (index != -1) result.substring(index + 1) else ""
    }

    private fun appendToExpression(value: String) {
        expressionBuffer.append(value)
        _uiState.value = _uiState.value.copy(
            expression = formatExpression(expressionBuffer.toString()),
            isError = false,
        )
    }

    private fun calculateResult(input: String?): Int {
        var candidate = expressionBuffer.toString()
        if (input != null) {
            candidate += input
        }

        return try {
            val raw = Expression(candidate.replace(",", ""), _uiState.value.angleMode, appContext).evaluate() ?: BigDecimal.ZERO
            val result = formatNumber(raw)
            expressionBuffer.clear()
            expressionBuffer.append(candidate)
            if (input != null) {
                buttonsStack.push(input)
            }
            _uiState.value = _uiState.value.copy(
                expression = formatExpression(expressionBuffer.toString()),
                result = result,
                rawResult = raw,
                decimalFraction = extractDecimalFraction(result),
                isError = false,
            )
            RESULT_SUCCESS
        } catch (_: ArithmeticException) {
            handleCalculationError(candidate, input, "∞")
            RESULT_ERROR
        } catch (_: NumberFormatException) {
            handleCalculationError(candidate, input, "error")
            RESULT_ERROR
        } catch (_: Exception) {
            justPressedExecuteButton = false
            RESULT_FATAL
        }
    }

    private fun handleCalculationError(candidate: String, input: String?, result: String) {
        expressionBuffer.clear()
        expressionBuffer.append(candidate)
        if (input != null) {
            buttonsStack.push(input)
        }
        _uiState.value = _uiState.value.copy(
            expression = formatExpression(expressionBuffer.toString()),
            result = result,
            rawResult = BigDecimal.ZERO,
            decimalFraction = "",
            isError = true,
            clearMode = resolveClearMode(),
        )
    }

    private fun updateExpressionDisplay() {
        _uiState.value = _uiState.value.copy(
            expression = formatExpression(expressionBuffer.toString()),
            isError = false,
        )
    }

    private fun updateResultDisplay() {
        _uiState.value = _uiState.value.copy(
            expression = formatExpression(expressionBuffer.toString()),
            clearMode = resolveClearMode(),
        )
    }

    private fun currentResultDecimal(): BigDecimal {
        return _uiState.value.result.replace(",", "").toBigDecimalOrNull() ?: BigDecimal.ZERO
    }

    private fun evaluateResult(expression: Expression): String {
        val result = expression.evaluate() ?: return "0"
        return formatNumber(result)
    }

    private fun formatNumber(value: BigDecimal): String {
        val decimalFormat = DecimalFormat().apply {
            isGroupingUsed = true
            groupingSize = 3
            maximumFractionDigits = 6
            decimalFormatSymbols = DecimalFormatSymbols(Locale.US)
        }
        return decimalFormat.format(value).replace("^-(?=0(.0*)?$)".toRegex(), "")
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

    private fun loadAngleMode(): Boolean {
        return appContext.getSharedPreferences("angleMode", Context.MODE_PRIVATE)
            .getBoolean("isDeg", true)
    }

    private fun updateTranslation() {
        val state = _uiState.value
        if (state.isCalculationPerformed) {
            val integerFraction = state.rawResult.setScale(0, BigDecimal.ROUND_DOWN).abs().toPlainString()
            val resultIsNegative = state.rawResult.compareTo(BigDecimal("-0.0000009")) < 0
            val decimalFraction = state.decimalFraction

            val translation = when (state.language) {
                LANGUAGE_ENGLISH -> {
                    var text = (if (resultIsNegative) "minus " else "") + NumberConveterAmerican.convert(integerFraction)
                    if (decimalFraction.isNotEmpty()) {
                        text += NumberConveterAmericanPartII.convert(decimalFraction)
                    }
                    text
                }
                LANGUAGE_FRENCH -> {
                    var text = (if (resultIsNegative) "moins " else "") + NumberConverterFrench.convert(integerFraction)
                    if (decimalFraction.isNotEmpty()) {
                        text += NumberConverterFrenchPartII.convert(decimalFraction)
                    }
                    text
                }
                LANGUAGE_ARABIC -> {
                    val arabic = NumberConverterArabic(state.rawResult.abs())
                    (if (resultIsNegative) "ناقص " else "") + arabic.convertToArabic()
                }
                LANGUAGE_PERSIAN -> {
                    var text = NumberConveterPersianPartI().convert(integerFraction)
                    if (decimalFraction.isNotEmpty()) {
                        val partII = NumberConverterPersianPartII.convert(decimalFraction)
                        text += if (state.rawResult.abs().compareTo(BigDecimal.ONE) >= 0) " ممیز $partII" else partII
                    }
                    (if (resultIsNegative) "منفی " else "") + text
                }
                else -> ""
            }
            _uiState.value = state.copy(translatedResult = translation)
        } else {
            _uiState.value = state.copy(translatedResult = state.expression)
        }
    }

    private fun loadLanguage(): Int {
        return appContext.getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE)
            .getInt("LANGUAGE", 0)
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
