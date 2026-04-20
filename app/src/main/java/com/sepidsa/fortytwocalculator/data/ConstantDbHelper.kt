package com.sepidsa.fortytwocalculator.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.sepidsa.fortytwocalculator.data.ConstantContract.ConstantEntry

class ConstantDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val sqlCreateConstantTable =
            "CREATE TABLE " + ConstantContract.ConstantEntry.TABLE_NAME + " (" +
                ConstantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ConstantEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ConstantEntry.COLUMN_NUMBER + " REAL NOT NULL, " +
                ConstantEntry.COLUMN_SELECTED + " INTEGER NOT NULL " +
                " );"

        sqLiteDatabase.execSQL(sqlCreateConstantTable)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(sqLiteDatabase)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        const val DATABASE_NAME: String = "constant.db"
    }
}
