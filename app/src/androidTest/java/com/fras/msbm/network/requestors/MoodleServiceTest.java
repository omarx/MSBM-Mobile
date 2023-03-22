package com.fras.msbm.network.requestors;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.fras.msbm.constants.MoodleConstants;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Shane on 6/1/2016.
 */
public class MoodleServiceTest extends InstrumentationTestCase {

    private MoodleClient client;

    @Override
    public void setUp() throws Exception {
        Context context = getInstrumentation().getContext();
        client = new MoodleService(context);
    }

    @Test
    public void testGetBaseUrl() throws Exception {
        assertEquals("The default url no longer matches", client.getBaseUrl(), MoodleConstants.BASE_URL);
        assertNotEquals("The default url is not consistent", client.getBaseUrl(), "some other url");
    }

    @Test
    public void testRequestToken() throws Exception {
        try {
            String token = client.requestToken("620065739", "12061994");
        } catch (MoodleException e) {

        }
    }
}