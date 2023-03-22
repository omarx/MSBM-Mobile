package com.fras.msbm.network;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Shane on 6/18/2016.
 */
public class Requestor {

    private OkHttpClient client;

    public Requestor(OkHttpClient client) {
        this.client = client;
    }

    public Call get(String url, Callback callback) {
        Request tokenRequest = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(tokenRequest);
        call.enqueue(callback);
        return call;
    }
}
