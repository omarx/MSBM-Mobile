package com.fras.msbm.fragments;


import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.fras.msbm.R;
import com.fras.msbm.constants.SocialMediaConstants;
import com.fras.msbm.events.TwitterTimelineRefreshFailedEvent;
import com.fras.msbm.events.network.NetworkConnectEvent;
import com.google.firebase.crash.FirebaseCrash;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiException;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

/**
 * A simple {@link Fragment} subclass.
 */
public class SocialFragment extends BaseFragment {
    public static final String TAG = SocialFragment.class.getName();

    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.list_view_tweets) ListView tweetsListView;
    @BindView(R.id.fab_tweet) FloatingActionButton tweetFab;

    public static SocialFragment newInstance() {
        return new SocialFragment();
    }

    public SocialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_social, container, false);
        ButterKnife.bind(this, rootView);
        tweetFab.setVisibility(View.GONE);
        initializeConfigurations();
        try {
            loadTwitterFeed();
        } catch (TwitterApiException e) {
            Toast.makeText(getContext(), "Failed to load twitter feed", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "twitterFeed:failed", e);
        }
        return rootView;
    }


    protected void initializeConfigurations() {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.twitter_api_key),
                getString(R.string.twitter_api_secret));
        Fabric.with(getContext(), new Twitter(authConfig));
        setToolbarTitle("Social Feed");
    }

    private void loadTwitterFeed() {
        final UserTimeline timeline = new UserTimeline.Builder()
                .screenName(SocialMediaConstants.MSBM_TWITTER_NAME)
                .includeRetweets(true)
                .includeReplies(true)
                .build();

        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(getContext())
                .setTimeline(timeline)
                .build();

        tweetsListView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(() ->
            refreshTimelineAdapterWithLayout(adapter, swipeRefreshLayout));
    }

    @OnClick(R.id.fab_tweet)
    public void onTweetFloatingActionButtonClicked(FloatingActionButton button) {
        Toast.makeText(getContext(), "Open tweet dialog", Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onEventMainThread(NetworkConnectEvent event) {

    }

    private void refreshTimelineAdapterWithLayout(TweetTimelineListAdapter adapter, SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setRefreshing(true);

        adapter.refresh(new Callback<TimelineResult<Tweet>>() {
            @Override
            public void success(Result<TimelineResult<Tweet>> result) {
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(TwitterException e) {
                FirebaseCrash.report(e);
                Log.e(TAG, "twitter:failure", e);
                eventBus.post(new TwitterTimelineRefreshFailedEvent());
            }
        });
    }
}
