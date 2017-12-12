package com.sepidsa.fortytwocalculator.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sepidsa.fortytwocalculator.data.CurrencyContract.CurrencyEntry;

/**
 * Created by Farshid on 5/15/2015.
 */
public class CurrencyDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "currency.db";

    public CurrencyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_CURRENCY_TABLE = "CREATE TABLE " + CurrencyContract.CurrencyEntry.TABLE_NAME + " (" +
                CurrencyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CurrencyEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                CurrencyEntry.COLUMN_VALUE + " TEXT NOT NULL, " +
                CurrencyEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                CurrencyEntry.COLUMN_PRIORITY + " INTEGER NOT NULL, " +
                CurrencyEntry.COLUMN_SELECTED + " INTEGER NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_CURRENCY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onCreate(sqLiteDatabase);

    }
}
