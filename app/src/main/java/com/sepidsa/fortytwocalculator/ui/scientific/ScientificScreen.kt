package com.sepidsa.fortytwocalculator.ui.scientific

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sepidsa.fortytwocalculator.R
import com.sepidsa.fortytwocalculator.ui.calculator.CalculatorUiState
import com.sepidsa.fortytwocalculator.ui.theme.DmMono
import com.sepidsa.fortytwocalculator.ui.theme.VoidBrand
import com.sepidsa.fortytwocalculator.ui.theme.VoidKeyActiveOpBg
import com.sepidsa.fortytwocalculator.ui.theme.VoidKeyActiveOpText
import com.sepidsa.fortytwocalculator.ui.theme.VoidKeyOpBg
import com.sepidsa.fortytwocalculator.ui.theme.VoidKeyOpBorder
import com.sepidsa.fortytwocalculator.ui.theme.VoidLightKeyOpBg
import com.sepidsa.fortytwocalculator.ui.theme.VoidLightKeyOpBorder

private val KEY_H = 52.dp
private val KEY_R = 14.dp
private val DIVIDER_COLOR = Color.White.copy(alpha = 0.07f)

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
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(top = 4.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        // Segmented mode toggles: INV | ARC | DEG ⇄ RAD
        ModeToggleRow(
            inverseMode = state.inverseMode,
            arcMode = state.arcMode,
            isDegrees = state.angleMode,
            onInverseToggle = { onInverseToggle(!state.inverseMode) },
            onArcToggle = { onArcToggle(!state.arcMode) },
            onAngleModeToggle = { onAngleModeToggle(!state.angleMode) },
            isDark = isDark,
        )

        HorizontalDivider(color = DIVIDER_COLOR, thickness = 1.dp)

        // Powers & roots — INV swaps √ ↔ x²
        // Values use mXparser syntax: sqrt( for root, ^2/^3 for powers
        val sqrtLabel = if (state.inverseMode) "x²" else "√"
        val sqrtValue = if (state.inverseMode) "^2" else "sqrt("
        val x2Label   = if (state.inverseMode) "√"  else "x²"
        val x2Value   = if (state.inverseMode) "sqrt(" else "^2"

        Row(
            modifier = Modifier.fillMaxWidth().height(KEY_H),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            SciKey("^",       "^",       isDark, onKeyPress, Modifier.weight(1f))
            SciKey(sqrtLabel, sqrtValue, isDark, onKeyPress, Modifier.weight(1f))
            SciKey(x2Label,   x2Value,   isDark, onKeyPress, Modifier.weight(1f))
            SciKey("x³",      "^3",      isDark, onKeyPress, Modifier.weight(1f))
        }

        // Constants & logs — INV: π→e, ln→eˣ (exp)
        // log stays as log10 in both modes (10^x requires binary op, deferred)
        val piLabel = if (state.inverseMode) "e"   else "π"
        val piValue = if (state.inverseMode) "e"   else "π"
        val lnLabel = if (state.inverseMode) "eˣ"  else "ln"
        val lnValue = if (state.inverseMode) "exp(" else "ln("

        Row(
            modifier = Modifier.fillMaxWidth().height(KEY_H),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            SciKey("!",     "!",        isDark, onKeyPress, Modifier.weight(1f))
            SciKey(piLabel, piValue,    isDark, onKeyPress, Modifier.weight(1f))
            SciKey(lnLabel, lnValue,    isDark, onKeyPress, Modifier.weight(1f))
            SciKey("log",   "log10(",   isDark, onKeyPress, Modifier.weight(1f))
        }

        // Divider: powers/logs ─── trig/hyperbolics
        HorizontalDivider(color = DIVIDER_COLOR, thickness = 1.dp)

        // Trig — ARC prepends "a" prefix
        val ap = if (state.arcMode) "a" else ""

        Row(
            modifier = Modifier.fillMaxWidth().height(KEY_H),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            SciKey("${ap}sin",  "${ap}sin(",  isDark, onKeyPress, Modifier.weight(1f))
            SciKey("${ap}cos",  "${ap}cos(",  isDark, onKeyPress, Modifier.weight(1f))
            SciKey("${ap}tan",  "${ap}tan(",  isDark, onKeyPress, Modifier.weight(1f))
            SciKey("EXP",       "E",          isDark, onKeyPress, Modifier.weight(1f))
        }

        // Hyperbolics + rand
        Row(
            modifier = Modifier.fillMaxWidth().height(KEY_H),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            SciKey("${ap}sinh", "${ap}sinh(", isDark, onKeyPress, Modifier.weight(1f))
            SciKey("${ap}cosh", "${ap}cosh(", isDark, onKeyPress, Modifier.weight(1f))
            SciKey("${ap}tanh", "${ap}tanh(", isDark, onKeyPress, Modifier.weight(1f))
            SciKey("rand",      "rand",       isDark, onKeyPress, Modifier.weight(1f))
        }

        // CONST chip — teal border signals "opens picker, not inserts value"
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            ConstChip(
                label = stringResource(R.string.constant),
                onClick = onConstantClick,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Mode toggles
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ModeToggleRow(
    inverseMode: Boolean,
    arcMode: Boolean,
    isDegrees: Boolean,
    onInverseToggle: () -> Unit,
    onArcToggle: () -> Unit,
    onAngleModeToggle: () -> Unit,
    isDark: Boolean,
) {
    val borderColor = if (isDark) VoidKeyOpBorder else VoidLightKeyOpBorder
    val shape = RoundedCornerShape(8.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .border(1.dp, borderColor, shape)
            .clip(shape),
    ) {
        SegmentButton(
            label = "INV",
            active = inverseMode,
            isDark = isDark,
            onClick = onInverseToggle,
            modifier = Modifier.weight(1f),
        )
        Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(borderColor))
        SegmentButton(
            label = "ARC",
            active = arcMode,
            isDark = isDark,
            onClick = onArcToggle,
            modifier = Modifier.weight(1f),
        )
        Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(borderColor))
        // DEG/RAD: always shows an active state since one mode is always selected
        SegmentButton(
            label = if (isDegrees) "DEG" else "RAD",
            active = true,
            isDark = isDark,
            onClick = onAngleModeToggle,
            modifier = Modifier.weight(1.5f),
        )
    }
}

