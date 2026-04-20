package com.sepidsa.fortytwocalculator.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.sepidsa.fortytwocalculator.ui.theme.Inter
import com.sepidsa.fortytwocalculator.ui.theme.VoidDarkBackground

/**
 * Auto-clear history sub-screen.
 * Four-option radio: Never / 30 days / 90 days / 1 year.
 */
@Composable
fun AutoClearScreen(
    appPreferences: AppPreferences,
    onBack: () -> Unit,
) {
    var selectedPeriod by remember { mutableIntStateOf(appPreferences.autoClearHistory) }

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
                text = stringResource(R.string.settings_auto_clear),
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            AutoClearOption(
                label = stringResource(R.string.auto_clear_never),
                isSelected = selectedPeriod == AppPreferences.AUTO_CLEAR_NEVER,
                onClick = {
                    selectedPeriod = AppPreferences.AUTO_CLEAR_NEVER
                    appPreferences.autoClearHistory = AppPreferences.AUTO_CLEAR_NEVER
                },
            )
            AutoClearOption(
                label = stringResource(R.string.auto_clear_30_days),
                isSelected = selectedPeriod == AppPreferences.AUTO_CLEAR_30_DAYS,
                onClick = {
                    selectedPeriod = AppPreferences.AUTO_CLEAR_30_DAYS
                    appPreferences.autoClearHistory = AppPreferences.AUTO_CLEAR_30_DAYS
                },
            )
            AutoClearOption(
                label = stringResource(R.string.auto_clear_90_days),
                isSelected = selectedPeriod == AppPreferences.AUTO_CLEAR_90_DAYS,
                onClick = {
                    selectedPeriod = AppPreferences.AUTO_CLEAR_90_DAYS
                    appPreferences.autoClearHistory = AppPreferences.AUTO_CLEAR_90_DAYS
                },
            )
            AutoClearOption(
                label = stringResource(R.string.auto_clear_1_year),
                isSelected = selectedPeriod == AppPreferences.AUTO_CLEAR_1_YEAR,
                onClick = {
                    selectedPeriod = AppPreferences.AUTO_CLEAR_1_YEAR
                    appPreferences.autoClearHistory = AppPreferences.AUTO_CLEAR_1_YEAR
                },
            )
        }
    }
}

@Composable
private fun AutoClearOption(
    label: String,
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
        Text(
            text = label,
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
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
