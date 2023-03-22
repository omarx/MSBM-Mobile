package com.fras.msbm.activities.general;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.fras.msbm.R;
import com.fras.msbm.events.network.NetworkConnectEvent;
import com.fras.msbm.events.network.NetworkDisconnectEvent;
import com.fras.msbm.fragments.SettingsFragment;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private Snackbar networkSnackbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        attachSettingsFragment();
        networkSnackbar = Snackbar.make(coordinatorLayout, "Network Unavailable", Snackbar.LENGTH_INDEFINITE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void attachSettingsFragment() {
        SettingsFragment fragment = SettingsFragment.newInstance();

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, fragment, fragment.getTag())
                .commit();
    }

    @Subscribe
    public void onEvent(NetworkConnectEvent event) {
        networkSnackbar.dismiss();
    }

    @Subscribe
    public void onEvent(NetworkDisconnectEvent event) {
        networkSnackbar.show();
    }
}
