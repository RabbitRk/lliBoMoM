package com.rabbitt.momobill.prefsManager;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    // Shared preferences file name
    public static final String PREF_NAME = "PREF_NAME";
    public static final String LOGIN = "IsFirstTimeLaunch";

    public static final String OWNER = "OWNER";

    public static final String USER_PREF = "USER_PREFS";

    public static final String USER_PHONE = "USER_PHONE";

    public static final String USER_NAME = "USER_NAME";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_LOC = "USER_LOC";
    public static final String USER_LOC_2 = "USER_LOC_2";
    public static final String USER_CITY = "USER_CITY";
    public static final String USER_STATE = "USER_STATE";
    public static final String USER_PIN = "USER_PIN";
    public static final String USER_GST = "USER_GST";
    public static final String KEY = "KEY";

    private SharedPreferences pref, userpref;
    private SharedPreferences.Editor editor, user_editor;

    public PrefsManager(Context context) {
        // shared pref mode
        int PRIVATE_MODE = 0;

        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        userpref = context.getSharedPreferences(USER_PREF, PRIVATE_MODE);
        user_editor = userpref.edit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(LOGIN, false);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(LOGIN, isFirstTime);
        editor.commit();
    }

    public boolean isOwner() {
        return pref.getBoolean(OWNER, false);
    }

    public void setOwner(boolean isFirstTime) {
        editor.putBoolean(OWNER, isFirstTime);
        editor.commit();
    }


    public void userPreferences_(
            String key,
            String name,
            String phone,
            String email,
            String add1,
            String add2,
            String city,
            String state,
            String pincode,
            String gst) {
        user_editor.putString(KEY,key);
        user_editor.putString(USER_NAME, name);
        user_editor.putString(USER_EMAIL, email);
        user_editor.putString(USER_PHONE, phone);
        user_editor.putString(USER_LOC, add1);
        user_editor.putString(USER_LOC_2, add2);
        user_editor.putString(USER_CITY, city);
        user_editor.putString(USER_STATE, state);
        user_editor.putString(USER_PIN, pincode);
        user_editor.putString(USER_GST, gst);
        user_editor.commit();
    }
}
