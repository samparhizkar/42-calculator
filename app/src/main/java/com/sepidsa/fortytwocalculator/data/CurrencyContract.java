package com.sepidsa.fortytwocalculator.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Farshid on 5/15/2015.
 */
public class CurrencyContract {

    public static final String CONTENT_AUTHORITY = "com.sepidsa.fortytwocalculator.currency";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CURRENCY = "currency";


    public static final class CurrencyEntry implements BaseColumns {
        public static final String TABLE_NAME = "currency";

        public static final String COLUMN_KEY = "name";
        public static final String COLUMN_VALUE = "number";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_SELECTED = "selected";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CURRENCY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CURRENCY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CURRENCY;

        public static Uri buildCurrencyUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


}
