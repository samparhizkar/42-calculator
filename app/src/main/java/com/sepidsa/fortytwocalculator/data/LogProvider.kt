package com.sepidsa.fortytwocalculator.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri

class LogProvider : ContentProvider() {
    private var mOpenHelper: LogDbHelper? = null

    override fun onCreate(): Boolean {
        mOpenHelper = LogDbHelper(requireNotNull(context))
        return true
    }

    override fun getType(uri: Uri): String {
        return when (sUriMatcher.match(uri)) {
            LOG -> LogContract.LogEntry.CONTENT_TYPE
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?,
    ): Cursor {
        val retCursor: Cursor = when (sUriMatcher.match(uri)) {
            LOG -> {
                mOpenHelper!!.readableDatabase.query(
                    LogContract.LogEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder,
                )
            }

            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        retCursor.setNotificationUri(requireNotNull(context).contentResolver, uri)
        return retCursor
    }

    override fun insert(uri: Uri, values: ContentValues): Uri {
        val db: SQLiteDatabase = mOpenHelper!!.writableDatabase
        val returnUri: Uri = when (sUriMatcher.match(uri)) {
            LOG -> {
                val id = db.insert(LogContract.LogEntry.TABLE_NAME, null, values)
                if (id > 0) LogContract.LogEntry.buildLogUri(id)
                else throw android.database.SQLException("Failed to insert row into $uri")
            }

            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        requireNotNull(context).contentResolver.notifyChange(uri, null)
        return returnUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db: SQLiteDatabase = mOpenHelper!!.writableDatabase

        val selectionVar = selection ?: "1"
        val rowsDeleted: Int = when (sUriMatcher.match(uri)) {
            LOG -> db.delete(LogContract.LogEntry.TABLE_NAME, selectionVar, selectionArgs)
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        if (rowsDeleted != 0) {
            requireNotNull(context).contentResolver.notifyChange(uri, null)
        }

        return rowsDeleted
    }

    override fun update(uri: Uri, values: ContentValues, selection: String?, selectionArgs: Array<String>?): Int {
        val db: SQLiteDatabase = mOpenHelper!!.writableDatabase
        val rowsUpdated: Int = when (sUriMatcher.match(uri)) {
            LOG -> db.update(LogContract.LogEntry.TABLE_NAME, values, selection, selectionArgs)
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        if (rowsUpdated != 0) {
            requireNotNull(context).contentResolver.notifyChange(uri, null)
        }

        return rowsUpdated
    }

    override fun bulkInsert(uri: Uri, values: Array<ContentValues>): Int {
        val db: SQLiteDatabase = mOpenHelper!!.writableDatabase
        return when (sUriMatcher.match(uri)) {
            LOG -> {
                db.beginTransaction()
                var returnCount = 0
                try {
                    for (value in values) {
                        val id = db.insert(LogContract.LogEntry.TABLE_NAME, null, value)
                        if (id != -1L) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                requireNotNull(context).contentResolver.notifyChange(uri, null)
                returnCount
            }

            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
    }

    companion object {
        private const val LOG = 100

        private val sUriMatcher: UriMatcher = buildUriMatcher()

        private fun buildUriMatcher(): UriMatcher {
            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority = LogContract.CONTENT_AUTHORITY
            matcher.addURI(authority, LogContract.PATH_LOG, LOG)
            return matcher
        }
    }
}
