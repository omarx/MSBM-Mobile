package com.fras.msbm.services;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fras.msbm.R;
import com.fras.msbm.utils.MoodleUtil;

/**
 * Created by Shane on 9/10/2016.
 */
public abstract class MoodleUrlBuilder {
    protected Context context;
    protected String baseUrl;

    public MoodleUrlBuilder(Context context) {
        this.context = context;
        baseUrl = getBaseUrl();
    }

    public abstract String getBaseUrl();

    public String getLoginEndpoint() {
        final String path = getStringResource(context, R.string.moodle_login_endpoint);
        return baseUrl + path;
    }

    public String buildTokenUrl(@NonNull String username, @NonNull String password) {
        final String url = getLoginEndpoint();
        return MoodleUtil.buildLoginUrl(url, username, password, "moodle_mobile_app");
    }

    public String getServiceEndpoint() {
        final String path = getStringResource(context, R.string.moodle_service_endpoint);
        return baseUrl + path;
    }

    private String getStringResource(@NonNull Context context, int stringId) {
        return context.getResources().getString(stringId);
    }

    public abstract String buildCoursesUrl(@NonNull String token, @NonNull String userId );

    public abstract String buildUserDetailsUrl(@NonNull String token, @NonNull String userId);

    public abstract String buildSiteInfoUrl(@NonNull String token);
}
