package com.sepidsa.fortytwocalculator;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class HelpActivity extends Activity implements ExpandableListView.OnChildClickListener, View.OnClickListener {

    private ArrayList<String> parentItems = new ArrayList<String>();
    private ArrayList<Object> childItems = new ArrayList<Object>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // this is not really  necessary as ExpandableListActivity contains an ExpandableList
        setContentView(R.layout.activity_help);


        ExpandableListView expandableList = (ExpandableListView) findViewById(R.id.list); // you can use (ExpandableListView) findViewById(R.id.list)

       ImageButton backButton = (ImageButton)findViewById(R.id.button_back);
        backButton.setOnClickListener(this);

        TextView dummy = (TextView)findViewById(R.id.help_page_title);
        dummy.setTypeface(Typeface.createFromAsset(getAssets(), "yekan.ttf"));
        expandableList.setDividerHeight(2);
        expandableList.setGroupIndicator(null);
        expandableList.setClickable(true);
        setGroupParents();
        setChildData();

        HelpExpandableAdapter adapter = new HelpExpandableAdapter(parentItems, childItems);

        adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
        expandableList.setAdapter(adapter);
        expandableList.setOnChildClickListener(this);
    }

    public void setGroupParents() {
        String [] helpTopicsArray = getResources().getStringArray(R.array.help_topics);
        for (int index = 0; index < helpTopicsArray.length;index ++){
           parentItems.add(helpTopicsArray[index]);
        }
    }

    public void setChildData() {

        // Android
        ArrayList<String> child;

        String [] helpSubTopicsArray = getResources().getStringArray(R.array.help_sub_topics);
        for (int index = 0; index < helpSubTopicsArray.length;index ++){
            child = new ArrayList<String>();
            child.add(helpSubTopicsArray[index]);

            childItems.add(child);
        }

    }

    /**
     * Callback method to be invoked when a child in this expandable list has
     * been clicked.
     *
     * @param parent        The ExpandableListView where the click happened
     * @param v             The view within the expandable list/ListView that was clicked
     * @param groupPosition The group position that contains the child that
     *                      was clicked
     * @param childPosition The child position within the group
     * @param id            The row id of the child that was clicked
     * @return True if the click was handled
     */
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_back:
                this.finish();
        }
    }
}