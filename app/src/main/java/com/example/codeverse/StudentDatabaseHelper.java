package com.example.codeverse;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.codeverse.Student;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StudentDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "StudentDatabaseHelper";
    private static final String DATABASE_NAME = "StudentDetails.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_STUDENTS = "StudentsDetails";
    private static final String KEY_ID = "id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_UNIVERSITY_ID = "university_id";
    private static final String KEY_NIC_NUMBER = "nic_number";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_DATE_OF_BIRTH = "date_of_birth";
    private static final String KEY_PHOTO_PATH = "photo_path";
    private static final String KEY_FACULTY = "faculty";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_BATCH = "batch";
    private static final String KEY_CURRENT_SEMESTER = "current_semester";
    private static final String KEY_ENROLLMENT_DATE = "enrollment_date";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TERMS_ACCEPTED = "terms_accepted";
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_ALTERNATE_NUMBER = "alternate_number";
    private static final String KEY_PERMANENT_ADDRESS = "permanent_address";
    private static final String KEY_CITY = "city";
    private static final String KEY_PROVINCE = "province";
    private static final String KEY_POSTAL_CODE = "postal_code";
    private static final String KEY_EMERGENCY_CONTACT_NAME = "emergency_contact_name";
    private static final String KEY_EMERGENCY_RELATIONSHIP = "emergency_relationship";
    private static final String KEY_EMERGENCY_CONTACT_NUMBER = "emergency_contact_number";
    private static final String KEY_REGISTRATION_DATE = "registration_date";
    private static final String KEY_STATUS = "status";
    private static StudentDatabaseHelper instance;

    public static synchronized StudentDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new StudentDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private StudentDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STUDENTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_FULL_NAME + " TEXT NOT NULL," +
                KEY_UNIVERSITY_ID + " TEXT UNIQUE NOT NULL," +
                KEY_NIC_NUMBER + " TEXT UNIQUE NOT NULL," +
                KEY_GENDER + " TEXT," +
                KEY_DATE_OF_BIRTH + " TEXT," +
                KEY_PHOTO_PATH + " TEXT," +
                KEY_FACULTY + " TEXT," +
                KEY_DEPARTMENT + " TEXT," +
                KEY_BATCH + " TEXT," +
                KEY_CURRENT_SEMESTER + " TEXT," +
                KEY_ENROLLMENT_DATE + " TEXT," +
                KEY_EMAIL + " TEXT UNIQUE NOT NULL," +
                KEY_USERNAME + " TEXT UNIQUE NOT NULL," +
                KEY_PASSWORD + " TEXT NOT NULL," +
                KEY_TERMS_ACCEPTED + " INTEGER DEFAULT 0," +
                KEY_MOBILE_NUMBER + " TEXT NOT NULL," +
                KEY_ALTERNATE_NUMBER + " TEXT," +
                KEY_PERMANENT_ADDRESS + " TEXT," +
                KEY_CITY + " TEXT," +
                KEY_PROVINCE + " TEXT," +
                KEY_POSTAL_CODE + " TEXT," +
                KEY_EMERGENCY_CONTACT_NAME + " TEXT," +
                KEY_EMERGENCY_RELATIONSHIP + " TEXT," +
                KEY_EMERGENCY_CONTACT_NUMBER + " TEXT," +
                KEY_REGISTRATION_DATE + " TEXT," +
                KEY_STATUS + " TEXT DEFAULT 'ACTIVE'" +
                ")";

        db.execSQL(CREATE_STUDENTS_TABLE);
        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
            onCreate(db);
        }
    }

    public long addStudent(Student student) {
        SQLiteDatabase db = getWritableDatabase();
        long studentId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_FULL_NAME, student.getFullName());
            values.put(KEY_UNIVERSITY_ID, student.getUniversityId());
            values.put(KEY_NIC_NUMBER, student.getNicNumber());
            values.put(KEY_GENDER, student.getGender());
            values.put(KEY_DATE_OF_BIRTH, student.getDateOfBirth());
            values.put(KEY_PHOTO_PATH, student.getPhotoPath());
            values.put(KEY_FACULTY, student.getFaculty());
            values.put(KEY_DEPARTMENT, student.getDepartment());
            values.put(KEY_BATCH, student.getBatch());
            values.put(KEY_CURRENT_SEMESTER, student.getCurrentSemester());
            values.put(KEY_ENROLLMENT_DATE, student.getEnrollmentDate());
            values.put(KEY_EMAIL, student.getEmail());
            values.put(KEY_USERNAME, student.getUsername());
            values.put(KEY_PASSWORD, student.getPassword());
            values.put(KEY_TERMS_ACCEPTED, student.isTermsAccepted() ? 1 : 0);
            values.put(KEY_MOBILE_NUMBER, student.getMobileNumber());
            values.put(KEY_ALTERNATE_NUMBER, student.getAlternateNumber());
            values.put(KEY_PERMANENT_ADDRESS, student.getPermanentAddress());
            values.put(KEY_CITY, student.getCity());
            values.put(KEY_PROVINCE, student.getProvince());
            values.put(KEY_POSTAL_CODE, student.getPostalCode());
            values.put(KEY_EMERGENCY_CONTACT_NAME, student.getEmergencyContactName());
            values.put(KEY_EMERGENCY_RELATIONSHIP, student.getEmergencyRelationship());
            values.put(KEY_EMERGENCY_CONTACT_NUMBER, student.getEmergencyContactNumber());
            values.put(KEY_REGISTRATION_DATE, student.getRegistrationDate());
            values.put(KEY_STATUS, student.getStatus());

            studentId = db.insertOrThrow(TABLE_STUDENTS, null, values);
            db.setTransactionSuccessful();
            Log.d(TAG, "Student added successfully with ID: " + studentId);
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to add student to database", e);
        } finally {
            db.endTransaction();
        }
        return studentId;
    }
    @SuppressLint("Range")
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String STUDENTS_SELECT_QUERY = "SELECT * FROM " + TABLE_STUDENTS;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(STUDENTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Student student = new Student();
                    student.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                    student.setFullName(cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME)));
                    student.setUniversityId(cursor.getString(cursor.getColumnIndex(KEY_UNIVERSITY_ID)));
                    student.setNicNumber(cursor.getString(cursor.getColumnIndex(KEY_NIC_NUMBER)));
                    student.setGender(cursor.getString(cursor.getColumnIndex(KEY_GENDER)));
                    student.setDateOfBirth(cursor.getString(cursor.getColumnIndex(KEY_DATE_OF_BIRTH)));
                    student.setPhotoPath(cursor.getString(cursor.getColumnIndex(KEY_PHOTO_PATH)));
                    student.setFaculty(cursor.getString(cursor.getColumnIndex(KEY_FACULTY)));
                    student.setDepartment(cursor.getString(cursor.getColumnIndex(KEY_DEPARTMENT)));
                    student.setBatch(cursor.getString(cursor.getColumnIndex(KEY_BATCH)));
                    student.setCurrentSemester(cursor.getString(cursor.getColumnIndex(KEY_CURRENT_SEMESTER)));
                    student.setEnrollmentDate(cursor.getString(cursor.getColumnIndex(KEY_ENROLLMENT_DATE)));
                    student.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                    student.setUsername(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
                    student.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
                    student.setTermsAccepted(cursor.getInt(cursor.getColumnIndex(KEY_TERMS_ACCEPTED)) == 1);
                    student.setMobileNumber(cursor.getString(cursor.getColumnIndex(KEY_MOBILE_NUMBER)));
                    student.setAlternateNumber(cursor.getString(cursor.getColumnIndex(KEY_ALTERNATE_NUMBER)));
                    student.setPermanentAddress(cursor.getString(cursor.getColumnIndex(KEY_PERMANENT_ADDRESS)));
                    student.setCity(cursor.getString(cursor.getColumnIndex(KEY_CITY)));
                    student.setProvince(cursor.getString(cursor.getColumnIndex(KEY_PROVINCE)));
                    student.setPostalCode(cursor.getString(cursor.getColumnIndex(KEY_POSTAL_CODE)));
                    student.setEmergencyContactName(cursor.getString(cursor.getColumnIndex(KEY_EMERGENCY_CONTACT_NAME)));
                    student.setEmergencyRelationship(cursor.getString(cursor.getColumnIndex(KEY_EMERGENCY_RELATIONSHIP)));
                    student.setEmergencyNumber(cursor.getString(cursor.getColumnIndex(KEY_EMERGENCY_CONTACT_NUMBER)));
                    student.setEnrollmentDate(cursor.getString(cursor.getColumnIndex(KEY_REGISTRATION_DATE)));
                    student.setStatus(cursor.getString(cursor.getColumnIndex(KEY_STATUS)));

                    students.add(student);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get students from database", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return students;
    }
    @SuppressLint("Range")
    public Student getStudentById(int studentId) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + KEY_ID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(studentId)});

        Student student = null;
        try {
            if (cursor.moveToFirst()) {
                student = new Student();
                student.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                student.setFullName(cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME)));
                student.setUniversityId(cursor.getString(cursor.getColumnIndex(KEY_UNIVERSITY_ID)));
                student.setNicNumber(cursor.getString(cursor.getColumnIndex(KEY_NIC_NUMBER)));
                student.setGender(cursor.getString(cursor.getColumnIndex(KEY_GENDER)));
                student.setDateOfBirth(cursor.getString(cursor.getColumnIndex(KEY_DATE_OF_BIRTH)));
                student.setPhotoPath(cursor.getString(cursor.getColumnIndex(KEY_PHOTO_PATH)));
                student.setFaculty(cursor.getString(cursor.getColumnIndex(KEY_FACULTY)));
                student.setDepartment(cursor.getString(cursor.getColumnIndex(KEY_DEPARTMENT)));
                student.setBatch(cursor.getString(cursor.getColumnIndex(KEY_BATCH)));
                student.setCurrentSemester(cursor.getString(cursor.getColumnIndex(KEY_CURRENT_SEMESTER)));
                student.setEnrollmentDate(cursor.getString(cursor.getColumnIndex(KEY_ENROLLMENT_DATE)));
                student.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                student.setUsername(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
                student.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
                student.setTermsAccepted(cursor.getInt(cursor.getColumnIndex(KEY_TERMS_ACCEPTED)) == 1);
                student.setMobileNumber(cursor.getString(cursor.getColumnIndex(KEY_MOBILE_NUMBER)));
                student.setAlternateNumber(cursor.getString(cursor.getColumnIndex(KEY_ALTERNATE_NUMBER)));
                student.setPermanentAddress(cursor.getString(cursor.getColumnIndex(KEY_PERMANENT_ADDRESS)));
                student.setCity(cursor.getString(cursor.getColumnIndex(KEY_CITY)));
                student.setProvince(cursor.getString(cursor.getColumnIndex(KEY_PROVINCE)));
                student.setPostalCode(cursor.getString(cursor.getColumnIndex(KEY_POSTAL_CODE)));
                student.setEmergencyContactName(cursor.getString(cursor.getColumnIndex(KEY_EMERGENCY_CONTACT_NAME)));
                student.setEmergencyRelationship(cursor.getString(cursor.getColumnIndex(KEY_EMERGENCY_RELATIONSHIP)));
                student.setEmergencyNumber(cursor.getString(cursor.getColumnIndex(KEY_EMERGENCY_CONTACT_NUMBER)));
                student.setEnrollmentDate(cursor.getString(cursor.getColumnIndex(KEY_REGISTRATION_DATE)));
                student.setStatus(cursor.getString(cursor.getColumnIndex(KEY_STATUS)));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get student from database", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return student;
    }
    public boolean isUniversityIdExists(String universityId) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_STUDENTS + " WHERE " + KEY_UNIVERSITY_ID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{universityId});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public boolean isEmailExists(String email, long excludeId) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_STUDENTS + " WHERE " + KEY_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_STUDENTS + " WHERE " + KEY_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public int updateStudent(Student student) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FULL_NAME, student.getFullName());
        values.put(KEY_UNIVERSITY_ID, student.getUniversityId());
        values.put(KEY_NIC_NUMBER, student.getNicNumber());
        values.put(KEY_GENDER, student.getGender());
        values.put(KEY_DATE_OF_BIRTH, student.getDateOfBirth());
        values.put(KEY_PHOTO_PATH, student.getPhotoPath());
        values.put(KEY_FACULTY, student.getFaculty());
        values.put(KEY_DEPARTMENT, student.getDepartment());
        values.put(KEY_BATCH, student.getBatch());
        values.put(KEY_CURRENT_SEMESTER, student.getCurrentSemester());
        values.put(KEY_ENROLLMENT_DATE, student.getEnrollmentDate());
        values.put(KEY_EMAIL, student.getEmail());
        values.put(KEY_USERNAME, student.getUsername());
        values.put(KEY_PASSWORD, student.getPassword());
        values.put(KEY_TERMS_ACCEPTED, student.isTermsAccepted() ? 1 : 0);
        values.put(KEY_MOBILE_NUMBER, student.getMobileNumber());
        values.put(KEY_ALTERNATE_NUMBER, student.getAlternateNumber());
        values.put(KEY_PERMANENT_ADDRESS, student.getPermanentAddress());
        values.put(KEY_CITY, student.getCity());
        values.put(KEY_PROVINCE, student.getProvince());
        values.put(KEY_POSTAL_CODE, student.getPostalCode());
        values.put(KEY_EMERGENCY_CONTACT_NAME, student.getEmergencyContactName());
        values.put(KEY_EMERGENCY_RELATIONSHIP, student.getEmergencyRelationship());
        values.put(KEY_EMERGENCY_CONTACT_NUMBER, student.getEmergencyContactNumber());
        values.put(KEY_STATUS, student.getStatus());

        return db.update(TABLE_STUDENTS, values, KEY_ID + " = ?", new String[]{String.valueOf(student.getId())});
    }


    // Add these methods to your StudentDatabaseHelper class

    public void deleteStudent(Student student) {
        SQLiteDatabase db = getWritableDatabase();

        // Delete the photo file first
        if (student.getPhotoPath() != null && !student.getPhotoPath().isEmpty()) {
            File photoFile = new File(student.getPhotoPath());
            if (photoFile.exists()) {
                boolean deleted = photoFile.delete();
                Log.d(TAG, "Photo deleted: " + deleted + " - " + student.getPhotoPath());
            }
        }

        // Delete from database
        int rowsDeleted = db.delete(TABLE_STUDENTS, KEY_ID + " = ?", new String[]{String.valueOf(student.getId())});
        Log.d(TAG, "Student deleted from database. Rows affected: " + rowsDeleted);
    }

    // Method to clean up orphaned image files
    public void cleanupOrphanedImages(Context context) {
        // Get all photo paths from database
        List<String> dbPhotoPaths = new ArrayList<>();
        String query = "SELECT " + KEY_PHOTO_PATH + " FROM " + TABLE_STUDENTS + " WHERE " + KEY_PHOTO_PATH + " IS NOT NULL";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String photoPath = cursor.getString(0);
                    if (photoPath != null && !photoPath.isEmpty()) {
                        dbPhotoPaths.add(photoPath);
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        // Check files in student_photos directory
        File photosDir = new File(context.getFilesDir(), "student_photos");
        if (photosDir.exists() && photosDir.isDirectory()) {
            File[] files = photosDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String filePath = file.getAbsolutePath();
                    if (!dbPhotoPaths.contains(filePath)) {
                        // This file is not referenced in database, delete it
                        boolean deleted = file.delete();
                        Log.d(TAG, "Orphaned image deleted: " + deleted + " - " + filePath);
                    }
                }
            }
        }
    }

    // Method to get student photo as Bitmap
    public static Bitmap getStudentPhoto(String photoPath) {
        if (photoPath == null || photoPath.isEmpty()) {
            return null;
        }

        try {
            File imageFile = new File(photoPath);
            if (imageFile.exists()) {
                return BitmapFactory.decodeFile(photoPath);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading student photo", e);
        }
        return null;
    }

    public void insertOrUpdateStudent(Student studentDetails) {
    }
}