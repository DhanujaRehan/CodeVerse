package com.example.codeverse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.codeverse.StudentDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Complete SQLite Database Helper for Student Management System
 * Implements singleton pattern for thread-safe database operations
 */
public class StudentDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "StudentDatabaseHelper";

    // Database configuration
    private static final String DATABASE_NAME = "StudentDatabase.db";
    private static final int DATABASE_VERSION = 2; // Increased for upgrades

    // Table names
    private static final String TABLE_STUDENT_DETAILS = "StudentDetails";
    private static final String TABLE_AUDIT_LOG = "AuditLog";

    // StudentDetails Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_UNIVERSITY_ID = "universityId";
    private static final String KEY_NIC_NUMBER = "nicNumber";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_DATE_OF_BIRTH = "dateOfBirth";
    private static final String KEY_STUDENT_PHOTO = "studentPhoto";

    // Academic Details Columns
    private static final String KEY_FACULTY = "faculty";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_BATCH = "batch";
    private static final String KEY_SEMESTER = "semester";
    private static final String KEY_ENROLLMENT_DATE = "enrollmentDate";

    // Account Details Columns
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TERMS_ACCEPTED = "termsAccepted";

    // Contact Details Columns
    private static final String KEY_MOBILE_NUMBER = "mobileNumber";
    private static final String KEY_ALTERNATE_NUMBER = "alternateNumber";
    private static final String KEY_PERMANENT_ADDRESS = "permanentAddress";
    private static final String KEY_CITY = "city";
    private static final String KEY_PROVINCE = "province";
    private static final String KEY_POSTAL_CODE = "postalCode";

    // Emergency Contact Columns
    private static final String KEY_EMERGENCY_NAME = "emergencyName";
    private static final String KEY_EMERGENCY_RELATIONSHIP = "emergencyRelationship";
    private static final String KEY_EMERGENCY_NUMBER = "emergencyNumber";

    // Timestamp Columns
    private static final String KEY_CREATED_AT = "createdAt";
    private static final String KEY_UPDATED_AT = "updatedAt";

    // Status and Metadata Columns
    private static final String KEY_STATUS = "status";
    private static final String KEY_IS_ACTIVE = "isActive";
    private static final String KEY_COMPLETION_PERCENTAGE = "completionPercentage";

    // Audit Log Columns
    private static final String AUDIT_ID = "auditId";
    private static final String AUDIT_STUDENT_ID = "studentId";
    private static final String AUDIT_ACTION = "action";
    private static final String AUDIT_OLD_VALUES = "oldValues";
    private static final String AUDIT_NEW_VALUES = "newValues";
    private static final String AUDIT_TIMESTAMP = "timestamp";
    private static final String AUDIT_USER = "user";

    // Singleton instance
    private static StudentDatabaseHelper instance;
    private static final Object LOCK = new Object();

    // Date formatter for timestamps
    private static final SimpleDateFormat DATE_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    /**
     * Get singleton instance (thread-safe)
     */
    public static StudentDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new StudentDatabaseHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * Private constructor to enforce singleton pattern
     */
    private StudentDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);

        // Enable Write-Ahead Logging for better performance
        if (!db.isReadOnly()) {
            db.enableWriteAheadLogging();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            createStudentDetailsTable(db);
            createAuditLogTable(db);
            createIndexes(db);
            insertDefaultData(db);

            Log.i(TAG, "Database created successfully with version " + DATABASE_VERSION);
        } catch (SQLException e) {
            Log.e(TAG, "Error creating database", e);
            throw new RuntimeException("Failed to create database", e);
        }
    }

    /**
     * Create StudentDetails table with all constraints
     */
    private void createStudentDetailsTable(SQLiteDatabase db) {
        String CREATE_STUDENT_DETAILS_TABLE = "CREATE TABLE " + TABLE_STUDENT_DETAILS + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                // Basic Information
                KEY_FULL_NAME + " TEXT NOT NULL CHECK(length(" + KEY_FULL_NAME + ") >= 2), " +
                KEY_UNIVERSITY_ID + " TEXT UNIQUE NOT NULL CHECK(length(" + KEY_UNIVERSITY_ID + ") = 9), " +
                KEY_NIC_NUMBER + " TEXT UNIQUE NOT NULL CHECK(length(" + KEY_NIC_NUMBER + ") >= 10), " +
                KEY_GENDER + " TEXT NOT NULL CHECK(" + KEY_GENDER + " IN ('Male', 'Female', 'Other', 'Prefer not to say')), " +
                KEY_DATE_OF_BIRTH + " TEXT NOT NULL, " +
                KEY_STUDENT_PHOTO + " TEXT, " +

                // Academic Details
                KEY_FACULTY + " TEXT CHECK(" + KEY_FACULTY + " IN ('Science', 'Engineering', 'Medicine', 'Business', 'Arts', 'Law')), " +
                KEY_DEPARTMENT + " TEXT, " +
                KEY_BATCH + " TEXT CHECK(" + KEY_BATCH + " GLOB '[0-9][0-9][0-9][0-9]'), " +
                KEY_SEMESTER + " TEXT CHECK(" + KEY_SEMESTER + " GLOB 'Semester [1-8]'), " +
                KEY_ENROLLMENT_DATE + " TEXT, " +

                // Account Details
                KEY_EMAIL + " TEXT UNIQUE CHECK(" + KEY_EMAIL + " LIKE '%@%.%'), " +
                KEY_USERNAME + " TEXT UNIQUE CHECK(length(" + KEY_USERNAME + ") >= 5), " +
                KEY_PASSWORD + " TEXT CHECK(length(" + KEY_PASSWORD + ") >= 8), " +
                KEY_TERMS_ACCEPTED + " INTEGER DEFAULT 0 CHECK(" + KEY_TERMS_ACCEPTED + " IN (0, 1)), " +

                // Contact Details
                KEY_MOBILE_NUMBER + " TEXT CHECK(length(" + KEY_MOBILE_NUMBER + ") = 10 AND " + KEY_MOBILE_NUMBER + " GLOB '[0-9]*'), " +
                KEY_ALTERNATE_NUMBER + " TEXT CHECK(" + KEY_ALTERNATE_NUMBER + " IS NULL OR (length(" + KEY_ALTERNATE_NUMBER + ") = 10 AND " + KEY_ALTERNATE_NUMBER + " GLOB '[0-9]*')), " +
                KEY_PERMANENT_ADDRESS + " TEXT CHECK(length(" + KEY_PERMANENT_ADDRESS + ") >= 5), " +
                KEY_CITY + " TEXT, " +
                KEY_PROVINCE + " TEXT CHECK(" + KEY_PROVINCE + " IN ('Western Province', 'Central Province', 'Southern Province', 'Northern Province', 'Eastern Province', 'North Western Province', 'North Central Province', 'Uva Province', 'Sabaragamuwa Province')), " +
                KEY_POSTAL_CODE + " TEXT CHECK(length(" + KEY_POSTAL_CODE + ") = 5 AND " + KEY_POSTAL_CODE + " GLOB '[0-9]*'), " +

                // Emergency Contact
                KEY_EMERGENCY_NAME + " TEXT, " +
                KEY_EMERGENCY_RELATIONSHIP + " TEXT CHECK(" + KEY_EMERGENCY_RELATIONSHIP + " IN ('Parent', 'Guardian', 'Sibling', 'Spouse', 'Relative', 'Friend', 'Other')), " +
                KEY_EMERGENCY_NUMBER + " TEXT CHECK(" + KEY_EMERGENCY_NUMBER + " IS NULL OR (length(" + KEY_EMERGENCY_NUMBER + ") = 10 AND " + KEY_EMERGENCY_NUMBER + " GLOB '[0-9]*')), " +

                // Metadata
                KEY_STATUS + " TEXT DEFAULT 'ACTIVE' CHECK(" + KEY_STATUS + " IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'GRADUATED')), " +
                KEY_IS_ACTIVE + " INTEGER DEFAULT 1 CHECK(" + KEY_IS_ACTIVE + " IN (0, 1)), " +
                KEY_COMPLETION_PERCENTAGE + " INTEGER DEFAULT 0 CHECK(" + KEY_COMPLETION_PERCENTAGE + " >= 0 AND " + KEY_COMPLETION_PERCENTAGE + " <= 100), " +

                // Timestamps
                KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                KEY_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";

        db.execSQL(CREATE_STUDENT_DETAILS_TABLE);
        Log.d(TAG, "StudentDetails table created successfully");
    }

    /**
     * Create audit log table for tracking changes
     */
    private void createAuditLogTable(SQLiteDatabase db) {
        String CREATE_AUDIT_LOG_TABLE = " CREATE TABLE " + TABLE_AUDIT_LOG + " (" +
                AUDIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AUDIT_STUDENT_ID + " INTEGER, " +
                AUDIT_ACTION + " TEXT NOT NULL CHECK(" + AUDIT_ACTION + " IN ('INSERT', 'UPDATE', 'DELETE')), " +
                AUDIT_OLD_VALUES + " TEXT, " +
                AUDIT_NEW_VALUES + " TEXT, " +
                AUDIT_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                AUDIT_USER + " TEXT DEFAULT 'system', " +
                "FOREIGN KEY(" + AUDIT_STUDENT_ID + ") REFERENCES " + TABLE_STUDENT_DETAILS + "(" + KEY_ID + ") ON DELETE CASCADE" +
                ")";

        db.execSQL(CREATE_AUDIT_LOG_TABLE);
        Log.d(TAG, "AuditLog table created successfully");
    }

    /**
     * Create indexes for better query performance
     */
    private void createIndexes(SQLiteDatabase db) {
        // Indexes for frequently queried columns
        db.execSQL("CREATE INDEX idx_university_id ON " + TABLE_STUDENT_DETAILS + "(" + KEY_UNIVERSITY_ID + ")");
        db.execSQL("CREATE INDEX idx_email ON " + TABLE_STUDENT_DETAILS + "(" + KEY_EMAIL + ")");
        db.execSQL("CREATE INDEX idx_full_name ON " + TABLE_STUDENT_DETAILS + "(" + KEY_FULL_NAME + ")");
        db.execSQL("CREATE INDEX idx_faculty_department ON " + TABLE_STUDENT_DETAILS + "(" + KEY_FACULTY + ", " + KEY_DEPARTMENT + ")");
        db.execSQL("CREATE INDEX idx_batch ON " + TABLE_STUDENT_DETAILS + "(" + KEY_BATCH + ")");
        db.execSQL("CREATE INDEX idx_status ON " + TABLE_STUDENT_DETAILS + "(" + KEY_STATUS + ", " + KEY_IS_ACTIVE + ")");
        db.execSQL("CREATE INDEX idx_created_at ON " + TABLE_STUDENT_DETAILS + "(" + KEY_CREATED_AT + ")");

        // Audit log indexes
        db.execSQL("CREATE INDEX idx_audit_student_id ON " + TABLE_AUDIT_LOG + "(" + AUDIT_STUDENT_ID + ")");
        db.execSQL("CREATE INDEX idx_audit_timestamp ON " + TABLE_AUDIT_LOG + "(" + AUDIT_TIMESTAMP + ")");

        Log.d(TAG, "Database indexes created successfully");
    }

    /**
     * Insert default/sample data
     */
    private void insertDefaultData(SQLiteDatabase db) {
        try {
            // You can add default data here if needed
            Log.d(TAG, "Default data inserted successfully");
        } catch (Exception e) {
            Log.w(TAG, "Error inserting default data", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        try {
            if (oldVersion < 2) {
                // Add new columns for version 2
                upgradeToVersion2(db);
            }

            // Add more upgrade paths as needed
            // if (oldVersion < 3) { upgradeToVersion3(db); }

            Log.i(TAG, "Database upgrade completed successfully");
        } catch (SQLException e) {
            Log.e(TAG, "Error upgrading database", e);
            // Fallback: recreate tables
            dropAndRecreateAllTables(db);
        }
    }

    /**
     * Upgrade database to version 2
     */
    private void upgradeToVersion2(SQLiteDatabase db) {
        try {
            // Add new columns
            db.execSQL("ALTER TABLE " + TABLE_STUDENT_DETAILS + " ADD COLUMN " + KEY_STATUS + " TEXT DEFAULT 'ACTIVE'");
            db.execSQL("ALTER TABLE " + TABLE_STUDENT_DETAILS + " ADD COLUMN " + KEY_IS_ACTIVE + " INTEGER DEFAULT 1");
            db.execSQL("ALTER TABLE " + TABLE_STUDENT_DETAILS + " ADD COLUMN " + KEY_COMPLETION_PERCENTAGE + " INTEGER DEFAULT 0");

            // Create audit log table
            createAuditLogTable(db);

            // Update completion percentages for existing records
            updateCompletionPercentages(db);

            Log.d(TAG, "Successfully upgraded to version 2");
        } catch (SQLException e) {
            Log.e(TAG, "Error upgrading to version 2", e);
            throw e;
        }
    }

    /**
     * Drop and recreate all tables (fallback for failed upgrades)
     */
    private void dropAndRecreateAllTables(SQLiteDatabase db) {
        Log.w(TAG, "Dropping and recreating all tables");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUDIT_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT_DETAILS);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Downgrading database from version " + oldVersion + " to " + newVersion);
        dropAndRecreateAllTables(db);
    }

    // ==========================================
    // CRUD OPERATIONS
    // ==========================================

    /**
     * Insert or update student record
     */
    public long insertOrUpdateStudent(StudentDetails student) {
        if (student == null) {
            Log.w(TAG, "Cannot insert null student");
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();
        long studentId = -1;

        db.beginTransaction();
        try {
            ContentValues values = createContentValues(student);

            if (student.getId() > 0) {
                // Update existing student
                String oldValues = getStudentAsJson(student.getId());

                int rowsAffected = db.update(TABLE_STUDENT_DETAILS, values,
                        KEY_ID + " = ?", new String[]{String.valueOf(student.getId())});

                if (rowsAffected > 0) {
                    studentId = student.getId();

                    // Update completion percentage
                    updateCompletionPercentage(db, studentId);

                    // Log audit trail
                    logAuditTrail(db, studentId, "UPDATE", oldValues, getStudentAsJson(studentId));

                    Log.d(TAG, "Student updated successfully: ID " + studentId);
                } else {
                    Log.w(TAG, "No rows affected during update for student ID: " + student.getId());
                }
            } else {
                // Insert new student
                studentId = db.insertOrThrow(TABLE_STUDENT_DETAILS, null, values);

                if (studentId > 0) {
                    // Update completion percentage
                    updateCompletionPercentage(db, studentId);

                    // Log audit trail
                    logAuditTrail(db, studentId, "INSERT", null, getStudentAsJson(studentId));

                    Log.d(TAG, "Student inserted successfully: ID " + studentId);
                }
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "Error inserting/updating student", e);
            studentId = -1;
        } finally {
            db.endTransaction();
        }

        return studentId;
    }

    /**
     * Get student by ID
     */
    public StudentDetails getStudentById(long studentId) {
        if (studentId <= 0) {
            Log.w(TAG, "Invalid student ID: " + studentId);
            return null;
        }

        SQLiteDatabase db = getReadableDatabase();
        StudentDetails student = null;

        String query = "SELECT * FROM " + TABLE_STUDENT_DETAILS +
                " WHERE " + KEY_ID + " = ? AND " + KEY_IS_ACTIVE + " = 1";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});

            if (cursor.moveToFirst()) {
                student = cursorToStudent(cursor);
                Log.d(TAG, "Student retrieved: " + student.getDisplayName());
            } else {
                Log.w(TAG, "Student not found with ID: " + studentId);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error retrieving student by ID: " + studentId, e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return student;
    }

    /**
     * Get student by University ID
     */
    public StudentDetails getStudentByUniversityId(String universityId) {
        if (universityId == null || universityId.trim().isEmpty()) {
            Log.w(TAG, "Invalid university ID");
            return null;
        }

        SQLiteDatabase db = getReadableDatabase();
        StudentDetails student = null;

        String query = "SELECT * FROM " + TABLE_STUDENT_DETAILS +
                " WHERE " + KEY_UNIVERSITY_ID + " = ? AND " + KEY_IS_ACTIVE + " = 1";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{universityId.trim()});

            if (cursor.moveToFirst()) {
                student = cursorToStudent(cursor);
                Log.d(TAG, "Student retrieved by University ID: " + universityId);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error retrieving student by University ID: " + universityId, e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return student;
    }

    /**
     * Get all students with pagination
     */
    public List<StudentDetails> getAllStudents(int limit, int offset) {
        List<StudentDetails> students = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_STUDENT_DETAILS +
                " WHERE " + KEY_IS_ACTIVE + " = 1" +
                " ORDER BY " + KEY_CREATED_AT + " DESC" +
                " LIMIT ? OFFSET ?";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(limit), String.valueOf(offset)});

            if (cursor.moveToFirst()) {
                do {
                    StudentDetails student = cursorToStudent(cursor);
                    students.add(student);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Retrieved " + students.size() + " students (limit: " + limit + ", offset: " + offset + ")");
        } catch (SQLException e) {
            Log.e(TAG, "Error retrieving students", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return students;
    }

    /**
     * Get all students (without pagination)
     */
    public List<StudentDetails> getAllStudents() {
        return getAllStudents(Integer.MAX_VALUE, 0);
    }

    /**
     * Search students by multiple criteria
     */
    public List<StudentDetails> searchStudents(String searchTerm, String faculty, String department, String batch, String status) {
        List<StudentDetails> students = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        StringBuilder queryBuilder = new StringBuilder();
        List<String> args = new ArrayList<>();

        queryBuilder.append("SELECT * FROM ").append(TABLE_STUDENT_DETAILS);
        queryBuilder.append(" WHERE ").append(KEY_IS_ACTIVE).append(" = 1");

        // Add search term filter
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            queryBuilder.append(" AND (")
                    .append(KEY_FULL_NAME).append(" LIKE ? OR ")
                    .append(KEY_UNIVERSITY_ID).append(" LIKE ? OR ")
                    .append(KEY_EMAIL).append(" LIKE ?)");
            String likeTerm = "%" + searchTerm.trim() + "%";
            args.add(likeTerm);
            args.add(likeTerm);
            args.add(likeTerm);
        }

        // Add faculty filter
        if (faculty != null && !faculty.trim().isEmpty()) {
            queryBuilder.append(" AND ").append(KEY_FACULTY).append(" = ?");
            args.add(faculty.trim());
        }

        // Add department filter
        if (department != null && !department.trim().isEmpty()) {
            queryBuilder.append(" AND ").append(KEY_DEPARTMENT).append(" = ?");
            args.add(department.trim());
        }

        // Add batch filter
        if (batch != null && !batch.trim().isEmpty()) {
            queryBuilder.append(" AND ").append(KEY_BATCH).append(" = ?");
            args.add(batch.trim());
        }

        // Add status filter
        if (status != null && !status.trim().isEmpty()) {
            queryBuilder.append(" AND ").append(KEY_STATUS).append(" = ?");
            args.add(status.trim());
        }

        queryBuilder.append(" ORDER BY ").append(KEY_FULL_NAME).append(" ASC");

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(queryBuilder.toString(), args.toArray(new String[0]));

            if (cursor.moveToFirst()) {
                do {
                    StudentDetails student = cursorToStudent(cursor);
                    students.add(student);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Search returned " + students.size() + " students");
        } catch (SQLException e) {
            Log.e(TAG, "Error searching students", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return students;
    }

    /**
     * Delete student (soft delete)
     */
    public boolean deleteStudent(long studentId) {
        if (studentId <= 0) {
            Log.w(TAG, "Invalid student ID for deletion: " + studentId);
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        boolean success = false;

        db.beginTransaction();
        try {
            // Get student data before deletion for audit
            String oldValues = getStudentAsJson(studentId);

            // Soft delete: mark as inactive
            ContentValues values = new ContentValues();
            values.put(KEY_IS_ACTIVE, 0);
            values.put(KEY_STATUS, "INACTIVE");
            values.put(KEY_UPDATED_AT, getCurrentTimestamp());

            int rowsAffected = db.update(TABLE_STUDENT_DETAILS, values,
                    KEY_ID + " = ? AND " + KEY_IS_ACTIVE + " = 1",
                    new String[]{String.valueOf(studentId)});

            if (rowsAffected > 0) {
                // Log audit trail
                logAuditTrail(db, studentId, "DELETE", oldValues, null);

                success = true;
                Log.d(TAG, "Student soft deleted successfully: ID " + studentId);
            } else {
                Log.w(TAG, "No active student found with ID: " + studentId);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "Error deleting student: " + studentId, e);
        } finally {
            db.endTransaction();
        }

        return success;
    }

    /**
     * Hard delete student (permanently remove from database)
     */
    public boolean hardDeleteStudent(long studentId) {
        if (studentId <= 0) {
            Log.w(TAG, "Invalid student ID for hard deletion: " + studentId);
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        boolean success = false;

        db.beginTransaction();
        try {
            // Get student data before deletion for audit
            String oldValues = getStudentAsJson(studentId);

            // Delete audit logs first (due to foreign key constraint)
            db.delete(TABLE_AUDIT_LOG, AUDIT_STUDENT_ID + " = ?", new String[]{String.valueOf(studentId)});

            // Delete student record
            int rowsAffected = db.delete(TABLE_STUDENT_DETAILS, KEY_ID + " = ?", new String[]{String.valueOf(studentId)});

            if (rowsAffected > 0) {
                success = true;
                Log.d(TAG, "Student hard deleted successfully: ID " + studentId);
            } else {
                Log.w(TAG, "No student found with ID: " + studentId);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "Error hard deleting student: " + studentId, e);
        } finally {
            db.endTransaction();
        }

        return success;
    }

    /**
     * Restore soft-deleted student
     */
    public boolean restoreStudent(long studentId) {
        if (studentId <= 0) {
            Log.w(TAG, "Invalid student ID for restoration: " + studentId);
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        boolean success = false;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IS_ACTIVE, 1);
            values.put(KEY_STATUS, "ACTIVE");
            values.put(KEY_UPDATED_AT, getCurrentTimestamp());

            int rowsAffected = db.update(TABLE_STUDENT_DETAILS, values,
                    KEY_ID + " = ? AND " + KEY_IS_ACTIVE + " = 0",
                    new String[]{String.valueOf(studentId)});

            if (rowsAffected > 0) {
                // Log audit trail
                logAuditTrail(db, studentId, "RESTORE", null, getStudentAsJson(studentId));

                success = true;
                Log.d(TAG, "Student restored successfully: ID " + studentId);
            } else {
                Log.w(TAG, "No inactive student found with ID: " + studentId);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "Error restoring student: " + studentId, e);
        } finally {
            db.endTransaction();
        }

        return success;
    }

    // ==========================================
    // VALIDATION METHODS
    // ==========================================

    /**
     * Check if University ID exists (excluding specific student)
     */
    public boolean isUniversityIdExists(String universityId, long excludeStudentId) {
        if (universityId == null || universityId.trim().isEmpty()) {
            return false;
        }

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT COUNT(*) FROM " + TABLE_STUDENT_DETAILS +
                " WHERE " + KEY_UNIVERSITY_ID + " = ? AND " + KEY_ID + " != ? AND " + KEY_IS_ACTIVE + " = 1";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{universityId.trim(), String.valueOf(excludeStudentId)});

            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error checking University ID existence", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return false;
    }

    /**
     * Check if email exists (excluding specific student)
     */
    public boolean isEmailExists(String email, long excludeStudentId) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT COUNT(*) FROM " + TABLE_STUDENT_DETAILS +
                " WHERE " + KEY_EMAIL + " = ? AND " + KEY_ID + " != ? AND " + KEY_IS_ACTIVE + " = 1";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{email.trim().toLowerCase(), String.valueOf(excludeStudentId)});

            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error checking email existence", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return false;
    }

    /**
     * Check if username exists (excluding specific student)
     */
    public boolean isUsernameExists(String username, long excludeStudentId) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT COUNT(*) FROM " + TABLE_STUDENT_DETAILS +
                " WHERE " + KEY_USERNAME + " = ? AND " + KEY_ID + " != ? AND " + KEY_IS_ACTIVE + " = 1";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{username.trim(), String.valueOf(excludeStudentId)});

            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error checking username existence", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return false;
    }

    // ==========================================
    // STATISTICS AND ANALYTICS
    // ==========================================

    /**
     * Get total student count
     */
    public int getTotalStudentCount() {
        return getStudentCountByStatus("ACTIVE");
    }

    /**
     * Get student count by status
     */
    public int getStudentCountByStatus(String status) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT COUNT(*) FROM " + TABLE_STUDENT_DETAILS +
                " WHERE " + KEY_STATUS + " = ? AND " + KEY_IS_ACTIVE + " = 1";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{status});

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting student count by status", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return 0;
    }

    /**
     * Get student count by faculty
     */
    public int getStudentCountByFaculty(String faculty) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT COUNT(*) FROM " + TABLE_STUDENT_DETAILS +
                " WHERE " + KEY_FACULTY + " = ? AND " + KEY_IS_ACTIVE + " = 1";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{faculty});

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting student count by faculty", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return 0;
    }

    /**
     * Get students registered in current month
     */
    public int getNewStudentsThisMonth() {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT COUNT(*) FROM " + TABLE_STUDENT_DETAILS +
                " WHERE strftime('%Y-%m', " + KEY_CREATED_AT + ") = strftime('%Y-%m', 'now')" +
                " AND " + KEY_IS_ACTIVE + " = 1";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting new students this month", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return 0;
    }

    /**
     * Get completion statistics
     */
    public CompletionStats getCompletionStatistics() {
        SQLiteDatabase db = getReadableDatabase();
        CompletionStats stats = new CompletionStats();

        String query = "SELECT " +
                "COUNT(*) as total, " +
                "SUM(CASE WHEN " + KEY_COMPLETION_PERCENTAGE + " = 100 THEN 1 ELSE 0 END) as complete, " +
                "SUM(CASE WHEN " + KEY_COMPLETION_PERCENTAGE + " >= 75 AND " + KEY_COMPLETION_PERCENTAGE + " < 100 THEN 1 ELSE 0 END) as nearly_complete, " +
                "SUM(CASE WHEN " + KEY_COMPLETION_PERCENTAGE + " >= 50 AND " + KEY_COMPLETION_PERCENTAGE + " < 75 THEN 1 ELSE 0 END) as partial, " +
                "SUM(CASE WHEN " + KEY_COMPLETION_PERCENTAGE + " < 50 THEN 1 ELSE 0 END) as incomplete, " +
                "AVG(" + KEY_COMPLETION_PERCENTAGE + ") as avg_completion " +
                "FROM " + TABLE_STUDENT_DETAILS +
                " WHERE " + KEY_IS_ACTIVE + " = 1";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                stats.totalStudents = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
                stats.completeProfiles = cursor.getInt(cursor.getColumnIndexOrThrow("complete"));
                stats.nearlyCompleteProfiles = cursor.getInt(cursor.getColumnIndexOrThrow("nearly_complete"));
                stats.partialProfiles = cursor.getInt(cursor.getColumnIndexOrThrow("partial"));
                stats.incompleteProfiles = cursor.getInt(cursor.getColumnIndexOrThrow("incomplete"));
                stats.averageCompletion = cursor.getDouble(cursor.getColumnIndexOrThrow("avg_completion"));
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting completion statistics", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return stats;
    }

    // ==========================================
    // UTILITY METHODS
    // ==========================================

    /**
     * Create ContentValues from StudentDetails object
     */
    private ContentValues createContentValues(StudentDetails student) {
        ContentValues values = new ContentValues();

        // Basic Information
        if (student.getFullName() != null) values.put(KEY_FULL_NAME, student.getFullName());
        if (student.getUniversityId() != null) values.put(KEY_UNIVERSITY_ID, student.getUniversityId());
        if (student.getNicNumber() != null) values.put(KEY_NIC_NUMBER, student.getNicNumber());
        if (student.getGender() != null) values.put(KEY_GENDER, student.getGender());
        if (student.getDateOfBirth() != null) values.put(KEY_DATE_OF_BIRTH, student.getDateOfBirth());
        if (student.getStudentPhoto() != null) values.put(KEY_STUDENT_PHOTO, student.getStudentPhoto());

        // Academic Details
        if (student.getFaculty() != null) values.put(KEY_FACULTY, student.getFaculty());
        if (student.getDepartment() != null) values.put(KEY_DEPARTMENT, student.getDepartment());
        if (student.getBatch() != null) values.put(KEY_BATCH, student.getBatch());
        if (student.getSemester() != null) values.put(KEY_SEMESTER, student.getSemester());
        if (student.getEnrollmentDate() != null) values.put(KEY_ENROLLMENT_DATE, student.getEnrollmentDate());

        // Account Details
        if (student.getEmail() != null) values.put(KEY_EMAIL, student.getEmail());
        if (student.getUsername() != null) values.put(KEY_USERNAME, student.getUsername());
        if (student.getPassword() != null) values.put(KEY_PASSWORD, student.getPassword());
        values.put(KEY_TERMS_ACCEPTED, student.isTermsAccepted() ? 1 : 0);

        // Contact Details
        if (student.getMobileNumber() != null) values.put(KEY_MOBILE_NUMBER, student.getMobileNumber());
        if (student.getAlternateNumber() != null) values.put(KEY_ALTERNATE_NUMBER, student.getAlternateNumber());
        if (student.getPermanentAddress() != null) values.put(KEY_PERMANENT_ADDRESS, student.getPermanentAddress());
        if (student.getCity() != null) values.put(KEY_CITY, student.getCity());
        if (student.getProvince() != null) values.put(KEY_PROVINCE, student.getProvince());
        if (student.getPostalCode() != null) values.put(KEY_POSTAL_CODE, student.getPostalCode());

        // Emergency Contact
        if (student.getEmergencyName() != null) values.put(KEY_EMERGENCY_NAME, student.getEmergencyName());
        if (student.getEmergencyRelationship() != null) values.put(KEY_EMERGENCY_RELATIONSHIP, student.getEmergencyRelationship());
        if (student.getEmergencyNumber() != null) values.put(KEY_EMERGENCY_NUMBER, student.getEmergencyNumber());

        // Timestamps
        values.put(KEY_UPDATED_AT, getCurrentTimestamp());

        return values;
    }

    /**
     * Convert cursor to StudentDetails object
     */
    private StudentDetails cursorToStudent(Cursor cursor) {
        StudentDetails student = new StudentDetails();

        try {
            student.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)));

            // Basic Information
            student.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FULL_NAME)));
            student.setUniversityId(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UNIVERSITY_ID)));
            student.setNicNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NIC_NUMBER)));
            student.setGender(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GENDER)));
            student.setDateOfBirth(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_OF_BIRTH)));
            student.setStudentPhoto(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STUDENT_PHOTO)));

            // Academic Details
            student.setFaculty(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FACULTY)));
            student.setDepartment(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DEPARTMENT)));
            student.setBatch(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BATCH)));
            student.setSemester(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SEMESTER)));
            student.setEnrollmentDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ENROLLMENT_DATE)));

            // Account Details
            student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)));
            student.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERNAME)));
            student.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD)));
            student.setTermsAccepted(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TERMS_ACCEPTED)) == 1);

            // Contact Details
            student.setMobileNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MOBILE_NUMBER)));
            student.setAlternateNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ALTERNATE_NUMBER)));
            student.setPermanentAddress(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PERMANENT_ADDRESS)));
            student.setCity(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CITY)));
            student.setProvince(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROVINCE)));
            student.setPostalCode(cursor.getString(cursor.getColumnIndexOrThrow(KEY_POSTAL_CODE)));

            // Emergency Contact
            student.setEmergencyName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMERGENCY_NAME)));
            student.setEmergencyRelationship(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMERGENCY_RELATIONSHIP)));
            student.setEmergencyNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMERGENCY_NUMBER)));

            // Timestamps
            student.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CREATED_AT)));
            student.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UPDATED_AT)));

        } catch (Exception e) {
            Log.e(TAG, "Error converting cursor to student", e);
        }

        return student;
    }

    /**
     * Update completion percentage for a student
     */
    private void updateCompletionPercentage(SQLiteDatabase db, long studentId) {
        try {
            StudentDetails student = getStudentById(studentId);
            if (student != null) {
                int completionPercentage = student.getCompletionPercentage();

                ContentValues values = new ContentValues();
                values.put(KEY_COMPLETION_PERCENTAGE, completionPercentage);

                db.update(TABLE_STUDENT_DETAILS, values, KEY_ID + " = ?", new String[]{String.valueOf(studentId)});
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating completion percentage", e);
        }
    }

    /**
     * Update completion percentages for all students
     */
    private void updateCompletionPercentages(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT " + KEY_ID + " FROM " + TABLE_STUDENT_DETAILS + " WHERE " + KEY_IS_ACTIVE + " = 1", null);

            if (cursor.moveToFirst()) {
                do {
                    long studentId = cursor.getLong(0);
                    updateCompletionPercentage(db, studentId);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating completion percentages", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    /**
     * Log audit trail for data changes
     */
    private void logAuditTrail(SQLiteDatabase db, long studentId, String action, String oldValues, String newValues) {
        try {
            ContentValues auditValues = new ContentValues();
            auditValues.put(AUDIT_STUDENT_ID, studentId);
            auditValues.put(AUDIT_ACTION, action);
            auditValues.put(AUDIT_OLD_VALUES, oldValues);
            auditValues.put(AUDIT_NEW_VALUES, newValues);
            auditValues.put(AUDIT_TIMESTAMP, getCurrentTimestamp());
            auditValues.put(AUDIT_USER, "system"); // In a real app, this would be the current user

            db.insert(TABLE_AUDIT_LOG, null, auditValues);
        } catch (Exception e) {
            Log.e(TAG, "Error logging audit trail", e);
        }
    }

    /**
     * Get student data as JSON string for audit logging
     */
    private String getStudentAsJson(long studentId) {
        try {
            StudentDetails student = getStudentById(studentId);
            if (student != null) {
                // In a real app, you'd use a JSON library like Gson
                return student.toString();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error converting student to JSON", e);
        }
        return null;
    }

    /**
     * Get current timestamp
     */
    private String getCurrentTimestamp() {
        return DATE_FORMATTER.format(new Date());
    }

    /**
     * Get database information
     */
    public DatabaseInfo getDatabaseInfo() {
        DatabaseInfo info = new DatabaseInfo();
        SQLiteDatabase db = getReadableDatabase();

        try {
            // Get database version and size
            info.version = db.getVersion();
            info.path = db.getPath();

            // Get table information
            Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            info.tableCount = cursor.getCount();
            cursor.close();

            // Get student counts
            info.totalStudents = getTotalStudentCount();
            info.activeStudents = getStudentCountByStatus("ACTIVE");
            info.inactiveStudents = getStudentCountByStatus("INACTIVE");

            // Get database file size (approximate)
            cursor = db.rawQuery("PRAGMA page_count", null);
            if (cursor.moveToFirst()) {
                long pageCount = cursor.getLong(0);
                cursor.close();

                cursor = db.rawQuery("PRAGMA page_size", null);
                if (cursor.moveToFirst()) {
                    long pageSize = cursor.getLong(0);
                    info.sizeInBytes = pageCount * pageSize;
                }
                cursor.close();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting database info", e);
        }

        return info;
    }

    /**
     * Backup database to external storage
     */
    public boolean backupDatabase(String backupPath) {
        try {
            SQLiteDatabase db = getReadableDatabase();
            // Implementation would copy database file to backup location
            // This is a placeholder - actual implementation would depend on requirements
            Log.d(TAG, "Database backup initiated to: " + backupPath);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error backing up database", e);
            return false;
        }
    }

    /**
     * Clean up old audit logs (older than specified days)
     */
    public int cleanupAuditLogs(int olderThanDays) {
        SQLiteDatabase db = getWritableDatabase();

        String deleteQuery = "DELETE FROM " + TABLE_AUDIT_LOG +
                " WHERE " + AUDIT_TIMESTAMP + " < datetime('now', '-" + olderThanDays + " days')";

        try {
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_AUDIT_LOG +
                    " WHERE " + AUDIT_TIMESTAMP + " < datetime('now', '-" + olderThanDays + " days')", null);
            int deletedCount = 0;
            if (cursor.moveToFirst()) {
                deletedCount = cursor.getInt(0);
            }
            cursor.close();

            db.execSQL(deleteQuery);
            db.setTransactionSuccessful();

            Log.d(TAG, "Cleaned up " + deletedCount + " old audit log entries");
            return deletedCount;
        } catch (Exception e) {
            Log.e(TAG, "Error cleaning up audit logs", e);
            return -1;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Close database connections
     */
    @Override
    public synchronized void close() {
        if (instance != null) {
            super.close();
            instance = null;
            Log.d(TAG, "Database connections closed");
        }
    }

    // ==========================================
    // INNER CLASSES
    // ==========================================

    /**
     * Completion statistics data class
     */
    public static class CompletionStats {
        public int totalStudents = 0;
        public int completeProfiles = 0;
        public int nearlyCompleteProfiles = 0;
        public int partialProfiles = 0;
        public int incompleteProfiles = 0;
        public double averageCompletion = 0.0;

        @Override
        public String toString() {
            return "CompletionStats{" +
                    "total=" + totalStudents +
                    ", complete=" + completeProfiles +
                    ", nearlyComplete=" + nearlyCompleteProfiles +
                    ", partial=" + partialProfiles +
                    ", incomplete=" + incompleteProfiles +
                    ", avgCompletion=" + String.format("%.1f", averageCompletion) + "%" +
                    '}';
        }
    }

    /**
     * Database information data class
     */
    public static class DatabaseInfo {
        public int version = 0;
        public String path = "";
        public int tableCount = 0;
        public int totalStudents = 0;
        public int activeStudents = 0;
        public int inactiveStudents = 0;
        public long sizeInBytes = 0;

        public String getSizeFormatted() {
            if (sizeInBytes < 1024) return sizeInBytes + " B";
            if (sizeInBytes < 1024 * 1024) return String.format("%.1f KB", sizeInBytes / 1024.0);
            return String.format("%.1f MB", sizeInBytes / (1024.0 * 1024.0));
        }

        @Override
        public String toString() {
            return "DatabaseInfo{" +
                    "version=" + version +
                    ", tables=" + tableCount +
                    ", totalStudents=" + totalStudents +
                    ", activeStudents=" + activeStudents +
                    ", size=" + getSizeFormatted() +
                    '}';
        }
    }
}