package com.fras.msbm.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fras.msbm.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceholderFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_COLOR = "color";

    private String title;
    private int colorResource;

    @BindView(R.id.main_layout) RelativeLayout mainLayout;
    @BindView(R.id.text_title) TextView titleTextView;

    public PlaceholderFragment() {
        // Required empty public constructor
    }

    public static PlaceholderFragment newInstance(String title, int colorResource) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_COLOR, colorResource);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            colorResource = getArguments().getInt(ARG_COLOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);
        ButterKnife.bind(this, rootView);
        titleTextView.setText(title);
        mainLayout.setBackgroundColor(getResources().getColor(colorResource));
        return rootView;
    }
}
