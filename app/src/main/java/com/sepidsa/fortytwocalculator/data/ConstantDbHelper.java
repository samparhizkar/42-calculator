package com.sepidsa.fortytwocalculator.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sepidsa.fortytwocalculator.data.ConstantContract.ConstantEntry;

/**
 * Created by Farshid on 5/15/2015.
 */
public class ConstantDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "constant.db";

    public ConstantDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_CONSTANT_TABLE = "CREATE TABLE " + ConstantContract.ConstantEntry.TABLE_NAME + " (" +
                ConstantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ConstantEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ConstantEntry.COLUMN_NUMBER + " REAL NOT NULL, " +
                ConstantEntry.COLUMN_SELECTED + " INTEGER NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_CONSTANT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onCreate(sqLiteDatabase);

    }
}
