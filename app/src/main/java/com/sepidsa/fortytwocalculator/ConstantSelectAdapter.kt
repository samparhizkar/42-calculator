package com.sepidsa.fortytwocalculator

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Typeface
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CursorAdapter
import android.widget.Switch
import android.widget.TextView
import com.sepidsa.fortytwocalculator.data.ConstantContract

/**
 * Created by Farshid on 5/20/2015.
 */
class ConstantSelectAdapter(context: Context, c: Cursor?, flags: Int) : CursorAdapter(context, c, flags) {

    private val mContext: Context = context

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.constant_select_item, parent, false)
        val viewHolder = ViewHolder(view)
        view.tag = viewHolder
        return view
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val viewHolder = view.tag as ViewHolder

        viewHolder.position = cursor.getInt(cursor.getColumnIndex(ConstantContract.ConstantEntry._ID))
        val name = cursor.getString(cursor.getColumnIndex(ConstantContract.ConstantEntry.COLUMN_NAME))
        val number = cursor.getDouble(cursor.getColumnIndex(ConstantContract.ConstantEntry.COLUMN_NUMBER))
        val selected = cursor.getInt(cursor.getColumnIndex(ConstantContract.ConstantEntry.COLUMN_SELECTED)) != 0

        viewHolder.nameView.text = name
        viewHolder.nameView.typeface = Typeface.createFromAsset(context.assets, "yekan.ttf")
        viewHolder.numberView.text = number.toString()
        viewHolder.selectedButton.isChecked = selected
        viewHolder.selectedButton.setOnClickListener(mSelectedOnClickListener)
        viewHolder.deleteButton.setOnClickListener(mDeleteOnClickListener)
    }

    class ViewHolder(view: View) {
        val nameView: TextView = view.findViewById(R.id.constant_name)
        val numberView: TextView = view.findViewById(R.id.constant_number)
        val selectedButton: Switch = view.findViewById(R.id.btn_add_star)
        val deleteButton: Button = view.findViewById(R.id.button_delete_item)
        var position: Int = 0
    }

    private val mSelectedOnClickListener = View.OnClickListener { view ->
        val parent = findParentRecursively(view)
        if (parent != null) {
            val viewHolder = parent.tag as ViewHolder
            val position = viewHolder.position

            val selection = ConstantContract.ConstantEntry._ID + "=?"
            val selectionArgs = arrayOf(position.toString())
            val uri: Uri = ConstantContract.ConstantEntry.CONTENT_URI

            val cursor = mContext.contentResolver.query(
                uri,
                null,
                selection,
                selectionArgs,
                null,
            )
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    var isCheckedInteger =
                        cursor.getInt(cursor.getColumnIndex(ConstantContract.ConstantEntry.COLUMN_SELECTED))
                    isCheckedInteger = 1 - isCheckedInteger
                    val values = ContentValues()
                    values.put(ConstantContract.ConstantEntry.COLUMN_SELECTED, isCheckedInteger)
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

    private val mDeleteOnClickListener = View.OnClickListener { view ->
        val parent = findParentRecursively(view)
        if (parent != null) {
            val viewHolder = parent.tag as ViewHolder
            val position = viewHolder.position
            val selection = ConstantContract.ConstantEntry._ID + "=?"
            val selectionArgs = arrayOf(position.toString())
            val uri: Uri = ConstantContract.ConstantEntry.CONTENT_URI

            mContext.contentResolver.delete(
                uri,
                selection,
                selectionArgs,
            )
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

