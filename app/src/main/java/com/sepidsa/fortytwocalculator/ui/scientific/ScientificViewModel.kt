package com.sepidsa.fortytwocalculator.ui.scientific

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ScientificUiState(
    val isInverseMode: Boolean = false,
    val isArcMode: Boolean = false,
    val angleMode: Boolean = false
)

class ScientificViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ScientificUiState())
    val uiState: StateFlow<ScientificUiState> = _uiState.asStateFlow()

    fun toggleInverseMode() {
        _uiState.value = _uiState.value.copy(isInverseMode = !_uiState.value.isInverseMode)
    }

    fun toggleArcMode() {
        _uiState.value = _uiState.value.copy(isArcMode = !_uiState.value.isArcMode)
    }

    fun setAngleMode(isDegree: Boolean) {
        _uiState.value = _uiState.value.copy(angleMode = isDegree)
    }
}