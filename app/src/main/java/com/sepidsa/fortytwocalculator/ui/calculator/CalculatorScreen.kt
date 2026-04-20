package com.sepidsa.fortytwocalculator.ui.calculator

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sepidsa.fortytwocalculator.R

private data class CalculatorKey(
    val label: String,
    val value: String,
    val type: KeyType,
    val onLongPressValue: String? = null,
)

private enum class KeyType {
    Digit,
    Operator,
    Primary,
    Danger,
}

@Composable
fun CalculatorScreen(
    state: CalculatorUiState,
    onKeyPress: (String) -> Unit,
    onAngleModeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val clearLabel = if (state.clearMode == ClearButtonMode.Clear) {
        stringResource(R.string.clear)
    } else {
        "\u232b"
    }

    val rows = listOf(
        listOf(
            CalculatorKey(
                label = clearLabel,
                value = if (state.clearMode == ClearButtonMode.Clear) "C" else "⌫",
                type = KeyType.Danger,
                onLongPressValue = "C",
            ),
            CalculatorKey(label = stringResource(R.string.parenthesis_open), value = "(", type = KeyType.Operator),
            CalculatorKey(label = stringResource(R.string.parenthesis_closed), value = ")", type = KeyType.Operator),
            CalculatorKey(label = stringResource(R.string.devide), value = "÷", type = KeyType.Operator, onLongPressValue = "MC"),
        ),
        listOf(
            CalculatorKey(label = "7", value = "7", type = KeyType.Digit),
            CalculatorKey(label = "8", value = "8", type = KeyType.Digit),
            CalculatorKey(label = "9", value = "9", type = KeyType.Digit),
            CalculatorKey(label = stringResource(R.string.multiply), value = "×", type = KeyType.Operator, onLongPressValue = "MC"),
        ),
        listOf(
            CalculatorKey(label = "4", value = "4", type = KeyType.Digit),
            CalculatorKey(label = "5", value = "5", type = KeyType.Digit),
            CalculatorKey(label = "6", value = "6", type = KeyType.Digit),
            CalculatorKey(label = stringResource(R.string.subtract), value = "−", type = KeyType.Operator, onLongPressValue = "M-"),
        ),
        listOf(
            CalculatorKey(label = "1", value = "1", type = KeyType.Digit),
            CalculatorKey(label = "2", value = "2", type = KeyType.Digit),
            CalculatorKey(label = "3", value = "3", type = KeyType.Digit),
            CalculatorKey(label = stringResource(R.string.add), value = "+", type = KeyType.Operator, onLongPressValue = "M+"),
        ),
        listOf(
            CalculatorKey(label = stringResource(R.string.point), value = ".", type = KeyType.Digit),
            CalculatorKey(label = "0", value = "0", type = KeyType.Digit),
            CalculatorKey(label = stringResource(R.string.percentage), value = "%", type = KeyType.Operator),
            CalculatorKey(label = stringResource(R.string.equals), value = "=", type = KeyType.Primary, onLongPressValue = "MR"),
        ),
    )

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                FilterChip(
                    selected = state.angleMode,
                    onClick = { onAngleModeChanged(!state.angleMode) },
                    label = {
                        Text(if (state.angleMode) "DEG" else "RAD")
                    },
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rows.forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        row.forEach { key ->
                            CalculatorKeyButton(
                                key = key,
                                isError = state.isError && key.type == KeyType.Danger,
                                onPress = { onKeyPress(key.value) },
                                onLongPress = key.onLongPressValue?.let { longPressValue ->
                                    { onKeyPress(longPressValue) }
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculatorKeyButton(
    key: CalculatorKey,
    isError: Boolean,
    onPress: () -> Unit,
    onLongPress: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.extraLarge
    val (containerColor, contentColor, borderColor) = when (key.type) {
        KeyType.Primary -> Triple(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary,
            Color.Transparent,
        )
        KeyType.Operator -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            Color.Transparent,
        )
        KeyType.Danger -> Triple(
            MaterialTheme.colorScheme.surface,
            if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline.copy(alpha = 0.32f),
        )
        KeyType.Digit -> Triple(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.onSurface,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.22f),
        )
    }

    Surface(
        modifier = modifier
            .clip(shape)
            .sizeIn(minHeight = 64.dp)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .combinedClickable(
                onClick = onPress,
                onLongClick = onLongPress,
            ),
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .heightIn(min = 56.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = key.label,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
