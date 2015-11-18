package com.example.xyzreader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesHelper {
    private static final String SHARED_PREFS = "shared_prefs";

    /**
     * Gets a preference key
     *
     * @param context the context
     * @param keyId the key id
     */
    private static String getKey(Context context, int keyId) {
        return context.getString(keyId);
    }

    /**
     * Gets a boolean preference value.
     *
     * @param context the context
     * @param keyId the key id
     * @param defaultValue the default value
     */
    public static boolean getBoolean(Context context, int keyId, boolean defaultValue) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(getKey(context, keyId), defaultValue);
        } catch (Exception ex) {
            ex.printStackTrace();
            return defaultValue;
        }
    }

    /**
     * Sets a boolean preference value.
     *
     * @param context the context
     * @param keyId the key id
     * @param value the value
     */
    public static void setBoolean(Context context, int keyId, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getKey(context, keyId), value);
        editor.apply();
    }
}
