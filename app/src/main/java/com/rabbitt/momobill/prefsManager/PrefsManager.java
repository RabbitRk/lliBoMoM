package com.rabbitt.momobill.prefsManager;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    // Shared preferences file name
    private static final String PREF_NAME = "PREF_NAME";
    private static final String LOGIN = "IsFirstTimeLaunch";
    private static final String USER_PREF = "USER_PREFS";
    private static final String USER_PHONE = "USER_PHONE";

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

    public void userPreferences_(String id, String username, String phonenumber, String email, String addressStr, String dobStr, String gender) {
//        user_editor.putString(USER_NAME,username);
//        user_editor.putString(USER_EMAIL,email);
//        user_editor.putString(USER_PHONE,phonenumber);
//        user_editor.putString(USER_LOC,addressStr);
//        user_editor.putString(USER_LOC_OFFICE,"");
//        user_editor.putString(USER_DOB,dobStr);
//        user_editor.putString(USER_GENDER,gender);
//        user_editor.putString(USER_BIO,"-");
//        user_editor.commit();
    }
}
