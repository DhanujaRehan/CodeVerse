package com.example.codeverse.Admin.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AdminSessionManager {
    private static final String PREF_NAME = "AdminSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_ADMIN_ID = "adminId";
    private static final String KEY_ADMIN_EMAIL = "adminEmail";
    private static final String KEY_ADMIN_NAME = "adminName";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public AdminSessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    public void createLoginSession(String adminId, String adminName, String adminEmail) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_ADMIN_ID, adminId);
        editor.putString(KEY_ADMIN_NAME, adminName);
        editor.putString(KEY_ADMIN_EMAIL, adminEmail);
        editor.commit();
    }
    public String getAdminId() {
        return pref.getString(KEY_ADMIN_ID, null);
    }

    public String getAdminName() {
        return pref.getString(KEY_ADMIN_NAME, null);
    }

    public String getAdminEmail() {
        return pref.getString(KEY_ADMIN_EMAIL, null);
    }

    public void logoutAdmin() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean("is_logged_in", false);
    }


    public boolean isSessionValid() {
        return isLoggedIn() &&
                getAdminId() != null &&
                getAdminName() != null &&
                getAdminEmail() != null;
    }
}