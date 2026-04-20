package com.sepidsa.fortytwocalculator.ui.history

import androidx.lifecycle.ViewModel
import com.sepidsa.fortytwocalculator.data.LogContract
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class LogEntry(
    val id: Long,
    val expression: String,
    val result: String,
    val timestamp: Long,
    val starred: Boolean = false
)

data class HistoryUiState(
    val logEntries: List<LogEntry> = emptyList(),
    val isLoading: Boolean = false
)

class HistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _newLogEntry = MutableSharedFlow<LogEntry>()
    val newLogEntry: SharedFlow<LogEntry> = _newLogEntry.asSharedFlow()

    fun loadHistory() {
        // TODO: Load history from database
    }

    fun addLogEntry(expression: String, result: String) {
        // TODO: Add log entry to database
        val entry = LogEntry(
            id = System.currentTimeMillis(), // placeholder
            expression = expression,
            result = result,
            timestamp = System.currentTimeMillis()
        )
        _uiState.value = _uiState.value.copy(
            logEntries = _uiState.value.logEntries + entry
        )
        // Emit to SharedFlow
        // TODO: Use coroutine scope
    }

    fun starLogEntry(id: Long) {
        // TODO: Star/unstar log entry
    }

    fun deleteLogEntry(id: Long) {
        // TODO: Delete log entry
    }
}