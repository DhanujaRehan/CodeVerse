package com.example.codeverse;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TimeTableDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TimeTable.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    public static final String TABLE_TIMETABLE = "TimeTable";

    // Column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SUBJECT_NAME = "subject_name";
    public static final String COLUMN_TEACHER_NAME = "teacher_name";
    public static final String COLUMN_CLASSROOM = "classroom";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";
    public static final String COLUMN_PDF_URL = "pdf_url";
    public static final String COLUMN_STATUS = "status";

    // Create table SQL statement
    private static final String CREATE_TABLE_TIMETABLE =
            "CREATE TABLE " + TABLE_TIMETABLE + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_SUBJECT_NAME + " TEXT NOT NULL," +
                    COLUMN_TEACHER_NAME + " TEXT," +
                    COLUMN_CLASSROOM + " TEXT," +
                    COLUMN_DATE + " TEXT NOT NULL," +
                    COLUMN_START_TIME + " TEXT NOT NULL," +
                    COLUMN_END_TIME + " TEXT NOT NULL," +
                    COLUMN_PDF_URL + " TEXT," +
                    COLUMN_STATUS + " TEXT DEFAULT 'active'" +
                    ")";

    public TimeTableDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TIMETABLE);

        // Insert sample data for testing
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMETABLE);
        onCreate(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Sample data for testing
        insertSampleClass(db, "Mathematics", "Dr. Smith", "Room 101", "2025-06-03", "09:00", "10:30", "https://example.com/math.pdf", "active");
        insertSampleClass(db, "Physics", "Prof. Johnson", "Lab 201", "2025-06-03", "11:00", "12:30", "https://example.com/physics.pdf", "active");
        insertSampleClass(db, "Chemistry", "Dr. Williams", "Lab 301", "2025-06-03", "14:00", "15:30", "https://example.com/chemistry.pdf", "active");
        insertSampleClass(db, "English", "Ms. Brown", "Room 102", "2025-06-04", "09:00", "10:00", "https://example.com/english.pdf", "scheduled");
        insertSampleClass(db, "History", "Mr. Davis", "Room 103", "2025-06-04", "10:30", "11:30", "https://example.com/history.pdf", "scheduled");
    }

    private void insertSampleClass(SQLiteDatabase db, String subject, String teacher, String classroom,
                                   String date, String startTime, String endTime, String pdfUrl, String status) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBJECT_NAME, subject);
        values.put(COLUMN_TEACHER_NAME, teacher);
        values.put(COLUMN_CLASSROOM, classroom);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_START_TIME, startTime);
        values.put(COLUMN_END_TIME, endTime);
        values.put(COLUMN_PDF_URL, pdfUrl);
        values.put(COLUMN_STATUS, status);

        db.insert(TABLE_TIMETABLE, null, values);
    }

    // Helper method to add new timetable entry
    public long addTimeTableEntry(String subject, String teacher, String classroom,
                                  String date, String startTime, String endTime, String pdfUrl, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SUBJECT_NAME, subject);
        values.put(COLUMN_TEACHER_NAME, teacher);
        values.put(COLUMN_CLASSROOM, classroom);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_START_TIME, startTime);
        values.put(COLUMN_END_TIME, endTime);
        values.put(COLUMN_PDF_URL, pdfUrl);
        values.put(COLUMN_STATUS, status);

        long id = db.insert(TABLE_TIMETABLE, null, values);
        db.close();
        return id;
    }

    // Helper method to update timetable entry
    public int updateTimeTableEntry(int id, String subject, String teacher, String classroom,
                                    String date, String startTime, String endTime, String pdfUrl, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SUBJECT_NAME, subject);
        values.put(COLUMN_TEACHER_NAME, teacher);
        values.put(COLUMN_CLASSROOM, classroom);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_START_TIME, startTime);
        values.put(COLUMN_END_TIME, endTime);
        values.put(COLUMN_PDF_URL, pdfUrl);
        values.put(COLUMN_STATUS, status);

        int rowsAffected = db.update(TABLE_TIMETABLE, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }

    // Helper method to delete timetable entry
    public void deleteTimeTableEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TIMETABLE, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}