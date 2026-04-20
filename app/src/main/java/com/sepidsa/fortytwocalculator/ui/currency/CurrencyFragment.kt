package com.sepidsa.fortytwocalculator.ui.currency

import android.app.Dialog
import android.database.Cursor
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.sepidsa.fortytwocalculator.data.ConstantContract

/**
 * Created by Farshid on 5/20/2015.
 */
class CurrencyFragment : DialogFragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private lateinit var mConstantUseAdapter: ConstantUseAdapter
    private lateinit var mListView: ListView
    private lateinit var mGotoSelect: Button
    private var mPosition: Int = ListView.INVALID_POSITION

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        loaderManager.initLoader(CONSTANT_LOADER, null, this)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mConstantUseAdapter = ConstantUseAdapter(requireActivity(), null, 0)
        val rootView = inflater.inflate(R.layout.fragment_constant_use, container, false)

        mListView = rootView.findViewById(R.id.listview_constant)
        mGotoSelect = rootView.findViewById(R.id.button_goto_select)
        mGotoSelect.typeface = Typeface.createFromAsset(requireActivity().assets, "yekan.ttf")

        val empty = rootView.findViewById<TextView>(R.id.empty_list)
        mListView.emptyView = empty
        mListView.adapter = mConstantUseAdapter

        mListView.onItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
            val resultView = view.findViewById<TextView>(R.id.constant_number)
            val result = resultView.text.toString()
            (activity as MainActivity).addNumberToCalculation(result)
            (activity as MainActivity).switchToMainFragment()
            dismiss()
        }

        mGotoSelect.setOnClickListener {
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

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        mConstantUseAdapter.swapCursor(data)
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mConstantUseAdapter.swapCursor(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(
            requireActivity(),
            ConstantContract.ConstantEntry.CONTENT_URI,
            CONSTANT_COLUMNS,
            ConstantContract.ConstantEntry.COLUMN_SELECTED + "=?",
            arrayOf("1"),
            null,
        )
    }

    private companion object {
        private const val SELECTED_KEY = "selected_position"
        private const val CONSTANT_LOADER = 0

        private val CONSTANT_COLUMNS = arrayOf(
            ConstantContract.ConstantEntry.TABLE_NAME + "." + ConstantContract.ConstantEntry._ID,
            ConstantContract.ConstantEntry.COLUMN_NAME,
            ConstantContract.ConstantEntry.COLUMN_NUMBER,
        )
    }
}

