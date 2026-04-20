package com.sepidsa.fortytwocalculator.ui.dialogs

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sepidsa.fortytwocalculator.R

/**
 * Language constants — must match CalculatorViewModel
 */
private const val LANGUAGE_PERSIAN = 0
private const val LANGUAGE_ENGLISH = 1
private const val LANGUAGE_FRENCH = 2
private const val LANGUAGE_ARABIC = 3

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    onLanguageChanged: (Int) -> Unit,
) {
    val context = LocalContext.current

    // Load persisted preferences
    val languagePrefs = context.getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE)

    var selectedLanguage by remember {
        mutableIntStateOf(languagePrefs.getInt("LANGUAGE", LANGUAGE_PERSIAN))
    }

    // Determine preview text for the current language
    val translationPreview = when (selectedLanguage) {
        LANGUAGE_PERSIAN -> stringResource(R.string.persian_42)
        LANGUAGE_ENGLISH -> stringResource(R.string.english_42)
        LANGUAGE_FRENCH -> stringResource(R.string.french_42)
        LANGUAGE_ARABIC -> stringResource(R.string.arabic_42)
        else -> "42"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("تنظیمات") // "Settings" in Persian, matching original UI
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Live Preview ──
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = translationPreview,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                HorizontalDivider()

                // ── Translation Language ──
                Text(
                    text = "زبان نتیجه محاسبه", // "Calculation result language" in Persian
                    style = MaterialTheme.typography.labelMedium
                )

                val languages = listOf(
                    LANGUAGE_PERSIAN to "فارسی",
                    LANGUAGE_ENGLISH to "English",
                    LANGUAGE_FRENCH to "Français",
                    LANGUAGE_ARABIC to "العربية",
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    languages.forEach { (langCode, langName) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLanguage == langCode,
                                onClick = {
                                    selectedLanguage = langCode
                                    // Persist
                                    languagePrefs.edit().putInt("LANGUAGE", langCode).apply()
                                    onLanguageChanged(langCode)
                                }
                            )
                            Text(
                                text = langName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                HorizontalDivider()
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.farsi_ok))
            }
        },
        shape = MaterialTheme.shapes.extraLarge
    )
}
