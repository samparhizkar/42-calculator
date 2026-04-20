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
 * Language picker sub-screen.
 * Full-screen destination with 4 rows, radio selection, checkmark on active.
 * Each row: language name in that language + language name in current app language below it.
 */
@Composable
fun LanguagePickerScreen(
    appPreferences: AppPreferences,
    onBack: () -> Unit,
    onLanguageChanged: (Int) -> Unit,
) {
    var selectedLanguage by remember { mutableIntStateOf(appPreferences.language) }

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
                text = stringResource(R.string.settings_language),
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            LanguageRow(
                nativeName = stringResource(R.string.lang_persian),
                localizedName = stringResource(R.string.lang_persian_native),
                isSelected = selectedLanguage == AppPreferences.LANG_PERSIAN,
                onClick = {
                    selectedLanguage = AppPreferences.LANG_PERSIAN
                    appPreferences.language = AppPreferences.LANG_PERSIAN
                    onLanguageChanged(AppPreferences.LANG_PERSIAN)
                },
            )
            LanguageRow(
                nativeName = stringResource(R.string.lang_english),
                localizedName = stringResource(R.string.lang_english_native),
                isSelected = selectedLanguage == AppPreferences.LANG_ENGLISH,
                onClick = {
                    selectedLanguage = AppPreferences.LANG_ENGLISH
                    appPreferences.language = AppPreferences.LANG_ENGLISH
                    onLanguageChanged(AppPreferences.LANG_ENGLISH)
                },
            )
            LanguageRow(
                nativeName = stringResource(R.string.lang_french),
                localizedName = stringResource(R.string.lang_french_native),
                isSelected = selectedLanguage == AppPreferences.LANG_FRENCH,
                onClick = {
                    selectedLanguage = AppPreferences.LANG_FRENCH
                    appPreferences.language = AppPreferences.LANG_FRENCH
                    onLanguageChanged(AppPreferences.LANG_FRENCH)
                },
            )
            LanguageRow(
                nativeName = stringResource(R.string.lang_arabic),
                localizedName = stringResource(R.string.lang_arabic_native),
                isSelected = selectedLanguage == AppPreferences.LANG_ARABIC,
                onClick = {
                    selectedLanguage = AppPreferences.LANG_ARABIC
                    appPreferences.language = AppPreferences.LANG_ARABIC
                    onLanguageChanged(AppPreferences.LANG_ARABIC)
                },
            )
        }
    }
}

@Composable
private fun LanguageRow(
    nativeName: String,
    localizedName: String,
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
                text = nativeName,
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = localizedName,
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
