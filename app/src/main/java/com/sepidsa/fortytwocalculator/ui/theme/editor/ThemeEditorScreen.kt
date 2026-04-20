package com.sepidsa.fortytwocalculator.ui.theme.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sepidsa.fortytwocalculator.R
import com.sepidsa.fortytwocalculator.ui.theme.AppearanceMode
import com.sepidsa.fortytwocalculator.ui.theme.KeyColorOptions
import com.sepidsa.fortytwocalculator.ui.theme.SeedPreset
import com.sepidsa.fortytwocalculator.ui.theme.ThemePreferences

/**
 * Full-screen theme editor with seed-based Material 3 theming.
 * Includes: preview card, seed presets, appearance toggle, dynamic color switch,
 * and paid key color override section.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeEditorScreen(
    themePreferences: ThemePreferences,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    isPremium: Boolean = false, // TODO: Wire to actual billing
) {
    // Local state for preview (not committed until apply)
    var selectedSeed by remember { mutableStateOf(themePreferences.seedPreset) }
    var customSeedColor by remember { mutableIntStateOf(themePreferences.customSeedColor) }
    var appearanceMode by remember { mutableStateOf(themePreferences.appearanceMode) }
    var dynamicColorEnabled by remember { mutableStateOf(themePreferences.isDynamicColorEnabled) }
    var keyColorOverride by remember { mutableStateOf(themePreferences.keyColorOverride) }

    var showCustomColorPicker by remember { mutableStateOf(false) }

    // Determine effective dark mode for preview
    val isDarkTheme = when (appearanceMode) {
        AppearanceMode.LIGHT -> false
        AppearanceMode.DARK -> true
        AppearanceMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.theme_editor_title)) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            // Live Preview Card
            MiniPreviewCard(
                seedPreset = selectedSeed,
                customSeedColor = if (selectedSeed == SeedPreset.CUSTOM) customSeedColor else null,
                darkTheme = isDarkTheme,
                dynamicColor = dynamicColorEnabled,
                keyColorOverride = keyColorOverride,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )

            // Theme Color Section
            SectionHeader(title = stringResource(R.string.theme_color))

            SeedColorRow(
                selectedSeed = selectedSeed,
                onSeedSelected = { selectedSeed = it },
                onCustomClick = { showCustomColorPicker = true },
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Appearance Section
            SectionHeader(title = stringResource(R.string.appearance))

            AppearanceToggle(
                selectedMode = appearanceMode,
                onModeSelected = { appearanceMode = it },
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Color Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.dynamic_color),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = stringResource(R.string.dynamic_color_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = dynamicColorEnabled,
                    onCheckedChange = { dynamicColorEnabled = it },
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // Advanced / Paid Section
            PaidSectionHeader(
                title = stringResource(R.string.key_color),
                isPremium = isPremium,
            )

            Text(
                text = stringResource(R.string.key_color_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            KeyColorRow(
                selectedColor = keyColorOverride,
                onColorSelected = { keyColorOverride = it },
                isPremium = isPremium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Reset to defaults
            TextButton(
                onClick = {
                    selectedSeed = SeedPreset.TEAL
                    customSeedColor = 0xFF1ABC9C.toInt()
                    appearanceMode = AppearanceMode.SYSTEM
                    dynamicColorEnabled = false
                    keyColorOverride = null
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(stringResource(R.string.reset_to_defaults))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Apply button
            Button(
                onClick = {
                    // Commit changes to preferences
                    themePreferences.seedPreset = selectedSeed
                    themePreferences.customSeedColor = customSeedColor
                    themePreferences.appearanceMode = appearanceMode
                    themePreferences.isDynamicColorEnabled = dynamicColorEnabled
                    themePreferences.keyColorOverride = keyColorOverride
                    themePreferences.applyAppearanceMode(appearanceMode)
                    onApply()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Text(stringResource(R.string.apply_changes))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Custom color picker dialog
    if (showCustomColorPicker) {
        HsvColorPickerDialog(
            initialColor = customSeedColor,
            onColorSelected = {
                customSeedColor = it
                selectedSeed = SeedPreset.CUSTOM
                showCustomColorPicker = false
            },
            onDismiss = { showCustomColorPicker = false },
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun PaidSectionHeader(
    title: String,
    isPremium: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (!isPremium) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Premium",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.paid_feature_label),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SeedColorRow(
    selectedSeed: SeedPreset,
    onSeedSelected: (SeedPreset) -> Unit,
    onCustomClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SeedPreset.entries.forEach { preset ->
            if (preset == SeedPreset.CUSTOM) {
                CustomColorChip(
                    isSelected = selectedSeed == SeedPreset.CUSTOM,
                    onClick = onCustomClick,
                )
            } else {
                ColorChip(
                    color = Color(preset.seedColor),
                    isSelected = selectedSeed == preset,
                    onClick = { onSeedSelected(preset) },
                )
            }
        }
    }
}

@Composable
private fun ColorChip(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                },
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
            )
        }
    }
}

@Composable
private fun CustomColorChip(
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(
                        Color.Red,
                        Color.Yellow,
                        Color.Green,
                        Color.Cyan,
                        Color.Blue,
                        Color.Magenta,
                    ),
                ),
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                },
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "+",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppearanceToggle(
    selectedMode: AppearanceMode,
    onModeSelected: (AppearanceMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth(),
    ) {
        SegmentedButton(
            selected = selectedMode == AppearanceMode.LIGHT,
            onClick = { onModeSelected(AppearanceMode.LIGHT) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
        ) {
            Text(stringResource(R.string.appearance_light))
        }
        SegmentedButton(
            selected = selectedMode == AppearanceMode.DARK,
            onClick = { onModeSelected(AppearanceMode.DARK) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
        ) {
            Text(stringResource(R.string.appearance_dark))
        }
        SegmentedButton(
            selected = selectedMode == AppearanceMode.SYSTEM,
            onClick = { onModeSelected(AppearanceMode.SYSTEM) },
            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
        ) {
            Text(stringResource(R.string.appearance_system))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KeyColorRow(
    selectedColor: Int?,
    onColorSelected: (Int?) -> Unit,
    isPremium: Boolean,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // None option (default)
        FilterChip(
            selected = selectedColor == null,
            onClick = { if (isPremium) onColorSelected(null) },
            label = { Text(stringResource(R.string.default_label)) },
            enabled = isPremium,
        )

        // Paid color options
        KeyColorOptions.allColors.forEach { color ->
            val isSelected = selectedColor == color

            if (isPremium) {
                ColorChip(
                    color = Color(color),
                    isSelected = isSelected,
                    onClick = { onColorSelected(color) },
                )
            } else {
                // Grayed out for non-premium
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(color).copy(alpha = 0.3f))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    )
                }
            }
        }
    }
}

// Extension function for Color luminance
private fun Color.luminance(): Float {
    val r = red * 0.2126f
    val g = green * 0.7152f
    val b = blue * 0.0722f
    return r + g + b
}
