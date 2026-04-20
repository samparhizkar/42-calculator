package com.sepidsa.fortytwocalculator.ui.currency

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun CurrencyScreen(
    state: CurrencyUiState,
    onConvert: (String, String, Double) -> Unit,
    onSetAmount: (Double) -> Unit,
    onSetFromCurrency: (String) -> Unit,
    onSetToCurrency: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Currency Conversion",
            style = MaterialTheme.typography.headlineSmall
        )

        // Amount input
        OutlinedTextField(
            value = state.amount.toString(),
            onValueChange = { value ->
                value.toDoubleOrNull()?.let { onSetAmount(it) }
            },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // From currency
        CurrencySelector(
            label = "From",
            selectedCurrency = state.selectedFrom,
            onSelect = onSetFromCurrency
        )

        // To currency
        CurrencySelector(
            label = "To",
            selectedCurrency = state.selectedTo,
            onSelect = onSetToCurrency
        )

        // Convert button
        Button(
            onClick = { onConvert(state.selectedFrom, state.selectedTo, state.amount) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Convert")
        }

        // Conversion history
        if (state.conversions.isNotEmpty()) {
            Text(
                text = "History",
                style = MaterialTheme.typography.titleMedium
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.conversions) { conversion ->
                    ConversionHistoryItem(conversion)
                }
            }
        }
    }
}

@Composable
fun CurrencySelector(
    label: String,
    selectedCurrency: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencies = listOf("USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "NZD")

    Column(modifier = modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(4) { index ->
                if (index < currencies.size) {
                    FilterChip(
                        selected = selectedCurrency == currencies[index],
                        onClick = { onSelect(currencies[index]) },
                        label = { Text(currencies[index], maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(4) { index ->
                if (index + 4 < currencies.size) {
                    FilterChip(
                        selected = selectedCurrency == currencies[index + 4],
                        onClick = { onSelect(currencies[index + 4]) },
                        label = { Text(currencies[index + 4], maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ConversionHistoryItem(
    conversion: CurrencyConversion,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${conversion.amount} ${conversion.fromCurrency} = ${conversion.result} ${conversion.toCurrency}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
