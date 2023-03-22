package com.fras.msbm.events.users;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Shane on 6/25/2016.
 */
public class UserLoginEvent {
    final FirebaseUser user;

    public UserLoginEvent(@NonNull FirebaseUser user) {
        this.user = user;
    }

    public FirebaseUser getUser(){
        return user;
    }
}
