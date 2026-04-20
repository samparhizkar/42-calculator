package com.sepidsa.fortytwocalculator.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sepidsa.fortytwocalculator.data.AppDatabase
import com.sepidsa.fortytwocalculator.data.LogEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.async

data class HistoryUiState(
    val logEntries: List<LogEntity> = emptyList()
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val logDao = AppDatabase.getInstance(application).logDao()

    val uiState: StateFlow<HistoryUiState> = logDao.getAllLogs()
        .map { HistoryUiState(logEntries = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HistoryUiState()
        )

    fun addLogEntry(expression: String, result: String): kotlinx.coroutines.Deferred<Long> {
        return viewModelScope.async {
            logDao.insert(
                LogEntity(
                    operation = expression,
                    result = result,
                    resultNoComma = result.replace(",", "")
                )
            )
        }
    }

    fun starLogEntry(id: Long, isStarred: Boolean) {
        viewModelScope.launch {
            logDao.updateStarred(id, if (isStarred) 1 else 0)
        }
    }

    fun deleteLogEntry(id: Long) {
        viewModelScope.launch {
            logDao.deleteById(id)
        }
    }

    fun updateTag(id: Long, tag: String) {
        viewModelScope.launch {
            logDao.updateTag(id, tag)
        }
    }

    suspend fun getLogById(id: Long): LogEntity? {
        return logDao.getLogById(id)
    }

    fun clearHistory(clearStarred: Boolean) {
        viewModelScope.launch {
            if (clearStarred) {
                logDao.deleteAll()
            } else {
                logDao.deleteNonStarred()
            }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            logDao.deleteAll()
        }
    }
}
