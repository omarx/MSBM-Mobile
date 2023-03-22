package com.fras.msbm.utils;

/**
 * Created by Antonio on 06/01/2017.
 * * Implementing this class allows an Activity class to collect the result of an AsyncTask
 * without needing to pass context or handling view modification restrictions
 */

public interface AsyncResponse {
    void processFinish(int newItems);
}
