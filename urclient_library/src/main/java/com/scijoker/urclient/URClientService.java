package com.scijoker.urclient;

import android.content.Context;

/**
 * Created by scijoker on 28.01.15.
 */
public class URClientService {
    private static VolleySingleton volleySingleton;
    private static Context CONTEXT;

    public static void init(Context context) {
        init(context, false);
    }

    public static void init(Context context, boolean isInDebugMode) {
        if (volleySingleton == null) {
            CONTEXT = context;
            volleySingleton = VolleySingleton.getInstance(context, isInDebugMode);
        }
    }

    public static Context getContext() {
        return CONTEXT;
    }
}
