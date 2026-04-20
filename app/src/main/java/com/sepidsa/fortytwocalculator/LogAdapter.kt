package com.sepidsa.fortytwocalculator

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import android.text.InputType
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.CursorAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.sepidsa.fortytwocalculator.data.LogContract

/**
 * Created by Farshid on 5/17/2015.
 */
class LogAdapter(context: Context, c: Cursor?, flags: Int) : CursorAdapter(context, c, flags) {

    private val mContext: Context = context

    override fun getViewTypeCount(): Int {
        return VIEW_TYPE_COUNT
    }

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_history, parent, false)
        val viewHolder = ViewHolder(view)
        view.tag = viewHolder
        return view
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val viewHolder = view.tag as ViewHolder

        val result = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_RESULT))
        val operation = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_OPERATION))
        val tag = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_TAG))
        val starred = cursor.getInt(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_STARRED)) != 0
        val iconColor = (mContext as MainActivity).dialpadFontColor
        viewHolder.position = cursor.getInt(cursor.getColumnIndex(LogContract.LogEntry._ID))

        viewHolder.resultView.text = result
        viewHolder.operationView.text = operation
        viewHolder.tagView.text = tag
        viewHolder.starredButton.isChecked = starred
        viewHolder.starredButton.setOnClickListener(mStarOnClickListener)

        viewHolder.tagView.setBackgroundColor((mContext as MainActivity).accentColorCode)
        viewHolder.tagView.typeface = Typeface.createFromAsset(context.assets, "notoregular.ttf")
        viewHolder.tagView.setOnClickListener(mLabelButtonOnClickListener)

        viewHolder.deleteButton.setOnClickListener(mDeleteButtonOnClickListener)
        viewHolder.deleteButton.typeface = Typeface.createFromAsset(context.assets, "flaticon.ttf")
        viewHolder.labelButton.setOnClickListener(mLabelButtonOnClickListener)
        viewHolder.labelButton.typeface = Typeface.createFromAsset(context.assets, "flaticon.ttf")
        viewHolder.shareButton.setOnClickListener(mShareButtonOnClickListener)
        viewHolder.shareButton.typeface = Typeface.createFromAsset(context.assets, "flaticon.ttf")

        viewHolder.useButton.setOnClickListener(mUseButtonOnClickListener)
        viewHolder.useButton.typeface = Typeface.createFromAsset(context.assets, "flaticon.ttf")

        viewHolder.resultView.setTextColor((context as MainActivity).accentColorCode)

        viewHolder.operationView.setTextColor(iconColor)
        if ((context as MainActivity).keypadBackgroundColorCode == Color.WHITE) {
            viewHolder.arrow.setTextColor(Color.parseColor("#EEEEEE"))
        } else {
            viewHolder.arrow.setTextColor(iconColor)
        }

        viewHolder.shareButton.setTextColor(iconColor)
        viewHolder.labelButton.setTextColor(iconColor)
        viewHolder.deleteButton.setTextColor(iconColor)
        viewHolder.useButton.setTextColor(iconColor)

        if ((viewHolder.toolbar.visibility == View.VISIBLE) && !viewHolder.checked) {
            val viewLayoutParams = viewHolder.toolbar.layoutParams as LinearLayout.LayoutParams
            viewLayoutParams.bottomMargin = 0 - viewHolder.toolbar.height
            viewHolder.toolbar.visibility = View.GONE
        } else {
            viewHolder.toolbar.visibility = View.VISIBLE
            viewHolder.checked = false
        }
        viewHolder.arrow.typeface = Typeface.createFromAsset(mContext.assets, "flaticon.ttf")
        viewHolder.arrow.rotation = 0f
        viewHolder.arrow.translationY = 0f
        viewHolder.arrow.alpha = 1f
    }

    class ViewHolder(view: View) {
        val resultView: TextView = view.findViewById(R.id.result)
        val operationView: TextView = view.findViewById(R.id.operation)
        val tagView: TextView = view.findViewById(R.id.LOG_tag)
        val starredButton: CheckBox = view.findViewById(R.id.log_checkbox)
        val toolbar: View = view.findViewById(R.id.toolbar)
        val arrow: TextView = view.findViewById(R.id.arrow)
        val tagBackground: FrameLayout = view.findViewById(R.id.tag_background)

        var position: Int = 0
        var checked: Boolean = false

        val deleteButton: Button = view.findViewById(R.id.deleteButton)
        val labelButton: Button = view.findViewById(R.id.labelButton)
        val shareButton: Button = view.findViewById(R.id.shareButton)
        val useButton: Button = view.findViewById(R.id.useButton)
    }

    private val mStarOnClickListener = View.OnClickListener { view ->
        val parent = findParentRecursively(view)
        if (parent != null) {
            val viewHolder = parent.tag as ViewHolder
            val position = viewHolder.position
            viewHolder.checked = true

            val selection = LogContract.LogEntry._ID + "=?"
            val selectionArgs = arrayOf(position.toString())
            val uri: Uri = LogContract.LogEntry.CONTENT_URI

            val cursor = mContext.contentResolver.query(
                uri,
                null,
                selection,
                selectionArgs,
                null,
            )
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    var isCheckedInteger = cursor.getInt(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_STARRED))
                    isCheckedInteger = 1 - isCheckedInteger
                    val values = ContentValues()
                    values.put(LogContract.LogEntry.COLUMN_STARRED, isCheckedInteger)
                    mContext.contentResolver.update(
                        uri,
                        values,
                        selection,
                        selectionArgs,
                    )
                }
                cursor.close()
            }
        }
    }

    private val mDeleteButtonOnClickListener = View.OnClickListener { view ->
        val parent = findParentRecursively(view)
        if (parent != null) {
            (mContext as MainActivity).playSound((mContext as MainActivity).clearSoundID)

            val viewHolder = parent.tag as ViewHolder
            val position = viewHolder.position
            val selection = LogContract.LogEntry._ID + "=?"
            val selectionArgs = arrayOf(position.toString())
            val uri: Uri = LogContract.LogEntry.CONTENT_URI

            mContext.contentResolver.delete(
                uri,
                selection,
                selectionArgs,
            )
        }
    }

    private val mLabelButtonOnClickListener = View.OnClickListener { view ->
        val parent = findParentRecursively(view)
        if (parent != null) {
            val viewHolder = parent.tag as ViewHolder
            val position = viewHolder.position

            val selection = LogContract.LogEntry._ID + "=?"
            val selectionArgs = arrayOf(position.toString())
            val uri: Uri = LogContract.LogEntry.CONTENT_URI

            val cursor = mContext.contentResolver.query(
                uri,
                null,
                selection,
                selectionArgs,
                null,
            )
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val currentLabel = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_TAG))

                    val builder = AlertDialog.Builder(mContext)
                    builder.setTitle(mContext.getString(R.string.farsi_label))

                    val input = EditText(mContext)
                    val inputMethodManager = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

                    input.inputType = InputType.TYPE_CLASS_TEXT
                    builder.setView(input)
                    builder.setCancelable(false)
                    input.setText(currentLabel)
                    input.requestFocus()
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

                    builder.setPositiveButton(mContext.getString(R.string.farsi_ok)) { _: DialogInterface, _: Int ->
                        val newLabel = input.text.toString()
                        val values = ContentValues()
                        values.put(LogContract.LogEntry.COLUMN_TAG, newLabel)
                        mContext.contentResolver.update(
                            uri,
                            values,
                            selection,
                            selectionArgs,
                        )
                        inputMethodManager.hideSoftInputFromWindow(input.windowToken, 0)
                    }
                    builder.setNegativeButton(mContext.getString(R.string.farsi_cancel)) { dialog, _ ->
                        inputMethodManager.hideSoftInputFromWindow(input.windowToken, 0)
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
                    input.setOnKeyListener(keyListener)
                }
                cursor.close()
            }
        }
    }

    private val mShareButtonOnClickListener = View.OnClickListener { view ->
        val parent = findParentRecursively(view)
        if (parent != null) {
            val viewHolder = parent.tag as ViewHolder
            val shareBody = "${viewHolder.operationView.text} = ${viewHolder.resultView.text}"
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val subject = viewHolder.tagView.text.toString()

            if (subject != "") {
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "$subject: $shareBody")
            } else {
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            }

            mContext.startActivity(Intent.createChooser(sharingIntent, "Share"))
        }
    }

    private val mUseButtonOnClickListener = View.OnClickListener { view ->
        val parent = findParentRecursively(view)
        if (parent != null) {
            val viewHolder = parent.tag as ViewHolder
            (mContext as MainActivity).addNumberToCalculation(viewHolder.resultView.text.toString())
            (mContext as MainActivity).switchToMainFragment()
        }
    }

    fun findParentRecursively(view: View): View? {
        if (view.tag != null) {
            return view
        }
        val parent = view.parent as? View ?: return null
        return findParentRecursively(parent)
    }

    private companion object {
        private const val VIEW_TYPE_COUNT = 1
    }
}

