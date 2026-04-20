package com.sepidsa.fortytwocalculator

import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.appcompat.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.CheckBox
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import com.sepidsa.fortytwocalculator.data.LogContract

/**
 * Created by Farshid on 5/17/2015.
 */
class AnimatedLogFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private val LOG_TAG_ = AnimatedLogFragment::class.java.simpleName

    private lateinit var mLogAdapter: LogAdapter
    private lateinit var mListView: ListView
    private lateinit var mClearButton: Button
    private lateinit var mExpandButton: Button
    private lateinit var mSearchView: SearchView
    private var mPosition: Int = ListView.INVALID_POSITION
    private val mLogFragment: Fragment = this

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        loaderManager.initLoader(LOG_LOADER, null, this)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mLogAdapter = LogAdapter(requireActivity(), null, 0)
        val rootView = inflater.inflate(R.layout.fragment_log, container, false)

        mListView = rootView.findViewById(R.id.listview_log)
        mClearButton = rootView.findViewById(R.id.button_clear_log)
        mExpandButton = rootView.findViewById(R.id.button_export_log)
        mSearchView = rootView.findViewById(R.id.log_search_view)

        val id = mSearchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val textView = mSearchView.findViewById<TextView>(id)
        textView.setTextColor((activity as MainActivity).accentColorCode)

        // setting icon typefaces for these 2 buttons
        mClearButton.typeface = Typeface.createFromAsset(requireActivity().assets, "flaticon.ttf")
        mExpandButton.typeface = Typeface.createFromAsset(requireActivity().assets, "flaticon.ttf")
        mExpandButton.setTextColor((activity as MainActivity).dialpadFontColor)
        mClearButton.setTextColor((activity as MainActivity).dialpadFontColor)

        val empty = rootView.findViewById<View>(R.id.empty_view)
        mListView.emptyView = empty
        mListView.adapter = mLogAdapter
        loaderManager.restartLoader(0, null, mLogFragment as LoaderManager.LoaderCallbacks<Cursor>)

        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String): Boolean {
                mSearchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNullOrEmpty()) {
                    loaderManager.restartLoader(0, null, mLogFragment as LoaderManager.LoaderCallbacks<Cursor>)
                } else {
                    val filter = Bundle()
                    filter.putString("filter", newText)
                    loaderManager.restartLoader(0, filter, mLogFragment as LoaderManager.LoaderCallbacks<Cursor>)
                }

                return true
            }
        })

        mListView.onItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
            val toolbar = view.findViewById<View>(R.id.toolbar)

            // Creating the expand animation for the item
            val expandAni = ExpandAnimation(requireActivity(), mListView, view, toolbar, 250)

            // Start the animation on the toolbar
            toolbar.startAnimation(expandAni)
        }

        mClearButton.setOnClickListener {
            val cursor = requireActivity().contentResolver.query(
                LogContract.LogEntry.CONTENT_URI,
                null,
                null,
                null,
                null,
            )

            if (cursor != null) {
                val hasAny = cursor.moveToNext()
                cursor.close()

                if (hasAny) {
                    val checkBoxView = View.inflate(requireActivity(), R.layout.checkbox, null)
                    val checkBox = checkBoxView.findViewById<CheckBox>(R.id.checkbox)

                    checkBox.text = requireActivity().getString(R.string.farsi_clear_starred)

                    val builder = AlertDialog.Builder(requireActivity())
                    builder.setTitle(requireActivity().getString(R.string.farsi_clear_log))
                    builder.setMessage(requireActivity().getString(R.string.farsi_clear_log_confirm))
                        .setView(checkBoxView)
                        .setCancelable(false)
                        .setPositiveButton(requireActivity().getString(R.string.farsi_yes)) { _: DialogInterface, _: Int ->
                            if (checkBox.isChecked) {
                                requireActivity().contentResolver.delete(LogContract.LogEntry.CONTENT_URI, null, null)
                            } else {
                                requireActivity().contentResolver.delete(
                                    LogContract.LogEntry.CONTENT_URI,
                                    LogContract.LogEntry.COLUMN_STARRED + "!=?",
                                    arrayOf("1"),
                                )
                            }
                            (activity as MainActivity).playSound((activity as MainActivity).clearSoundID)
                        }
                        .setNegativeButton(requireActivity().getString(R.string.farsi_no)) { dialog, _ ->
                            dialog.cancel()
                        }.show()
                } else {
                    Toast.makeText(requireActivity(), requireActivity().getString(R.string.farsi_list_is_empty), Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireActivity(), requireActivity().getString(R.string.farsi_list_is_empty), Toast.LENGTH_SHORT).show()
            }
        }

        mExpandButton.setOnClickListener {
            var shareText = ""
            val cursor = requireActivity().contentResolver.query(
                LogContract.LogEntry.CONTENT_URI,
                null,
                null,
                null,
                null,
            )

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val result = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_RESULT))
                    val operation = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_OPERATION))
                    var tag = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_TAG))
                    val starred = cursor.getInt(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_STARRED)) != 0

                    if (tag != "") tag += " :\n"
                    var starString = ""
                    if (starred) starString = " (*)"

                    shareText += "$tag$operation = $result$starString\n\n"
                }
                cursor.close()
            }

            if (shareText != "") {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                val subject = "42 Calculations"
                shareText += "\nGenerated by 42 calculator42."

                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText)

                requireActivity().startActivity(Intent.createChooser(sharingIntent, "Share"))
            } else {
                Toast.makeText(requireActivity(), requireActivity().getString(R.string.farsi_list_is_empty), Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        mLogAdapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mLogAdapter.swapCursor(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mPosition = mListView.lastVisiblePosition
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return if (args == null) {
            CursorLoader(
                requireActivity(),
                LogContract.LogEntry.CONTENT_URI,
                LOG_COLUMNS,
                null,
                null,
                null,
            )
        } else {
            val filter = args.getString("filter")
            CursorLoader(
                requireActivity(),
                LogContract.LogEntry.CONTENT_URI,
                LOG_COLUMNS,
                LogContract.LogEntry.COLUMN_TAG + " like ?" + " or " +
                    LogContract.LogEntry.COLUMN_RESULT_NO_COMMA + " like ?" + " or " +
                    LogContract.LogEntry.COLUMN_RESULT + " like ?" + " or " +
                    LogContract.LogEntry.COLUMN_OPERATION + " like ?",
                arrayOf("%$filter%", "%$filter%", "%$filter%", "%$filter%"),
                null,
            )
        }
    }

    fun scrollToLast() {
        mListView.smoothScrollToPosition(mLogAdapter.count + 1)
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