@Composable
private fun SegmentButton(
    label: String,
    active: Boolean,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg = if (active) VoidKeyActiveOpBg else Color.Transparent
    val textColor = when {
        active -> VoidKeyActiveOpText
        isDark -> Color.White.copy(alpha = 0.40f)
        else   -> Color.Black.copy(alpha = 0.40f)
    }
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontFamily = DmMono,
            fontSize = 13.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Function key
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SciKey(
    label: String,
    value: String,
    isDark: Boolean,
    onKeyPress: (String) -> Unit,
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
        label = "sci_key_scale",
    )

    val bg     = if (isDark) VoidKeyOpBg     else VoidLightKeyOpBg
    val border = if (isDark) VoidKeyOpBorder else VoidLightKeyOpBorder
    val rippleColor = if (isDark) Color.White.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.12f)
    val textColor   = if (isDark) Color.White.copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.75f)
    val shape = RoundedCornerShape(KEY_R)

    // Font size: 14sp for ≤3 chars, 12sp for 4 chars, 11sp for longer (asin/sinh etc.)
    val fontSize = when {
        label.length <= 3 -> 14.sp
        label.length == 4 -> 12.sp
        else              -> 11.sp
    }

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(shape)
            .background(bg, shape)
            .border(1.dp, border, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = rippleColor),
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onKeyPress(value)
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontFamily = DmMono,
            fontSize = fontSize,
            fontWeight = FontWeight.Normal,
            color = textColor,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CONST chip
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ConstChip(label: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .clip(shape)
            .border(1.dp, VoidBrand, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontFamily = DmMono,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = VoidBrand,
        )
    }
}
