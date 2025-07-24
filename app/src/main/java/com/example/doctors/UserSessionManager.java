package com.example.doctors;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_ROLE = "role";
    private static final String KEY_NAME = "name";

    public static String getUserRole(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ROLE, null);
    }

    public static String getUserName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_NAME, "");
    }
}
