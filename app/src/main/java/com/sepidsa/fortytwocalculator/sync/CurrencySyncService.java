package com.sepidsa.fortytwocalculator.sync;

/**
 * Created by Farshid on 6/6/2015.
 */
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CurrencySyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static CurrencySyncAdapter sCurrencySyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("CurrencySyncService", "onCreate - CurrencySyncService");
        synchronized (sSyncAdapterLock) {
            if (sCurrencySyncAdapter == null) {
                sCurrencySyncAdapter = new CurrencySyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sCurrencySyncAdapter.getSyncAdapterBinder();
    }
}