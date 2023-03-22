package com.fras.msbm.fragments;

import android.test.ActivityInstrumentationTestCase2;

import com.fras.msbm.R;
import com.fras.msbm.activities.general.MainActivity;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

/**
 * Created by Shane on 6/2/2016.
 */
public class NewsFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    @Inject NewsFragment newsFragment;

    public NewsFragmentTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    @Before
    public void setUp() throws Exception {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_navigation_fragment, newsFragment, NewsFragment.TAG)
                .commit();
    }

    @Test
    public void testA() throws Exception {

    }
}