package com.sepidsa.fortytwocalculator.ui.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sepidsa.fortytwocalculator.data.LogEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onDelete: (Long) -> Unit,
    onStarToggle: (Long, Boolean) -> Unit,
    onUpdateTag: (Long, String) -> Unit,
    onUseResult: (String) -> Unit,
    onClearAll: (Boolean) -> Unit,
    onShare: (LogEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearDialog by remember { mutableStateOf(false) }
    var clearStarredOnly by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("History") },
            actions = {
                IconButton(onClick = { showClearDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear History")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(state.logEntries, key = { it.id }) { log ->
                HistoryItem(
                    log = log,
                    onDelete = { onDelete(log.id) },
                    onStarToggle = { onStarToggle(log.id, it) },
                    onUpdateTag = { onUpdateTag(log.id, it) },
                    onUseResult = { onUseResult(log.result) },
                    onShare = { onShare(log) }
                )
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear History") },
            text = {
                Column {
                    Text("Are you sure you want to clear the history?")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { clearStarredOnly = !clearStarredOnly }
                    ) {
                        Checkbox(
                            checked = clearStarredOnly,
                            onCheckedChange = { clearStarredOnly = it }
                        )
                        Text("Keep starred items")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onClearAll(!clearStarredOnly)
                    showClearDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun HistoryItem(
    log: LogEntity,
    onDelete: () -> Unit,
    onStarToggle: (Boolean) -> Unit,
    onUpdateTag: (String) -> Unit,
    onUseResult: () -> Unit,
    onShare: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showTagDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (!log.tag.isNullOrEmpty()) {
                        Text(
                            text = log.tag!!,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = log.operation,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = log.result,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(onClick = { onStarToggle(log.starred == 0) }) {
                    Icon(
                        imageVector = if (log.starred != 0) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Star",
                        tint = if (log.starred != 0) Color(0xFFFFC107) else LocalContentColor.current
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                    IconButton(onClick = { showTagDialog = true }) {
                        Icon(Icons.Default.Label, contentDescription = "Label")
                    }
                    IconButton(onClick = onShare) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    Button(onClick = onUseResult) {
                        Text("Use")
                    }
                }
            }
        }
    }

    if (showTagDialog) {
        var tagText by remember { mutableStateOf(log.tag ?: "") }
        AlertDialog(
            onDismissRequest = { showTagDialog = false },
            title = { Text("Edit Label") },
            text = {
                OutlinedTextField(
                    value = tagText,
                    onValueChange = { tagText = it },
                    label = { Text("Label") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onUpdateTag(tagText)
                    showTagDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTagDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
