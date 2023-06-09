package com.fras.msbm;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

/**
 * Created by Shane on 9/2/2016.
 */

public class VideoFragment extends YouTubePlayerSupportFragment {
    /**
     * Creates a new instance of this Fragment and passes the video URL
     * to YouTube player
     * @param url YouTube channel id that follows http://www.youtube.com/watch?v=
     * @return an instance of this VideoFragment
     */
    public static VideoFragment newInstance(String url) {
        final VideoFragment fragment = new VideoFragment();
        final Bundle bundle = new Bundle();
        bundle.putString("VIDEO_ID", url);

        fragment.setArguments(bundle);
        fragment.initialize();

        return fragment;
    }

    /**
     * Abstracts the initialization of the YouTube video found at the URL given in
     * newInstance(). A YouTubePLayer.OnInitializedListener starts loading the video
     * as soon as initialization is complete.
     */
    private void initialize() {
        initialize(getArguments().getString("VIDEO_ID"), new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    /*
                    * Used in place of youTubePlayer.cueVideo() so that the
                    * video will play automatically
                    * */
                    youTubePlayer.loadVideo(getArguments().getString("VIDEO_ID"));
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(getContext(), "Initialization Failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}
