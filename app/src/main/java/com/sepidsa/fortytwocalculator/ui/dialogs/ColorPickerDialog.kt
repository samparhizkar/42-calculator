package com.sepidsa.fortytwocalculator.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Full-screen color picker dialog that replaces ColorPickerActivity.
 * Provides accent color and keypad background color selection with a classic theme toggle.
 */
@Composable
fun ColorPickerDialog(
    initialAccentColor: Int,
    initialKeypadColor: Int,
    isClassicTheme: Boolean,
    onAcceptColors: (accentColor: Int, keypadColor: Int, useClassicTheme: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedAccentColor by remember { mutableStateOf(initialAccentColor) }
    var selectedKeypadColor by remember { mutableStateOf(initialKeypadColor) }
    var useClassicTheme by remember { mutableStateOf(isClassicTheme) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top section - Accent Color Picker
                AccentColorSection(
                    selectedColor = selectedAccentColor,
                    onColorSelected = { selectedAccentColor = it },
                    onBackClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )

                // Bottom section - Keypad Color Picker
                KeypadColorSection(
                    selectedColor = selectedKeypadColor,
                    onColorSelected = { selectedKeypadColor = it },
                    modifier = Modifier.weight(1f)
                )

                // Bottom bar with Classic Theme toggle
                ClassicThemeBar(
                    useClassicTheme = useClassicTheme,
                    onClassicThemeToggle = { useClassicTheme = it },
                    onApply = {
                        onAcceptColors(selectedAccentColor, selectedKeypadColor, useClassicTheme)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun AccentColorSection(
    selectedColor: Int,
    onColorSelected: (Int) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(selectedColor))
            .padding(16.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "صفحه نمایش", // Display screen (Persian)
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp)) // Balance the back button
        }

        // Accent color grid
        ColorGrid(
            colors = accentColors,
            selectedColor = selectedColor,
            onColorSelected = onColorSelected,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun KeypadColorSection(
    selectedColor: Int,
    onColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val fontColor = remember(selectedColor) {
        getKeypadFontColor(selectedColor)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(selectedColor))
            .padding(16.dp)
    ) {
        // Header with gold star for premium feature
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "*",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFFFFC107), // Gold color
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = "صفحه کلید", // Keypad (Persian)
                style = MaterialTheme.typography.headlineSmall,
                color = Color(fontColor)
            )
        }

        // Keypad color grid
        ColorGrid(
            colors = keypadColors,
            selectedColor = selectedColor,
            onColorSelected = onColorSelected,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ColorGrid(
    colors: List<Int>,
    selectedColor: Int,
    onColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Split colors into rows of 6
        colors.chunked(6).forEach { rowColors ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowColors.forEach { color ->
                    ColorSwatch(
                        color = color,
                        isSelected = selectedColor == color,
                        onClick = { onColorSelected(color) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    color: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color(color))
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) Color.White else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // Checkmark for selected color
            Text(
                text = "✓",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun ClassicThemeBar(
    useClassicTheme: Boolean,
    onClassicThemeToggle: (Boolean) -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Classic theme toggle row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "*",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFFC107), // Gold color
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = "تم کلاسیک", // Classic Theme (Persian)
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Switch(
                    checked = useClassicTheme,
                    onCheckedChange = onClassicThemeToggle
                )
            }

            // Gold version tip
            Text(
                text = "موارد ستاره دار فقط در نسخه طلایی", // Starred items are gold version only (Persian)
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Apply button
            Button(
                onClick = onApply,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("اعمال تغییرات") // Apply Changes (Persian)
            }
        }
    }
}

/**
 * Get the appropriate font color for the keypad based on background color
 */
private fun getKeypadFontColor(backgroundColor: Int): Int {
    // Map of background colors to their corresponding font colors
    // Based on dialpad_font_color_choice_values array
    val fontColors = listOf(
        0xFF9E9E9E.toInt(), // Gray for white
        0xFFE0E0E0.toInt(), // Light gray for dark
        0xFF388E3C.toInt(), // Green
        0xFF795548.toInt(), // Brown
        0xFFAFB42B.toInt(), // Lime
        0xFF009688.toInt(), // Teal
        0xFF9E9E9E.toInt(), // Gray
        0xFF4DB6AC.toInt(), // Light teal
        0xFFE0F7FA.toInt(), // Cyan light
        0xFFCFD8DC.toInt(), // Blue gray
        0xFF78909C.toInt(), // Blue gray dark
        0xFFE0F7FA.toInt(), // Cyan light
        0xFFC5CAE9.toInt(), // Indigo light
        0xFFFCE4EC.toInt(), // Pink light
        0xFFFCE4EC.toInt(), // Pink light
        0xFFF3E5F5.toInt(), // Purple light
        0xFFC0CA33.toInt(), // Lime dark
        0xFFECEFF1.toInt(), // Blue gray light
    )

    val index = keypadColors.indexOf(backgroundColor)
    return if (index >= 0 && index < fontColors.size) {
        fontColors[index]
    } else {
        0xFF9E9E9E.toInt() // Default gray
    }
}

// Accent colors from default_color_choice_values array
private val accentColors = listOf(
    0xFF1abc9c.toInt(), // Teal
    0xFF009688.toInt(), // Teal dark
    0xFF004D40.toInt(), // Teal darker
    0xFF336e7b.toInt(), // Blue gray
    0xFF00bcd4.toInt(), // Cyan
    0xFF29B6F6.toInt(), // Light blue
    0xFF01579B.toInt(), // Dark blue
    0xFF607D8B.toInt(), // Blue gray
    0xFF000000.toInt(), // Black
    0xFF795548.toInt(), // Brown
    0xFFFFC107.toInt(), // Amber
    0xFFff6e40.toInt(), // Deep orange
    0xFFF06292.toInt(), // Pink
    0xFFD81B60.toInt(), // Pink dark
    0xFFBA68C8.toInt(), // Purple light
    0xFF7C4DFF.toInt(), // Deep purple
    0xFFC51162.toInt(), // Pink accent
    0xFF880E4F.toInt(), // Pink darker
)

// Keypad colors from keypad_color_choice_values array
private val keypadColors = listOf(
    0xFFffffff.toInt(), // White
    0xFF1c1c1c.toInt(), // Dark
    0xFFA5D6A7.toInt(), // Green light
    0xFFDCE775.toInt(), // Lime
    0xFFF4FF81.toInt(), // Lime light
    0xFFB9F6CA.toInt(), // Green accent
    0xFFFFF59D.toInt(), // Yellow
    0xFFFFCC80.toInt(), // Orange light
    0xFFFFAB91.toInt(), // Deep orange light
    0xFF80CBC4.toInt(), // Teal light
    0xFFB2EBF2.toInt(), // Cyan light
    0xFFFFCDD2.toInt(), // Red light
    0xFFCE93D8.toInt(), // Purple light
    0xFFFF80AB.toInt(), // Pink accent
    0xFFE1BEE7.toInt(), // Purple lighter
    0xFFB39DDB.toInt(), // Deep purple light
    0xFF9FA8DA.toInt(), // Indigo light
    0xFF80DEEA.toInt(), // Cyan accent
)
