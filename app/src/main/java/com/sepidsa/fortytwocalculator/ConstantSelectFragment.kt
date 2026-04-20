package com.sepidsa.fortytwocalculator

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.database.Cursor
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import android.text.InputType
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.sepidsa.fortytwocalculator.data.ConstantContract

/**
 * Created by Farshid on 5/20/2015.
 */
class ConstantSelectFragment : DialogFragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private lateinit var mConstantSelectAdapter: ConstantSelectAdapter
    private lateinit var mListView: ListView
    private lateinit var mAddButton: Button
    private var mPosition: Int = ListView.INVALID_POSITION

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        loaderManager.initLoader(CONSTANT_LOADER, null, this)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mConstantSelectAdapter = ConstantSelectAdapter(requireActivity(), null, 0)
        val rootView = inflater.inflate(R.layout.fragment_constant_select, container, false)

        mListView = rootView.findViewById(R.id.listview_constant)
        mAddButton = rootView.findViewById(R.id.button_add_item)
        mAddButton.typeface = Typeface.createFromAsset(requireActivity().assets, "yekan.ttf")
        val empty = rootView.findViewById<TextView>(R.id.empty_list)
        mListView.emptyView = empty
        mListView.adapter = mConstantSelectAdapter

        mListView.onItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
            val resultView = view.findViewById<TextView>(R.id.constant_number)
            val result = resultView.text.toString()
            (activity as MainActivity).addNumberToCalculation(result)
            (activity as MainActivity).switchToMainFragment()
            dismiss()
        }

        mAddButton.setOnClickListener {
            val li = LayoutInflater.from(requireActivity())
            val promptsView = li.inflate(R.layout.constant_input_dialog, null)
            val builder = AlertDialog.Builder(requireActivity())

            builder.setTitle(requireActivity().getString(R.string.farsi_enter_constant))
            builder.setCancelable(false)

            val name = promptsView.findViewById<EditText>(R.id.input_name)
            val number = promptsView.findViewById<EditText>(R.id.input_number)
            val inputMethodManager =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

            name.inputType = InputType.TYPE_CLASS_TEXT
            number.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            builder.setView(promptsView)
            name.requestFocus()
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

            builder.setPositiveButton(requireActivity().getString(R.string.farsi_ok)) { dialog: DialogInterface, _: Int ->
                try {
                    val newName = name.text.toString()
                    val newNumber = number.text.toString().toDouble()
                    val values = ContentValues()
                    values.put(ConstantContract.ConstantEntry.COLUMN_NAME, newName)
                    values.put(ConstantContract.ConstantEntry.COLUMN_NUMBER, newNumber)
                    values.put(ConstantContract.ConstantEntry.COLUMN_SELECTED, 1)

                    requireActivity().contentResolver.insert(
                        ConstantContract.ConstantEntry.CONTENT_URI,
                        values,
                    )
                    inputMethodManager.hideSoftInputFromWindow(name.windowToken, 0)
                } catch (nfe: NumberFormatException) {
                    dialog.cancel()
                }
            }
            builder.setNegativeButton(requireActivity().getString(R.string.farsi_cancel)) { dialog, _ ->
                inputMethodManager.hideSoftInputFromWindow(name.windowToken, 0)
                dialog.cancel()
            }

            val dialog = builder.create()
            dialog.show()
            val keyListener = View.OnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_CENTER,
                        KeyEvent.KEYCODE_ENTER,
                        -> {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()
                            return@OnKeyListener true
                        }
                    }
                }
                false
            }
            number.setOnKeyListener(keyListener)
        }

        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        mConstantSelectAdapter.swapCursor(data)
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mConstantSelectAdapter.swapCursor(null)
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
            null,
            null,
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
            ConstantContract.ConstantEntry.COLUMN_SELECTED,
        )
    }
}

