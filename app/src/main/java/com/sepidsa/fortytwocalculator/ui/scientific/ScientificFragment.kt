package com.sepidsa.fortytwocalculator.ui.scientific

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepidsa.fortytwocalculator.MainActivity
import com.sepidsa.fortytwocalculator.R
import com.sepidsa.fortytwocalculator.ui.calculator.CalculatorViewModel
import com.sepidsa.fortytwocalculator.ui.constants.ConstantUseFragment
import com.sepidsa.fortytwocalculator.ui.theme.AppTheme

/**
 * Created by Ehsan on 5/29/14.
 */
class ScientificFragment : Fragment() {

    private val viewModel: CalculatorViewModel by viewModels({ requireActivity() })

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
                    ScientificScreen(
                        state = state,
                        onKeyPress = { value ->
                            viewModel.onButtonPressed(value)
                            (activity as? MainActivity)?.switchToMainFragment()
                        },
                        onInverseToggle = viewModel::setInverseMode,
                        onArcToggle = viewModel::setArcMode,
                        onAngleModeToggle = { isDeg ->
                            viewModel.setAngleMode(isDeg)
                            (activity as? MainActivity)?.setAngleMode(isDeg)
                        },
                        onConstantClick = {
                            val fm: FragmentManager = (activity as MainActivity).supportFragmentManager
                            val constantUseDialog = ConstantUseFragment()
                            constantUseDialog.show(fm, "fragment_constant_use")
                        },
                        modifier = Modifier,
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_MESSAGE: String = "EXTRA_MESSAGE"

        @JvmStatic
        fun newInstance(message: String): ScientificFragment {
            val f = ScientificFragment()
            val bdl = Bundle(1)
            bdl.putString(EXTRA_MESSAGE, message)
            f.arguments = bdl
            return f
        }
    }
}
