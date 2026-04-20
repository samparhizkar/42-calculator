package com.sepidsa.fortytwocalculator.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sepidsa.fortytwocalculator.R
import com.sepidsa.fortytwocalculator.data.AppPreferences
import com.sepidsa.fortytwocalculator.ui.theme.DmMono
import com.sepidsa.fortytwocalculator.ui.theme.Inter
import com.sepidsa.fortytwocalculator.ui.theme.VoidDarkBackground

/**
 * Number style sub-screen.
 * Only shown when language is Persian or Arabic.
 * Two-option radio: Western (1 2 3) vs Arabic-Indic (١ ٢ ٣).
 * Live preview line showing "1,234.56" in both styles.
 */
@Composable
fun NumberStyleScreen(
    appPreferences: AppPreferences,
    onBack: () -> Unit,
) {
    var selectedStyle by remember { mutableIntStateOf(appPreferences.numberStyle) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidDarkBackground)
    ) {
        // Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.settings_number_style),
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Western option
            NumberStyleOption(
                label = stringResource(R.string.number_style_western),
                preview = "1,234.56",
                isSelected = selectedStyle == AppPreferences.NUMBER_STYLE_WESTERN,
                onClick = {
                    selectedStyle = AppPreferences.NUMBER_STYLE_WESTERN
                    appPreferences.numberStyle = AppPreferences.NUMBER_STYLE_WESTERN
                },
            )

            // Arabic-Indic option
            NumberStyleOption(
                label = stringResource(R.string.number_style_arabic_indic),
                preview = "١٬٢٣٤٫٥٦",
                isSelected = selectedStyle == AppPreferences.NUMBER_STYLE_ARABIC_INDIC,
                onClick = {
                    selectedStyle = AppPreferences.NUMBER_STYLE_ARABIC_INDIC
                    appPreferences.numberStyle = AppPreferences.NUMBER_STYLE_ARABIC_INDIC
                },
            )
        }
    }
}

@Composable
private fun NumberStyleOption(
    label: String,
    preview: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = preview,
                fontFamily = DmMono,
                fontWeight = FontWeight.Light,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        }
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(start = 16.dp),
        thickness = 1.dp,
        color = Color.White.copy(alpha = 0.06f),
    )
}
