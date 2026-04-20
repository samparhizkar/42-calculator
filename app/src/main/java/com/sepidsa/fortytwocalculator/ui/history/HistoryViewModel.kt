package com.sepidsa.fortytwocalculator.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sepidsa.fortytwocalculator.data.AppDatabase
import com.sepidsa.fortytwocalculator.data.LogEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class HistoryFilter { ALL, STARRED, LABELED }

data class HistorySection(
    val label: String,
    val items: List<LogEntity>,
)

data class HistoryUiState(
    val sections: List<HistorySection> = emptyList(),
    val filter: HistoryFilter = HistoryFilter.ALL,
    val isEmpty: Boolean = true,
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val logDao = AppDatabase.getInstance(application).logDao()

    private val _filter = MutableStateFlow(HistoryFilter.ALL)

    val uiState: StateFlow<HistoryUiState> = combine(
        logDao.getAllLogs(),
        _filter,
    ) { logs, filter ->
        val filtered = when (filter) {
            HistoryFilter.ALL -> logs
            HistoryFilter.STARRED -> logs.filter { it.starred != 0 }
            HistoryFilter.LABELED -> logs.filter { it.tag.isNotEmpty() }
        }
        HistoryUiState(
            sections = groupByDate(filtered),
            filter = filter,
            isEmpty = filtered.isEmpty(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState(),
    )

    fun setFilter(filter: HistoryFilter) {
        _filter.value = filter
    }

    fun addLogEntry(expression: String, result: String, words: String = ""): kotlinx.coroutines.Deferred<Long> {
        return viewModelScope.async {
            logDao.insert(
                LogEntity(
                    operation = expression,
                    result = result,
                    resultNoComma = result.replace(",", ""),
                    words = words,
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

    /** Deletes the entry immediately; caller holds the LogEntity to offer undo. */
    fun deleteWithUndo(log: LogEntity, onDeleted: () -> Unit) {
        viewModelScope.launch {
            logDao.deleteById(log.id)
            onDeleted()
        }
    }

    fun restoreLogEntry(log: LogEntity) {
        viewModelScope.launch {
            logDao.insert(log.copy(id = 0))
        }
    }

    fun updateTag(id: Long, tag: String) {
        viewModelScope.launch {
            logDao.updateTag(id, tag)
        }
    }

    suspend fun getLogById(id: Long): LogEntity? = logDao.getLogById(id)

    fun clearHistory(clearStarred: Boolean) {
        viewModelScope.launch {
            if (clearStarred) logDao.deleteAll() else logDao.deleteNonStarred()
        }
    }

    fun clearAll() {
        viewModelScope.launch { logDao.deleteAll() }
    }

    private fun groupByDate(logs: List<LogEntity>): List<HistorySection> {
        if (logs.isEmpty()) return emptyList()

        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val todayMs = todayCal.timeInMillis
        val yesterdayMs = todayMs - 86_400_000L
        val displayFmt = SimpleDateFormat("MMM d", Locale.getDefault())

        return logs
            .groupBy { entry ->
                when {
                    entry.createdAt >= todayMs -> "Today"
                    entry.createdAt >= yesterdayMs -> "Yesterday"
                    else -> displayFmt.format(Date(entry.createdAt))
                }
            }
            .map { (label, items) -> HistorySection(label, items) }
    }
}
