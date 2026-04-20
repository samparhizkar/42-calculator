package com.sepidsa.fortytwocalculator.ui.constants

import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.sepidsa.fortytwocalculator.MainActivity
import com.sepidsa.fortytwocalculator.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ConstantUseFragment : DialogFragment() {

    private val viewModel: ConstantViewModel by viewModels()
    private lateinit var constantUseAdapter: ConstantUseAdapter
    private lateinit var listView: ListView
    private lateinit var gotoSelect: Button
    private var position: Int = ListView.INVALID_POSITION

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState
                .map { state -> state.constants.filter { it.selected != 0 } }
                .collectLatest { selectedConstants ->
                    constantUseAdapter.updateConstants(selectedConstants)
                    if (position != ListView.INVALID_POSITION) {
                        listView.smoothScrollToPosition(position)
                        position = ListView.INVALID_POSITION
                    }
                }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        constantUseAdapter = ConstantUseAdapter(requireActivity())
        val rootView = inflater.inflate(R.layout.fragment_constant_use, container, false)

        listView = rootView.findViewById(R.id.listview_constant)
        gotoSelect = rootView.findViewById(R.id.button_goto_select)
        gotoSelect.typeface = Typeface.createFromAsset(requireActivity().assets, "yekan.ttf")

        val empty = rootView.findViewById<TextView>(R.id.empty_list)
        listView.emptyView = empty
        listView.adapter = constantUseAdapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
            val resultView = view.findViewById<TextView>(R.id.constant_number)
            val result = resultView.text.toString()
            (activity as MainActivity).addNumberToCalculation(result)
            (activity as MainActivity).switchToMainFragment()
            dismiss()
        }

        gotoSelect.setOnClickListener {
            val fm: FragmentManager = (activity as MainActivity).supportFragmentManager
            val constantSelectDialog = ConstantSelectFragment()
            constantSelectDialog.show(fm, "fragment_constant_select")
        }

        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (position != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, position)
        }
        super.onSaveInstanceState(outState)
    }

    private companion object {
        private const val SELECTED_KEY = "selected_position"
    }
}
