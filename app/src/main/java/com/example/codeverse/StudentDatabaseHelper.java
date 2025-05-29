package com.example.codeverse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_BATCH = "batch";
    private static final String KEY_SEMESTER = "semester";
    private static final String KEY_ENROLLMENT_DATE = "enrollment_date";

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
                KEY_DEPARTMENT + " TEXT," +
                KEY_BATCH + " TEXT," +
                KEY_SEMESTER + " TEXT," +
                KEY_ENROLLMENT_DATE + " TEXT," +
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
    public long insertStudent(Students student) {
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
            values.put(KEY_DEPARTMENT, student.getDepartment());
            values.put(KEY_BATCH, student.getBatch());
            values.put(KEY_SEMESTER, student.getSemester());
            values.put(KEY_ENROLLMENT_DATE, student.getEnrollmentDate());

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
    public Students getStudentById(long studentId) {
        SQLiteDatabase db = getReadableDatabase();
        Students student = null;

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
    public List<Students> getAllStudents() {
        List<Students> students = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String STUDENTS_SELECT_QUERY =
                "SELECT * FROM " + TABLE_STUDENTS +
                        " ORDER BY " + KEY_CREATED_AT + " DESC";

        Cursor cursor = db.rawQuery(STUDENTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Students student = getStudentFromCursor(cursor);
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
    public int updateStudent(Students student) {
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
            values.put(KEY_DEPARTMENT, student.getDepartment());
            values.put(KEY_BATCH, student.getBatch());
            values.put(KEY_SEMESTER, student.getSemester());
            values.put(KEY_ENROLLMENT_DATE, student.getEnrollmentDate());

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

    // Update only academic details for a student
    public int updateStudentAcademicDetails(long studentId, String faculty, String department,
                                            String batch, String semester, String enrollmentDate) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = 0;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_FACULTY, faculty);
            values.put(KEY_DEPARTMENT, department);
            values.put(KEY_BATCH, batch);
            values.put(KEY_SEMESTER, semester);
            values.put(KEY_ENROLLMENT_DATE, enrollmentDate);
            values.put(KEY_UPDATED_AT, getCurrentTimestamp());

            rowsAffected = db.update(TABLE_STUDENTS, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(studentId)});

            db.setTransactionSuccessful();
            Log.d(TAG, "Student academic details updated. Rows affected: " + rowsAffected);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update student academic details: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        return rowsAffected;
    }

    // Delete a student
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
    public List<Students> searchStudents(String searchQuery) {
        List<Students> students = new ArrayList<>();
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
                    Students student = getStudentFromCursor(cursor);
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
    public void deleteAllStudents() {
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

    // Helper method to get Student object from cursor
    private Students getStudentFromCursor(Cursor cursor) {
        Students student = new Students();

        student.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)));
        student.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FULL_NAME)));
        student.setUniversityId(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UNIVERSITY_ID)));
        student.setNicNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NIC_NUMBER)));
        student.setDateOfBirth(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_OF_BIRTH)));
        student.setGender(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GENDER)));
        student.setPhotoUri(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHOTO_URI)));

        // Academic Information
        student.setFaculty(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FACULTY)));
        student.setDepartment(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DEPARTMENT)));
        student.setBatch(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BATCH)));
        student.setSemester(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SEMESTER)));
        student.setEnrollmentDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ENROLLMENT_DATE)));

        student.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CREATED_AT)));
        student.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UPDATED_AT)));

        return student;
    }

    // Helper method to get current timestamp
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}