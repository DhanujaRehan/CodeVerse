package com.example.codeverse.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.codeverse.Students.Models.Student;

public class StudentSessionManager {
    private static final String PREF_NAME = "student_session";
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_STUDENT_NAME = "student_name";
    private static final String KEY_UNIVERSITY_ID = "university_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_BATCH = "batch";
    private static final String KEY_FACULTY = "faculty";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public StudentSessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createLoginSession(Student student) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_STUDENT_ID, student.getId());
        editor.putString(KEY_STUDENT_NAME, student.getFullName());
        editor.putString(KEY_UNIVERSITY_ID, student.getUniversityId());
        editor.putString(KEY_EMAIL, student.getEmail());
        editor.putString(KEY_BATCH, student.getBatch());
        editor.putString(KEY_FACULTY, student.getFaculty());
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public long getStudentId() {
        return prefs.getLong(KEY_STUDENT_ID, -1);
    }

    public String getStudentName() {
        return prefs.getString(KEY_STUDENT_NAME, "");
    }

    public String getUniversityId() {
        return prefs.getString(KEY_UNIVERSITY_ID, "");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getBatch() {
        return prefs.getString(KEY_BATCH, "");
    }

    public String getFaculty() {
        return prefs.getString(KEY_FACULTY, "");
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}