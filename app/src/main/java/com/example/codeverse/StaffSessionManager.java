package com.example.codeverse;

import android.content.Context;
import android.content.SharedPreferences;

public class StaffSessionManager {

    private static final String PREF_NAME = "StaffSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_STAFF_ID = "staffId";
    private static final String KEY_STAFF_NAME = "staffName";
    private static final String KEY_STAFF_EMAIL = "staffEmail";
    private static final String KEY_STAFF_DEPARTMENT = "staffDepartment";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public StaffSessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void createLoginSession(String staffId, String staffName, String staffEmail, String staffDepartment) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_STAFF_ID, staffId);
        editor.putString(KEY_STAFF_NAME, staffName);
        editor.putString(KEY_STAFF_EMAIL, staffEmail);
        editor.putString(KEY_STAFF_DEPARTMENT, staffDepartment);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getStaffId() {
        return preferences.getString(KEY_STAFF_ID, null);
    }

    public String getStaffName() {
        return preferences.getString(KEY_STAFF_NAME, null);
    }

    public String getStaffEmail() {
        return preferences.getString(KEY_STAFF_EMAIL, null);
    }

    public String getStaffDepartment() {
        return preferences.getString(KEY_STAFF_DEPARTMENT, null);
    }

    public void logoutStaff() {
        editor.clear();
        editor.commit();
    }
}