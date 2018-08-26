package com.example.android.store.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class PrefManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    int VIEW_PRIVATE = 0;
    private static final String SHARED_PREFERENCES = "welcome";
    private static final String FIRST_LAUNCH = "first_launch";

    public PrefManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, VIEW_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setFirstLaunch(boolean firstLaunch) {
        editor.putBoolean(FIRST_LAUNCH, firstLaunch);
        editor.commit();
    }

    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(FIRST_LAUNCH, true);
    }


}
