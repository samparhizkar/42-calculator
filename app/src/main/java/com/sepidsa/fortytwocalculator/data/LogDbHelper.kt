package com.sepidsa.fortytwocalculator.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.sepidsa.fortytwocalculator.data.LogContract.LogEntry

class LogDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val sqlCreateLogTable =
            "CREATE TABLE " + LogContract.LogEntry.TABLE_NAME + " (" +
                LogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LogEntry.COLUMN_RESULT + " TEXT NOT NULL, " +
                LogEntry.COLUMN_RESULT_NO_COMMA + " TEXT NOT NULL, " +
                LogEntry.COLUMN_OPERATION + " TEXT NOT NULL, " +
                LogEntry.COLUMN_TAG + " TEXT NOT NULL, " +
                LogEntry.COLUMN_STARRED + " INTEGER NOT NULL " +
                " );"

        sqLiteDatabase.execSQL(sqlCreateLogTable)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(sqLiteDatabase)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        const val DATABASE_NAME: String = "log.db"
    }
}
