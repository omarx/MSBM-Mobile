package com.fras.msbm.activities.general;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.fras.msbm.events.users.UserLoginEvent;
import com.fras.msbm.events.users.UserLogoutEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Shane on 6/25/2016.
 *
 * This class abstracts common methods shared by most classes
 * to reduce boilerplate code. This includes configuring
 * the underlying event bus.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    private EventBus eventBus = EventBus.getDefault();
    protected FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);
        authStateListener = auth -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null)
                eventBus.postSticky(new UserLogoutEvent());
            else
                eventBus.postSticky(new UserLoginEvent(user));
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventBus.isRegistered(this)) eventBus.unregister(this);
    }

    public void openLoginActivity() {
        openActivity(LoginActivity.class);
    }

    public void openSettingsActivity() {
        openActivity(SettingsActivity.class);
    }

    public void openActivity(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }
}
