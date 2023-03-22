package com.fras.msbm.utils;

import android.os.Build;

/**
 * Created by Shane on 6/8/2016.
 */
public final class VersionUtil {

    public VersionUtil() {
        throw new AssertionError("Cannot instantiate VersionUtil");
    }

    public static boolean isLollipopOrGreater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
