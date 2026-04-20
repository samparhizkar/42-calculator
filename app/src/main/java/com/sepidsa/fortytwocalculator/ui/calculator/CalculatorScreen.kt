package com.sepidsa.fortytwocalculator.ui.calculator

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sepidsa.fortytwocalculator.R
import com.sepidsa.fortytwocalculator.ui.theme.DmMono
import com.sepidsa.fortytwocalculator.ui.theme.VoidKeyActiveOpBg
import com.sepidsa.fortytwocalculator.ui.theme.VoidKeyActiveOpBorder
import com.sepidsa.fortytwocalculator.ui.theme.VoidKeyActiveOpText
import com.sepidsa.fortytwocalculator.ui.theme.VoidKeyDigitBg
import com.sepidsa.fortytwocalculator.ui.theme.VoidKeyDigitBorder
import com.sepidsa.fortytwocalculator.ui.theme.VoidKeyEqBg
import com.sepidsa.fortytwocalculator.ui.theme.VoidKeyErrorBg
import com.sepidsa.fortytwocalculator.ui.theme.VoidKeyErrorBorder
import com.sepidsa.fortytwocalculator.ui.theme.VoidKeyErrorText
import com.sepidsa.fortytwocalculator.ui.theme.VoidKeyOpBg
import com.sepidsa.fortytwocalculator.ui.theme.VoidKeyOpBorder
import com.sepidsa.fortytwocalculator.ui.theme.VoidLightKeyActiveOpBg
import com.sepidsa.fortytwocalculator.ui.theme.VoidLightKeyActiveOpBorder
import com.sepidsa.fortytwocalculator.ui.theme.VoidLightKeyActiveOpText
import com.sepidsa.fortytwocalculator.ui.theme.VoidLightKeyDigitBg
import com.sepidsa.fortytwocalculator.ui.theme.VoidLightKeyDigitBorder
import com.sepidsa.fortytwocalculator.ui.theme.VoidLightKeyErrorBg
import com.sepidsa.fortytwocalculator.ui.theme.VoidLightKeyErrorBorder
import com.sepidsa.fortytwocalculator.ui.theme.VoidLightKeyErrorText
import com.sepidsa.fortytwocalculator.ui.theme.VoidLightKeyOpBg
import com.sepidsa.fortytwocalculator.ui.theme.VoidLightKeyOpBorder
import com.sepidsa.fortytwocalculator.ui.theme.VoidOnBrand

private data class CalculatorKey(
    val label: String,
    val value: String,
    val type: KeyType,
    val onLongPressValue: String? = null,
)

private enum class KeyType {
    Digit,
    Operator,
    Equal,
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
    val clearValue = if (state.clearMode == ClearButtonMode.Clear) "C" else "⌫"

    val rows = listOf(
        listOf(
            CalculatorKey(label = clearLabel, value = clearValue, type = KeyType.Danger, onLongPressValue = "C"),
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
            CalculatorKey(label = stringResource(R.string.equals), value = "=", type = KeyType.Equal, onLongPressValue = "MR"),
        ),
    )

    val isDark = isSystemInDarkTheme()

    Box(modifier = modifier.fillMaxSize()) {
        // Corner ambient glow — teal lamp emanating from = key corner, always present
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.BottomEnd)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            VoidKeyEqBg.copy(alpha = 0.18f),
                            Color.Transparent,
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
                .padding(top = 4.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            rows.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    row.forEach { key ->
                        VoidKeyButton(
                            key = key,
                            isActiveOperator = state.activeOperator == key.value,
                            isDark = isDark,
                            onSurface = MaterialTheme.colorScheme.onSurface,
                            onPress = { onKeyPress(key.value) },
                            onLongPress = key.onLongPressValue?.let { lv -> { onKeyPress(lv) } },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

private data class VoidKeyColors(
    val bg: Color,
    val border: Color,
    val text: Color,
    val ripple: Color,
)

private fun resolveKeyColors(
    type: KeyType,
    isActive: Boolean,
    isDark: Boolean,
    onSurface: Color,
): VoidKeyColors = if (isDark) {
    when {
        type == KeyType.Equal -> VoidKeyColors(
            bg = VoidKeyEqBg,
            border = Color.Transparent,
            text = VoidOnBrand,
            ripple = Color.White.copy(alpha = 0.35f),
        )
        isActive -> VoidKeyColors(
            bg = VoidKeyActiveOpBg,
            border = VoidKeyActiveOpBorder,
            text = VoidKeyActiveOpText,
            ripple = VoidKeyActiveOpText.copy(alpha = 0.25f),
        )
        type == KeyType.Operator -> VoidKeyColors(
            bg = VoidKeyOpBg,
            border = VoidKeyOpBorder,
            text = onSurface,
            ripple = Color.White.copy(alpha = 0.22f),
        )
        type == KeyType.Danger -> VoidKeyColors(
            bg = VoidKeyErrorBg,
            border = VoidKeyErrorBorder,
            text = VoidKeyErrorText,
            ripple = Color(0xFFFF6464).copy(alpha = 0.25f),
        )
        else -> VoidKeyColors(
            bg = VoidKeyDigitBg,
            border = VoidKeyDigitBorder,
            text = onSurface,
            ripple = Color.White.copy(alpha = 0.18f),
        )
    }
} else {
    when {
        type == KeyType.Equal -> VoidKeyColors(
            bg = VoidKeyEqBg,
            border = Color.Transparent,
            text = VoidOnBrand,
            ripple = Color.White.copy(alpha = 0.35f),
        )
        isActive -> VoidKeyColors(
            bg = VoidLightKeyActiveOpBg,
            border = VoidLightKeyActiveOpBorder,
            text = VoidLightKeyActiveOpText,
            ripple = VoidLightKeyActiveOpText.copy(alpha = 0.20f),
        )
        type == KeyType.Operator -> VoidKeyColors(
            bg = VoidLightKeyOpBg,
            border = VoidLightKeyOpBorder,
            text = onSurface,
            ripple = Color.Black.copy(alpha = 0.12f),
        )
        type == KeyType.Danger -> VoidKeyColors(
            bg = VoidLightKeyErrorBg,
            border = VoidLightKeyErrorBorder,
            text = VoidLightKeyErrorText,
            ripple = Color.Red.copy(alpha = 0.12f),
        )
        else -> VoidKeyColors(
            bg = VoidLightKeyDigitBg,
            border = VoidLightKeyDigitBorder,
            text = onSurface,
            ripple = Color.Black.copy(alpha = 0.08f),
        )
    }
}

@Composable
private fun VoidKeyButton(
    key: CalculatorKey,
    isActiveOperator: Boolean,
    isDark: Boolean,
    onSurface: Color,
    onPress: () -> Unit,
    onLongPress: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = if (isPressed) {
            tween(durationMillis = 50, easing = LinearEasing)
        } else {
            spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMediumLow)
        },
        label = "key_scale",
    )

    val colors = resolveKeyColors(key.type, isActiveOperator, isDark, onSurface)
    val shape = RoundedCornerShape(14.dp)

    // Label font size: 20sp single char, 16sp 2-char, 13sp 3+ char
    val fontSize = when {
        key.label.length <= 1 -> 20.sp
        key.label.length == 2 -> 16.sp
        else -> 13.sp
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(colors.bg, shape)
            .border(1.dp, colors.border, shape)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = colors.ripple),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onPress()
                },
                onLongClick = onLongPress?.let { lp ->
                    {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        lp()
                    }
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = key.label,
            fontFamily = DmMono,
            fontSize = fontSize,
            fontWeight = FontWeight.Normal,
            color = colors.text,
        )
    }
}
