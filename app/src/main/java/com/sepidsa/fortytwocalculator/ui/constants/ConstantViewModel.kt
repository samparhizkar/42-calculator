package com.sepidsa.fortytwocalculator.ui.constants

import androidx.lifecycle.ViewModel
import com.sepidsa.fortytwocalculator.data.ConstantContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class ConstantEntry(
    val id: Long,
    val name: String,
    val value: BigDecimal,
    val description: String? = null
)

data class ConstantsUiState(
    val constants: List<ConstantEntry> = emptyList(),
    val isLoading: Boolean = false
)

class ConstantViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ConstantsUiState())
    val uiState: StateFlow<ConstantsUiState> = _uiState.asStateFlow()

    fun loadConstants() {
        // TODO: Load constants from database
    }

    fun addConstant(name: String, value: BigDecimal, description: String?) {
        // TODO: Add constant to database
    }

    fun updateConstant(id: Long, name: String, value: BigDecimal, description: String?) {
        // TODO: Update constant in database
    }

    fun deleteConstant(id: Long) {
        // TODO: Delete constant from database
    }
}