package com.sepidsa.fortytwocalculator.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepidsa.fortytwocalculator.MainActivity
import com.sepidsa.fortytwocalculator.ui.theme.AppTheme

class CalculatorFragment : Fragment() {

    private val viewModel: CalculatorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                AppTheme {
                    CalculatorScreen(
                        state = state,
                        onKeyPress = viewModel::onButtonPressed,
                        onAngleModeChanged = ::onAngleModeChanged,
                        modifier = Modifier,
                    )
                }
            }
        }
    }

    fun setClearButtonText(input: String) {
        // Kept for legacy compatibility if called from MainActivity, but logic is now in ViewModel
    }

    fun changeFontThickness() {
        // Kept for legacy compatibility if called from MainActivity
    }

    private fun onAngleModeChanged(isDegree: Boolean) {
        viewModel.setAngleMode(isDegree)
        (activity as? MainActivity)?.setAngleMode(isDegree)
    }
}
