package com.sepidsa.fortytwocalculator.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Farshid on 6/6/2015.
 */
public class CurrencyAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private CurrencyAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new CurrencyAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
