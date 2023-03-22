package com.fras.msbm.utils;

/**
 * Created by Shane on 8/15/2016.
 */
public class StringUtils {
    public static final String DASH_DELIMITER = "-";

    private StringUtils() {
        throw new AssertionError("Do not instantiate StringUtils");
    }

    public static String[] tokenizeLectureName(String name) {
        return name.split(DASH_DELIMITER);
    }
}
