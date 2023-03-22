package com.fras.msbm.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Shane on 6/29/2016.
 */
public abstract class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();
    protected final EventBus eventBus = EventBus.getDefault();

    public void setToolbarTitle(String title) {
        getActivity().setTitle(title);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (eventBus.isRegistered(this)) eventBus.unregister(this);
    }
}
