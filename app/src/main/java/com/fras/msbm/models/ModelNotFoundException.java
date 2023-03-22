package com.fras.msbm.models;

/**
 * Created by Shane on 6/18/2016.
 */
public class ModelNotFoundException extends Exception {

    public ModelNotFoundException() {
    }

    public ModelNotFoundException(String message) {
        super(message);
    }

    public ModelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
