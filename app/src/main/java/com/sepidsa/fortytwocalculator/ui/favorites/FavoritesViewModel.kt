package com.sepidsa.fortytwocalculator.ui.favorites

import androidx.lifecycle.ViewModel
import com.sepidsa.fortytwocalculator.data.LogContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FavoriteEntry(
    val id: Long,
    val expression: String,
    val result: String,
    val timestamp: Long
)

data class FavoritesUiState(
    val favoriteEntries: List<FavoriteEntry> = emptyList(),
    val isLoading: Boolean = false
)

class FavoritesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    fun loadFavorites() {
        // TODO: Load favorite entries from database
    }

    fun addToFavorites(expression: String, result: String) {
        // TODO: Add entry to favorites
    }

    fun removeFromFavorites(id: Long) {
        // TODO: Remove entry from favorites
    }
}