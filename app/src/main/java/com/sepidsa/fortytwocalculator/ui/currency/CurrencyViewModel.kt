package com.sepidsa.fortytwocalculator.ui.currency

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CurrencyConversion(
    val fromCurrency: String,
    val toCurrency: String,
    val amount: Double,
    val result: Double
)

data class CurrencyUiState(
    val conversions: List<CurrencyConversion> = emptyList(),
    val selectedFrom: String = "USD",
    val selectedTo: String = "EUR",
    val amount: Double = 1.0,
    val isLoading: Boolean = false
)

class CurrencyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    fun convertCurrency(fromCurrency: String, toCurrency: String, amount: Double) {
        // TODO: Call currency conversion API or local data
        // For now, this is a placeholder
    }

    fun setFromCurrency(currency: String) {
        val current = _uiState.value
        _uiState.value = current.copy(selectedFrom = currency)
    }

    fun setToCurrency(currency: String) {
        val current = _uiState.value
        _uiState.value = current.copy(selectedTo = currency)
    }

    fun setAmount(amount: Double) {
        val current = _uiState.value
        _uiState.value = current.copy(amount = amount)
    }
}
