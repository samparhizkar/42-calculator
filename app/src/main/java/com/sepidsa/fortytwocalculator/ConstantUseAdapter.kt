package com.sepidsa.fortytwocalculator

import android.content.Context
import android.database.Cursor
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import android.widget.Toast
import com.sepidsa.fortytwocalculator.data.ConstantContract

/**
 * Created by Farshid on 5/20/2015.
 */
class ConstantUseAdapter(context: Context, c: Cursor?, flags: Int) : CursorAdapter(context, c, flags) {

    private val mContext: Context = context

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.constant_use_item, parent, false)
        val viewHolder = ViewHolder(view)
        view.tag = viewHolder
        return view
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val viewHolder = view.tag as ViewHolder

        viewHolder.position = cursor.getInt(cursor.getColumnIndex(ConstantContract.ConstantEntry._ID))
        val name = cursor.getString(cursor.getColumnIndex(ConstantContract.ConstantEntry.COLUMN_NAME))
        val number = cursor.getDouble(cursor.getColumnIndex(ConstantContract.ConstantEntry.COLUMN_NUMBER))

        viewHolder.nameView.text = name
        viewHolder.nameView.typeface = Typeface.createFromAsset(context.assets, "yekan.ttf")
        viewHolder.numberView.text = number.toString()
    }

    class ViewHolder(view: View) {
        val nameView: TextView = view.findViewById(R.id.constant_name)
        val numberView: TextView = view.findViewById(R.id.constant_number)
        var position: Int = 0
    }

    private fun showMessage(message: String) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }
}

