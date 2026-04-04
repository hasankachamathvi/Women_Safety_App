package com.example.yuwathi.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Stores a lightweight admin-login flag for screens that require admin access.
 */
public final class AdminSessionManager {
    private static final String PREFS_NAME = "admin_session";
    private static final String KEY_ADMIN_LOGGED_IN = "is_admin_logged_in";

    private AdminSessionManager() {
        // Utility class.
    }

    public static void setAdminLoggedIn(Context context, boolean isLoggedIn) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_ADMIN_LOGGED_IN, isLoggedIn).apply();
    }

    public static boolean isAdminLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_ADMIN_LOGGED_IN, false);
    }

    public static void clearSession(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}

