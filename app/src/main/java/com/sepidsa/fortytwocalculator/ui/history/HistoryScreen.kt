package com.sepidsa.fortytwocalculator.ui.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sepidsa.fortytwocalculator.data.LogEntity
import com.sepidsa.fortytwocalculator.ui.theme.DmMono
import com.sepidsa.fortytwocalculator.ui.theme.Inter
import com.sepidsa.fortytwocalculator.ui.theme.VoidDarkBackground
import kotlinx.coroutines.launch

// ── VOID palette constants ──────────────────────────────────────────────────
private val VoidSurface = Color(0xFF0E1413)
private val VoidOutline = Color(0xFF3F4948)
private val VoidPrimary = Color(0xFF80D5C6)
private val VoidOnSurfaceVariant = Color(0xFFBEC9C7)
private val VoidBrand = Color(0xFF1ABC9C)
private val VoidBrandMuted = Color(0x211ABC9C)   // rgba(26,188,156,0.13)
private val VoidSectionHeader = Color(0x40FFFFFF) // rgba(255,255,255,0.25)
private val VoidExpressionText = Color(0x47FFFFFF) // rgba(255,255,255,0.28)
private val VoidWordsText = Color(0x2EFFFFFF)    // rgba(255,255,255,0.18)
private val VoidStarAmber = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onDelete: (Long) -> Unit,
    onStarToggle: (Long, Boolean) -> Unit,
    onUpdateTag: (Long, String) -> Unit,
    onUseEntry: (String) -> Unit,
    onClearAll: (Boolean) -> Unit,
    onShare: (LogEntity) -> Unit,
    onFilterChange: (HistoryFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showClearDialog by remember { mutableStateOf(false) }
    var clearKeepStarred by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VoidDarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Filter chips ─────────────────────────────────────────────
            FilterChipRow(
                selected = state.filter,
                onSelect = onFilterChange,
            )

            if (state.isEmpty) {
                // ── Empty state ──────────────────────────────────────────
                HistoryEmptyState(
                    filter = state.filter,
                    modifier = Modifier.weight(1f),
                )
            } else {
                // ── History list ─────────────────────────────────────────
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    state.sections.forEach { section ->
                        item(key = "header_${section.label}") {
                            SectionHeader(label = section.label)
                        }
                        items(section.items, key = { it.id }) { log ->
                            SwipeToDeleteRow(
                                onDelete = {
                                    onDelete(log.id)
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Deleted",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Short,
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            // Restore handled by caller via onDelete undo path
                                        }
                                    }
                                },
                            ) {
                                HistoryRow(
                                    log = log,
                                    onStarToggle = { onStarToggle(log.id, it) },
                                    onUpdateTag = { onUpdateTag(log.id, it) },
                                    onUse = { onUseEntry(log.result) },
                                    onShare = { onShare(log) },
                                    onDelete = { onDelete(log.id) },
                                )
                            }
                        }
                    }
                }
            }

            // ── Clear button row ─────────────────────────────────────────
            TextButton(
                onClick = { showClearDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            ) {
                Text(
                    "Clear History",
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    fontFamily = Inter,
                    fontSize = 13.sp,
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = VoidSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            title = { Text("Clear History", fontFamily = Inter, fontWeight = FontWeight.Medium) },
            text = {
                Column {
                    Text(
                        "Remove all history entries?",
                        fontFamily = Inter,
                        color = VoidOnSurfaceVariant,
                        fontSize = 14.sp,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { clearKeepStarred = !clearKeepStarred },
                    ) {
                        Checkbox(checked = clearKeepStarred, onCheckedChange = { clearKeepStarred = it })
                        Text("Keep starred items", fontFamily = Inter, fontSize = 14.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onClearAll(!clearKeepStarred)
                    showClearDialog = false
                }) { Text("Clear", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
            },
        )
    }
}

// ── Filter chip row ─────────────────────────────────────────────────────────

@Composable
private fun FilterChipRow(
    selected: HistoryFilter,
    onSelect: (HistoryFilter) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HistoryFilterChip(label = "All",       active = selected == HistoryFilter.ALL,     onClick = { onSelect(HistoryFilter.ALL) })
        HistoryFilterChip(label = "★ Starred", active = selected == HistoryFilter.STARRED, onClick = { onSelect(HistoryFilter.STARRED) })
        HistoryFilterChip(label = "🏷 Labeled", active = selected == HistoryFilter.LABELED, onClick = { onSelect(HistoryFilter.LABELED) })
    }
}

@Composable
private fun HistoryFilterChip(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    val bg by animateColorAsState(if (active) VoidBrandMuted else Color.Transparent, label = "chipBg")
    val textColor by animateColorAsState(if (active) VoidBrand else VoidOnSurfaceVariant, label = "chipText")
    val borderColor by animateColorAsState(if (active) VoidBrand.copy(alpha = 0.4f) else VoidOutline.copy(alpha = 0.5f), label = "chipBorder")

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(0.5.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontFamily = DmMono,
            fontSize = 12.sp,
            color = textColor,
        )
    }
}

// ── Date section header ─────────────────────────────────────────────────────

@Composable
private fun SectionHeader(label: String) {
    Text(
        text = label.uppercase(),
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 1.6.sp,
        color = VoidSectionHeader,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 6.dp),
    )
}

