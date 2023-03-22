package com.fras.msbm.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fras.msbm.R;
import com.fras.msbm.models.User;

/**
 * Created by Shane on 9/4/2016.
 */
public class UserSession {
    private SharedPreferences preferences;
    private Context context;

    private static int PRIVATE_MODE = 0;

    public UserSession(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean("is_login", false);
    }

    public void clearData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public void saveUser(User user) {
        setPreference(extractStringFromId(R.string.token_key), user.getToken());
        setPreference(extractStringFromId(R.string.username_key), user.getUsername());
        setPreference(extractStringFromId(R.string.first_name_key), user.getFirstName());
        setPreference(extractStringFromId(R.string.last_name_key), user.getLastName());
        setPreference(extractStringFromId(R.string.user_id_key), String.valueOf(user.getUserId()));
        setPreference(extractStringFromId(R.string.profile_url_key), user.getProfileUrl());
        setPreference(extractStringFromId(R.string.email_key), user.getProfileUrl());
    }

    public User loadUser() {
        String username = getStringPreference(extractStringFromId(R.string.username_key));
        String firstName = getStringPreference(extractStringFromId(R.string.first_name_key));
        String lastName = getStringPreference(extractStringFromId(R.string.last_name_key));
        String userId = getStringPreference(extractStringFromId(R.string.user_id_key));
        String profileUrl = getStringPreference(extractStringFromId(R.string.profile_url_key));
        String token = getStringPreference(extractStringFromId(R.string.token_key));
        String email = getStringPreference(extractStringFromId(R.string.email_key));

        return User.builder().username(username)
                .firstName(firstName).lastName(lastName)
                .email(email).profileUrl(profileUrl)
                .userId(Integer.valueOf(userId))
                .token(token).build();
    }

    private String extractStringFromId(int key) {
        return context.getString(key);
    }

    private String getStringPreference(String key) {
        return preferences.getString(key, "");
    }

    private void setPreference(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

}
