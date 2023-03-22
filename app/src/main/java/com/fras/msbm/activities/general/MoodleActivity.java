package com.fras.msbm.activities.general;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.fras.msbm.R;
import com.fras.msbm.events.network.NetworkConnectEvent;
import com.fras.msbm.events.network.NetworkDisconnectEvent;
import com.fras.msbm.fragments.ListCoursesFragment;
import com.fras.msbm.fragments.PlaceholderFragment;
import com.gigamole.navigationtabstrip.NavigationTabStrip;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoodleActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = MoodleActivity.class.getName();

    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.navigation_view) NavigationView navigationView;
    @BindView(R.id.navigation_tabs_strip) NavigationTabStrip navigationTabStrip;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.viewpager) ViewPager viewpager;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moodle);
        ButterKnife.bind(this);
        setupLayouts();
    }

    private void setupLayouts() {
        setupToolbar();
        setupViewPager();
    }

    private void setupViewPager() {
        List<Fragment> tabFragments = new ArrayList<>();
        tabFragments.add(ListCoursesFragment.newInstance());
        tabFragments.add(PlaceholderFragment.newInstance("News", R.color.blue));

        MoodleTabsPagerAdapter tabsPagerAdapter = new MoodleTabsPagerAdapter(getSupportFragmentManager(), tabFragments);
        viewpager.setAdapter(tabsPagerAdapter);
        navigationTabStrip.setViewPager(viewpager);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_about:
                openActivity(AboutActivity.class);
                break;
            case R.id.nav_setting:
                openSettingsActivity();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Subscribe
    public void onEventMainThread(NetworkDisconnectEvent event) {
        Snackbar.make(coordinatorLayout, "Cannot connect to internet", Snackbar.LENGTH_LONG).show();
    }

    @Subscribe
    public void onEventMainThread(NetworkConnectEvent event) {
        Snackbar.make(coordinatorLayout, "Back online!", Snackbar.LENGTH_SHORT).show();
    }

    public static class MoodleTabsPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public MoodleTabsPagerAdapter(FragmentManager fm, List<Fragment> tabFragments) {
            super(fm);
            fragments = tabFragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            final List<String> titles = new ArrayList<>(Arrays
                    .asList("Courses", "News"));
            return titles.get(position);
        }
    }
}
