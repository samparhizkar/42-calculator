package com.sepidsa.fortytwocalculator.data

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

object LogContract {
    const val CONTENT_AUTHORITY: String = "com.sepidsa.fortytwocalculator"
    val BASE_CONTENT_URI: Uri = Uri.parse("content://$CONTENT_AUTHORITY")

    const val PATH_LOG: String = "log"

    object LogEntry : BaseColumns {
        const val TABLE_NAME: String = "log"

        const val COLUMN_RESULT: String = "result"
        const val COLUMN_RESULT_NO_COMMA: String = "result_no_comma"
        const val COLUMN_OPERATION: String = "operation"
        const val COLUMN_TAG: String = "tag"
        const val COLUMN_STARRED: String = "starred"

        val CONTENT_URI: Uri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOG).build()

        const val CONTENT_TYPE: String =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOG
        const val CONTENT_ITEM_TYPE: String =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOG

        @JvmStatic
        fun buildLogUri(id: Long): Uri = ContentUris.withAppendedId(CONTENT_URI, id)
    }
}
