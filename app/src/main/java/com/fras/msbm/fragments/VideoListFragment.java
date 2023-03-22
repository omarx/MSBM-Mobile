package com.fras.msbm.fragments;

/**
 * Created by Antonio on 10/10/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


import com.fras.msbm.R;
import com.fras.msbm.adapter.VideoListAdapter;
import com.fras.msbm.models.YouTubeContent;
import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

/***********************************************************************************
 * The MIT License (MIT)

 * Copyright (c) 2015 Scott Cooper

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ***********************************************************************************/

public class VideoListFragment extends ListFragment {

    /**
     * Empty constructor
     */
    public VideoListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new VideoListAdapter(getActivity()));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        final Context context = getActivity();
        final String DEVELOPER_KEY = getString(R.string.youtube_key);
        final YouTubeContent.YouTubeVideo video = YouTubeContent.ITEMS.get(position);

        //Issue #3 - Need to resolve StandalonePlayer as well
        if (YouTubeIntents.canResolvePlayVideoIntent(context)) {
            //Opens in the StandAlonePlayer, defaults to fullscreen
            startActivity(YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                    DEVELOPER_KEY, video.id));
        }else{
            Toast.makeText(getActivity(), "Please Update YouTube App To Play Video", Toast.LENGTH_SHORT).show();
        }

    }

}