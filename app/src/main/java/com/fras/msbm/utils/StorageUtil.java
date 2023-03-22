package com.fras.msbm.utils;

import android.os.Environment;

/**
 * Created by Shane on 6/18/2016.
 */
public class StorageUtil {
    public StorageUtil() {
        throw new AssertionError("Cannot instantiate storage util");
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public  static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}
