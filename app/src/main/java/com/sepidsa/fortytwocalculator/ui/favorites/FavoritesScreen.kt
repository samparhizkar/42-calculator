package com.sepidsa.fortytwocalculator.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun FavoritesScreen(
    state: FavoritesUiState,
    onRemove: (Long) -> Unit,
    onUseResult: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.favoriteEntries.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No favorites yet", style = MaterialTheme.typography.bodyMedium)
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.favoriteEntries, key = { it.id }) { entry ->
                FavoritesEntryItem(
                    entry = entry,
                    onRemove = { onRemove(entry.id) },
                    onUseResult = { onUseResult(entry.result) }
                )
            }
        }
    }
}

@Composable
fun FavoritesEntryItem(
    entry: FavoriteEntry,
    onRemove: () -> Unit,
    onUseResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = entry.expression,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "= ${entry.result}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = onUseResult,
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Use", style = MaterialTheme.typography.labelSmall)
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
