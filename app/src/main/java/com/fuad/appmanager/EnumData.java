package com.fuad.appmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

public class EnumData {

    public static final String KEY_SHOW_SYSTEM_APP = "show_system_app";
    public static final String KEY_SHOW_USER_APP = "show_user_app";
    public static final String KEY_SORT_BY = "sort_by";
    public static final String SORT_BY_APP_TITLE = "app_title";
    public static final String SORT_BY_SIZE = "size";
    public static ArrayList<AppItem> userApps = new ArrayList<>();
    public static ArrayList<AppItem> systemApps = new ArrayList<>();

    public static void setInSharedPref(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getFromSharedPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }
}
