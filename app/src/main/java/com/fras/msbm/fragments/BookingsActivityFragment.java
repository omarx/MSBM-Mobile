package com.fras.msbm.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fras.msbm.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookingsActivityFragment extends Fragment {

    public BookingsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookings, container, false);
    }
}
