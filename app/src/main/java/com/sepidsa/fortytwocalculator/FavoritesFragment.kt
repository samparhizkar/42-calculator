package com.sepidsa.fortytwocalculator

import android.app.Dialog
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.sepidsa.fortytwocalculator.data.LogContract

/**
 * Created by Farshid on 5/20/2015.
 */
class FavoritesFragment : DialogFragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private lateinit var mFavoritesAdapter: FavoritesAdapter
    private lateinit var mListView: ListView
    private var mPosition: Int = ListView.INVALID_POSITION

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        loaderManager.initLoader(LOG_LOADER, null, this)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mFavoritesAdapter = FavoritesAdapter(requireActivity(), null, 0)
        val rootView = inflater.inflate(R.layout.fragment_favorite, container, false)

        mListView = rootView.findViewById(R.id.listview_log)
        val empty = rootView.findViewById<TextView>(R.id.empty_list)
        mListView.emptyView = empty
        mListView.adapter = mFavoritesAdapter

        mListView.onItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
            val resultView = view.findViewById<TextView>(R.id.result)
            val result = resultView.text.toString()
            (activity as MainActivity).addNumberToCalculation(result)
            (activity as MainActivity).switchToMainFragment()
            dismiss()
        }

        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        mFavoritesAdapter.swapCursor(data)
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mFavoritesAdapter.swapCursor(null)
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
            LogContract.LogEntry.CONTENT_URI,
            LOG_COLUMNS,
            LogContract.LogEntry.COLUMN_STARRED + "=?",
            arrayOf("1"),
            null,
        )
    }

    private companion object {
        private const val SELECTED_KEY = "selected_position"
        private const val LOG_LOADER = 0

        private val LOG_COLUMNS = arrayOf(
            LogContract.LogEntry.TABLE_NAME + "." + LogContract.LogEntry._ID,
            LogContract.LogEntry.COLUMN_RESULT,
            LogContract.LogEntry.COLUMN_OPERATION,
            LogContract.LogEntry.COLUMN_TAG,
            LogContract.LogEntry.COLUMN_STARRED,
        )
    }
}

