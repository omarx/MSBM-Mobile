package com.fras.msbm.utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fras.msbm.models.data.MoodleData;

/**
 * Created by Shane on 7/2/2016.
 */
public class MoodleUtil {
    public static final String TAG = MoodleUtil.class.getName();

    final private static String TOKEN_PARAM = "wstoken";
    final private static String WS_PARAM = "wsfunction";
    final private static String USERNAME_PARAM = "username";
    final private static String PASSWORD_PARAM = "password";
    final private static String SERVICE_PARAM = "service";
    final private static String USER_IDS_PARAM = "userids[0]";
    final private static String USER_ID_PARAM = "userid";
    final private static String MOODLE_REST_FORMAT_PARAM = "moodlewsrestformat";
    final private static String MOODLE_REST_FORMAT_JSON = "json";
    final private static String WS_SITE_INFO = "core_webservice_get_site_info";
    final private static String WS_USER_INFO = "core_user_get_users_by_id";

    final private static String SERVICE_VALUE = "moodle_mobile_app";

    public MoodleUtil() {
        throw new AssertionError("Cannot instantiate MoodleUtil");
    }

    public static String getUserEnrollCourses(String token, String userId){
        return "http://ourvle.mona.uwi.edu/webservice/rest/server.php?wstoken=" + token + "&userid=" + userId + "&moodlewsrestformat=json&wsfunction=moodle_enrol_get_users_courses";
    }

    public static String getCourseContent(String token, String courseId, String moodleInstance) {
        if(moodleInstance.toLowerCase().equals("ourvle")){
//            Log.e(TAG," OurVLE Instance");
            return "http://ourvle.mona.uwi.edu/webservice/rest/server.php?wstoken=" + token + "&wsfunction=core_course_get_contents&courseid=" + courseId + "&moodlewsrestformat=json";
        }else{
//            Log.e(TAG," MSBM Instance");
            return "http://elearning2.msbm-uwi.org/webservice/rest/server.php?wstoken=" + token + "&wsfunction=core_course_get_contents&courseid=" + courseId + "&moodlewsrestformat=json";
        }

    }

    public static String buildUserDetailInfoUrl(@NonNull String url, @NonNull String token, @NonNull String userId, @NonNull String function) {

        final Uri uri = Uri.parse(url).buildUpon()
                .appendQueryParameter(TOKEN_PARAM, token)
                .appendQueryParameter(WS_PARAM, "core_user_get_users_by_id")
                .appendQueryParameter(MOODLE_REST_FORMAT_PARAM, "json")
                .appendQueryParameter(USER_IDS_PARAM, userId)
                .build();

        Log.i(TAG, "user:detail - " + uri.toString());

        return uri.toString();
    }

    public static String buildSiteInfoUrl(@NonNull String url, @NonNull String token, @NonNull String function) {

        final Uri uri = Uri.parse(url).buildUpon()
                .appendQueryParameter(TOKEN_PARAM, token)
                .appendQueryParameter(WS_PARAM, function)
                .appendQueryParameter(MOODLE_REST_FORMAT_PARAM, MOODLE_REST_FORMAT_JSON)
                .build();

        Log.i(TAG, "user:basic - " + uri.toString());

        return uri.toString();
    }

    public static String buildLoginUrl(@NonNull String url, @NonNull String username, @NonNull String password, @NonNull String function) {

        final Uri uri = Uri.parse(url).buildUpon()
                .appendQueryParameter(USERNAME_PARAM, username)
                .appendQueryParameter(PASSWORD_PARAM, password)
                .appendQueryParameter(SERVICE_PARAM, function)
                .appendQueryParameter(MOODLE_REST_FORMAT_PARAM, MOODLE_REST_FORMAT_JSON)
                .build();

        Log.i(TAG, "user:login - " + uri.toString());

        return uri.toString();
    }

    public static String buildCoursesUrl(@NonNull String url, @NonNull String token, @NonNull String userId, @NonNull String function) {
        final Uri uri = Uri.parse(url).buildUpon()
                .appendQueryParameter(TOKEN_PARAM, token)
                .appendQueryParameter("userid", String.valueOf(userId))
                .appendQueryParameter(WS_PARAM, function)
                .appendQueryParameter(MOODLE_REST_FORMAT_PARAM, MOODLE_REST_FORMAT_JSON)
                .build();

        return uri.toString();
    }

    public static MoodleData parseUrlForTokenAndUserId(@NonNull String url) {
        Uri uri = Uri.parse(url);
        MoodleData data = new MoodleData();
        data.token = uri.getQueryParameter(TOKEN_PARAM);
        data.userId = uri.getQueryParameter(USER_IDS_PARAM);
        return data;
    }

    // TODO : add msbm moodle ws function
    // https://gitlab.com/fras-ja/msbm-mobile-kurogo/blob/master/sites/MSBM/lib/MoodleDataRetriever.php
}
