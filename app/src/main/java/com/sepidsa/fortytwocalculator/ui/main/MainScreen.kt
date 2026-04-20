package com.sepidsa.fortytwocalculator.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sepidsa.fortytwocalculator.ui.calculator.CalculatorScreen
import com.sepidsa.fortytwocalculator.ui.calculator.CalculatorUiState
import com.sepidsa.fortytwocalculator.ui.history.HistoryScreen
import com.sepidsa.fortytwocalculator.ui.history.HistoryUiState
import com.sepidsa.fortytwocalculator.ui.scientific.ScientificScreen
import com.sepidsa.fortytwocalculator.ui.favorites.FavoritesScreen
import com.sepidsa.fortytwocalculator.ui.favorites.FavoritesUiState
import com.sepidsa.fortytwocalculator.ui.constants.ConstantsScreen
import com.sepidsa.fortytwocalculator.ui.constants.ConstantsUiState
import com.sepidsa.fortytwocalculator.ui.currency.CurrencyScreen
import com.sepidsa.fortytwocalculator.ui.currency.CurrencyUiState
import com.sepidsa.fortytwocalculator.ui.dialogs.ColorPickerDialog
import com.sepidsa.fortytwocalculator.ui.dialogs.SettingsDialog
import com.sepidsa.fortytwocalculator.ui.dialogs.AboutDialog
import com.sepidsa.fortytwocalculator.ui.dialogs.HelpDialog
import com.sepidsa.fortytwocalculator.data.LogEntity
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    calculatorState: CalculatorUiState,
    historyState: HistoryUiState,
    onCalculatorKeyPress: (String) -> Unit,
    onAngleModeToggle: (Boolean) -> Unit,
    onDeleteHistoryItem: (Long) -> Unit,
    onStarHistoryItem: (Long, Boolean) -> Unit,
    onUpdateHistoryTag: (Long, String) -> Unit,
    onClearHistory: (Boolean) -> Unit,
    onShareHistoryItem: (LogEntity) -> Unit,
    onScientificKeyPress: (String) -> Unit,
    onInverseToggle: (Boolean) -> Unit,
    onArcToggle: (Boolean) -> Unit,
    onConstantClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSettingsFontChanged: (Int) -> Unit,
    onSettingsLanguageChanged: (Int) -> Unit,
    onRateUs: () -> Unit,
    onMuteClick: () -> Unit,
    onColorsClick: () -> Unit,
    onContactUs: () -> Unit,
    onAddStarClick: () -> Unit,
    onAddLabelClick: () -> Unit,
    onAboutClick: () -> Unit,
    onHelpClick: () -> Unit,
    isMuted: Boolean
) {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
    var showAbout by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                onHelpClick = { showHelp = true; scope.launch { drawerState.close() } },
                onRateClick = onRateUs,
                onAboutClick = { showAbout = true; scope.launch { drawerState.close() } },
                onContactClick = onContactUs
            )
        },
        gesturesEnabled = true
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top section with Display
            CalculatorDisplay(
                state = calculatorState,
                onAddStarClick = onAddStarClick,
                onAddLabelClick = onAddLabelClick,
                modifier = Modifier.weight(0.3f)
            )

            // Middle section with Translation and Favorites
            TranslationBar(
                state = calculatorState,
                onFavoritesClick = { /* Show favorites dialog */ },
                modifier = Modifier.weight(0.15f)
            )

            // Bottom section with Pager and Controls
            Column(modifier = Modifier.weight(0.55f)) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    when (page) {
                        0 -> HistoryScreen(
                            state = historyState,
                            onDelete = onDeleteHistoryItem,
                            onStarToggle = onStarHistoryItem,
                            onUpdateTag = onUpdateHistoryTag,
                            onUseResult = { result ->
                                onCalculatorKeyPress(result)
                            },
                            onClearAll = onClearHistory,
                            onShare = onShareHistoryItem
                        )
                        1 -> CalculatorScreen(
                            state = calculatorState,
                            onKeyPress = onCalculatorKeyPress,
                            onAngleModeChanged = onAngleModeToggle
                        )
                        2 -> ScientificScreen(
                            state = calculatorState,
                            onKeyPress = onScientificKeyPress,
                            onInverseToggle = onInverseToggle,
                            onArcToggle = onArcToggle,
                            onAngleModeToggle = onAngleModeToggle,
                            onConstantClick = onConstantClick
                        )
                    }
                }

                // Pager Indicator
                PageIndicator(
                    pagerState = pagerState,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp)
                )

                // Bottom Buttons
                BottomActionBar(
                    onSettingsClick = { showSettings = true },
                    onMuteClick = onMuteClick,
                    onColorsClick = onColorsClick,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onAboutClick = { showAbout = true },
                    onHelpClick = { showHelp = true },
                    isMuted = isMuted
                )
            }
        }
    }

    // Dialogs
    if (showAbout) {
        AboutDialog(onDismiss = { showAbout = false })
    }
    if (showHelp) {
        HelpDialog(onDismiss = { showHelp = false })
    }
    if (showSettings) {
        SettingsDialog(
            onDismiss = { showSettings = false },
            onFontChanged = onSettingsFontChanged,
            onLanguageChanged = onSettingsLanguageChanged,
            onRateUs = onRateUs
        )
    }
}

