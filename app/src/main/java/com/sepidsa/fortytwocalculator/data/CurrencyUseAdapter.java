package com.sepidsa.fortytwocalculator.data;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.sepidsa.fortytwocalculator.R;

import java.util.HashMap;

/**
 * Created by Farshid on 5/20/2015.
 */
public class CurrencyUseAdapter extends CursorAdapter {

    private Context mContext;
    private HashMap<String, String> currencyMap;



    public CurrencyUseAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        currencyMap = new HashMap<>(24);
        currencyMap.put("dollar", context.getString(R.string.farsi_dollar));
        currencyMap.put("eur", context.getString(R.string.farsi_eur));
        currencyMap.put("gbp", context.getString(R.string.farsi_gbp));
        currencyMap.put("try", context.getString(R.string.farsi_try));
        currencyMap.put("aed", context.getString(R.string.farsi_aed));
        currencyMap.put("cad", context.getString(R.string.farsi_cad));
        currencyMap.put("cny", context.getString(R.string.farsi_cny));
        currencyMap.put("dkk", context.getString(R.string.farsi_dkk));
        currencyMap.put("ons", context.getString(R.string.farsi_ons));
        currencyMap.put("mesghal", context.getString(R.string.farsi_mesghal));
        currencyMap.put("geram18", context.getString(R.string.farsi_geram18));
        currencyMap.put("geram24", context.getString(R.string.farsi_geram24));
        currencyMap.put("silver", context.getString(R.string.farsi_silver));
        currencyMap.put("sekeb", context.getString(R.string.farsi_sekeb));
        currencyMap.put("sekee", context.getString(R.string.farsi_sekee));
        currencyMap.put("nim", context.getString(R.string.farsi_nim));
        currencyMap.put("rob", context.getString(R.string.farsi_rob));
        currencyMap.put("gerami", context.getString(R.string.farsi_gerami));

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.currency_use_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.position = cursor.getInt((cursor.getColumnIndex(CurrencyContract.CurrencyEntry._ID)));
        String key = cursor.getString(cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COLUMN_KEY));
        String number = cursor.getString(cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COLUMN_VALUE));
        int type = cursor.getInt(cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COLUMN_TYPE));
//        viewHolder.flagView.setTextColor(Color.WHITE);
//        viewHolder.flagView.setTypeface(Typeface.createFromAsset(context.getAssets(), "flaticon.ttf"));
//        switch (type) {
//            case 1:
//                viewHolder.flagView.setBackgroundColor(Color.GREEN);
//                viewHolder.flagView.setText("G");

//                break;
//            case 2:
//                viewHolder.flagView.setBackgroundColor(Color.YELLOW);
//                viewHolder.flagView.setText("Y");

//        }
        viewHolder.nameView.setText(currencyMap.get(key));
        viewHolder.nameView.setTypeface(Typeface.createFromAsset(context.getAssets(), "yekan.ttf"));
        viewHolder.numberView.setText(number);
        viewHolder.numberView.setTypeface(Typeface.createFromAsset(context.getAssets(), "notoregular.ttf"));



    }



    public static class ViewHolder {
        public final TextView nameView;
        public final TextView numberView;
//        public final TextView flagView;
        public int position;


        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.currency_translation);
            numberView = (TextView) view.findViewById(R.id.currency_value);
//            flagView = (TextView) view.findViewById(R.id.currency_flag);
        }
    }


}
