package com.sepidsa.fortytwocalculator.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Farshid on 5/15/2015.
 */
public class CurrencyProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private  CurrencyDbHelper mOpenHelper;

    static final int CURRENCY = 100;

    //private static final SQLiteQueryBuilder sCurrencyQueryBuilder;
    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CurrencyContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, CurrencyContract.PATH_CURRENCY, CURRENCY);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new CurrencyDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CURRENCY:
                return CurrencyContract.CurrencyEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case CURRENCY:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CurrencyContract.CurrencyEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case CURRENCY: {
                long _id = db.insert(CurrencyContract.CurrencyEntry.TABLE_NAME, null, values);
                if(_id > 0 )
                    returnUri = CurrencyContract.CurrencyEntry.buildCurrencyUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if (selection == null) selection = "1";
        switch (match) {
            case CURRENCY: {
                rowsDeleted = db.delete(CurrencyContract.CurrencyEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case CURRENCY: {
                rowsUpdated = db.update(CurrencyContract.CurrencyEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CURRENCY:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(CurrencyContract.CurrencyEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
