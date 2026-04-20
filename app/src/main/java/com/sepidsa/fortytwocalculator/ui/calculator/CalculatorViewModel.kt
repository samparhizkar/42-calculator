package com.sepidsa.fortytwocalculator.ui.calculator

import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.sepidsa.fortytwocalculator.Expression
import com.sepidsa.fortytwocalculator.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.Stack

data class CalculatorUiState(
    val expression: String = "",
    val result: String = "0",
    val isError: Boolean = false,
    val memory: BigDecimal = BigDecimal(0),
    val angleMode: Boolean = false
)

class CalculatorViewModel : ViewModel() {

    private val _newLogEntry = MutableSharedFlow<Pair<String, String>>()
    val newLogEntry: SharedFlow<Pair<String, String>> = _newLogEntry.asSharedFlow()

    private val expressionBuffer = StringBuilder()
    private val buttonsStack = Stack<String>()
    private var justPressedExecuteButton = true
    private var rawResult = BigDecimal(0)

    fun onButtonPressed(buttonValue: String) {
        when (buttonValue) {
            "C" -> performClear()
            "MC" -> performMC()
            "MR" -> performMR()
            "M+" -> performMPlus()
            "M-" -> performMMinus()
            "⌫" -> performBackspace()
            "=" -> performEquals()
            else -> handleInput(buttonValue)
        }
    }

    private fun performClear() {
        expressionBuffer.clear()
        buttonsStack.clear()
        justPressedExecuteButton = true
        _uiState.value = _uiState.value.copy(
            expression = "",
            result = "0",
            isError = false
        )
    }

    private fun performMC() {
        _uiState.value = _uiState.value.copy(memory = BigDecimal(0))
    }

    private fun performMR() {
        addNumberToCalculation(_uiState.value.memory.toPlainString())
    }

    private fun performMPlus() {
        val currentResult = BigDecimal(_uiState.value.result.replace(",", ""))
        val newMemory = _uiState.value.memory.add(currentResult)
        _uiState.value = _uiState.value.copy(memory = newMemory)
        buttonsStack.clear()
        buttonsStack.push(_uiState.value.result)
        justPressedExecuteButton = true
    }

    private fun performMMinus() {
        val currentResult = BigDecimal(_uiState.value.result.replace(",", ""))
        val newMemory = _uiState.value.memory.subtract(currentResult)
        _uiState.value = _uiState.value.copy(memory = newMemory)
        buttonsStack.clear()
        buttonsStack.push(_uiState.value.result)
        justPressedExecuteButton = true
    }

    private fun performBackspace() {
        if (buttonsStack.isNotEmpty()) {
            val lastButton = buttonsStack.pop()
            expressionBuffer.setLength(expressionBuffer.length - lastButton.length)

            if (buttonsStack.isNotEmpty() && isOperator(buttonsStack.peek())) {
                updateExpressionDisplay()
            } else {
                calculateResult(null)
                updateResultDisplay()
            }

            _uiState.value = _uiState.value.copy(expression = expressionBuffer.toString())
        }
    }

    private fun performEquals() {
        if (expressionBuffer.isNotEmpty()) {
            if (calculateResult(null) == 0.toByte()) {
                justPressedExecuteButton = true
                // TODO: Send log message
            }
        }
    }

    private fun handleInput(buttonValue: String) {
        if (justPressedExecuteButton) {
            if (buttonValue[0].isDigit() ||
                buttonValue == "π" ||
                buttonValue == "e"
            ) {
                expressionBuffer.clear()
                buttonsStack.clear()
            }
        }
        justPressedExecuteButton = false

        if (preventCommonErrors(buttonValue)) {
            // TODO: Play error sound and animate
            return
        }

        if (isOperator(buttonValue)) {
            appendToExpression(buttonValue)
            buttonsStack.push(buttonValue)
            updateExpressionDisplay()
        } else {
            if (calculateResult(buttonValue) != 2.toByte()) {
                updateResultDisplay()
            }
        }
    }

    private fun preventCommonErrors(buttonValue: String): Boolean {
        if (expressionBuffer.isEmpty()) {
            return when (buttonValue) {
                "+", "÷", "×", ")" -> true
                else -> false
            }
        }

        if (fixSuccessiveOperators(buttonValue)) return true
        if (buttonValue == ".") return fixDoublePoints()

        return false
    }

    private fun fixSuccessiveOperators(input: String): Boolean {
        if (expressionBuffer.length > 1) {
            val lastChar = expressionBuffer.last()
            if (lastChar == '×' || lastChar == '÷') {
                if (input == "−") return false
            }
            if (isOperator(input) && isOperator(lastChar.toString())) {
                return true
            }
        }
        return false
    }

