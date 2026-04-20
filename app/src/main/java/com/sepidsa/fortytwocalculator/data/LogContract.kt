package com.sepidsa.fortytwocalculator.data

import android.net.Uri
import android.provider.BaseColumns

object LogContract {
    const val AUTHORITY = "com.sepidsa.fortytwocalculator.LogProvider"
    val BASE_CONTENT_URI = Uri.parse("content://$AUTHORITY")
    const val PATH_LOG = "log"

    object LogEntry : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOG).build()
        const val CONTENT_TYPE = "vnd.android.cursor.dir/$AUTHORITY/$PATH_LOG"
        const val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/$AUTHORITY/$PATH_LOG"

        const val TABLE_NAME = "log"
        const val _ID = BaseColumns._ID
        const val COLUMN_RESULT = "result"
        const val COLUMN_RESULT_NO_COMMA = "result_no_comma"
        const val COLUMN_OPERATION = "operation"
        const val COLUMN_TAG = "tag"
        const val COLUMN_STARRED = "starred"
    }
}
