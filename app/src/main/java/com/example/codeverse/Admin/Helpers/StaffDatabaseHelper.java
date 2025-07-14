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

    private static final String DATABASE_NAME = "StaffDetails.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_STAFF = "StaffDetails";

    private static final String KEY_ID = "id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CONTACT_NUMBER = "contact_number";
    private static final String KEY_NIC_NUMBER = "nic_number";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_DATE_OF_BIRTH = "date_of_birth";
    private static final String KEY_PHOTO_URI = "photo_uri";

    private static final String KEY_POSITION = "position";
    private static final String KEY_PROGRAMME = "programme";
    private static final String KEY_TEACHING_SUBJECT = "teaching_subject";
    private static final String KEY_PROGRAM_COORDINATING = "program_coordinating";
    private static final String KEY_PASSWORD = "password";

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
                KEY_EMAIL + " TEXT," +
                KEY_CONTACT_NUMBER + " TEXT," +
                KEY_NIC_NUMBER + " TEXT UNIQUE NOT NULL," +
                KEY_GENDER + " TEXT," +
                KEY_DATE_OF_BIRTH + " TEXT," +
                KEY_PHOTO_URI + " TEXT," +
                KEY_POSITION + " TEXT," +
                KEY_PROGRAMME + " TEXT," +
                KEY_TEACHING_SUBJECT + " TEXT," +
                KEY_PROGRAM_COORDINATING + " TEXT," +
                KEY_PASSWORD + " TEXT," +
                KEY_HIGHEST_QUALIFICATION + " TEXT," +
                KEY_FIELD_OF_STUDY + " TEXT," +
                KEY_UNIVERSITY + " TEXT," +
                KEY_GRADUATION_YEAR + " TEXT," +
                KEY_EXPERIENCE_YEARS + " TEXT," +
                KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                KEY_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";

        db.execSQL(CREATE_STAFF_TABLE);
        Log.d(TAG, "Database tables created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAFF);
            onCreate(db);
        }
    }

    public long insertStaff(Staff staff) {
        SQLiteDatabase db = this.getWritableDatabase();
        long staffId = -1;

        Log.d(TAG, "Attempting to insert staff: " + staff.getFullName());

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_FULL_NAME, staff.getFullName());

            String email = staff.getEmail();
            if (email != null && !email.trim().isEmpty()) {
                values.put(KEY_EMAIL, email.trim());
            } else {
                values.putNull(KEY_EMAIL);
            }

            values.put(KEY_CONTACT_NUMBER, staff.getContactNumber());
            values.put(KEY_NIC_NUMBER, staff.getNicNumber());
            values.put(KEY_GENDER, staff.getGender());
            values.put(KEY_DATE_OF_BIRTH, staff.getDateOfBirth());
            values.put(KEY_PHOTO_URI, staff.getPhotoUri());

            values.put(KEY_POSITION, staff.getPosition());
            values.put(KEY_PROGRAMME, staff.getDepartment());
            values.put(KEY_TEACHING_SUBJECT, staff.getTeachingSubject());
            values.put(KEY_PROGRAM_COORDINATING, staff.getProgramCoordinating());
            values.put(KEY_PASSWORD, staff.getPassword());

            values.put(KEY_HIGHEST_QUALIFICATION, staff.getHighestQualification());
            values.put(KEY_FIELD_OF_STUDY, staff.getFieldOfStudy());
            values.put(KEY_UNIVERSITY, staff.getUniversity());
            values.put(KEY_GRADUATION_YEAR, staff.getGraduationYear());
            values.put(KEY_EXPERIENCE_YEARS, staff.getExperienceYears());

            String currentTimestamp = getCurrentTimestamp();
            values.put(KEY_CREATED_AT, currentTimestamp);
            values.put(KEY_UPDATED_AT, currentTimestamp);

            Log.d(TAG, "Inserting staff with values: " + values.toString());

            staffId = db.insertOrThrow(TABLE_STAFF, null, values);
            db.setTransactionSuccessful();

            Log.d(TAG, "Staff inserted successfully with ID: " + staffId);
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to add staff to database: " + e.getMessage(), e);
            Log.e(TAG, "Staff details - Name: " + staff.getFullName() + ", NIC: " + staff.getNicNumber() + ", Email: " + staff.getEmail());
        } finally {
            db.endTransaction();
        }

        return staffId;
    }

    public Staff getStaffById(long staffId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Staff staff = null;

        String STAFF_SELECT_QUERY =
                "SELECT * FROM " + TABLE_STAFF +
                        " WHERE " + KEY_ID + " = ?";

        Cursor cursor = db.rawQuery(STAFF_SELECT_QUERY, new String[]{String.valueOf(staffId)});
        try {
            if (cursor.moveToFirst()) {
                staff = getStaffFromCursor(cursor);
                Log.d(TAG, "Staff retrieved by ID: " + staffId);
            } else {
                Log.w(TAG, "No staff found with ID: " + staffId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get staff from database: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return staff;
    }

    public Staff getStaffByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            Log.w(TAG, "Email is null or empty");
            return null;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Staff staff = null;

        String STAFF_SELECT_QUERY =
                "SELECT * FROM " + TABLE_STAFF +
                        " WHERE " + KEY_EMAIL + " = ? AND " + KEY_EMAIL + " IS NOT NULL AND " + KEY_EMAIL + " != ''";

        Cursor cursor = db.rawQuery(STAFF_SELECT_QUERY, new String[]{email.trim()});
        try {
            if (cursor.moveToFirst()) {
                staff = getStaffFromCursor(cursor);
                Log.d(TAG, "Staff retrieved by email: " + email);
            } else {
                Log.w(TAG, "No staff found with email: " + email);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get staff by email from database: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return staff;
    }

    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

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
            Log.d(TAG, "Retrieved " + staffList.size() + " staff members");
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get staff from database: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return staffList;
    }

    public int updateStaff(Staff staff) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = 0;

        Log.d(TAG, "Attempting to update staff ID: " + staff.getId());

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_FULL_NAME, staff.getFullName());

            String email = staff.getEmail();
            if (email != null && !email.trim().isEmpty()) {
                values.put(KEY_EMAIL, email.trim());
            } else {
                values.putNull(KEY_EMAIL);
            }

            values.put(KEY_CONTACT_NUMBER, staff.getContactNumber());
            values.put(KEY_NIC_NUMBER, staff.getNicNumber());
            values.put(KEY_GENDER, staff.getGender());
            values.put(KEY_DATE_OF_BIRTH, staff.getDateOfBirth());
            values.put(KEY_PHOTO_URI, staff.getPhotoUri());

            values.put(KEY_POSITION, staff.getPosition());
            values.put(KEY_PROGRAMME, staff.getDepartment());
            values.put(KEY_TEACHING_SUBJECT, staff.getTeachingSubject());
            values.put(KEY_PROGRAM_COORDINATING, staff.getProgramCoordinating());
            values.put(KEY_PASSWORD, staff.getPassword());

            values.put(KEY_HIGHEST_QUALIFICATION, staff.getHighestQualification());
            values.put(KEY_FIELD_OF_STUDY, staff.getFieldOfStudy());
            values.put(KEY_UNIVERSITY, staff.getUniversity());
            values.put(KEY_GRADUATION_YEAR, staff.getGraduationYear());
            values.put(KEY_EXPERIENCE_YEARS, staff.getExperienceYears());

            values.put(KEY_UPDATED_AT, getCurrentTimestamp());

            rowsAffected = db.update(TABLE_STAFF, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(staff.getId())});

            if (rowsAffected > 0) {
                db.setTransactionSuccessful();
                Log.d(TAG, "Staff updated successfully. Rows affected: " + rowsAffected);
            } else {
                Log.w(TAG, "No rows affected during update. Staff ID might not exist: " + staff.getId());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to update staff: " + e.getMessage(), e);
        } finally {
            db.endTransaction();
        }

        return rowsAffected;
    }

    public int updateStaffPersonalDetails(long staffId, String fullName, String email,
                                          String contactNumber, String nicNumber, String gender,
                                          String dateOfBirth, String photoUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = 0;

        Log.d(TAG, "Updating personal details for staff ID: " + staffId);

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_FULL_NAME, fullName);

            if (email != null && !email.trim().isEmpty()) {
                values.put(KEY_EMAIL, email.trim());
            } else {
                values.putNull(KEY_EMAIL);
            }

            values.put(KEY_CONTACT_NUMBER, contactNumber);
            values.put(KEY_NIC_NUMBER, nicNumber);
            values.put(KEY_GENDER, gender);
            values.put(KEY_DATE_OF_BIRTH, dateOfBirth);
            values.put(KEY_PHOTO_URI, photoUri);
            values.put(KEY_UPDATED_AT, getCurrentTimestamp());

            rowsAffected = db.update(TABLE_STAFF, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(staffId)});

            if (rowsAffected > 0) {
                db.setTransactionSuccessful();
                Log.d(TAG, "Staff personal details updated successfully. Rows affected: " + rowsAffected);
            } else {
                Log.w(TAG, "No rows affected during personal details update. Staff ID might not exist: " + staffId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to update staff personal details: " + e.getMessage(), e);
        } finally {
            db.endTransaction();
        }

        return rowsAffected;
    }

    public int updateStaffProfessionalDetails(long staffId, String position, String programme,
                                              String teachingSubject, String programCoordinating, String password,
                                              String highestQualification, String fieldOfStudy,
                                              String university, String graduationYear, String experienceYears) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = 0;

        Log.d(TAG, "Updating professional details for staff ID: " + staffId);

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_POSITION, position);
            values.put(KEY_PROGRAMME, programme);
            values.put(KEY_TEACHING_SUBJECT, teachingSubject);
            values.put(KEY_PROGRAM_COORDINATING, programCoordinating);
            values.put(KEY_PASSWORD, password);
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
                Log.w(TAG, "No rows affected during professional details update. Staff ID might not exist: " + staffId);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error while trying to update staff professional details: " + e.getMessage(), e);
        } finally {
            db.endTransaction();
        }

        return rowsAffected;
    }

    public void deleteStaff(long staffId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            int rowsDeleted = db.delete(TABLE_STAFF, KEY_ID + " = ?", new String[]{String.valueOf(staffId)});
            db.setTransactionSuccessful();
            Log.d(TAG, "Staff deleted with ID: " + staffId + ". Rows deleted: " + rowsDeleted);
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to delete staff: " + e.getMessage(), e);
        } finally {
            db.endTransaction();
        }
    }

    public void deleteStaffWithPhotoCleanup(Staff staff) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            if (staff.getPhotoUri() != null && !staff.getPhotoUri().isEmpty()) {
                File photoFile = new File(staff.getPhotoUri());
                if (photoFile.exists()) {
                    boolean deleted = photoFile.delete();
                    Log.d(TAG, "Photo deleted: " + deleted + " - " + staff.getPhotoUri());
                }
            }

            int rowsDeleted = db.delete(TABLE_STAFF, KEY_ID + " = ?",
                    new String[]{String.valueOf(staff.getId())});
            db.setTransactionSuccessful();
            Log.d(TAG, "Staff deleted from database. Rows affected: " + rowsDeleted);
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to delete staff with photo cleanup: " + e.getMessage(), e);
        } finally {
            db.endTransaction();
        }
    }

    public boolean isEmailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        boolean exists = false;

        String query = "SELECT 1 FROM " + TABLE_STAFF +
                " WHERE " + KEY_EMAIL + " = ? AND " + KEY_EMAIL + " IS NOT NULL AND " + KEY_EMAIL + " != ''";

        Cursor cursor = db.rawQuery(query, new String[]{email.trim()});
        try {
            exists = cursor.getCount() > 0;
            Log.d(TAG, "Email exists check for '" + email + "': " + exists);
        } catch (Exception e) {
            Log.e(TAG, "Error checking email: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return exists;
    }

    public boolean isNicExists(String nicNumber) {
        if (nicNumber == null || nicNumber.trim().isEmpty()) {
            return false;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        boolean exists = false;

        String query = "SELECT 1 FROM " + TABLE_STAFF +
                " WHERE " + KEY_NIC_NUMBER + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{nicNumber.trim()});
        try {
            exists = cursor.getCount() > 0;
            Log.d(TAG, "NIC exists check for '" + nicNumber + "': " + exists);
        } catch (Exception e) {
            Log.e(TAG, "Error checking NIC number: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return exists;
    }

    public List<Staff> searchStaff(String searchQuery) {
        List<Staff> staffList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

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
            Log.d(TAG, "Search for '" + searchQuery + "' returned " + staffList.size() + " results");
        } catch (Exception e) {
            Log.e(TAG, "Error while searching staff: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return staffList;
    }

    public int getStaffCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;

        String COUNT_QUERY = "SELECT COUNT(*) FROM " + TABLE_STAFF;
        Cursor cursor = db.rawQuery(COUNT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            Log.d(TAG, "Total staff count: " + count);
        } catch (Exception e) {
            Log.e(TAG, "Error while getting staff count: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return count;
    }

    private Staff getStaffFromCursor(Cursor cursor) {
        Staff staff = new Staff();

        try {
            staff.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)));
            staff.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FULL_NAME)));
            staff.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)));
            staff.setContactNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTACT_NUMBER)));
            staff.setNicNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NIC_NUMBER)));
            staff.setGender(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GENDER)));
            staff.setDateOfBirth(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_OF_BIRTH)));
            staff.setPhotoUri(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHOTO_URI)));

            staff.setPosition(cursor.getString(cursor.getColumnIndexOrThrow(KEY_POSITION)));
            staff.setDepartment(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROGRAMME)));
            staff.setTeachingSubject(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TEACHING_SUBJECT)));
            staff.setProgramCoordinating(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROGRAM_COORDINATING)));
            staff.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD)));

            staff.setHighestQualification(cursor.getString(cursor.getColumnIndexOrThrow(KEY_HIGHEST_QUALIFICATION)));
            staff.setFieldOfStudy(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FIELD_OF_STUDY)));
            staff.setUniversity(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UNIVERSITY)));
            staff.setGraduationYear(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GRADUATION_YEAR)));
            staff.setExperienceYears(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EXPERIENCE_YEARS)));

            staff.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CREATED_AT)));
            staff.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UPDATED_AT)));
        } catch (Exception e) {
            Log.e(TAG, "Error creating staff object from cursor: " + e.getMessage(), e);
        }

        return staff;
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

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