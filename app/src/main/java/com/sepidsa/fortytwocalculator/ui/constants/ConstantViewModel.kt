package com.sepidsa.fortytwocalculator.ui.constants

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sepidsa.fortytwocalculator.data.AppDatabase
import com.sepidsa.fortytwocalculator.data.ConstantEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class ConstantsUiState(
    val constants: List<ConstantEntity> = emptyList(),
    val isLoading: Boolean = false
)

class ConstantViewModel(application: Application) : AndroidViewModel(application) {

    private val constantDao = AppDatabase.getInstance(application).constantDao()

    val uiState: StateFlow<ConstantsUiState> = constantDao.getAllConstants()
        .map { ConstantsUiState(constants = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ConstantsUiState(isLoading = true)
        )

    fun insertConstant(constant: ConstantEntity) {
        viewModelScope.launch {
            constantDao.insert(constant)
        }
    }

    fun addConstant(name: String, value: Double) {
        viewModelScope.launch {
            constantDao.insert(
                ConstantEntity(
                    name = name,
                    number = value
                )
            )
        }
    }

    fun updateConstant(id: Long, name: String, value: Double, selected: Int) {
        viewModelScope.launch {
            constantDao.update(
                ConstantEntity(
                    id = id,
                    name = name,
                    number = value,
                    selected = selected
                )
            )
        }
    }

    fun deleteConstant(id: Long) {
        viewModelScope.launch {
            constantDao.deleteById(id)
        }
    }

    fun selectConstant(id: Long, isSelected: Boolean) {
        viewModelScope.launch {
            constantDao.updateSelected(id, if (isSelected) 1 else 0)
        }
    }
}
