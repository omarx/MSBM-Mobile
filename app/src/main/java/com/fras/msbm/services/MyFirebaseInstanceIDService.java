package com.fras.msbm.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Antonio on 16/11/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    public static final String TAG = MyFirebaseInstanceIDService.class.getName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        Log.e(TAG, "Token Message Called.");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
    }

}
