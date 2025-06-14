package com.example.codeverse.Students.Utils;

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


    private static final String KEY_NIC_NUMBER = "nic_number";
    private static final String KEY_DATE_OF_BIRTH = "date_of_birth";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_PHOTO_URI = "photo_uri";
    private static final String KEY_SEMESTER = "semester";
    private static final String KEY_ENROLLMENT_DATE = "enrollment_date";
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_ALTERNATE_NUMBER = "alternate_number";
    private static final String KEY_PERMANENT_ADDRESS = "permanent_address";
    private static final String KEY_CITY = "city";
    private static final String KEY_PROVINCE = "province";
    private static final String KEY_POSTAL_CODE = "postal_code";
    private static final String KEY_EMERGENCY_NAME = "emergency_name";
    private static final String KEY_EMERGENCY_RELATIONSHIP = "emergency_relationship";
    private static final String KEY_EMERGENCY_NUMBER = "emergency_number";
    private static final String KEY_USERNAME = "username";

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


        editor.putString(KEY_NIC_NUMBER, student.getNicNumber());
        editor.putString(KEY_DATE_OF_BIRTH, student.getDateOfBirth());
        editor.putString(KEY_GENDER, student.getGender());
        editor.putString(KEY_PHOTO_URI, student.getPhotoUri());
        editor.putString(KEY_SEMESTER, student.getSemester());
        editor.putString(KEY_ENROLLMENT_DATE, student.getEnrollmentDate());
        editor.putString(KEY_MOBILE_NUMBER, student.getMobileNumber());
        editor.putString(KEY_ALTERNATE_NUMBER, student.getAlternateNumber());
        editor.putString(KEY_PERMANENT_ADDRESS, student.getPermanentAddress());
        editor.putString(KEY_CITY, student.getCity());
        editor.putString(KEY_PROVINCE, student.getProvince());
        editor.putString(KEY_POSTAL_CODE, student.getPostalCode());
        editor.putString(KEY_EMERGENCY_NAME, student.getEmergencyName());
        editor.putString(KEY_EMERGENCY_RELATIONSHIP, student.getEmergencyRelationship());
        editor.putString(KEY_EMERGENCY_NUMBER, student.getEmergencyNumber());
        editor.putString(KEY_USERNAME, student.getUsername());

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


    public Student getStudentDetails() {
        if (!isLoggedIn()) {
            return null;
        }

        Student student = new Student();

        student.setId(prefs.getLong(KEY_STUDENT_ID, -1));
        student.setFullName(prefs.getString(KEY_STUDENT_NAME, ""));
        student.setUniversityId(prefs.getString(KEY_UNIVERSITY_ID, ""));
        student.setNicNumber(prefs.getString(KEY_NIC_NUMBER, ""));
        student.setDateOfBirth(prefs.getString(KEY_DATE_OF_BIRTH, ""));
        student.setGender(prefs.getString(KEY_GENDER, ""));
        student.setPhotoUri(prefs.getString(KEY_PHOTO_URI, ""));

        student.setFaculty(prefs.getString(KEY_FACULTY, ""));
        student.setBatch(prefs.getString(KEY_BATCH, ""));
        student.setSemester(prefs.getString(KEY_SEMESTER, ""));
        student.setEnrollmentDate(prefs.getString(KEY_ENROLLMENT_DATE, ""));

        student.setMobileNumber(prefs.getString(KEY_MOBILE_NUMBER, ""));
        student.setAlternateNumber(prefs.getString(KEY_ALTERNATE_NUMBER, ""));
        student.setPermanentAddress(prefs.getString(KEY_PERMANENT_ADDRESS, ""));
        student.setCity(prefs.getString(KEY_CITY, ""));
        student.setProvince(prefs.getString(KEY_PROVINCE, ""));
        student.setPostalCode(prefs.getString(KEY_POSTAL_CODE, ""));

        student.setEmergencyName(prefs.getString(KEY_EMERGENCY_NAME, ""));
        student.setEmergencyRelationship(prefs.getString(KEY_EMERGENCY_RELATIONSHIP, ""));
        student.setEmergencyNumber(prefs.getString(KEY_EMERGENCY_NUMBER, ""));

        student.setEmail(prefs.getString(KEY_EMAIL, ""));
        student.setUsername(prefs.getString(KEY_USERNAME, ""));

        return student;
    }


    public void updateStudentSession(Student student) {
        if (student != null) {
            createLoginSession(student);
        }
    }


    public boolean isStudentLoggedIn(long studentId) {
        return isLoggedIn() && getStudentId() == studentId;
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}