// ── Swipe-to-delete wrapper ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteRow(
    onDelete: () -> Unit,
    content: @Composable () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.4f },
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFF93000A).copy(alpha = 0.85f)
                    else -> Color.Transparent
                },
                label = "swipeBg",
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                    )
                }
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
    ) {
        content()
    }
}

// ── History row card ────────────────────────────────────────────────────────

@Composable
private fun HistoryRow(
    log: LogEntity,
    onStarToggle: (Boolean) -> Unit,
    onUpdateTag: (String) -> Unit,
    onUse: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showTagDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(VoidSurface)
            .border(0.5.dp, VoidOutline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded }
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Label chip (shown regardless of star state — fixes dead-label bug)
                    if (log.tag.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 6.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(VoidBrandMuted)
                                .border(0.5.dp, VoidBrand.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                        ) {
                            Text(
                                text = log.tag,
                                fontFamily = DmMono,
                                fontSize = 11.sp,
                                color = VoidBrand,
                            )
                        }
                    }

                    // Expression
                    Text(
                        text = log.operation,
                        fontFamily = DmMono,
                        fontWeight = FontWeight.Light,
                        fontSize = 13.sp,
                        color = VoidExpressionText,
                        maxLines = 1,
                    )

                    Spacer(Modifier.height(4.dp))

                    // Result (large)
                    Text(
                        text = log.result,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Light,
                        fontSize = 34.sp,
                        color = VoidPrimary,
                        maxLines = 1,
                    )

                    // Words / spoken form
                    if (log.words.isNotEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = log.words,
                            fontFamily = DmMono,
                            fontWeight = FontWeight.Light,
                            fontStyle = FontStyle.Italic,
                            fontSize = 11.sp,
                            color = VoidWordsText,
                            maxLines = 1,
                        )
                    }
                }

                // Star toggle
                IconButton(
                    onClick = { onStarToggle(log.starred == 0) },
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        imageVector = if (log.starred != 0) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = if (log.starred != 0) "Unstar" else "Star",
                        tint = if (log.starred != 0) VoidStarAmber else VoidOnSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            // Expanded action row
            AnimatedVisibility(visible = expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    // Delete
                    ActionChip(
                        icon = { Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(14.dp)) },
                        label = "Delete",
                        onClick = onDelete,
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    )
                    // Label
                    ActionChip(
                        icon = { Icon(Icons.Default.Label, contentDescription = "Label", modifier = Modifier.size(14.dp)) },
                        label = "Label",
                        onClick = { showTagDialog = true },
                        tint = VoidOnSurfaceVariant,
                    )
                    // Share
                    ActionChip(
                        icon = { Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(14.dp)) },
                        label = "Share",
                        onClick = onShare,
                        tint = VoidOnSurfaceVariant,
                    )
                    // Use
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(VoidBrandMuted)
                            .border(0.5.dp, VoidBrand.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .clickable(onClick = onUse)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "Use",
                            fontFamily = DmMono,
                            fontSize = 12.sp,
                            color = VoidBrand,
                        )
                    }
                }
            }
        }
    }

    if (showTagDialog) {
        TagEditDialog(
            current = log.tag,
            onConfirm = { onUpdateTag(it); showTagDialog = false },
            onDismiss = { showTagDialog = false },
        )
    }
}

@Composable
private fun ActionChip(
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit,
    tint: Color,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(0.5.dp, VoidOutline.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        CompositionLocalProvider(LocalContentColor provides tint) {
            icon()
            Text(
                text = label,
                fontFamily = DmMono,
                fontSize = 11.sp,
                color = tint,
            )
        }
    }
}

// ── Tag dialog ──────────────────────────────────────────────────────────────

@Composable
private fun TagEditDialog(
    current: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember(current) { mutableStateOf(current) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VoidSurface,
        title = { Text("Edit Label", fontFamily = Inter, fontWeight = FontWeight.Medium) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Label", fontFamily = Inter) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VoidBrand,
                    focusedLabelColor = VoidBrand,
                    cursorColor = VoidBrand,
                ),
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) {
                Text("OK", color = VoidBrand)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

// ── Empty states ────────────────────────────────────────────────────────────

@Composable
private fun HistoryEmptyState(filter: HistoryFilter, modifier: Modifier = Modifier) {
    val (emoji, line1, line2) = when (filter) {
        HistoryFilter.ALL -> Triple("🧮", "No calculations yet.", "Results will appear here after you press =.")
        HistoryFilter.STARRED -> Triple("★", "Nothing starred yet.", "Tap the star on any result to save it here.")
        HistoryFilter.LABELED -> Triple("🏷", "No labeled entries.", "Add a label to any calculation to find it here.")
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(emoji, fontSize = 36.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            text = line1,
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = VoidOnSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = line2,
            fontFamily = Inter,
            fontSize = 13.sp,
            color = VoidOnSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
    }
}
