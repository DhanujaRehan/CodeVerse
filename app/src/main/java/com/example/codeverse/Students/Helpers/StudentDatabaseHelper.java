package com.example.codeverse.Students.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.codeverse.Students.Models.Student;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "StudentDetails.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_STUDENTS = "StudentDetails";

    // Student Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_UNIVERSITY_ID = "university_id";
    private static final String KEY_NIC_NUMBER = "nic_number";
    private static final String KEY_DATE_OF_BIRTH = "date_of_birth";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_PHOTO_URI = "photo_uri";

    // Academic Information Columns
    private static final String KEY_FACULTY = "faculty";
    private static final String KEY_BATCH = "batch";
    private static final String KEY_SEMESTER = "semester";
    private static final String KEY_ENROLLMENT_DATE = "enrollment_date";

    // Contact Information Columns
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_ALTERNATE_NUMBER = "alternate_number";
    private static final String KEY_PERMANENT_ADDRESS = "permanent_address";
    private static final String KEY_CITY = "city";
    private static final String KEY_PROVINCE = "province";
    private static final String KEY_POSTAL_CODE = "postal_code";

    // Emergency Contact Information Columns
    private static final String KEY_EMERGENCY_NAME = "emergency_name";
    private static final String KEY_EMERGENCY_RELATIONSHIP = "emergency_relationship";
    private static final String KEY_EMERGENCY_NUMBER = "emergency_number";

    // Account Information Columns
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TERMS_ACCEPTED = "terms_accepted";

    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_UPDATED_AT = "updated_at";

    private static final String TAG = "StudentDatabaseHelper";

    public StudentDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is created for the FIRST time.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STUDENTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_FULL_NAME + " TEXT NOT NULL," +
                KEY_UNIVERSITY_ID + " TEXT UNIQUE NOT NULL," +
                KEY_NIC_NUMBER + " TEXT UNIQUE NOT NULL," +
                KEY_DATE_OF_BIRTH + " TEXT NOT NULL," +
                KEY_GENDER + " TEXT NOT NULL," +
                KEY_PHOTO_URI + " TEXT," +
                KEY_FACULTY + " TEXT," +
                KEY_BATCH + " TEXT," +
                KEY_SEMESTER + " TEXT," +
                KEY_ENROLLMENT_DATE + " TEXT," +
                KEY_MOBILE_NUMBER + " TEXT," +
                KEY_ALTERNATE_NUMBER + " TEXT," +
                KEY_PERMANENT_ADDRESS + " TEXT," +
                KEY_CITY + " TEXT," +
                KEY_PROVINCE + " TEXT," +
                KEY_POSTAL_CODE + " TEXT," +
                KEY_EMERGENCY_NAME + " TEXT," +
                KEY_EMERGENCY_RELATIONSHIP + " TEXT," +
                KEY_EMERGENCY_NUMBER + " TEXT," +
                KEY_EMAIL + " TEXT UNIQUE," +
                KEY_USERNAME + " TEXT UNIQUE," +
                KEY_PASSWORD + " TEXT," +
                KEY_TERMS_ACCEPTED + " INTEGER DEFAULT 0," +
                KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                KEY_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";

        db.execSQL(CREATE_STUDENTS_TABLE);
        Log.d(TAG, "Database tables created");
    }

    // Called when the database needs to be upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
            onCreate(db);
        }
    }

    // Insert a student into the database
    public long insertStudent(Student student) {
        SQLiteDatabase db = getWritableDatabase();
        long studentId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_FULL_NAME, student.getFullName());
            values.put(KEY_UNIVERSITY_ID, student.getUniversityId());
            values.put(KEY_NIC_NUMBER, student.getNicNumber());
            values.put(KEY_DATE_OF_BIRTH, student.getDateOfBirth());
            values.put(KEY_GENDER, student.getGender());
            values.put(KEY_PHOTO_URI, student.getPhotoUri());

            // Academic Information
            values.put(KEY_FACULTY, student.getFaculty());
            values.put(KEY_BATCH, student.getBatch());
            values.put(KEY_SEMESTER, student.getSemester());
            values.put(KEY_ENROLLMENT_DATE, student.getEnrollmentDate());

            // Contact Information
            values.put(KEY_MOBILE_NUMBER, student.getMobileNumber());
            values.put(KEY_ALTERNATE_NUMBER, student.getAlternateNumber());
            values.put(KEY_PERMANENT_ADDRESS, student.getPermanentAddress());
            values.put(KEY_CITY, student.getCity());
            values.put(KEY_PROVINCE, student.getProvince());
            values.put(KEY_POSTAL_CODE, student.getPostalCode());

            // Emergency Contact Information
            values.put(KEY_EMERGENCY_NAME, student.getEmergencyName());
            values.put(KEY_EMERGENCY_RELATIONSHIP, student.getEmergencyRelationship());
            values.put(KEY_EMERGENCY_NUMBER, student.getEmergencyNumber());

            // Account Information
            values.put(KEY_EMAIL, student.getEmail());
            values.put(KEY_USERNAME, student.getUsername());
            values.put(KEY_PASSWORD, student.getPassword());
            values.put(KEY_TERMS_ACCEPTED, student.isTermsAccepted() ? 1 : 0);

            // Get current timestamp
            String currentTimestamp = getCurrentTimestamp();
            values.put(KEY_CREATED_AT, currentTimestamp);
            values.put(KEY_UPDATED_AT, currentTimestamp);

            studentId = db.insertOrThrow(TABLE_STUDENTS, null, values);
            db.setTransactionSuccessful();

            Log.d(TAG, "Student inserted with ID: " + studentId);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add student to database: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        return studentId;
    }

    // Get a student by ID
    public Student getStudentById(long studentId) {
        SQLiteDatabase db = getReadableDatabase();
        Student student = null;

        String STUDENT_SELECT_QUERY =
                "SELECT * FROM " + TABLE_STUDENTS +
                        " WHERE " + KEY_ID + " = ?";

        Cursor cursor = db.rawQuery(STUDENT_SELECT_QUERY, new String[]{String.valueOf(studentId)});
        try {
            if (cursor.moveToFirst()) {
                student = getStudentFromCursor(cursor);
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get student from database: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return student;
    }

    // Get all students
    public List<Student> getAllStudent() {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String STUDENTS_SELECT_QUERY =
                "SELECT * FROM " + TABLE_STUDENTS +
                        " ORDER BY " + KEY_CREATED_AT + " DESC";

        Cursor cursor = db.rawQuery(STUDENTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Student student = getStudentFromCursor(cursor);
                    students.add(student);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get students from database: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return students;
    }

    // Update a student
    public int updateStudent(Student student) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = 0;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_FULL_NAME, student.getFullName());
            values.put(KEY_UNIVERSITY_ID, student.getUniversityId());
            values.put(KEY_NIC_NUMBER, student.getNicNumber());
            values.put(KEY_DATE_OF_BIRTH, student.getDateOfBirth());
            values.put(KEY_GENDER, student.getGender());
            values.put(KEY_PHOTO_URI, student.getPhotoUri());

            // Academic Information
            values.put(KEY_FACULTY, student.getFaculty());
            values.put(KEY_BATCH, student.getBatch());
            values.put(KEY_SEMESTER, student.getSemester());
            values.put(KEY_ENROLLMENT_DATE, student.getEnrollmentDate());

            // Contact Information
            values.put(KEY_MOBILE_NUMBER, student.getMobileNumber());
            values.put(KEY_ALTERNATE_NUMBER, student.getAlternateNumber());
            values.put(KEY_PERMANENT_ADDRESS, student.getPermanentAddress());
            values.put(KEY_CITY, student.getCity());
            values.put(KEY_PROVINCE, student.getProvince());
            values.put(KEY_POSTAL_CODE, student.getPostalCode());

            // Emergency Contact Information
            values.put(KEY_EMERGENCY_NAME, student.getEmergencyName());
            values.put(KEY_EMERGENCY_RELATIONSHIP, student.getEmergencyRelationship());
            values.put(KEY_EMERGENCY_NUMBER, student.getEmergencyNumber());

            // Account Information
            values.put(KEY_EMAIL, student.getEmail());
            values.put(KEY_USERNAME, student.getUsername());
            values.put(KEY_PASSWORD, student.getPassword());
            values.put(KEY_TERMS_ACCEPTED, student.isTermsAccepted() ? 1 : 0);

            values.put(KEY_UPDATED_AT, getCurrentTimestamp());

            rowsAffected = db.update(TABLE_STUDENTS, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(student.getId())});

            db.setTransactionSuccessful();
            Log.d(TAG, "Student updated. Rows affected: " + rowsAffected);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update student: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        return rowsAffected;
    }

    // Update only account details for a student
    public int updateStudentAccountDetails(long studentId, String email, String username,
                                           String password, boolean termsAccepted) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = 0;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_EMAIL, email);
            values.put(KEY_USERNAME, username);
            values.put(KEY_PASSWORD, password);
            values.put(KEY_TERMS_ACCEPTED, termsAccepted ? 1 : 0);
            values.put(KEY_UPDATED_AT, getCurrentTimestamp());

            rowsAffected = db.update(TABLE_STUDENTS, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(studentId)});

            db.setTransactionSuccessful();
            Log.d(TAG, "Student account details updated. Rows affected: " + rowsAffected);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update student account details: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        return rowsAffected;
    }

    // Update only contact details for a student
    public int updateStudentContactDetails(long studentId, String mobileNumber, String alternateNumber,
                                           String permanentAddress, String city, String province, String postalCode,
                                           String emergencyName, String emergencyRelationship, String emergencyNumber) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = 0;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_MOBILE_NUMBER, mobileNumber);
            values.put(KEY_ALTERNATE_NUMBER, alternateNumber);
            values.put(KEY_PERMANENT_ADDRESS, permanentAddress);
            values.put(KEY_CITY, city);
            values.put(KEY_PROVINCE, province);
            values.put(KEY_POSTAL_CODE, postalCode);
            values.put(KEY_EMERGENCY_NAME, emergencyName);
            values.put(KEY_EMERGENCY_RELATIONSHIP, emergencyRelationship);
            values.put(KEY_EMERGENCY_NUMBER, emergencyNumber);
            values.put(KEY_UPDATED_AT, getCurrentTimestamp());

            rowsAffected = db.update(TABLE_STUDENTS, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(studentId)});

            db.setTransactionSuccessful();
            Log.d(TAG, "Student contact details updated. Rows affected: " + rowsAffected);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update student contact details: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        return rowsAffected;
    }

    // Update only academic details for a student
    // Enhanced version of updateStudentAcademicDetails method for debugging
    public int updateStudentAcademicDetails(long studentId, String faculty,
                                            String batch, String semester, String enrollmentDate) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = 0;

        Log.d(TAG, "updateStudentAcademicDetails called with:");
        Log.d(TAG, "  studentId: " + studentId);
        Log.d(TAG, "  faculty: " + faculty);
        Log.d(TAG, "  batch: " + batch);
        Log.d(TAG, "  semester: " + semester);
        Log.d(TAG, "  enrollmentDate: " + enrollmentDate);

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_FACULTY, faculty);
            values.put(KEY_BATCH, batch);
            values.put(KEY_SEMESTER, semester);
            values.put(KEY_ENROLLMENT_DATE, enrollmentDate);
            values.put(KEY_UPDATED_AT, getCurrentTimestamp());

            // Add debugging to check what we're updating
            Log.d(TAG, "Executing update query for student ID: " + studentId);

            rowsAffected = db.update(TABLE_STUDENTS, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(studentId)});

            if (rowsAffected > 0) {
                db.setTransactionSuccessful();
                Log.d(TAG, "Student academic details updated successfully. Rows affected: " + rowsAffected);

                // Verify the update by querying the record
                verifyAcademicUpdate(db, studentId, faculty, batch, semester, enrollmentDate);
            } else {
                Log.e(TAG, "No rows affected during update. Student ID might not exist: " + studentId);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error while trying to update student academic details: " + e.getMessage(), e);
        } finally {
            db.endTransaction();
        }

        return rowsAffected;
    }

    // Helper method to verify the academic details were actually saved
    private void verifyAcademicUpdate(SQLiteDatabase db, long studentId, String expectedFaculty,
                                      String expectedBatch, String expectedSemester, String expectedEnrollmentDate) {
        String verifyQuery = "SELECT " + KEY_FACULTY + ", " + KEY_BATCH + ", " +
                KEY_SEMESTER + ", " + KEY_ENROLLMENT_DATE +
                " FROM " + TABLE_STUDENTS + " WHERE " + KEY_ID + " = ?";

        Cursor cursor = db.rawQuery(verifyQuery, new String[]{String.valueOf(studentId)});
        try {
            if (cursor.moveToFirst()) {
                String actualFaculty = cursor.getString(0);
                String actualBatch = cursor.getString(1);
                String actualSemester = cursor.getString(2);
                String actualEnrollmentDate = cursor.getString(3);

                Log.d(TAG, "Verification - Data saved in database:");
                Log.d(TAG, "  Faculty: " + actualFaculty + " (expected: " + expectedFaculty + ")");
                Log.d(TAG, "  Batch: " + actualBatch + " (expected: " + expectedBatch + ")");
                Log.d(TAG, "  Semester: " + actualSemester + " (expected: " + expectedSemester + ")");
                Log.d(TAG, "  Enrollment Date: " + actualEnrollmentDate + " (expected: " + expectedEnrollmentDate + ")");
            } else {
                Log.e(TAG, "Verification failed - No record found for student ID: " + studentId);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    // Delete a student by ID
    public void deleteStudent(long studentId) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            db.delete(TABLE_STUDENTS, KEY_ID + " = ?", new String[]{String.valueOf(studentId)});
            db.setTransactionSuccessful();
            Log.d(TAG, "Student deleted with ID: " + studentId);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete student: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    // Delete a student object (with photo cleanup)
    public void deleteStudentWithPhotoCleanup(Student student) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            // Delete the photo file first
            if (student.getPhotoUri() != null && !student.getPhotoUri().isEmpty()) {
                File photoFile = new File(student.getPhotoUri());
                if (photoFile.exists()) {
                    boolean deleted = photoFile.delete();
                    Log.d(TAG, "Photo deleted: " + deleted + " - " + student.getPhotoUri());
                }
            }

            // Delete from database
            int rowsDeleted = db.delete(TABLE_STUDENTS, KEY_ID + " = ?", new String[]{String.valueOf(student.getId())});
            db.setTransactionSuccessful();
            Log.d(TAG, "Student deleted from database. Rows affected: " + rowsDeleted);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete student with photo cleanup: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    // Check if University ID already exists
    public boolean isUniversityIdExists(String universityId) {
        SQLiteDatabase db = getReadableDatabase();
        boolean exists = false;

        String query = "SELECT 1 FROM " + TABLE_STUDENTS +
                " WHERE " + KEY_UNIVERSITY_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{universityId});
        try {
            exists = cursor.getCount() > 0;
        } catch (Exception e) {
            Log.d(TAG, "Error checking university ID: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return exists;
    }

    // Check if Email already exists
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = getReadableDatabase();
        boolean exists = false;

        String query = "SELECT 1 FROM " + TABLE_STUDENTS +
                " WHERE " + KEY_EMAIL + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{email});
        try {
            exists = cursor.getCount() > 0;
        } catch (Exception e) {
            Log.d(TAG, "Error checking email: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return exists;
    }

    // Check if Username already exists
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        boolean exists = false;

        String query = "SELECT 1 FROM " + TABLE_STUDENTS +
                " WHERE " + KEY_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username});
        try {
            exists = cursor.getCount() > 0;
        } catch (Exception e) {
            Log.d(TAG, "Error checking username: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return exists;
    }

    // Check if NIC number already exists
    public boolean isNicExists(String nicNumber) {
        SQLiteDatabase db = getReadableDatabase();
        boolean exists = false;

        String query = "SELECT 1 FROM " + TABLE_STUDENTS +
                " WHERE " + KEY_NIC_NUMBER + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{nicNumber});
        try {
            exists = cursor.getCount() > 0;
        } catch (Exception e) {
            Log.d(TAG, "Error checking NIC number: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return exists;
    }

    // Search students by name or university ID
    public List<Student> searchStudent(String searchQuery) {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String SEARCH_QUERY =
                "SELECT * FROM " + TABLE_STUDENTS +
                        " WHERE " + KEY_FULL_NAME + " LIKE ? OR " + KEY_UNIVERSITY_ID + " LIKE ?" +
                        " ORDER BY " + KEY_FULL_NAME + " ASC";

        String searchPattern = "%" + searchQuery + "%";
        Cursor cursor = db.rawQuery(SEARCH_QUERY, new String[]{searchPattern, searchPattern});

        try {
            if (cursor.moveToFirst()) {
                do {
                    Student student = getStudentFromCursor(cursor);
                    students.add(student);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while searching students: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return students;
    }

    // Get student count
    public int getStudentCount() {
        SQLiteDatabase db = getReadableDatabase();
        int count = 0;

        String COUNT_QUERY = "SELECT COUNT(*) FROM " + TABLE_STUDENTS;
        Cursor cursor = db.rawQuery(COUNT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while getting student count: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return count;
    }

    // Delete all students
    public void deleteAllStudent() {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            db.delete(TABLE_STUDENTS, null, null);
            db.setTransactionSuccessful();
            Log.d(TAG, "All students deleted");
        } catch (Exception e) {
            Log.d(TAG, "Error while deleting all students: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    // Method to clean up orphaned image files
    public void cleanupOrphanedImages(Context context) {
        // Get all photo paths from database
        List<String> dbPhotoPaths = new ArrayList<>();
        String query = "SELECT " + KEY_PHOTO_URI + " FROM " + TABLE_STUDENTS + " WHERE " + KEY_PHOTO_URI + " IS NOT NULL";
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
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
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

    // Helper method to get Student object from cursor
    private Student getStudentFromCursor(Cursor cursor) {
        Student student = new Student();

        student.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)));
        student.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FULL_NAME)));
        student.setUniversityId(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UNIVERSITY_ID)));
        student.setNicNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NIC_NUMBER)));
        student.setDateOfBirth(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_OF_BIRTH)));
        student.setGender(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GENDER)));
        student.setPhotoUri(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHOTO_URI)));

        // Academic Information
        student.setFaculty(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FACULTY)));
        student.setBatch(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BATCH)));
        student.setSemester(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SEMESTER)));
        student.setEnrollmentDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ENROLLMENT_DATE)));

        // Contact Information
        student.setMobileNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MOBILE_NUMBER)));
        student.setAlternateNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ALTERNATE_NUMBER)));
        student.setPermanentAddress(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PERMANENT_ADDRESS)));
        student.setCity(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CITY)));
        student.setProvince(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROVINCE)));
        student.setPostalCode(cursor.getString(cursor.getColumnIndexOrThrow(KEY_POSTAL_CODE)));

        // Emergency Contact Information
        student.setEmergencyName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMERGENCY_NAME)));
        student.setEmergencyRelationship(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMERGENCY_RELATIONSHIP)));
        student.setEmergencyNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMERGENCY_NUMBER)));

        // Account Information
        student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)));
        student.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERNAME)));
        student.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD)));
        student.setTermsAccepted(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TERMS_ACCEPTED)) == 1);

        student.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CREATED_AT)));
        student.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UPDATED_AT)));

        return student;
    }

    // Helper method to get current timestamp
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
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
}