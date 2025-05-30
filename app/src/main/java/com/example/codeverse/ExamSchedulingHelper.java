package com.example.codeverse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class ExamSchedulingHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ExamScheduling.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "ExamSchedule";

    public ExamSchedulingHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "subject_name TEXT, "
                + "course_code TEXT, "
                + "exam_type TEXT, "
                + "semester TEXT, "
                + "instructor TEXT, "
                + "exam_date TEXT, "
                + "start_time TEXT, "
                + "end_time TEXT, "
                + "room TEXT, "
                + "student_count INTEGER, "
                + "notes TEXT, "
                + "status TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertExam(Exam exam) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("subject_name", exam.getSubjectName());
        values.put("course_code", exam.getCourseCode());
        values.put("exam_type", exam.getExamType());
        values.put("semester", exam.getSemester());
        values.put("instructor", exam.getInstructor());
        values.put("exam_date", exam.getExamDate());
        values.put("start_time", exam.getStartTime());
        values.put("end_time", exam.getEndTime());
        values.put("room", exam.getRoom());
        values.put("student_count", exam.getStudentCount());
        values.put("notes", exam.getNotes());
        values.put("status", exam.getStatus());
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public List<Exam> getAllExams() {
        List<Exam> examList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Exam exam = new Exam();
                exam.setId(cursor.getInt(0));
                exam.setSubjectName(cursor.getString(1));
                exam.setCourseCode(cursor.getString(2));
                exam.setExamType(cursor.getString(3));
                exam.setSemester(cursor.getString(4));
                exam.setInstructor(cursor.getString(5));
                exam.setExamDate(cursor.getString(6));
                exam.setStartTime(cursor.getString(7));
                exam.setEndTime(cursor.getString(8));
                exam.setRoom(cursor.getString(9));
                exam.setStudentCount(cursor.getInt(10));
                exam.setNotes(cursor.getString(11));
                exam.setStatus(cursor.getString(12));
                examList.add(exam);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return examList;
    }

    public int updateExam(Exam exam) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("subject_name", exam.getSubjectName());
        values.put("course_code", exam.getCourseCode());
        values.put("exam_type", exam.getExamType());
        values.put("semester", exam.getSemester());
        values.put("instructor", exam.getInstructor());
        values.put("exam_date", exam.getExamDate());
        values.put("start_time", exam.getStartTime());
        values.put("end_time", exam.getEndTime());
        values.put("room", exam.getRoom());
        values.put("student_count", exam.getStudentCount());
        values.put("notes", exam.getNotes());
        values.put("status", exam.getStatus());
        int result = db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(exam.getId())});
        db.close();
        return result;
    }

    public void deleteExam(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}