@Composable
fun CalculatorDisplay(
    state: CalculatorUiState,
    onAddStarClick: () -> Unit,
    onAddLabelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (state.angleMode) "DEG" else "RAD",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "M = ${state.memory}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = state.result,
                style = MaterialTheme.typography.displayMedium,
                maxLines = 2
            )
        }
    }
}

@Composable
fun TranslationBar(
    state: CalculatorUiState,
    onFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onFavoritesClick) {
            Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Favorites")
        }
        Text(
            text = state.translatedResult,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun PageIndicator(pagerState: androidx.compose.foundation.pager.PagerState, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(pagerState.pageCount) { iteration ->
            val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
        }
    }
}

@Composable
fun BottomActionBar(
    onSettingsClick: () -> Unit,
    onMuteClick: () -> Unit,
    onColorsClick: () -> Unit,
    onMenuClick: () -> Unit,
    onAboutClick: () -> Unit,
    onHelpClick: () -> Unit,
    isMuted: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = onHelpClick) {
            Icon(Icons.Default.HelpOutline, contentDescription = "Help")
        }
        IconButton(onClick = onSettingsClick) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }
        IconButton(onClick = onMuteClick) {
            Icon(
                if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                contentDescription = "Mute"
            )
        }
        IconButton(onClick = onColorsClick) {
            Icon(Icons.Default.Palette, contentDescription = "Theme")
        }
        IconButton(onClick = onAboutClick) {
            Icon(Icons.Default.Info, contentDescription = "About")
        }
        IconButton(onClick = onMenuClick) {
            Icon(Icons.Default.Menu, contentDescription = "Menu")
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Navigation Drawer
// ─────────────────────────────────────────────────────────────────────────────

private data class DrawerItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
private fun AppDrawer(
    onHelpClick: () -> Unit,
    onRateClick: () -> Unit,
    onAboutClick: () -> Unit,
    onContactClick: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        // Drawer header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "ماشین حساب ۴۲",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "42 Calculator",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider()

        // Drawer items — matches original drawer_menu.xml
        val items = listOf(
            DrawerItem(
                label = "راهنما", // Help
                icon = Icons.Default.HelpOutline,
                onClick = onHelpClick
            ),
            DrawerItem(
                label = "امتیاز و نظر", // Rate & Review
                icon = Icons.Default.Star,
                onClick = onRateClick
            ),
            DrawerItem(
                label = "درباره", // About
                icon = Icons.Default.Info,
                onClick = onAboutClick
            ),
            DrawerItem(
                label = "پیام به ما", // Contact Us
                icon = Icons.Default.Email,
                onClick = onContactClick
            )
        )

        items.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = false,
                onClick = item.onClick,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}
