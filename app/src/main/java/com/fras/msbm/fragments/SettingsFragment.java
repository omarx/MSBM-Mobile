package com.fras.msbm.fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.fras.msbm.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Shane on 9/6/2016.
 */
public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    public static final String TAG = SettingsFragment.class.getSimpleName();

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_logout)));
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();
        String stringKey = preference.getKey();

        if (stringKey.equals(getString(R.string.pref_logout))) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
        }

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int preferenceIndex = listPreference.findIndexOfValue(stringValue);
            if (preferenceIndex >= 0)
                preference.setSummary(listPreference.getEntries()[preferenceIndex]);
        } else
            preference.setSummary(stringValue);


        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Preference logoutPreference = findPreference(getString(R.string.pref_logout));

//        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
//            preferenceScreen.removePreference(logoutPreference);
//        } else {
//            preferenceScreen.addPreference(logoutPreference);
//        }

        return true;
    }
}
