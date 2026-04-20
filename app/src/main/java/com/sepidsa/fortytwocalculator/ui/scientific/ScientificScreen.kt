package com.sepidsa.fortytwocalculator.ui.scientific

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.HorizontalDivider
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
import com.sepidsa.fortytwocalculator.ui.calculator.CalculatorUiState

@Composable
fun ScientificScreen(
    state: CalculatorUiState,
    onKeyPress: (String) -> Unit,
    onInverseToggle: (Boolean) -> Unit,
    onArcToggle: (Boolean) -> Unit,
    onAngleModeToggle: (Boolean) -> Unit,
    onConstantClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // First Row: Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ControlKey(
                label = stringResource(R.string.constant),
                onClick = onConstantClick,
                modifier = Modifier.weight(1f)
            )
            ToggleKey(
                label = "INV",
                checked = state.inverseMode,
                onCheckedChange = onInverseToggle,
                modifier = Modifier.weight(1f)
            )
            ToggleKey(
                label = "ARC",
                checked = state.arcMode,
                onCheckedChange = onArcToggle,
                modifier = Modifier.weight(1f)
            )
            ToggleKey(
                label = if (state.angleMode) "DEG" else "RAD",
                checked = state.angleMode,
                onCheckedChange = onAngleModeToggle,
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        // Second Row: Math Ops
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScientificKey(label = "√", value = "√", onKeyPress = onKeyPress, modifier = Modifier.weight(1f))
            ScientificKey(label = "^", value = "^", onKeyPress = onKeyPress, modifier = Modifier.weight(1f))
            ScientificKey(label = "x²", value = "²", onKeyPress = onKeyPress, modifier = Modifier.weight(1f))
            ScientificKey(label = "x³", value = "³", onKeyPress = onKeyPress, modifier = Modifier.weight(1f))
        }

        // Third Row: More Math
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScientificKey(label = "!", value = "!", onKeyPress = onKeyPress, modifier = Modifier.weight(1f))
            ScientificKey(
                label = if (state.inverseMode) "e" else "π",
                value = if (state.inverseMode) "e" else "π",
                onKeyPress = onKeyPress,
                modifier = Modifier.weight(1f)
            )
            ScientificKey(label = "ln", value = "ln(", onKeyPress = onKeyPress, modifier = Modifier.weight(1f))
            ScientificKey(label = "log", value = "log(", onKeyPress = onKeyPress, modifier = Modifier.weight(1f))
        }

        // Fourth Row: Trig
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val prefix = if (state.arcMode) "a" else ""
            ScientificKey(
                label = prefix + getTrigLabel(state.inverseMode, "sin", "csc"),
                value = prefix + getTrigLabel(state.inverseMode, "sin", "csc") + "(",
                onKeyPress = onKeyPress,
                modifier = Modifier.weight(1f)
            )
            ScientificKey(
                label = prefix + getTrigLabel(state.inverseMode, "cos", "sec"),
                value = prefix + getTrigLabel(state.inverseMode, "cos", "sec") + "(",
                onKeyPress = onKeyPress,
                modifier = Modifier.weight(1f)
            )
            ScientificKey(
                label = prefix + getTrigLabel(state.inverseMode, "tan", "cot"),
                value = prefix + getTrigLabel(state.inverseMode, "tan", "cot") + "(",
                onKeyPress = onKeyPress,
                modifier = Modifier.weight(1f)
            )
            ScientificKey(label = "EXP", value = "E", onKeyPress = onKeyPress, modifier = Modifier.weight(1f))
        }

        // Fifth Row: Hyperbolic + Rand
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val prefix = if (state.arcMode) "a" else ""
            ScientificKey(
                label = prefix + getTrigLabel(state.inverseMode, "sinh", "csch"),
                value = prefix + getTrigLabel(state.inverseMode, "sinh", "csch") + "(",
                onKeyPress = onKeyPress,
                modifier = Modifier.weight(1f)
            )
            ScientificKey(
                label = prefix + getTrigLabel(state.inverseMode, "cosh", "sech"),
                value = prefix + getTrigLabel(state.inverseMode, "cosh", "sech") + "(",
                onKeyPress = onKeyPress,
                modifier = Modifier.weight(1f)
            )
            ScientificKey(
                label = prefix + getTrigLabel(state.inverseMode, "tanh", "coth"),
                value = prefix + getTrigLabel(state.inverseMode, "tanh", "coth") + "(",
                onKeyPress = onKeyPress,
                modifier = Modifier.weight(1f)
            )
            ScientificKey(label = "rand", value = "rand", onKeyPress = onKeyPress, modifier = Modifier.weight(1f))
        }
    }
}

private fun getTrigLabel(inverse: Boolean, normal: String, inverted: String): String {
    return if (inverse) inverted else normal
}

@Composable
private fun ControlKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KeySurface(
        onClick = onClick,
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun ToggleKey(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    KeySurface(
        onClick = { onCheckedChange(!checked) },
        modifier = modifier,
        color = if (checked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        contentColor = if (checked) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        border = if (!checked) MaterialTheme.colorScheme.outline.copy(alpha = 0.12f) else null
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
private fun ScientificKey(
    label: String,
    value: String,
    onKeyPress: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    KeySurface(
        onClick = { onKeyPress(value) },
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun KeySurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    border: Color? = null,
    content: @Composable () -> Unit,
) {
    val shape = MaterialTheme.shapes.medium
    Surface(
        modifier = modifier
            .clip(shape)
            .clickable { onClick() }
            .then(if (border != null) Modifier.border(1.dp, border, shape) else Modifier),
        color = color,
        contentColor = contentColor,
        shape = shape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .sizeIn(minHeight = 48.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
