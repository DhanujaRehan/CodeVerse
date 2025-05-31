package com.example.codeverse.Admin.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.codeverse.Admin.Models.Staff;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StaffDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "StaffDetails.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_STAFF = "StaffDetails";

    // Staff Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CONTACT_NUMBER = "contact_number";
    private static final String KEY_NIC_NUMBER = "nic_number";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_DATE_OF_BIRTH = "date_of_birth";
    private static final String KEY_PHOTO_URI = "photo_uri";

    // Professional Information Columns
    private static final String KEY_POSITION = "position";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_TEACHING_SUBJECT = "teaching_subject";
    private static final String KEY_PROGRAM_COORDINATING = "program_coordinating";

    // Educational Qualifications Columns
    private static final String KEY_HIGHEST_QUALIFICATION = "highest_qualification";
    private static final String KEY_FIELD_OF_STUDY = "field_of_study";
    private static final String KEY_UNIVERSITY = "university";
    private static final String KEY_GRADUATION_YEAR = "graduation_year";
    private static final String KEY_EXPERIENCE_YEARS = "experience_years";

    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_UPDATED_AT = "updated_at";

    private static final String TAG = "StaffDatabaseHelper";

    public StaffDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STAFF_TABLE = "CREATE TABLE " + TABLE_STAFF +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_FULL_NAME + " TEXT NOT NULL," +
                KEY_EMAIL + " TEXT UNIQUE," +
                KEY_CONTACT_NUMBER + " TEXT," +
                KEY_NIC_NUMBER + " TEXT UNIQUE NOT NULL," +
                KEY_GENDER + " TEXT," +
                KEY_DATE_OF_BIRTH + " TEXT," +
                KEY_PHOTO_URI + " TEXT," +
                KEY_POSITION + " TEXT," +
                KEY_DEPARTMENT + " TEXT," +
                KEY_TEACHING_SUBJECT + " TEXT," +
                KEY_PROGRAM_COORDINATING + " TEXT," +
                KEY_HIGHEST_QUALIFICATION + " TEXT," +
                KEY_FIELD_OF_STUDY + " TEXT," +
                KEY_UNIVERSITY + " TEXT," +
                KEY_GRADUATION_YEAR + " TEXT," +
                KEY_EXPERIENCE_YEARS + " TEXT," +
                KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                KEY_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";

        db.execSQL(CREATE_STAFF_TABLE);
        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAFF);
            onCreate(db);
        }
    }

    // Insert a staff member into the database
    public long insertStaff(Staff staff) {
        SQLiteDatabase db = getWritableDatabase();
        long staffId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_FULL_NAME, staff.getFullName());
            values.put(KEY_EMAIL, staff.getEmail());
            values.put(KEY_CONTACT_NUMBER, staff.getContactNumber());
            values.put(KEY_NIC_NUMBER, staff.getNicNumber());
            values.put(KEY_GENDER, staff.getGender());
            values.put(KEY_DATE_OF_BIRTH, staff.getDateOfBirth());
            values.put(KEY_PHOTO_URI, staff.getPhotoUri());

            // Professional Information
            values.put(KEY_POSITION, staff.getPosition());
            values.put(KEY_DEPARTMENT, staff.getDepartment());
            values.put(KEY_TEACHING_SUBJECT, staff.getTeachingSubject());
            values.put(KEY_PROGRAM_COORDINATING, staff.getProgramCoordinating());

            // Educational Qualifications
            values.put(KEY_HIGHEST_QUALIFICATION, staff.getHighestQualification());
            values.put(KEY_FIELD_OF_STUDY, staff.getFieldOfStudy());
            values.put(KEY_UNIVERSITY, staff.getUniversity());
            values.put(KEY_GRADUATION_YEAR, staff.getGraduationYear());
            values.put(KEY_EXPERIENCE_YEARS, staff.getExperienceYears());

            // Get current timestamp
            String currentTimestamp = getCurrentTimestamp();
            values.put(KEY_CREATED_AT, currentTimestamp);
            values.put(KEY_UPDATED_AT, currentTimestamp);

            staffId = db.insertOrThrow(TABLE_STAFF, null, values);
            db.setTransactionSuccessful();

            Log.d(TAG, "Staff inserted with ID: " + staffId);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add staff to database: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        return staffId;
    }

    // Get a staff member by ID
    public Staff getStaffById(long staffId) {
        SQLiteDatabase db = getReadableDatabase();
        Staff staff = null;

        String STAFF_SELECT_QUERY =
                "SELECT * FROM " + TABLE_STAFF +
                        " WHERE " + KEY_ID + " = ?";

        Cursor cursor = db.rawQuery(STAFF_SELECT_QUERY, new String[]{String.valueOf(staffId)});
        try {
            if (cursor.moveToFirst()) {
                staff = getStaffFromCursor(cursor);
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get staff from database: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return staff;
    }

    // Get all staff members
    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String STAFF_SELECT_QUERY =
                "SELECT * FROM " + TABLE_STAFF +
                        " ORDER BY " + KEY_CREATED_AT + " DESC";

        Cursor cursor = db.rawQuery(STAFF_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Staff staff = getStaffFromCursor(cursor);
                    staffList.add(staff);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get staff from database: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return staffList;
    }

    // Update a staff member
    public int updateStaff(Staff staff) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = 0;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_FULL_NAME, staff.getFullName());
            values.put(KEY_EMAIL, staff.getEmail());
            values.put(KEY_CONTACT_NUMBER, staff.getContactNumber());
            values.put(KEY_NIC_NUMBER, staff.getNicNumber());
            values.put(KEY_GENDER, staff.getGender());
            values.put(KEY_DATE_OF_BIRTH, staff.getDateOfBirth());
            values.put(KEY_PHOTO_URI, staff.getPhotoUri());

            // Professional Information
            values.put(KEY_POSITION, staff.getPosition());
            values.put(KEY_DEPARTMENT, staff.getDepartment());
            values.put(KEY_TEACHING_SUBJECT, staff.getTeachingSubject());
            values.put(KEY_PROGRAM_COORDINATING, staff.getProgramCoordinating());

            // Educational Qualifications
            values.put(KEY_HIGHEST_QUALIFICATION, staff.getHighestQualification());
            values.put(KEY_FIELD_OF_STUDY, staff.getFieldOfStudy());
            values.put(KEY_UNIVERSITY, staff.getUniversity());
            values.put(KEY_GRADUATION_YEAR, staff.getGraduationYear());
            values.put(KEY_EXPERIENCE_YEARS, staff.getExperienceYears());

            values.put(KEY_UPDATED_AT, getCurrentTimestamp());

            rowsAffected = db.update(TABLE_STAFF, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(staff.getId())});

            db.setTransactionSuccessful();
            Log.d(TAG, "Staff updated. Rows affected: " + rowsAffected);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update staff: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        return rowsAffected;
    }

    // Update only personal details for a staff member
    public int updateStaffPersonalDetails(long staffId, String fullName, String email,
                                          String contactNumber, String nicNumber, String gender,
                                          String dateOfBirth, String photoUri) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = 0;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_FULL_NAME, fullName);
            values.put(KEY_EMAIL, email);
            values.put(KEY_CONTACT_NUMBER, contactNumber);
            values.put(KEY_NIC_NUMBER, nicNumber);
            values.put(KEY_GENDER, gender);
            values.put(KEY_DATE_OF_BIRTH, dateOfBirth);
            values.put(KEY_PHOTO_URI, photoUri);
            values.put(KEY_UPDATED_AT, getCurrentTimestamp());

            rowsAffected = db.update(TABLE_STAFF, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(staffId)});

            db.setTransactionSuccessful();
            Log.d(TAG, "Staff personal details updated. Rows affected: " + rowsAffected);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update staff personal details: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        return rowsAffected;
    }

    // Update only professional details for a staff member
    public int updateStaffProfessionalDetails(long staffId, String position, String department,
                                              String teachingSubject, String programCoordinating,
                                              String highestQualification, String fieldOfStudy,
                                              String university, String graduationYear, String experienceYears) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = 0;

        Log.d(TAG, "updateStaffProfessionalDetails called with:");
        Log.d(TAG, "  staffId: " + staffId);
        Log.d(TAG, "  position: " + position);
        Log.d(TAG, "  department: " + department);

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_POSITION, position);
            values.put(KEY_DEPARTMENT, department);
            values.put(KEY_TEACHING_SUBJECT, teachingSubject);
            values.put(KEY_PROGRAM_COORDINATING, programCoordinating);
            values.put(KEY_HIGHEST_QUALIFICATION, highestQualification);
            values.put(KEY_FIELD_OF_STUDY, fieldOfStudy);
            values.put(KEY_UNIVERSITY, university);
            values.put(KEY_GRADUATION_YEAR, graduationYear);
            values.put(KEY_EXPERIENCE_YEARS, experienceYears);
            values.put(KEY_UPDATED_AT, getCurrentTimestamp());

            rowsAffected = db.update(TABLE_STAFF, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(staffId)});

            if (rowsAffected > 0) {
                db.setTransactionSuccessful();
                Log.d(TAG, "Staff professional details updated successfully. Rows affected: " + rowsAffected);
            } else {
                Log.e(TAG, "No rows affected during update. Staff ID might not exist: " + staffId);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error while trying to update staff professional details: " + e.getMessage(), e);
        } finally {
            db.endTransaction();
        }

        return rowsAffected;
    }

    // Delete a staff member by ID
    public void deleteStaff(long staffId) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            db.delete(TABLE_STAFF, KEY_ID + " = ?", new String[]{String.valueOf(staffId)});
            db.setTransactionSuccessful();
            Log.d(TAG, "Staff deleted with ID: " + staffId);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete staff: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    // Delete a staff member with photo cleanup
    public void deleteStaffWithPhotoCleanup(Staff staff) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            // Delete the photo file first
            if (staff.getPhotoUri() != null && !staff.getPhotoUri().isEmpty()) {
                File photoFile = new File(staff.getPhotoUri());
                if (photoFile.exists()) {
                    boolean deleted = photoFile.delete();
                    Log.d(TAG, "Photo deleted: " + deleted + " - " + staff.getPhotoUri());
                }
            }

            // Delete from database
            int rowsDeleted = db.delete(TABLE_STAFF, KEY_ID + " = ?",
                    new String[]{String.valueOf(staff.getId())});
            db.setTransactionSuccessful();
            Log.d(TAG, "Staff deleted from database. Rows affected: " + rowsDeleted);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete staff with photo cleanup: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    // Check if Email already exists
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = getReadableDatabase();
        boolean exists = false;

        String query = "SELECT 1 FROM " + TABLE_STAFF +
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

    // Check if NIC number already exists
    public boolean isNicExists(String nicNumber) {
        SQLiteDatabase db = getReadableDatabase();
        boolean exists = false;

        String query = "SELECT 1 FROM " + TABLE_STAFF +
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

    // Search staff by name
    public List<Staff> searchStaff(String searchQuery) {
        List<Staff> staffList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String SEARCH_QUERY =
                "SELECT * FROM " + TABLE_STAFF +
                        " WHERE " + KEY_FULL_NAME + " LIKE ? OR " + KEY_EMAIL + " LIKE ?" +
                        " ORDER BY " + KEY_FULL_NAME + " ASC";

        String searchPattern = "%" + searchQuery + "%";
        Cursor cursor = db.rawQuery(SEARCH_QUERY, new String[]{searchPattern, searchPattern});

        try {
            if (cursor.moveToFirst()) {
                do {
                    Staff staff = getStaffFromCursor(cursor);
                    staffList.add(staff);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while searching staff: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return staffList;
    }

    // Get staff count
    public int getStaffCount() {
        SQLiteDatabase db = getReadableDatabase();
        int count = 0;

        String COUNT_QUERY = "SELECT COUNT(*) FROM " + TABLE_STAFF;
        Cursor cursor = db.rawQuery(COUNT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while getting staff count: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return count;
    }

    // Helper method to get Staff object from cursor
    private Staff getStaffFromCursor(Cursor cursor) {
        Staff staff = new Staff();

        staff.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)));
        staff.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FULL_NAME)));
        staff.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)));
        staff.setContactNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTACT_NUMBER)));
        staff.setNicNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NIC_NUMBER)));
        staff.setGender(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GENDER)));
        staff.setDateOfBirth(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_OF_BIRTH)));
        staff.setPhotoUri(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHOTO_URI)));

        // Professional Information
        staff.setPosition(cursor.getString(cursor.getColumnIndexOrThrow(KEY_POSITION)));
        staff.setDepartment(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DEPARTMENT)));
        staff.setTeachingSubject(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TEACHING_SUBJECT)));
        staff.setProgramCoordinating(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROGRAM_COORDINATING)));

        // Educational Qualifications
        staff.setHighestQualification(cursor.getString(cursor.getColumnIndexOrThrow(KEY_HIGHEST_QUALIFICATION)));
        staff.setFieldOfStudy(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FIELD_OF_STUDY)));
        staff.setUniversity(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UNIVERSITY)));
        staff.setGraduationYear(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GRADUATION_YEAR)));
        staff.setExperienceYears(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EXPERIENCE_YEARS)));

        staff.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CREATED_AT)));
        staff.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UPDATED_AT)));

        return staff;
    }

    // Helper method to get current timestamp
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Method to get staff photo as Bitmap
    public static Bitmap getStaffPhoto(String photoPath) {
        if (photoPath == null || photoPath.isEmpty()) {
            return null;
        }

        try {
            File imageFile = new File(photoPath);
            if (imageFile.exists()) {
                return BitmapFactory.decodeFile(photoPath);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading staff photo", e);
        }
        return null;
    }
}