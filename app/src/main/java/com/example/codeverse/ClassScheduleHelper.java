package com.example.codeverse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.codeverse.ScheduleClassModel;
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

    public long insertSchedule(ScheduleClassModel schedule, String date) {
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

    public List<ScheduleClassModel> getSchedulesByDate(String date, boolean isStudentSchedule) {
        List<ScheduleClassModel> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COL_DATE + " = ? AND " + COL_IS_STUDENT_SCHEDULE + " = ?";
        String[] selectionArgs = {date, isStudentSchedule ? "1" : "0"};

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, COL_START_TIME);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    ScheduleClassModel schedule = new ScheduleClassModel(
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_MODULE_NUMBER)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_LECTURER_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_CLASSROOM)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_START_TIME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_END_TIME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_AM_PM)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_STUDENT_SCHEDULE)) == 1,
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS))
                    );
                    schedule.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
                    schedules.add(schedule);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return schedules;
    }

    public int updateSchedule(ScheduleClassModel schedule, String date) {
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

        String whereClause = COL_ID + " = ?";
        String[] whereArgs = {String.valueOf(schedule.getId())};

        int result = db.update(TABLE_NAME, values, whereClause, whereArgs);
        db.close();
        return result;
    }

    public int deleteSchedule(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COL_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        int result = db.delete(TABLE_NAME, whereClause, whereArgs);
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

        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    dates.add(cursor.getString(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return dates;
    }

    public void clearAllSchedules() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public int getScheduleCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return count;
    }
}