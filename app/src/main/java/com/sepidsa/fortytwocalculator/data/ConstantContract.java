package com.sepidsa.fortytwocalculator.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Farshid on 5/15/2015.
 */
public class ConstantContract {

    public static final String CONTENT_AUTHORITY = "com.sepidsa.fortytwocalculator.constants";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CONSTANT = "constant";


    public static final class ConstantEntry implements BaseColumns {
        public static final String TABLE_NAME = "constant";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NUMBER = "number";
        public static final String COLUMN_SELECTED = "selected";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONSTANT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONSTANT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONSTANT;

        public static Uri buildConstantUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


}
