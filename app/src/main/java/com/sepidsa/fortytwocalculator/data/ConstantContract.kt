package com.sepidsa.fortytwocalculator.data

import android.net.Uri
import android.provider.BaseColumns

object ConstantContract {
    const val AUTHORITY = "com.sepidsa.fortytwocalculator.ConstantProvider"
    val BASE_CONTENT_URI = Uri.parse("content://$AUTHORITY")
    const val PATH_CONSTANT = "constant"

    object ConstantEntry : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONSTANT).build()
        const val CONTENT_TYPE = "vnd.android.cursor.dir/$AUTHORITY/$PATH_CONSTANT"
        const val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/$AUTHORITY/$PATH_CONSTANT"

        const val TABLE_NAME = "constant"
        const val _ID = BaseColumns._ID
        const val COLUMN_NAME = "name"
        const val COLUMN_NUMBER = "number"
        const val COLUMN_SELECTED = "selected"
    }
}
