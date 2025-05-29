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

     private static final String DATABASE_NAME = "StudentDetails.db";
    private static final int DATABASE_VERSION = 1;

     private static final String TABLE_STUDENTS = "StudentDetails";

     private static final String KEY_ID = "id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_UNIVERSITY_ID = "university_id";
    private static final String KEY_NIC_NUMBER = "nic_number";
    private static final String KEY_DATE_OF_BIRTH = "date_of_birth";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_PHOTO_URI = "photo_uri";

     private static final String KEY_FACULTY = "faculty";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_BATCH = "batch";
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

     @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
             db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
            onCreate(db);
        }
    }

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

             values.put(KEY_FACULTY, student.getFaculty());
            values.put(KEY_DEPARTMENT, student.getDepartment());
            values.put(KEY_BATCH, student.getBatch());
            values.put(KEY_SEMESTER, student.getSemester());
            values.put(KEY_ENROLLMENT_DATE, student.getEnrollmentDate());

             values.put(KEY_MOBILE_NUMBER, student.getMobileNumber());
            values.put(KEY_ALTERNATE_NUMBER, student.getAlternateNumber());
            values.put(KEY_PERMANENT_ADDRESS, student.getPermanentAddress());
            values.put(KEY_CITY, student.getCity());
            values.put(KEY_PROVINCE, student.getProvince());
            values.put(KEY_POSTAL_CODE, student.getPostalCode());

             values.put(KEY_EMERGENCY_NAME, student.getEmergencyName());
            values.put(KEY_EMERGENCY_RELATIONSHIP, student.getEmergencyRelationship());
            values.put(KEY_EMERGENCY_NUMBER, student.getEmergencyNumber());

             values.put(KEY_EMAIL, student.getEmail());
            values.put(KEY_USERNAME, student.getUsername());
            values.put(KEY_PASSWORD, student.getPassword());
            values.put(KEY_TERMS_ACCEPTED, student.isTermsAccepted() ? 1 : 0);

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

             values.put(KEY_FACULTY, student.getFaculty());
            values.put(KEY_DEPARTMENT, student.getDepartment());
            values.put(KEY_BATCH, student.getBatch());
            values.put(KEY_SEMESTER, student.getSemester());
            values.put(KEY_ENROLLMENT_DATE, student.getEnrollmentDate());

             values.put(KEY_MOBILE_NUMBER, student.getMobileNumber());
            values.put(KEY_ALTERNATE_NUMBER, student.getAlternateNumber());
            values.put(KEY_PERMANENT_ADDRESS, student.getPermanentAddress());
            values.put(KEY_CITY, student.getCity());
            values.put(KEY_PROVINCE, student.getProvince());
            values.put(KEY_POSTAL_CODE, student.getPostalCode());

             values.put(KEY_EMERGENCY_NAME, student.getEmergencyName());
            values.put(KEY_EMERGENCY_RELATIONSHIP, student.getEmergencyRelationship());
            values.put(KEY_EMERGENCY_NUMBER, student.getEmergencyNumber());

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

     private Students getStudentFromCursor(Cursor cursor) {
        Students student = new Students();

        student.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)));
        student.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FULL_NAME)));
        student.setUniversityId(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UNIVERSITY_ID)));
        student.setNicNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NIC_NUMBER)));
        student.setDateOfBirth(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_OF_BIRTH)));
        student.setGender(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GENDER)));
        student.setPhotoUri(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHOTO_URI)));

         student.setFaculty(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FACULTY)));
        student.setDepartment(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DEPARTMENT)));
        student.setBatch(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BATCH)));
        student.setSemester(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SEMESTER)));
        student.setEnrollmentDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ENROLLMENT_DATE)));

         student.setMobileNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MOBILE_NUMBER)));
        student.setAlternateNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ALTERNATE_NUMBER)));
        student.setPermanentAddress(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PERMANENT_ADDRESS)));
        student.setCity(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CITY)));
        student.setProvince(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROVINCE)));
        student.setPostalCode(cursor.getString(cursor.getColumnIndexOrThrow(KEY_POSTAL_CODE)));

         student.setEmergencyName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMERGENCY_NAME)));
        student.setEmergencyRelationship(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMERGENCY_RELATIONSHIP)));
        student.setEmergencyNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMERGENCY_NUMBER)));

         student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)));
        student.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERNAME)));
        student.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD)));
        student.setTermsAccepted(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TERMS_ACCEPTED)) == 1);

        student.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CREATED_AT)));
        student.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UPDATED_AT)));

        return student;
    }

     private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}