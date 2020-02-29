package com.fallgod.weather.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by JackPan on 2019/11/11
 * Describe:SharedPreferences工具类
 */
public class SPUtil {
    private static final String SP_FILE_NAME = "stormy_weather";

    public static final String SP_WEATHER = "weather";
    public static final String SP_WEATHER_ID = "weather_id";
    public static final String SP_BING_IMG = "bing_image";

    public static void putString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        editor.clear();
    }

    public static String getString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, value);
    }
}
