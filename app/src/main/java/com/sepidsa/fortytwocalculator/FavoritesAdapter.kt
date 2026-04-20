package com.sepidsa.fortytwocalculator

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Typeface
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.CursorAdapter
import android.widget.TextView
import com.sepidsa.fortytwocalculator.data.LogContract

/**
 * Created by Farshid on 5/20/2015.
 */
class FavoritesAdapter(context: Context, c: Cursor?, flags: Int) : CursorAdapter(context, c, flags) {

    private val mContext: Context = context

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_favorite, parent, false)
        val viewHolder = ViewHolder(view)
        view.tag = viewHolder
        return view
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val viewHolder = view.tag as ViewHolder

        viewHolder.position = cursor.getInt(cursor.getColumnIndex(LogContract.LogEntry._ID))
        val result = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_RESULT))
        val operation = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_OPERATION))
        val tag = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_TAG))
        val starred = cursor.getInt(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_STARRED)) != 0

        viewHolder.resultView.text = result
        viewHolder.resultView.setTextColor((mContext as MainActivity).accentColorCode)
        viewHolder.operationView.text = operation
        viewHolder.tagView.text = tag
        viewHolder.tagView.setBackgroundColor((mContext as MainActivity).accentColorCode)
        viewHolder.tagView.typeface = Typeface.createFromAsset(context.assets, "notoregular.ttf")

        viewHolder.starredButton.isChecked = starred
        viewHolder.starredButton.setOnClickListener(mStarOnClickListener)
    }

    class ViewHolder(view: View) {
        val resultView: TextView = view.findViewById(R.id.result)
        val operationView: TextView = view.findViewById(R.id.operation)
        val tagView: TextView = view.findViewById(R.id.LOG_tag)
        val starredButton: CheckBox = view.findViewById(R.id.log_checkbox)
        var position: Int = 0
    }

    private val mStarOnClickListener = CompoundButton.OnClickListener { view, _ ->
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

    fun findParentRecursively(view: View): View? {
        if (view.tag != null) {
            return view
        }
        val parent = view.parent as? View ?: return null
        return findParentRecursively(parent)
    }
}

