package com.sepidsa.fortytwocalculator

import android.app.Activity
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckedTextView
import android.widget.TextView
import android.widget.Toast
import java.util.ArrayList

class HelpExpandableAdapter(
    private val parentItems: ArrayList<String>,
    private val childtems: ArrayList<Any>,
) : BaseExpandableListAdapter() {

    private lateinit var activity: Activity
    private lateinit var inflater: LayoutInflater
    private lateinit var child: ArrayList<String>

    fun setInflater(inflater: LayoutInflater, activity: Activity) {
        this.inflater = inflater
        this.activity = activity
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        @Suppress("UNCHECKED_CAST")
        child = childtems[groupPosition] as ArrayList<String>

        var rowView = convertView
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.list_item_help_child, null)
        }

        val textView = rowView.findViewById<TextView>(R.id.textView1)
        textView.text = child[childPosition]

        rowView.setOnClickListener {
            Toast.makeText(activity, child[childPosition], Toast.LENGTH_SHORT).show()
        }

        (rowView as TextView).typeface = Typeface.createFromAsset(activity.assets, "bbc.ttf")
        return rowView
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        var rowView = convertView
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.list_item_help_group, null)
        }

        (rowView as CheckedTextView).text = parentItems[groupPosition]
        rowView.isChecked = isExpanded
        (rowView as TextView).typeface = Typeface.createFromAsset(activity.assets, "yekan.ttf")

        return rowView
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any? {
        return null
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return 0
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        @Suppress("UNCHECKED_CAST")
        return (childtems[groupPosition] as ArrayList<String>).size
    }

    override fun getGroup(groupPosition: Int): Any? {
        return null
    }

    override fun getGroupCount(): Int {
        return parentItems.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }
}

