package com.smartbuilders.synchronizer.ids.syncadapter.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * http://developer.android.com/training/sync-adapters/creating-authenticator.html
 * Created by Alberto on 23/3/2016.
 */
public class AuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new Authenticator(this);
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