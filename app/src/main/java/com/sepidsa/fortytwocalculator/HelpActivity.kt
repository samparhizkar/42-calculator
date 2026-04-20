package com.sepidsa.fortytwocalculator

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ExpandableListView
import android.widget.ImageButton
import android.widget.TextView
import java.util.ArrayList

class HelpActivity : Activity(), ExpandableListView.OnChildClickListener, View.OnClickListener {

    private val parentItems = ArrayList<String>()
    private val childItems = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_help)

        val expandableList = findViewById<ExpandableListView>(R.id.list)

        val backButton = findViewById<ImageButton>(R.id.button_back)
        backButton.setOnClickListener(this)

        val dummy = findViewById<TextView>(R.id.help_page_title)
        dummy.typeface = Typeface.createFromAsset(assets, "yekan.ttf")

        expandableList.dividerHeight = 2
        expandableList.setGroupIndicator(null)
        expandableList.isClickable = true

        setGroupParents()
        setChildData()

        val adapter = HelpExpandableAdapter(parentItems, childItems)
        adapter.setInflater(getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, this)
        expandableList.setAdapter(adapter)
        expandableList.setOnChildClickListener(this)
    }

    fun setGroupParents() {
        val helpTopicsArray = resources.getStringArray(R.array.help_topics)
        for (index in helpTopicsArray.indices) {
            parentItems.add(helpTopicsArray[index])
        }
    }

    fun setChildData() {
        val helpSubTopicsArray = resources.getStringArray(R.array.help_sub_topics)
        for (index in helpSubTopicsArray.indices) {
            val child = ArrayList<String>()
            child.add(helpSubTopicsArray[index])
            childItems.add(child)
        }
    }

    override fun onChildClick(
        parent: ExpandableListView,
        v: View,
        groupPosition: Int,
        childPosition: Int,
        id: Long,
    ): Boolean {
        return false
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.button_back) {
            finish()
        }
    }
}

