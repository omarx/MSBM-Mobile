package com.fras.msbm.exceptions.moodle;

/**
 * Created by Shane on 7/20/2016.
 */
public class InvalidCredentialsException extends Exception {
    private String errorCode;
    private String errorMessage;

    public InvalidCredentialsException() {
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
