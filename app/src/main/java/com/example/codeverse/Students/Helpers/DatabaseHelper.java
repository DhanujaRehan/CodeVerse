package com.example.codeverse.Students.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Timetable.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TIMETABLE_TABLE =
            "CREATE TABLE TimeTable (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "week_title TEXT NOT NULL, " +
                    "week_start_date TEXT NOT NULL, " +
                    "week_end_date TEXT NOT NULL, " +
                    "pdf_path TEXT NOT NULL, " +
                    "file_size TEXT, " +
                    "status TEXT DEFAULT 'Available', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

    private static final String CREATE_DOWNLOADS_TABLE =
            "CREATE TABLE downloads (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "timetable_id INTEGER, " +
                    "file_name TEXT NOT NULL, " +
                    "download_date TEXT NOT NULL, " +
                    "type TEXT DEFAULT 'timetable', " +
                    "FOREIGN KEY(timetable_id) REFERENCES TimeTable(id))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TIMETABLE_TABLE);
        db.execSQL(CREATE_DOWNLOADS_TABLE);
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS downloads");
        db.execSQL("DROP TABLE IF EXISTS TimeTable");
        onCreate(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        String[] sampleData = {
                "INSERT INTO TimeTable (week_title, week_start_date, week_end_date, pdf_path, file_size, status) VALUES " +
                        "('Week 1 - January 2025', '2025-01-06', '2025-01-12', 'https://example.com/timetables/week1.pdf', '2.1 MB', 'Available')",

                "INSERT INTO TimeTable (week_title, week_start_date, week_end_date, pdf_path, file_size, status) VALUES " +
                        "('Week 2 - January 2025', '2025-01-13', '2025-01-19', 'https://example.com/timetables/week2.pdf', '1.8 MB', 'Available')",

                "INSERT INTO TimeTable (week_title, week_start_date, week_end_date, pdf_path, file_size, status) VALUES " +
                        "('Week 3 - January 2025', '2025-01-20', '2025-01-26', 'https://example.com/timetables/week3.pdf', '2.3 MB', 'Available')",

                "INSERT INTO TimeTable (week_title, week_start_date, week_end_date, pdf_path, file_size, status) VALUES " +
                        "('Week 4 - January 2025', '2025-01-27', '2025-02-02', 'https://example.com/timetables/week4.pdf', '1.9 MB', 'Unavailable')",

                "INSERT INTO TimeTable (week_title, week_start_date, week_end_date, pdf_path, file_size, status) VALUES " +
                        "('Week 5 - February 2025', '2025-02-03', '2025-02-09', 'https://example.com/timetables/week5.pdf', '2.0 MB', 'Available')"
        };

        for (String sql : sampleData) {
            db.execSQL(sql);
        }
    }
}