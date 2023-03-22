package com.fras.msbm.network.tasks;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Shane on 7/6/2016.
 */

public class DownloadTask extends AsyncTask<String, Integer, String> {
    private EventBus bus = EventBus.getDefault();

    @Override
    protected String doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
