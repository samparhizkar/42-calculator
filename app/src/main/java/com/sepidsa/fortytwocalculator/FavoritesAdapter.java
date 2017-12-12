package com.sepidsa.fortytwocalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.sepidsa.fortytwocalculator.data.LogContract;

/**
 * Created by Farshid on 5/20/2015.
 */
public class FavoritesAdapter extends CursorAdapter {

    private Context mContext;



    public FavoritesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_favorite, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.position = cursor.getInt((cursor.getColumnIndex(LogContract.LogEntry._ID)));
        String result = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_RESULT));
        String operation = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_OPERATION));
        String tag = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_TAG));
        Boolean starred = cursor.getInt(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_STARRED)) != 0;



        viewHolder.resultView.setText(result);
        viewHolder.resultView.setTextColor(((MainActivity) mContext).getAccentColorCode());
        viewHolder.operationView.setText(operation);
        viewHolder.tagView.setText(tag);
        if(((MainActivity) context).isRetroThemeSelected()) {
            viewHolder.tagView.setBackgroundColor(Color.parseColor("#bdbdbd"));

        }else {
            viewHolder.tagView.setBackgroundColor(((MainActivity) mContext).getAccentColorCode());

        }
        viewHolder.tagView.setTypeface(Typeface.createFromAsset(context.getAssets(), "notoregular.ttf"));

        viewHolder.starredButton.setChecked(starred);
        viewHolder.starredButton.setOnClickListener(mStarOnClickListener);

    }



    public static class ViewHolder {
        public final TextView resultView;
        public final TextView operationView;
        public final TextView tagView;
        public final CheckBox starredButton;
        public int position;

        public ViewHolder(View view) {
            resultView = (TextView) view.findViewById(R.id.result);
            operationView = (TextView) view.findViewById(R.id.operation);
            tagView = (TextView) view.findViewById(R.id.LOG_tag);
            starredButton = (CheckBox) view.findViewById(R.id.log_checkbox);

        }
    }


    private CompoundButton.OnClickListener mStarOnClickListener = new CompoundButton.OnClickListener() {
        @Override
        public void onClick(View view) {
            View parent = findParentRecursively(view);
            if (parent != null) {
                ViewHolder viewHolder = (ViewHolder) parent.getTag();
                final int position = viewHolder.position;

                String selection = LogContract.LogEntry._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(position)};
                Uri uri = LogContract.LogEntry.CONTENT_URI;

                Cursor cursor = mContext.getContentResolver().query(
                        uri,
                        null,
                        selection,
                        selectionArgs,
                        null
                );
                if (cursor.moveToFirst()) {
                    cursor.moveToFirst();
                    int isCheckedInteger = cursor.getInt(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_STARRED));
                    isCheckedInteger = 1 - isCheckedInteger;
                    ContentValues values = new ContentValues();
                    values.put(LogContract.LogEntry.COLUMN_STARRED, isCheckedInteger);
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
