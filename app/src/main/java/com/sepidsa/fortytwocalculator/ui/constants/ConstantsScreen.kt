package com.sepidsa.fortytwocalculator.ui.constants

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sepidsa.fortytwocalculator.data.ConstantEntity

@Composable
fun ConstantsScreen(
    state: ConstantsUiState,
    onAddConstant: (String, Double) -> Unit,
    onDeleteConstant: (Long) -> Unit,
    onSelectConstant: (Long, Boolean) -> Unit,
    onUseConstant: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (state.constants.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No constants yet", style = MaterialTheme.typography.bodyMedium)
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.constants, key = { it.id }) { constant ->
                ConstantItem(
                    constant = constant,
                    onDelete = { onDeleteConstant(constant.id) },
                    onToggleSelect = { selected ->
                        onSelectConstant(constant.id, selected)
                    },
                    onUse = { onUseConstant(constant.number) }
                )
            }
        }
    }
}

@Composable
fun ConstantItem(
    constant: ConstantEntity,
    onDelete: () -> Unit,
    onToggleSelect: (Boolean) -> Unit,
    onUse: () -> Unit,
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
                    text = constant.name,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = constant.number.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = constant.selected == 1,
                    onCheckedChange = onToggleSelect
                )
                Button(
                    onClick = onUse,
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Use", style = MaterialTheme.typography.labelSmall)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
