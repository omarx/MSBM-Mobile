package com.fras.msbm.activities;

import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Shane on 6/19/2016.
 */
public class WelcomeActivityTest extends ActivityTestRule<WelcomeActivity> {

    private WelcomeActivity mActivity;

    public WelcomeActivityTest(Class<WelcomeActivity> activityClass) {
        super(WelcomeActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        mActivity = getActivity();
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

    @Test
    public void testOnNextButtonClicked() throws Exception {


    }

    @Test
    public void testOnCreate() throws Exception {

    }

    @Test
    public void testOpenNextFragment() throws Exception {

    }

    @Test
    public void testOpenLoginActivity() throws Exception {

    }
}