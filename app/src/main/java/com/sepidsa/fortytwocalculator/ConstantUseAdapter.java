package com.sepidsa.fortytwocalculator;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sepidsa.fortytwocalculator.data.ConstantContract;

/**
 * Created by Farshid on 5/20/2015.
 */
public class ConstantUseAdapter extends CursorAdapter {

    private Context mContext;



    public ConstantUseAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.constant_use_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.position = cursor.getInt((cursor.getColumnIndex(ConstantContract.ConstantEntry._ID)));
        String name = cursor.getString(cursor.getColumnIndex(ConstantContract.ConstantEntry.COLUMN_NAME));
        Double number = cursor.getDouble(cursor.getColumnIndex(ConstantContract.ConstantEntry.COLUMN_NUMBER));


        viewHolder.nameView.setText(name);
//        viewHolder.nameView.setTextColor(((MainActivity)mContext).getAccentColorCode());
        viewHolder.nameView.setTypeface(Typeface.createFromAsset(context.getAssets(), "yekan.ttf"));
        viewHolder.numberView.setText(Double.toString(number));

    }



    public static class ViewHolder {
        public final TextView nameView;
        public final TextView numberView;
        public int position;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.constant_name);
            numberView = (TextView) view.findViewById(R.id.constant_number);
        }
    }


    private void showMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }


}
