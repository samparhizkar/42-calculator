package com.sepidsa.fortytwocalculator.ui.theme.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sepidsa.fortytwocalculator.ui.theme.PreviewTheme
import com.sepidsa.fortytwocalculator.ui.theme.SeedPreset
import com.sepidsa.fortytwocalculator.ui.theme.VoidBrand

/**
 * Compact non-interactive calculator preview that shows how the theme looks.
 * Recolors via the enclosing PreviewTheme.
 */
@Composable
fun MiniPreviewCard(
    seedPreset: SeedPreset,
    customSeedColor: Int? = null,
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    keyColorOverride: Int? = null,
    modifier: Modifier = Modifier,
) {
    PreviewTheme(
        seedPreset = seedPreset,
        customSeedColor = customSeedColor,
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        keyColorOverride = keyColorOverride,
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(0.85f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                // Display area
                DisplayArea()

                Spacer(modifier = Modifier.height(12.dp))

                // Keypad grid (4x4 for preview)
                KeypadGrid()
            }
        }
    }
}

@Composable
private fun DisplayArea() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
    ) {
        // Expression
        Text(
            text = "566 + 369 =",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 10.sp,
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Result
        Text(
            text = "935",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 28.sp,
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Words (spoken form)
        Text(
            text = "nine hundred thirty-five",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            fontSize = 8.sp,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
        )
    }
}

@Composable
private fun KeypadGrid() {
    val keys = listOf(
        listOf("C", "(", ")", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "−"),
        listOf("1", "2", "3", "+"),
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                row.forEach { key ->
                    PreviewKey(
                        label = key,
                        isOperator = key in listOf("÷", "×", "−", "+", "C", "(", ")"),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        // Bottom row with equals
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            PreviewKey(
                label = ".",
                isOperator = false,
                modifier = Modifier.weight(1f),
            )
            PreviewKey(
                label = "0",
                isOperator = false,
                modifier = Modifier.weight(1f),
            )
            PreviewKey(
                label = "%",
                isOperator = true,
                modifier = Modifier.weight(1f),
            )
            PreviewKey(
                label = "=",
                isEquals = true,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PreviewKey(
    label: String,
    isOperator: Boolean = false,
    isEquals: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = when {
        isEquals -> VoidBrand
        isOperator -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val textColor = when {
        isEquals -> Color.Black
        isOperator -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )
    }
}
