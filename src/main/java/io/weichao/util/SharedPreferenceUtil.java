package io.weichao.util;

import android.content.Context;

public class SharedPreferenceUtil {
    public static final String NAME = "io.weichao.util.sharedPreference";

    public static String getString(Context context, String key) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getString(key, "");
    }

    public static boolean getBoolean(Context context, String key) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getBoolean(key, false);
    }

    public static int getInt(Context context, String key) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getInt(key, 0);
    }

    public static long getLong(Context context, String key) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getLong(key, 0L);
    }

    public static float getFloat(Context context, String key) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getFloat(key, 0F);
    }

    public static void put(Context context, String key, String value) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit().putString(key, value).commit();
    }

    public static void put(Context context, String key, boolean value) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit().putBoolean(key, value).commit();
    }

    public static void put(Context context, String key, int value) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit().putInt(key, value).commit();
    }

    public static void put(Context context, String key, long value) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit().putLong(key, value).commit();
    }

    public static void put(Context context, String key, float value) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit().putFloat(key, value).commit();
    }
}
