package com.fras.msbm.services;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fras.msbm.R;
import com.fras.msbm.utils.MoodleUtil;

/**
 * Created by Shane on 9/10/2016.
 */
public class MsbmUrlBuilder extends MoodleUrlBuilder {

    public MsbmUrlBuilder(Context context) {
        super(context);
    }

    @Override
    public String getBaseUrl() {
        return context.getResources().getString(R.string.moodle_msbm_base_url);
    }

    @Override
    public String buildCoursesUrl(@NonNull String token, @NonNull String userId) {
        final String url = getServiceEndpoint();
        return MoodleUtil.buildCoursesUrl(url, token, userId, "moodle_enrol_get_users_courses");
    }

    @Override
    public String buildUserDetailsUrl(@NonNull String token, @NonNull String userId) {
        final String url = getServiceEndpoint();
        return MoodleUtil.buildUserDetailInfoUrl(url, token, userId, "core_user_get_users_by_id");
    }

    @Override
    public String buildSiteInfoUrl(@NonNull String token) {
        final String url = getServiceEndpoint();
        return MoodleUtil.buildSiteInfoUrl(url, token, "core_webservice_get_site_info");
    }
}
