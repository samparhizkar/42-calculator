package com.sepidsa.fortytwocalculator.ui.constants

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.sepidsa.fortytwocalculator.R
import com.sepidsa.fortytwocalculator.data.ConstantEntity

class ConstantUseAdapter(
    private val context: Context
) : BaseAdapter() {

    private var constants: List<ConstantEntity> = emptyList()

    fun updateConstants(newConstants: List<ConstantEntity>) {
        constants = newConstants
        notifyDataSetChanged()
    }

    override fun getCount(): Int = constants.size
    override fun getItem(position: Int): ConstantEntity = constants[position]
    override fun getItemId(position: Int): Long = constants[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.constant_use_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val constant = getItem(position)

        viewHolder.nameView.text = constant.name
        viewHolder.nameView.typeface = Typeface.createFromAsset(context.assets, "yekan.ttf")
        viewHolder.numberView.text = constant.number.toString()

        return view
    }

    private class ViewHolder(view: View) {
        val nameView: TextView = view.findViewById(R.id.constant_name)
        val numberView: TextView = view.findViewById(R.id.constant_number)
    }
}
