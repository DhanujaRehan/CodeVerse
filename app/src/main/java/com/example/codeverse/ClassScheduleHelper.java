package com.example.codeverse;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.codeverse.Staff.Models.ScheduleModel;
import java.util.ArrayList;
import java.util.List;

public class ClassScheduleHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ClassSchedules.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "ClassSchedules";

    private static final String COL_ID = "id";
    private static final String COL_SUBJECT_NAME = "subject_name";
    private static final String COL_MODULE_NUMBER = "module_number";
    private static final String COL_LECTURER_NAME = "lecturer_name";
    private static final String COL_CLASSROOM = "classroom";
    private static final String COL_START_TIME = "start_time";
    private static final String COL_END_TIME = "end_time";
    private static final String COL_AM_PM = "am_pm";
    private static final String COL_IS_STUDENT_SCHEDULE = "is_student_schedule";
    private static final String COL_STATUS = "status";
    private static final String COL_DATE = "schedule_date";

    public ClassScheduleHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SUBJECT_NAME + " TEXT NOT NULL, " +
                COL_MODULE_NUMBER + " TEXT NOT NULL, " +
                COL_LECTURER_NAME + " TEXT NOT NULL, " +
                COL_CLASSROOM + " TEXT NOT NULL, " +
                COL_START_TIME + " TEXT NOT NULL, " +
                COL_END_TIME + " TEXT NOT NULL, " +
                COL_AM_PM + " TEXT NOT NULL, " +
                COL_IS_STUDENT_SCHEDULE + " INTEGER NOT NULL, " +
                COL_STATUS + " TEXT NOT NULL, " +
                COL_DATE + " TEXT NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertSchedule(ScheduleModel schedule, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_SUBJECT_NAME, schedule.getSubjectName());
        values.put(COL_MODULE_NUMBER, schedule.getModuleNumber());
        values.put(COL_LECTURER_NAME, schedule.getLecturerName());
        values.put(COL_CLASSROOM, schedule.getClassroom());
        values.put(COL_START_TIME, schedule.getStartTime());
        values.put(COL_END_TIME, schedule.getEndTime());
        values.put(COL_AM_PM, schedule.getAmPm());
        values.put(COL_IS_STUDENT_SCHEDULE, schedule.isStudentSchedule() ? 1 : 0);
        values.put(COL_STATUS, schedule.getStatus());
        values.put(COL_DATE, date);

        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result;
    }

    @SuppressLint("Range")
    public List<ScheduleModel> getSchedulesByDate(String date, boolean isStudentSchedule) {
        List<ScheduleModel> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COL_DATE + " = ? AND " + COL_IS_STUDENT_SCHEDULE + " = ?";
        String[] selectionArgs = {date, isStudentSchedule ? "1" : "0"};

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, COL_START_TIME);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") ScheduleModel schedule = new ScheduleModel(
                        cursor.getString(cursor.getColumnIndex(COL_SUBJECT_NAME)),
                        cursor.getString(cursor.getColumnIndex(COL_MODULE_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(COL_LECTURER_NAME)),
                        cursor.getString(cursor.getColumnIndex(COL_CLASSROOM)),
                        cursor.getString(cursor.getColumnIndex(COL_START_TIME)),
                        cursor.getString(cursor.getColumnIndex(COL_END_TIME)),
                        cursor.getString(cursor.getColumnIndex(COL_AM_PM)),
                        cursor.getInt(cursor.getColumnIndex(COL_IS_STUDENT_SCHEDULE)) == 1,
                        cursor.getString(cursor.getColumnIndex(COL_STATUS))
                );
                schedule.setId(cursor.getLong(cursor.getColumnIndex(COL_ID)));
                schedules.add(schedule);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return schedules;
    }

    public int updateSchedule(ScheduleModel schedule, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_SUBJECT_NAME, schedule.getSubjectName());
        values.put(COL_MODULE_NUMBER, schedule.getModuleNumber());
        values.put(COL_LECTURER_NAME, schedule.getLecturerName());
        values.put(COL_CLASSROOM, schedule.getClassroom());
        values.put(COL_START_TIME, schedule.getStartTime());
        values.put(COL_END_TIME, schedule.getEndTime());
        values.put(COL_AM_PM, schedule.getAmPm());
        values.put(COL_IS_STUDENT_SCHEDULE, schedule.isStudentSchedule() ? 1 : 0);
        values.put(COL_STATUS, schedule.getStatus());
        values.put(COL_DATE, date);

        int result = db.update(TABLE_NAME, values, COL_ID + " = ?", new String[]{String.valueOf(schedule.getId())});
        db.close();
        return result;
    }

    public int deleteSchedule(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    public List<String> getDatesWithSchedules(boolean isStudentSchedule) {
        List<String> dates = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT " + COL_DATE + " FROM " + TABLE_NAME +
                " WHERE " + COL_IS_STUDENT_SCHEDULE + " = ?";
        String[] selectionArgs = {isStudentSchedule ? "1" : "0"};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                dates.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return dates;
    }
}