package com.sepidsa.fortytwocalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.sepidsa.fortytwocalculator.data.ConstantContract;

/**
 * Created by Farshid on 5/20/2015.
 */
public class ConstantSelectAdapter extends CursorAdapter {

    private Context mContext;



    public ConstantSelectAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.constant_select_item, parent, false);
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
        Boolean selected = cursor.getInt(cursor.getColumnIndex(ConstantContract.ConstantEntry.COLUMN_SELECTED)) != 0;



        viewHolder.nameView.setText(name);
//        viewHolder.nameView.setTextColor(((MainActivity)mContext).getAccentColorCode());
        viewHolder.nameView.setTypeface(Typeface.createFromAsset(context.getAssets(), "yekan.ttf"));
        viewHolder.numberView.setText(Double.toString(number));
        viewHolder.selectedButton.setChecked(selected);
        viewHolder.selectedButton.setOnClickListener(mSelectedOnClickListener);
        viewHolder.deleteButton.setOnClickListener(mDeleteOnClickListener);

    }



    public static class ViewHolder {
        public final TextView nameView;
        public final TextView numberView;
        public final Switch selectedButton;
        public final Button deleteButton;
        public int position;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.constant_name);
            numberView = (TextView) view.findViewById(R.id.constant_number);
            selectedButton = (Switch) view.findViewById(R.id.btn_add_star);
            deleteButton = (Button) view.findViewById(R.id.button_delete_item);

        }
    }


    private Switch.OnClickListener mSelectedOnClickListener = new Switch.OnClickListener() {
        @Override
        public void onClick(View view) {
            View parent = findParentRecursively(view);
            if (parent != null) {
                ViewHolder viewHolder = (ViewHolder) parent.getTag();
                final int position = viewHolder.position;

                String selection = ConstantContract.ConstantEntry._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(position)};
                Uri uri = ConstantContract.ConstantEntry.CONTENT_URI;

                Cursor cursor = mContext.getContentResolver().query(
                        uri,
                        null,
                        selection,
                        selectionArgs,
                        null
                );
                if (cursor.moveToFirst()) {
                    int isCheckedInteger = cursor.getInt(cursor.getColumnIndex(ConstantContract.ConstantEntry.COLUMN_SELECTED));
                    isCheckedInteger = 1 - isCheckedInteger;
                    ContentValues values = new ContentValues();
                    values.put(ConstantContract.ConstantEntry.COLUMN_SELECTED, isCheckedInteger);
                    mContext.getContentResolver().update(
                            uri,
                            values,
                            selection,
                            selectionArgs
                    );



                }
            }
        }
    };

    private Button.OnClickListener mDeleteOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            final View parent = findParentRecursively(view);
            if (parent != null) {
                ViewHolder viewHolder = (ViewHolder) parent.getTag();
                final int position = viewHolder.position;
                String selection = ConstantContract.ConstantEntry._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(position)};
                Uri uri = ConstantContract.ConstantEntry.CONTENT_URI;

                mContext.getContentResolver().delete(
                        uri,
                        selection,
                        selectionArgs
                );
            }
        }
    };

    public View findParentRecursively(View view) {
        if (view.getTag() != null) {
            return view;
        }
        View parent = (View) view.getParent();
        if (parent == null) {
            return null;
        }
        return findParentRecursively(parent);
    }





}
