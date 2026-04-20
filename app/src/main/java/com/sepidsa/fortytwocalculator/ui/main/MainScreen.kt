package com.sepidsa.fortytwocalculator.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sepidsa.fortytwocalculator.ui.calculator.CalculatorScreen
import com.sepidsa.fortytwocalculator.ui.calculator.CalculatorUiState
import com.sepidsa.fortytwocalculator.ui.history.HistoryScreen
import com.sepidsa.fortytwocalculator.ui.history.HistoryUiState
import com.sepidsa.fortytwocalculator.ui.scientific.ScientificScreen
import com.sepidsa.fortytwocalculator.ui.dialogs.SettingsDialog
import com.sepidsa.fortytwocalculator.ui.theme.DmMono
import com.sepidsa.fortytwocalculator.ui.theme.VoidDarkBackground
import com.sepidsa.fortytwocalculator.data.LogEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
    onUseHistoryEntry: (String) -> Unit,
    onHistoryFilterChange: (com.sepidsa.fortytwocalculator.ui.history.HistoryFilter) -> Unit,
    onScientificKeyPress: (String) -> Unit,
    onInverseToggle: (Boolean) -> Unit,
    onArcToggle: (Boolean) -> Unit,
    onConstantClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSettingsLanguageChanged: (Int) -> Unit,
    onColorsClick: () -> Unit,
    onAddStarClick: () -> Unit,
    onAddLabelClick: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 2 })
    var showSettings by remember { mutableStateOf(false) }
    var showScientific by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidDarkBackground)
    ) {
        // Display area: expression + result + words
        CalculatorDisplay(
            state = calculatorState,
            onAddStarClick = onAddStarClick,
            onAddLabelClick = onAddLabelClick,
            onAngleModeChanged = onAngleModeToggle,
            modifier = Modifier.weight(0.38f)
        )

        // Pager: History | Calculator
        Column(modifier = Modifier.weight(0.62f)) {
            // SCI expand affordance — visible above the keypad at all times
            SciExpandButton(
                onClick = { showScientific = true },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

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
                        onUseEntry = { result ->
                            onUseHistoryEntry(result)
                            scope.launch { pagerState.animateScrollToPage(1) }
                        },
                        onClearAll = onClearHistory,
                        onShare = onShareHistoryItem,
                        onFilterChange = onHistoryFilterChange,
                    )

                    else -> CalculatorScreen(
                        state = calculatorState,
                        onKeyPress = onCalculatorKeyPress,
                        onAngleModeChanged = onAngleModeToggle
                    )
                }
            }

            PageIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 6.dp)
            )

            BottomActionBar(
                onSettingsClick = { showSettings = true },
                onColorsClick = onColorsClick,
            )
        }
    }

    if (showSettings) {
        SettingsDialog(
            onDismiss = { showSettings = false },
            onLanguageChanged = onSettingsLanguageChanged
        )
    }
    if (showScientific) {
        ModalBottomSheet(
            onDismissRequest = { showScientific = false },
            sheetState = sheetState,
            containerColor = Color(0xFF0E0E0E),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(
                topStart = 20.dp, topEnd = 20.dp,
            ),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(32.dp)
                        .height(4.dp)
                        .background(
                            Color.White.copy(alpha = 0.15f),
                            androidx.compose.foundation.shape.RoundedCornerShape(2.dp),
                        )
                )
            },
        ) {
            ScientificScreen(
                state = calculatorState,
                onKeyPress = { value ->
                    onScientificKeyPress(value)
                    showScientific = false
                },
                onInverseToggle = onInverseToggle,
                onArcToggle = onArcToggle,
                onAngleModeToggle = onAngleModeToggle,
                onConstantClick = onConstantClick,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Display area
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CalculatorDisplay(
    state: CalculatorUiState,
    onAddStarClick: () -> Unit,
    onAddLabelClick: () -> Unit,
    onAngleModeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Dynamic result font size based on character count
    val resultFontSize = when {
        state.result.length <= 4 -> 72.sp
        state.result.length <= 6 -> 56.sp
        state.result.length <= 8 -> 44.sp
        state.result.length <= 10 -> 36.sp
        else -> 28.sp
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(VoidDarkBackground)
            .padding(top = 44.dp, start = 20.dp, end = 20.dp, bottom = 14.dp)
    ) {
        // DEG/RAD — top right, tappable to toggle angle mode
        Text(
            text = if (state.angleMode) "DEG" else "RAD",
            fontFamily = DmMono,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable { onAngleModeChanged(!state.angleMode) }
                .padding(4.dp),
        )

        // History / star icon — top left
        Row(
            modifier = Modifier.align(Alignment.TopStart),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = "History",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f),
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onAddStarClick() },
            )
        }

        // Expression + result + words — bottom-aligned, right-aligned
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
        ) {
            // Expression line
            if (state.expression.isNotEmpty()) {
                Text(
                    text = state.expression,
                    fontFamily = DmMono,
                    fontWeight = FontWeight.Light,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                    maxLines = 1,
                    textAlign = TextAlign.End,
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Result
            Text(
                text = state.result,
                fontFamily = DmMono,
                fontWeight = FontWeight.Light,
                fontSize = resultFontSize,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                textAlign = TextAlign.End,
            )

            // Words — typewriter revealed after = press
            if (state.isCalculationPerformed && state.translatedResult.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.translatedResult,
                    fontFamily = DmMono,
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f),
                    maxLines = 1,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SCI expand button
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SciExpandButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
    Box(
        modifier = modifier
            .padding(vertical = 5.dp)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.10f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "SCI  ▲",
            fontFamily = DmMono,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.38f),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Page indicator + bottom bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun PageIndicator(pagerState: androidx.compose.foundation.pager.PagerState, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(pagerState.pageCount) { iteration ->
            val color = if (pagerState.currentPage == iteration) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f)
            }
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, CircleShape)
            )
        }
    }
}

@Composable
fun BottomActionBar(
    onSettingsClick: () -> Unit,
    onColorsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = onSettingsClick) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
            )
        }
        IconButton(onClick = onColorsClick) {
            Icon(
                Icons.Default.Palette,
                contentDescription = "Theme",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
            )
        }
    }
}

