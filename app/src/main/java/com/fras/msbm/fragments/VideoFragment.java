package com.fras.msbm.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fras.msbm.R;
import com.fras.msbm.events.users.UserLoginEvent;
import com.google.android.youtube.player.YouTubePlayer;

import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

public class VideoFragment extends BaseFragment {

    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, rootView);
        setToolbarTitle(getString(R.string.title_videos));
        YouTubePlayer youtube;

        return rootView;
    }

    @Subscribe
    public void onEvent(UserLoginEvent event) {}
}
