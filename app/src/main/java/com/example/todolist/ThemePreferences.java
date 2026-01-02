package com.example.todolist;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemePreferences {

    private static final String PREF_NAME = "theme_pref";
    private static final String KEY_DARK_MODE = "dark_mode";

    private ThemePreferences() {
        // prevent instantiation
    }

    public static void setDarkMode(Context context, boolean isDark) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit()
                .putBoolean(KEY_DARK_MODE, isDark)
                .apply();
    }

    public static boolean isDarkMode(Context context) {
        return context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_DARK_MODE, false);  // default = light mode
    }
}
