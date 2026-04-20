package com.sepidsa.fortytwocalculator.data

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

object ConstantContract {
    const val CONTENT_AUTHORITY: String = "com.sepidsa.fortytwocalculator.constants"
    val BASE_CONTENT_URI: Uri = Uri.parse("content://$CONTENT_AUTHORITY")

    const val PATH_CONSTANT: String = "constant"

    object ConstantEntry : BaseColumns {
        const val TABLE_NAME: String = "constant"

        const val COLUMN_NAME: String = "name"
        const val COLUMN_NUMBER: String = "number"
        const val COLUMN_SELECTED: String = "selected"

        val CONTENT_URI: Uri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONSTANT).build()

        const val CONTENT_TYPE: String =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONSTANT
        const val CONTENT_ITEM_TYPE: String =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONSTANT

        @JvmStatic
        fun buildConstantUri(id: Long): Uri = ContentUris.withAppendedId(CONTENT_URI, id)
    }
}
