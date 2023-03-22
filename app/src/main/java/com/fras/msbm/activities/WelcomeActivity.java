package com.fras.msbm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fras.msbm.R;
import com.fras.msbm.activities.general.LoginActivity;
import com.fras.msbm.fragments.WelcomeFragment;
import com.fras.msbm.utils.VersionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.pager_welcome_slides) ViewPager pagerWelcomeSlides;
    @BindView(R.id.layout_dots) LinearLayout linearLayoutDots;
    @BindView(R.id.button_next) Button buttonNext;
    @BindView(R.id.button_skip) Button buttonSkip;

    SharedPreferences sharedPreferences;

    private final WelcomeFragment[] fragments = {
            WelcomeFragment.newInstance(R.color.bg_screen1, R.mipmap.ic_launcher, "Courses", "Placeholder text"),
            WelcomeFragment.newInstance(R.color.bg_screen2, R.mipmap.ic_launcher, "Events", "Placeholder text"),
            WelcomeFragment.newInstance(R.color.bg_screen3, R.mipmap.ic_launcher, "Rewards", "Placeholder text")};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Checking for first time launch - before calling setContentView()
        sharedPreferences = getSharedPreferences("welcome", 0);

        if (!sharedPreferences.getBoolean("is_first_launch", true)) {
            launchLoginActivity();
            finish();
        }

        if (Build.VERSION.SDK_INT >= 21) getWindow()
                .getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        addBottomDots(0);
//        changeStatusBarColor();

        WelcomeViewPagerAdapter viewPagerAdapter = new WelcomeViewPagerAdapter(getSupportFragmentManager(), fragments);
        pagerWelcomeSlides.setAdapter(viewPagerAdapter);
        pagerWelcomeSlides.addOnPageChangeListener(viewPageChangeListener);
    }

    private void changeStatusBarColor() {
        if (VersionUtil.isLollipopOrGreater()) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @OnClick(R.id.button_next)
    public void openNextFragment(Button button) {
        int current = pagerWelcomeSlides.getCurrentItem() + (+1);

        if (current < fragments.length)
            pagerWelcomeSlides.setCurrentItem(current);
        else
            launchLoginActivity();
    }

    @OnClick(R.id.button_skip)
    public void openLoginActivity(Button button) {
        launchLoginActivity();
    }

    private void launchLoginActivity() {
        finish();
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
    }

    private void addBottomDots(int currentPage) {
        TextView[] textViewDots = new TextView[fragments.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        linearLayoutDots.removeAllViews();

        for (int i = 0; i < textViewDots.length; i++) {
            textViewDots[i] = new TextView(this);
            textViewDots[i].setText(Html.fromHtml("&#8226;"));
            textViewDots[i].setTextSize(35);
            textViewDots[i].setTextColor(colorsInactive[currentPage]);
            linearLayoutDots.addView(textViewDots[i]);
        }

        if (textViewDots.length > 0)
            textViewDots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    public final class WelcomeViewPagerAdapter extends FragmentPagerAdapter {
        private WelcomeFragment[] welcomeFragments;

        public WelcomeViewPagerAdapter(FragmentManager fm, WelcomeFragment[] fragments) {
            super(fm);
            welcomeFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return welcomeFragments[position];
        }

        @Override
        public int getCount() {
            return welcomeFragments.length;
        }
    }

    ViewPager.OnPageChangeListener viewPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if (position == fragments.length - 1) {
                buttonNext.setText(getString(R.string.start));
                buttonSkip.setVisibility(View.GONE);
            } else {
                buttonNext.setText(getString(R.string.next));
                buttonSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
