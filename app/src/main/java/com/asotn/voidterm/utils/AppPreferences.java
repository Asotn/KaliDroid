/*
 * VoidTerm - AppPreferences
 * Thin wrapper around SharedPreferences for user-configurable settings.
 *
 * Developer : Asotn | https://github.com/Asotn | s.pi@outlook.sa
 * License   : GPL-3.0
 */
package com.asotn.voidterm.utils;

import android.content.Context;
import android.content.SharedPreferences;

public final class AppPreferences {

    private static final String PREFS_NAME = "voidterm_settings";
    private static final String KEY_KALI_MIRROR = "kali_mirror";
    private static final String KEY_FONT_SIZE = "font_size";
    private static final String KEY_KEEP_SCREEN_ON = "keep_screen_on";

    private static final String DEFAULT_MIRROR = "https://http.kali.org/kali";
    private static final int DEFAULT_FONT_SIZE = 14;

    private static AppPreferences instance;

    private final SharedPreferences prefs;

    private AppPreferences(Context ctx) {
        prefs = ctx.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized AppPreferences get(Context ctx) {
        if (instance == null) {
            instance = new AppPreferences(ctx);
        }
        return instance;
    }

    public String getKaliMirror() {
        return prefs.getString(KEY_KALI_MIRROR, DEFAULT_MIRROR);
    }

    public void setKaliMirror(String url) {
        prefs.edit().putString(KEY_KALI_MIRROR, url).apply();
    }

    public int getFontSize() {
        return prefs.getInt(KEY_FONT_SIZE, DEFAULT_FONT_SIZE);
    }

    public void setFontSize(int size) {
        prefs.edit().putInt(KEY_FONT_SIZE, size).apply();
    }

    public boolean getKeepScreenOn() {
        return prefs.getBoolean(KEY_KEEP_SCREEN_ON, true);
    }

    public void setKeepScreenOn(boolean keepOn) {
        prefs.edit().putBoolean(KEY_KEEP_SCREEN_ON, keepOn).apply();
    }
}
