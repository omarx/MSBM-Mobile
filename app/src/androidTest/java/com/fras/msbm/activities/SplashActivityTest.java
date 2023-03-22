package com.fras.msbm.activities;

import android.test.ActivityInstrumentationTestCase2;

import static org.junit.Assert.*;

/**
 * Created by Shane on 5/20/2016.
 */
public class SplashActivityTest
        extends ActivityInstrumentationTestCase2<SplashActivity> {

    private SplashActivity activity;

    public SplashActivityTest() {
        super(SplashActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setup();
    }
}