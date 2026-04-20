package com.sepidsa.fortytwocalculator.ui.settings

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sepidsa.fortytwocalculator.BuildConfig
import com.sepidsa.fortytwocalculator.R
import com.sepidsa.fortytwocalculator.data.AppPreferences
import com.sepidsa.fortytwocalculator.ui.theme.Inter
import com.sepidsa.fortytwocalculator.ui.theme.VoidDarkBackground

/**
 * Full-screen settings following VOID v2 design spec.
 *
 * Background: #080808
 * Section headers: Inter 700 11px uppercase, primary colour, 1.6 letter-spacing
 * Rows: Inter 400 16px onSurface / values 13px onSurfaceVariant
 * Min-height: 56dp, padding 16dp
 * Dividers: 1px rgba(255,255,255,0.06) indented 56dp
 */
@Composable
fun SettingsScreen(
    appPreferences: AppPreferences,
    onBack: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onNavigateToNumberStyle: () -> Unit,
    onNavigateToAngleMode: () -> Unit,
    onNavigateToAutoClear: () -> Unit,
    onNavigateToTheme: () -> Unit,
    onNavigateToConstants: () -> Unit,
) {
    val context = LocalContext.current

    // Live preference state
    var keyHapticsEnabled by remember { mutableStateOf(appPreferences.keyHapticsEnabled) }

    // Current language display value
    val languageLabel = when (appPreferences.language) {
        AppPreferences.LANG_PERSIAN -> stringResource(R.string.lang_persian)
        AppPreferences.LANG_ENGLISH -> stringResource(R.string.lang_english)
        AppPreferences.LANG_FRENCH -> stringResource(R.string.lang_french)
        AppPreferences.LANG_ARABIC -> stringResource(R.string.lang_arabic)
        else -> ""
    }

    // Number style display value
    val numberStyleLabel = when (appPreferences.numberStyle) {
        AppPreferences.NUMBER_STYLE_WESTERN -> stringResource(R.string.number_style_western)
        AppPreferences.NUMBER_STYLE_ARABIC_INDIC -> stringResource(R.string.number_style_arabic_indic)
        else -> ""
    }

    // Angle mode display value
    val angleModeLabel = if (appPreferences.isDegree) {
        stringResource(R.string.angle_mode_deg)
    } else {
        stringResource(R.string.angle_mode_rad)
    }

    // Auto-clear display value
    val autoClearLabel = when (appPreferences.autoClearHistory) {
        AppPreferences.AUTO_CLEAR_NEVER -> stringResource(R.string.auto_clear_never)
        AppPreferences.AUTO_CLEAR_30_DAYS -> stringResource(R.string.auto_clear_30_days)
        AppPreferences.AUTO_CLEAR_90_DAYS -> stringResource(R.string.auto_clear_90_days)
        AppPreferences.AUTO_CLEAR_1_YEAR -> stringResource(R.string.auto_clear_1_year)
        else -> stringResource(R.string.auto_clear_never)
    }

    val versionName = try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        pInfo.versionName ?: ""
    } catch (_: Exception) {
        BuildConfig.VERSION_NAME
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidDarkBackground)
    ) {
        // ── Toolbar ───────────────────────────────────────────────────────────
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
                text = stringResource(R.string.settings_title),
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            )
        }

        // ── Scrollable content ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // GENERAL
            SectionHeader(title = stringResource(R.string.settings_general))
            SettingsRow(
                title = stringResource(R.string.settings_language),
                value = languageLabel,
                onClick = onNavigateToLanguage,
            )
            if (appPreferences.shouldShowNumberStyle) {
                SettingsRow(
                    title = stringResource(R.string.settings_number_style),
                    value = numberStyleLabel,
                    onClick = onNavigateToNumberStyle,
                )
            }

            // CALCULATOR
            SectionHeader(title = stringResource(R.string.settings_calculator))
            SettingsRow(
                title = stringResource(R.string.settings_default_angle_mode),
                value = angleModeLabel,
                onClick = onNavigateToAngleMode,
            )
            SettingsSwitchRow(
                title = stringResource(R.string.settings_key_haptics),
                icon = {
                    Icon(
                        Icons.Default.Vibration,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    )
                },
                checked = keyHapticsEnabled,
                onCheckedChange = { enabled ->
                    keyHapticsEnabled = enabled
                    appPreferences.keyHapticsEnabled = enabled
                },
            )

            // APPEARANCE
            SectionHeader(title = stringResource(R.string.settings_appearance))
            SettingsRow(
                title = stringResource(R.string.settings_theme),
                value = null,
                onClick = onNavigateToTheme,
            )

            // HISTORY
            SectionHeader(title = stringResource(R.string.settings_history))
            SettingsRow(
                title = stringResource(R.string.settings_auto_clear),
                value = autoClearLabel,
                onClick = onNavigateToAutoClear,
            )

            // CONSTANTS
            SectionHeader(title = stringResource(R.string.settings_constants))
            SettingsRow(
                title = stringResource(R.string.settings_manage_constants),
                value = null,
                onClick = onNavigateToConstants,
            )

            // ABOUT
            SectionHeader(title = stringResource(R.string.settings_about))
            SettingsRow(
                title = stringResource(R.string.settings_version),
                value = versionName,
                onClick = null,
                showChevron = false,
            )
            SettingsRow(
                title = stringResource(R.string.settings_oss_licenses),
                value = null,
                onClick = {
                    // OSS licenses — using standard Android intent
                    try {
                        val intent = Intent(
                            "android.settings.APPLICATION_DETAILS_SETTINGS",
                            android.net.Uri.parse("package:${context.packageName}")
                        )
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        // Fallback: no-op
                    }
                },
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Section header ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 1.6.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 4.dp),
    )
}

// ── Settings row with navigation ──────────────────────────────────────────────

@Composable
private fun SettingsRow(
    title: String,
    value: String?,
    onClick: (() -> Unit)?,
    showChevron: Boolean = true,
) {
    val clickable = onClick != null
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .then(
                if (clickable) Modifier.clickable { onClick?.invoke() }
                else Modifier
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        if (value != null) {
            Text(
                text = value,
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (showChevron && clickable == true) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
        if (showChevron && clickable == true) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
        }
    }
    IndentedDivider()
}

// ── Settings switch row ───────────────────────────────────────────────────────

@Composable
private fun SettingsSwitchRow(
    title: String,
    icon: @Composable () -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
            ),
        )
    }
    IndentedDivider()
}

// ── Indented divider (1px, rgba(255,255,255,0.06), indented 56dp) ───────────

@Composable
private fun IndentedDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp),
        thickness = 1.dp,
        color = Color.White.copy(alpha = 0.06f),
    )
}
