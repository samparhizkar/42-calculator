package com.sepidsa.fortytwocalculator.ui.currency

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class CurrencyUiState(
    val exchangeRates: Map<String, BigDecimal> = emptyMap(),
    val selectedFromCurrency: String = "USD",
    val selectedToCurrency: String = "EUR",
    val conversionResult: BigDecimal = BigDecimal(0),
    val inputAmount: BigDecimal = BigDecimal(1),
    val isLoading: Boolean = false,
    val error: String? = null
)

class CurrencyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    fun loadExchangeRates() {
        // TODO: Load exchange rates from API or database
    }

    fun setFromCurrency(currency: String) {
        // TODO: Update from currency
    }

    fun setToCurrency(currency: String) {
        // TODO: Update to currency
    }

    fun setInputAmount(amount: BigDecimal) {
        // TODO: Update input amount and recalculate
    }

    fun swapCurrencies() {
        // TODO: Swap from and to currencies
    }

    fun refreshRates() {
        // TODO: Refresh exchange rates
    }
}