package com.example.codeverse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.codeverse.StudentClassSchedule;
import com.example.codeverse.LecturerClassSchedule;
import java.util.ArrayList;
import java.util.List;

public class ClassScheduleHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ClassSchedules.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_STUDENT_SCHEDULE = "StudentClassSchedule";
    private static final String TABLE_LECTURER_SCHEDULE = "LecturersClassSchedule";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SUBJECT_NAME = "subjectName";
    private static final String COLUMN_MODULE_NUMBER = "moduleNumber";
    private static final String COLUMN_LECTURER_NAME = "lecturerName";
    private static final String COLUMN_CLASSROOM = "classroom";
    private static final String COLUMN_START_TIME = "startTime";
    private static final String COLUMN_END_TIME = "endTime";
    private static final String COLUMN_AM_PM = "amPm";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_DATE = "date";

    public ClassScheduleHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createStudentTable = "CREATE TABLE " + TABLE_STUDENT_SCHEDULE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SUBJECT_NAME + " TEXT, " +
                COLUMN_MODULE_NUMBER + " TEXT, " +
                COLUMN_LECTURER_NAME + " TEXT, " +
                COLUMN_CLASSROOM + " TEXT, " +
                COLUMN_START_TIME + " TEXT, " +
                COLUMN_END_TIME + " TEXT, " +
                COLUMN_AM_PM + " TEXT, " +
                COLUMN_STATUS + " TEXT, " +
                COLUMN_DATE + " TEXT)";

        String createLecturerTable = "CREATE TABLE " + TABLE_LECTURER_SCHEDULE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SUBJECT_NAME + " TEXT, " +
                COLUMN_MODULE_NUMBER + " TEXT, " +
                COLUMN_LECTURER_NAME + " TEXT, " +
                COLUMN_CLASSROOM + " TEXT, " +
                COLUMN_START_TIME + " TEXT, " +
                COLUMN_END_TIME + " TEXT, " +
                COLUMN_AM_PM + " TEXT, " +
                COLUMN_STATUS + " TEXT, " +
                COLUMN_DATE + " TEXT)";

        db.execSQL(createStudentTable);
        db.execSQL(createLecturerTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LECTURER_SCHEDULE);
        onCreate(db);
    }

    public long insertStudentSchedule(StudentClassSchedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SUBJECT_NAME, schedule.getSubjectName());
        values.put(COLUMN_MODULE_NUMBER, schedule.getModuleNumber());
        values.put(COLUMN_LECTURER_NAME, schedule.getLecturerName());
        values.put(COLUMN_CLASSROOM, schedule.getClassroom());
        values.put(COLUMN_START_TIME, schedule.getStartTime());
        values.put(COLUMN_END_TIME, schedule.getEndTime());
        values.put(COLUMN_AM_PM, schedule.getAmPm());
        values.put(COLUMN_STATUS, schedule.getStatus());
        values.put(COLUMN_DATE, schedule.getDate());

        long result = db.insert(TABLE_STUDENT_SCHEDULE, null, values);
        db.close();
        return result;
    }

    public long insertLecturerSchedule(LecturerClassSchedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SUBJECT_NAME, schedule.getSubjectName());
        values.put(COLUMN_MODULE_NUMBER, schedule.getModuleNumber());
        values.put(COLUMN_LECTURER_NAME, schedule.getLecturerName());
        values.put(COLUMN_CLASSROOM, schedule.getClassroom());
        values.put(COLUMN_START_TIME, schedule.getStartTime());
        values.put(COLUMN_END_TIME, schedule.getEndTime());
        values.put(COLUMN_AM_PM, schedule.getAmPm());
        values.put(COLUMN_STATUS, schedule.getStatus());
        values.put(COLUMN_DATE, schedule.getDate());

        long result = db.insert(TABLE_LECTURER_SCHEDULE, null, values);
        db.close();
        return result;
    }

    public List<StudentClassSchedule> getStudentSchedulesByDate(String date) {
        List<StudentClassSchedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_STUDENT_SCHEDULE + " WHERE " + COLUMN_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{date});

        if (cursor.moveToFirst()) {
            do {
                StudentClassSchedule schedule = new StudentClassSchedule();
                schedule.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                schedule.setSubjectName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT_NAME)));
                schedule.setModuleNumber(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODULE_NUMBER)));
                schedule.setLecturerName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LECTURER_NAME)));
                schedule.setClassroom(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASSROOM)));
                schedule.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)));
                schedule.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)));
                schedule.setAmPm(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AM_PM)));
                schedule.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                schedule.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                schedules.add(schedule);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return schedules;
    }

    public List<LecturerClassSchedule> getLecturerSchedulesByDate(String date) {
        List<LecturerClassSchedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_LECTURER_SCHEDULE + " WHERE " + COLUMN_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{date});

        if (cursor.moveToFirst()) {
            do {
                LecturerClassSchedule schedule = new LecturerClassSchedule();
                schedule.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                schedule.setSubjectName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT_NAME)));
                schedule.setModuleNumber(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODULE_NUMBER)));
                schedule.setLecturerName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LECTURER_NAME)));
                schedule.setClassroom(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASSROOM)));
                schedule.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)));
                schedule.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)));
                schedule.setAmPm(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AM_PM)));
                schedule.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                schedule.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                schedules.add(schedule);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return schedules;
    }

    public boolean updateStudentSchedule(StudentClassSchedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SUBJECT_NAME, schedule.getSubjectName());
        values.put(COLUMN_MODULE_NUMBER, schedule.getModuleNumber());
        values.put(COLUMN_LECTURER_NAME, schedule.getLecturerName());
        values.put(COLUMN_CLASSROOM, schedule.getClassroom());
        values.put(COLUMN_START_TIME, schedule.getStartTime());
        values.put(COLUMN_END_TIME, schedule.getEndTime());
        values.put(COLUMN_AM_PM, schedule.getAmPm());
        values.put(COLUMN_STATUS, schedule.getStatus());
        values.put(COLUMN_DATE, schedule.getDate());

        int result = db.update(TABLE_STUDENT_SCHEDULE, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(schedule.getId())});
        db.close();
        return result > 0;
    }

    public boolean updateLecturerSchedule(LecturerClassSchedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SUBJECT_NAME, schedule.getSubjectName());
        values.put(COLUMN_MODULE_NUMBER, schedule.getModuleNumber());
        values.put(COLUMN_LECTURER_NAME, schedule.getLecturerName());
        values.put(COLUMN_CLASSROOM, schedule.getClassroom());
        values.put(COLUMN_START_TIME, schedule.getStartTime());
        values.put(COLUMN_END_TIME, schedule.getEndTime());
        values.put(COLUMN_AM_PM, schedule.getAmPm());
        values.put(COLUMN_STATUS, schedule.getStatus());
        values.put(COLUMN_DATE, schedule.getDate());

        int result = db.update(TABLE_LECTURER_SCHEDULE, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(schedule.getId())});
        db.close();
        return result > 0;
    }

    public boolean deleteStudentSchedule(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_STUDENT_SCHEDULE, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public boolean deleteLecturerSchedule(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_LECTURER_SCHEDULE, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }
}