package com.sepidsa.fortytwocalculator.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Farshid on 5/15/2015.
 */
public class LogContract {

    public static final String CONTENT_AUTHORITY = "com.sepidsa.fortytwocalculator";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_LOG = "log";


    public static final class LogEntry implements BaseColumns {
        public static final String TABLE_NAME = "log";

        public static final String COLUMN_RESULT = "result";
        public static final String COLUMN_RESULT_NO_COMMA = "result_no_comma";
        public static final String COLUMN_OPERATION = "operation";
        public static final String COLUMN_TAG = "tag";
        public static final String COLUMN_STARRED = "starred";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOG).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOG;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOG;

        public static Uri buildLogUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


}
