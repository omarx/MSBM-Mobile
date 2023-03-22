package com.fras.msbm.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fras.msbm.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeFragment extends Fragment {

    public static final String ARG_IMAGE = "image";
    public static final String ARG_TITLE= "title";
    public static final String ARG_CONTENT = "content";
    public static final String ARG_BACKGROUND = "background";

    @BindView(R.id.layout_main) RelativeLayout layoutMain;
    @BindView(R.id.image_welcome) ImageView imageWelcome;
    @BindView(R.id.text_title) TextView textTitle;
    @BindView(R.id.text_content) TextView textContent;

    private int backgroundResource;
    private int imageResource;
    private String title;
    private String content;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    public static WelcomeFragment newInstance(int backgroundColorResource, int imageResource, String title, String content) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_IMAGE, imageResource);
        arguments.putString(ARG_TITLE, title);
        arguments.putString(ARG_CONTENT, content);
        arguments.putInt(ARG_BACKGROUND, backgroundColorResource);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (null != arguments) {
            imageResource = arguments.getInt(ARG_IMAGE);
            title = arguments.getString(ARG_TITLE);
            content = arguments.getString(ARG_CONTENT);
            backgroundResource = arguments.getInt(ARG_BACKGROUND);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.bind(this, rootView);
        layoutMain.setBackgroundColor(getContext().getResources().getColor(backgroundResource));
        imageWelcome.setImageResource(imageResource);
        textTitle.setText(title);
        textContent.setText(content);
        return rootView;
    }
}
