package com.fras.msbm.services;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Shane on 9/11/2016.
 */
public class MoodleUrlBuilderFactory {
    public static final String OURVLE = "ourvle";
    public static final String MSBM = "msbm";

    private final Context context;

    public MoodleUrlBuilderFactory(Context context) {
        this.context = context;
    }

    public MoodleUrlBuilder getUrlBuilder(@NonNull String moodleInstance){
        switch (moodleInstance) {
            case MSBM:
                return new MsbmUrlBuilder(context);
            case OURVLE:
                return new OurvleUrlBuilder(context);
        }
        return new NullUrlBuilder(context);
    }
}
