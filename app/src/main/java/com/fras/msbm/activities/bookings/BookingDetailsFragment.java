package com.fras.msbm.activities.bookings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fras.msbm.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookingDetailsFragment extends Fragment {

    public BookingDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        PullToZoomListViewEx
       View view = inflater.inflate(R.layout.fragment_booking_details, container, false);

        return  view;
    }
}