    private fun fixDoublePoints(): Boolean {
        var legalStart = -1
        for (i in expressionBuffer.indices) {
            val char = expressionBuffer[i]
            if (!char.isDigit() && char != '.') {
                legalStart = i
            }
        }
        val lastDot = expressionBuffer.lastIndexOf('.')
        return lastDot > legalStart
    }

    private fun isOperator(value: String): Boolean {
        return value in listOf("+", "-", "×", "÷", "(", ")", "%")
    }

    private fun addNumberToCalculation(inputString: String) {
        val currentExpression = expressionBuffer.toString()
        if (currentExpression.isNotEmpty() && isOperator(currentExpression.last().toString())) {
            expressionBuffer.append(inputString)
            val expression = Expression(expressionBuffer.toString(), _uiState.value.angleMode, context)
            val result = evaluateResult(expression)
            rawResult = BigDecimal(result.replace(",", ""))
            _uiState.value = _uiState.value.copy(result = result)
            for (char in inputString) {
                buttonsStack.push(char.toString())
            }
            updateResultDisplay()
        } else {
            val df = DecimalFormat()
            df.isGroupingUsed = true
            df.groupingSize = 3
            df.maximumFractionDigits = 6
            df.decimalFormatSymbols = DecimalFormatSymbols(Locale.US)
            val inputDecimal = BigDecimal(inputString.replace(",", ""))
            val formattedResult = df.format(inputDecimal).replace("^-(?=0(.0*)?$)".toRegex(), "")
            rawResult = BigDecimal(formattedResult.replace(",", ""))
            buttonsStack.clear()
            for (char in formattedResult.replace(",", "")) {
                buttonsStack.push(char.toString())
            }
            justPressedExecuteButton = true
            expressionBuffer.clear()
            expressionBuffer.append(rawResult.toString())
            _uiState.value = _uiState.value.copy(result = formattedResult)
        }
    }

    private fun appendToExpression(value: String) {
        expressionBuffer.append(value)
        _uiState.value = _uiState.value.copy(expression = expressionBuffer.toString())
    }

    private fun calculateResult(input: String?): Byte {
        var testSubject = expressionBuffer.toString()
        if (input != null) {
            testSubject += input
        }
        return try {
            val withoutCommas = testSubject.replace(",", "")
            val expression = Expression(withoutCommas, _uiState.value.angleMode, context)
            val result = evaluateResult(expression)
            expressionBuffer.clear()
            expressionBuffer.append(testSubject)
            if (input != null) {
                buttonsStack.push(input)
            }
            _uiState.value = _uiState.value.copy(result = result, isError = false)
            // Emit log entry
            viewModelScope.launch {
                _newLogEntry.emit(Pair(testSubject, result))
            }
            0
        } catch (e: ArithmeticException) {
            expressionBuffer.clear()
            expressionBuffer.append(testSubject)
            if (input != null) {
                buttonsStack.push(input)
            }
            _uiState.value = _uiState.value.copy(result = "∞", isError = true)
            1
        } catch (e: NumberFormatException) {
            expressionBuffer.clear()
            expressionBuffer.append(testSubject)
            if (input != null) {
                buttonsStack.push(input)
            }
            _uiState.value = _uiState.value.copy(result = "error", isError = true)
            1
        } catch (e: Exception) {
            e.printStackTrace()
            justPressedExecuteButton = false
            2
        }
    }

    private fun evaluateResult(expression: Expression): String {
        val result = expression.evaluate()
        if (result != null) {
            val df = DecimalFormat()
            df.isGroupingUsed = true
            df.groupingSize = 3
            df.maximumFractionDigits = 6
            df.decimalFormatSymbols = DecimalFormatSymbols(Locale.US)
            return df.format(result)
        }
        return "0"
    }

    private fun updateExpressionDisplay() {
        val formatted = expressionBuffer.toString().replace(
            "(?<!\\.\\d{0,6})\\d+?(?=(?:\\d{3})+(?:\\D|$))".toRegex(),
            "$0,"
        )
        _uiState.value = _uiState.value.copy(expression = formatted)
    }

    private fun updateResultDisplay() {
        // Update the result display if needed
    }

    private fun isOperator(value: String): Boolean {
        return value in listOf("+", "-", "×", "÷", "(", ")", "%")
    }

    fun setAngleMode(isDegree: Boolean) {
        _uiState.value = _uiState.value.copy(angleMode = isDegree)
    }
}