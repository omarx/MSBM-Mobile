package com.fras.msbm.receivers.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.fras.msbm.events.network.NetworkConnectEvent;
import com.fras.msbm.events.network.NetworkDisconnectEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Shane on 6/19/2016.
 */
public class NetworkStateChangeReceiver extends BroadcastReceiver {
    public static final String TAG = NetworkStateChangeReceiver.class.getSimpleName();

    EventBus connectivityBus = EventBus.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            connectivityBus.post(new NetworkConnectEvent());
            Log.i(TAG, "onReceive:networkAvailable");
        } else {
            connectivityBus.post(new NetworkDisconnectEvent());
            Log.i(TAG, "onReceive:networkUnavailable");
        }
    }
}
