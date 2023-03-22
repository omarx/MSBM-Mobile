package com.fras.msbm.services;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Shane on 9/11/2016.
 */
public class NullUrlBuilder extends MoodleUrlBuilder {
    public NullUrlBuilder(Context context) {
        super(context);
    }

    @Override
    public String getBaseUrl() {
        return null;
    }

    @Override
    public String buildCoursesUrl(@NonNull String token, @NonNull String userId) {
        return null;
    }

    @Override
    public String buildUserDetailsUrl(@NonNull String token, @NonNull String userId) {
        return null;
    }

    @Override
    public String buildSiteInfoUrl(@NonNull String token) {
        return null;
    }

}
