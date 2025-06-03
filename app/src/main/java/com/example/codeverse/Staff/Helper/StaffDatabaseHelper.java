package com.example.codeverse.Staff.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.codeverse.Staff.Models.StaffDetails;

import java.util.ArrayList;
import java.util.List;

public class StaffDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "StaffDatabase";
    private static final String DATABASE_NAME = "StaffDetails.db";
    private static final int DATABASE_VERSION = 1;


    private static final String TABLE_STAFF = "staff_details";


    private static final String COLUMN_ID = "id";
    private static final String COLUMN_STAFF_ID = "staff_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DEPARTMENT = "department";
    private static final String COLUMN_POSITION = "position";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_OFFICE_HOURS = "office_hours";
    private static final String COLUMN_OFFICE_LOCATION = "office_location";
    private static final String COLUMN_SPECIALIZATION = "specialization";
    private static final String COLUMN_YEARS_SERVICE = "years_service";
    private static final String COLUMN_EDUCATION = "education";
    private static final String COLUMN_RESEARCH_TAGS = "research_tags";
    private static final String COLUMN_RESEARCH_DESCRIPTION = "research_description";
    private static final String COLUMN_PUBLICATIONS = "publications";
    private static final String COLUMN_LINKEDIN = "linkedin";
    private static final String COLUMN_RESEARCH_GATE = "research_gate";
    private static final String COLUMN_GITHUB = "github";
    private static final String COLUMN_PROFILE_IMAGE_PATH = "profile_image_path";
    private static final String COLUMN_CREATED_DATE = "created_date";
    private static final String COLUMN_UPDATED_DATE = "updated_date";


    private static final String CREATE_STAFF_TABLE = "CREATE TABLE " + TABLE_STAFF + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_STAFF_ID + " TEXT UNIQUE NOT NULL,"
            + COLUMN_NAME + " TEXT NOT NULL,"
            + COLUMN_DEPARTMENT + " TEXT,"
            + COLUMN_POSITION + " TEXT,"
            + COLUMN_EMAIL + " TEXT UNIQUE,"
            + COLUMN_PHONE + " TEXT,"
            + COLUMN_OFFICE_HOURS + " TEXT,"
            + COLUMN_OFFICE_LOCATION + " TEXT,"
            + COLUMN_SPECIALIZATION + " TEXT,"
            + COLUMN_YEARS_SERVICE + " INTEGER,"
            + COLUMN_EDUCATION + " TEXT,"
            + COLUMN_RESEARCH_TAGS + " TEXT,"
            + COLUMN_RESEARCH_DESCRIPTION + " TEXT,"
            + COLUMN_PUBLICATIONS + " INTEGER,"
            + COLUMN_LINKEDIN + " TEXT,"
            + COLUMN_RESEARCH_GATE + " TEXT,"
            + COLUMN_GITHUB + " TEXT,"
            + COLUMN_PROFILE_IMAGE_PATH + " TEXT,"
            + COLUMN_CREATED_DATE + " TEXT,"
            + COLUMN_UPDATED_DATE + " TEXT"
            + ")";

    private static StaffDatabaseHelper instance;

    public static synchronized StaffDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new StaffDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private StaffDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "Database helper initialized");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating staff database table");
        try {
            db.execSQL(CREATE_STAFF_TABLE);
            Log.d(TAG, "Staff table created successfully");


            insertSampleData(db);
        } catch (Exception e) {
            Log.e(TAG, "Error creating staff table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAFF);
        onCreate(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        Log.d(TAG, "Inserting sample staff data");

        ContentValues values = new ContentValues();
        values.put(COLUMN_STAFF_ID, "FAC2023104");
        values.put(COLUMN_NAME, "Dr. Sarah Wilson");
        values.put(COLUMN_DEPARTMENT, "Computer Science Department");
        values.put(COLUMN_POSITION, "Associate Professor");
        values.put(COLUMN_EMAIL, "sarah.wilson@university.edu");
        values.put(COLUMN_PHONE, "(555) 987-6543");
        values.put(COLUMN_OFFICE_HOURS, "Mon, Wed: 10:00 AM - 12:00 PM\nThu: 2:00 PM - 4:00 PM");
        values.put(COLUMN_OFFICE_LOCATION, "Computer Science Building, Room 301");
        values.put(COLUMN_SPECIALIZATION, "Artificial Intelligence, Machine Learning");
        values.put(COLUMN_YEARS_SERVICE, 12);
        values.put(COLUMN_EDUCATION, "Ph.D. in Computer Science, MIT\nM.S. in Computer Science, Stanford University");
        values.put(COLUMN_RESEARCH_TAGS, "Artificial Intelligence, Machine Learning, Computer Vision, Natural Language Processing, Data Science");
        values.put(COLUMN_RESEARCH_DESCRIPTION, "Dr. Wilson's research focuses on developing novel machine learning algorithms for computer vision and natural language processing applications.");
        values.put(COLUMN_PUBLICATIONS, 42);
        values.put(COLUMN_LINKEDIN, "linkedin.com/in/sarahwilson");
        values.put(COLUMN_RESEARCH_GATE, "researchgate.net/profile/Sarah_Wilson");
        values.put(COLUMN_GITHUB, "github.com/sarahwilson");
        values.put(COLUMN_CREATED_DATE, getCurrentDateTime());
        values.put(COLUMN_UPDATED_DATE, getCurrentDateTime());

        long result = db.insert(TABLE_STAFF, null, values);
        Log.d(TAG, "Sample data inserted with ID: " + result);
    }


    public long insertStaff(StaffDetails staff) {
        Log.d(TAG, "Inserting staff: " + staff.getName());

        SQLiteDatabase db = null;
        long result = -1;

        try {
            db = this.getWritableDatabase();
            ContentValues values = createContentValues(staff);
            values.put(COLUMN_CREATED_DATE, getCurrentDateTime());
            values.put(COLUMN_UPDATED_DATE, getCurrentDateTime());

            result = db.insert(TABLE_STAFF, null, values);

            if (result != -1) {
                Log.d(TAG, "Staff inserted successfully with ID: " + result);
            } else {
                Log.e(TAG, "Failed to insert staff");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error inserting staff: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (db != null) db.close();
        }

        return result;
    }


    public int updateStaff(StaffDetails staff) {
        Log.d(TAG, "Updating staff: " + staff.getStaffId());

        SQLiteDatabase db = null;
        int result = 0;

        try {
            db = this.getWritableDatabase();
            ContentValues values = createContentValues(staff);
            values.put(COLUMN_UPDATED_DATE, getCurrentDateTime());

            result = db.update(TABLE_STAFF, values,
                    COLUMN_STAFF_ID + " = ?",
                    new String[]{staff.getStaffId()});

            if (result > 0) {
                Log.d(TAG, "Staff updated successfully. Rows affected: " + result);
            } else {
                Log.w(TAG, "No rows updated for staff ID: " + staff.getStaffId());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error updating staff: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (db != null) db.close();
        }

        return result;
    }


    public StaffDetails getStaffByStaffId(String staffId) {
        Log.d(TAG, "Getting staff by ID: " + staffId);

        SQLiteDatabase db = null;
        Cursor cursor = null;
        StaffDetails staff = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_STAFF, null,
                    COLUMN_STAFF_ID + " = ?",
                    new String[]{staffId},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                staff = createStaffFromCursor(cursor);
                Log.d(TAG, "Staff found: " + staff.getName());
            } else {
                Log.w(TAG, "No staff found with ID: " + staffId);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting staff by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return staff;
    }


    public List<StaffDetails> getAllStaff() {
        Log.d(TAG, "Getting all staff members");

        List<StaffDetails> staffList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_STAFF, null, null, null, null, null,
                    COLUMN_NAME + " ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    StaffDetails staff = createStaffFromCursor(cursor);
                    if (staff != null) {
                        staffList.add(staff);
                    }
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Retrieved " + staffList.size() + " staff members");

        } catch (Exception e) {
            Log.e(TAG, "Error getting all staff: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return staffList;
    }


    public int deleteStaff(String staffId) {
        Log.d(TAG, "Deleting staff: " + staffId);

        SQLiteDatabase db = null;
        int result = 0;

        try {
            db = this.getWritableDatabase();
            result = db.delete(TABLE_STAFF,
                    COLUMN_STAFF_ID + " = ?",
                    new String[]{staffId});

            if (result > 0) {
                Log.d(TAG, "Staff deleted successfully. Rows affected: " + result);
            } else {
                Log.w(TAG, "No staff deleted for ID: " + staffId);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error deleting staff: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (db != null) db.close();
        }

        return result;
    }


    public boolean staffExists(String staffId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean exists = false;

        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_STAFF,
                    new String[]{COLUMN_ID},
                    COLUMN_STAFF_ID + " = ?",
                    new String[]{staffId},
                    null, null, null);

            exists = cursor != null && cursor.getCount() > 0;

        } catch (Exception e) {
            Log.e(TAG, "Error checking if staff exists: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return exists;
    }


    private ContentValues createContentValues(StaffDetails staff) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_STAFF_ID, staff.getStaffId());
        values.put(COLUMN_NAME, staff.getName());
        values.put(COLUMN_DEPARTMENT, staff.getDepartment());
        values.put(COLUMN_POSITION, staff.getPosition());
        values.put(COLUMN_EMAIL, staff.getEmail());
        values.put(COLUMN_PHONE, staff.getPhone());
        values.put(COLUMN_OFFICE_HOURS, staff.getOfficeHours());
        values.put(COLUMN_OFFICE_LOCATION, staff.getOfficeLocation());
        values.put(COLUMN_SPECIALIZATION, staff.getSpecialization());
        values.put(COLUMN_YEARS_SERVICE, staff.getYearsService());
        values.put(COLUMN_EDUCATION, staff.getEducation());
        values.put(COLUMN_RESEARCH_TAGS, staff.getResearchTags());
        values.put(COLUMN_RESEARCH_DESCRIPTION, staff.getResearchDescription());
        values.put(COLUMN_PUBLICATIONS, staff.getPublications());
        values.put(COLUMN_LINKEDIN, staff.getLinkedin());
        values.put(COLUMN_RESEARCH_GATE, staff.getResearchGate());
        values.put(COLUMN_GITHUB, staff.getGithub());
        values.put(COLUMN_PROFILE_IMAGE_PATH, staff.getProfileImagePath());

        return values;
    }


    private StaffDetails createStaffFromCursor(Cursor cursor) {
        try {
            StaffDetails staff = new StaffDetails();

            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            if (idIndex >= 0) staff.setId(cursor.getInt(idIndex));

            int staffIdIndex = cursor.getColumnIndex(COLUMN_STAFF_ID);
            if (staffIdIndex >= 0) staff.setStaffId(cursor.getString(staffIdIndex));

            int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
            if (nameIndex >= 0) staff.setName(cursor.getString(nameIndex));

            int departmentIndex = cursor.getColumnIndex(COLUMN_DEPARTMENT);
            if (departmentIndex >= 0) staff.setDepartment(cursor.getString(departmentIndex));

            int positionIndex = cursor.getColumnIndex(COLUMN_POSITION);
            if (positionIndex >= 0) staff.setPosition(cursor.getString(positionIndex));

            int emailIndex = cursor.getColumnIndex(COLUMN_EMAIL);
            if (emailIndex >= 0) staff.setEmail(cursor.getString(emailIndex));

            int phoneIndex = cursor.getColumnIndex(COLUMN_PHONE);
            if (phoneIndex >= 0) staff.setPhone(cursor.getString(phoneIndex));

            int officeHoursIndex = cursor.getColumnIndex(COLUMN_OFFICE_HOURS);
            if (officeHoursIndex >= 0) staff.setOfficeHours(cursor.getString(officeHoursIndex));

            int officeLocationIndex = cursor.getColumnIndex(COLUMN_OFFICE_LOCATION);
            if (officeLocationIndex >= 0) staff.setOfficeLocation(cursor.getString(officeLocationIndex));

            int specializationIndex = cursor.getColumnIndex(COLUMN_SPECIALIZATION);
            if (specializationIndex >= 0) staff.setSpecialization(cursor.getString(specializationIndex));

            int yearsServiceIndex = cursor.getColumnIndex(COLUMN_YEARS_SERVICE);
            if (yearsServiceIndex >= 0) staff.setYearsService(cursor.getInt(yearsServiceIndex));

            int educationIndex = cursor.getColumnIndex(COLUMN_EDUCATION);
            if (educationIndex >= 0) staff.setEducation(cursor.getString(educationIndex));

            int researchTagsIndex = cursor.getColumnIndex(COLUMN_RESEARCH_TAGS);
            if (researchTagsIndex >= 0) staff.setResearchTags(cursor.getString(researchTagsIndex));

            int researchDescriptionIndex = cursor.getColumnIndex(COLUMN_RESEARCH_DESCRIPTION);
            if (researchDescriptionIndex >= 0) staff.setResearchDescription(cursor.getString(researchDescriptionIndex));

            int publicationsIndex = cursor.getColumnIndex(COLUMN_PUBLICATIONS);
            if (publicationsIndex >= 0) staff.setPublications(cursor.getInt(publicationsIndex));

            int linkedinIndex = cursor.getColumnIndex(COLUMN_LINKEDIN);
            if (linkedinIndex >= 0) staff.setLinkedin(cursor.getString(linkedinIndex));

            int researchGateIndex = cursor.getColumnIndex(COLUMN_RESEARCH_GATE);
            if (researchGateIndex >= 0) staff.setResearchGate(cursor.getString(researchGateIndex));

            int githubIndex = cursor.getColumnIndex(COLUMN_GITHUB);
            if (githubIndex >= 0) staff.setGithub(cursor.getString(githubIndex));

            int profileImagePathIndex = cursor.getColumnIndex(COLUMN_PROFILE_IMAGE_PATH);
            if (profileImagePathIndex >= 0) staff.setProfileImagePath(cursor.getString(profileImagePathIndex));

            int createdDateIndex = cursor.getColumnIndex(COLUMN_CREATED_DATE);
            if (createdDateIndex >= 0) staff.setCreatedDate(cursor.getString(createdDateIndex));

            int updatedDateIndex = cursor.getColumnIndex(COLUMN_UPDATED_DATE);
            if (updatedDateIndex >= 0) staff.setUpdatedDate(cursor.getString(updatedDateIndex));

            return staff;

        } catch (Exception e) {
            Log.e(TAG, "Error creating staff from cursor: " + e.getMessage());
            return null;
        }
    }


    private String getCurrentDateTime() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                java.util.Locale.getDefault()).format(new java.util.Date());
    }


    public String getDatabasePath() {
        return this.getReadableDatabase().getPath();
    }


    public boolean testConnection() {
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Log.d(TAG, "Database connection test successful");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Database connection test failed: " + e.getMessage());
            return false;
        } finally {
            if (db != null) db.close();
        }
    }
}