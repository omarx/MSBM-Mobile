package com.fras.msbm.activities.general;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fras.msbm.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AboutFragment extends Fragment {
    public static final String TAG = AboutFragment.class.getSimpleName();

    @BindView(R.id.text_title_about_msbm) TextView msbmTitleTextView;
    @BindView(R.id.text_title_about_app) TextView appTitleTextView;
    @BindView(R.id.text_title_tips) TextView tipsTitleTextView;

    @BindView(R.id.text_content_about_msbm) TextView msbmContentTextView;
    @BindView(R.id.text_content_about_app) TextView appContentTextView;
    @BindView(R.id.text_content_tips) TextView tipsContentTextView;

    public AboutFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, rootView);
        setupLayout();
        return rootView;
    }

    private void setupLayout() {
        final ArrayList<TextView> titleViews = new ArrayList<>(Arrays.asList(msbmTitleTextView, appTitleTextView, tipsTitleTextView));
        final List<TextView> contentViews = new ArrayList<>(Arrays.asList(msbmContentTextView, appContentTextView, tipsContentTextView));
        hideViews(contentViews);
        addOnClickListeners(titleViews, contentViews);
    }

    private void addOnClickListeners(List<TextView> titleViews, List<TextView> contentViews) {
        for (int i = 0; i < titleViews.size(); i++) {
            final TextView titleTextView = titleViews.get(i);
            final TextView contentView = contentViews.get(i);

            titleTextView.setOnClickListener(view -> {
                if (isViewVisible(contentView)) return;
                toggleContentViewVisibilities(contentViews, contentView);
            });
        }
    }

    private void toggleContentViewVisibilities(List<TextView> views, TextView contentView) {
        for (int i = 0; i < views.size(); i++) {
            final TextView currentView = views.get(i);

            if (contentView.equals(currentView)) {
                contentView.setVisibility((isViewVisible(contentView)) ? View.GONE : View.VISIBLE);
                continue;
            }

            hideView(currentView);
        }
    }

    private boolean isViewVisible(TextView contentView) {
        return contentView.getVisibility() == View.VISIBLE;
    }

    private void hideViews(List<TextView> views) {
        for (TextView contentView : views) {
            hideView(contentView);
        }
    }

    private void hideView(TextView view) {
        view.setVisibility(View.GONE);
    }
}
