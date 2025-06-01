package com.example.codeverse.Students.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.codeverse.Students.Models.AssignmentModel;

import java.util.ArrayList;
import java.util.List;

public class AssignmentUploadHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AssignmentsUploads.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ASSIGNMENTS = "AssignmentsUpload";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_SUBJECT = "subject";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_FILE_NAME = "file_name";
    private static final String COLUMN_FILE_PATH = "file_path";
    private static final String COLUMN_UPLOAD_DATE = "upload_date";
    private static final String COLUMN_FILE_SIZE = "file_size";
    private static final String COLUMN_STATUS = "status";

    private static final String CREATE_TABLE_ASSIGNMENTS =
            "CREATE TABLE " + TABLE_ASSIGNMENTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_SUBJECT + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_FILE_NAME + " TEXT NOT NULL, " +
                    COLUMN_FILE_PATH + " TEXT NOT NULL, " +
                    COLUMN_UPLOAD_DATE + " INTEGER NOT NULL, " +
                    COLUMN_FILE_SIZE + " INTEGER DEFAULT 0, " +
                    COLUMN_STATUS + " TEXT DEFAULT 'uploaded'" +
                    ")";

    public AssignmentUploadHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ASSIGNMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNMENTS);
        onCreate(db);
    }

    public long insertAssignment(AssignmentModel assignment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, assignment.getTitle());
        values.put(COLUMN_SUBJECT, assignment.getSubject());
        values.put(COLUMN_DESCRIPTION, assignment.getDescription());
        values.put(COLUMN_FILE_NAME, assignment.getFileName());
        values.put(COLUMN_FILE_PATH, assignment.getFilePath());
        values.put(COLUMN_UPLOAD_DATE, assignment.getUploadDate());
        values.put(COLUMN_FILE_SIZE, assignment.getFileSize());
        values.put(COLUMN_STATUS, assignment.getStatus());

        long id = db.insert(TABLE_ASSIGNMENTS, null, values);
        db.close();

        return id;
    }

    public List<AssignmentModel> getAllAssignments() {
        List<AssignmentModel> assignments = new ArrayList<>();
        String selectQuery = "SELECT " + COLUMN_ID + "," + COLUMN_TITLE + "," + COLUMN_SUBJECT + "," +
                COLUMN_DESCRIPTION + "," + COLUMN_FILE_NAME + "," + COLUMN_FILE_PATH + "," +
                COLUMN_UPLOAD_DATE + "," + COLUMN_FILE_SIZE + "," + COLUMN_STATUS +
                " FROM " + TABLE_ASSIGNMENTS + " ORDER BY " + COLUMN_UPLOAD_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                AssignmentModel assignment = new AssignmentModel();
                assignment.setId(cursor.getInt(0));
                assignment.setTitle(cursor.getString(1));
                assignment.setSubject(cursor.getString(2));
                assignment.setDescription(cursor.getString(3));
                assignment.setFileName(cursor.getString(4));
                assignment.setFilePath(cursor.getString(5));
                assignment.setUploadDate(cursor.getLong(6));
                assignment.setFileSize(cursor.getLong(7));
                assignment.setStatus(cursor.getString(8));

                assignments.add(assignment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return assignments;
    }

    public AssignmentModel getAssignmentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + COLUMN_ID + "," + COLUMN_TITLE + "," + COLUMN_SUBJECT + "," +
                COLUMN_DESCRIPTION + "," + COLUMN_FILE_NAME + "," + COLUMN_FILE_PATH + "," +
                COLUMN_UPLOAD_DATE + "," + COLUMN_FILE_SIZE + "," + COLUMN_STATUS +
                " FROM " + TABLE_ASSIGNMENTS + " WHERE " + COLUMN_ID + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        AssignmentModel assignment = null;

        if (cursor.moveToFirst()) {
            assignment = new AssignmentModel();
            assignment.setId(cursor.getInt(0));
            assignment.setTitle(cursor.getString(1));
            assignment.setSubject(cursor.getString(2));
            assignment.setDescription(cursor.getString(3));
            assignment.setFileName(cursor.getString(4));
            assignment.setFilePath(cursor.getString(5));
            assignment.setUploadDate(cursor.getLong(6));
            assignment.setFileSize(cursor.getLong(7));
            assignment.setStatus(cursor.getString(8));
        }

        cursor.close();
        db.close();

        return assignment;
    }

    public List<AssignmentModel> getAssignmentsBySubject(String subject) {
        List<AssignmentModel> assignments = new ArrayList<>();
        String selectQuery = "SELECT " + COLUMN_ID + "," + COLUMN_TITLE + "," + COLUMN_SUBJECT + "," +
                COLUMN_DESCRIPTION + "," + COLUMN_FILE_NAME + "," + COLUMN_FILE_PATH + "," +
                COLUMN_UPLOAD_DATE + "," + COLUMN_FILE_SIZE + "," + COLUMN_STATUS +
                " FROM " + TABLE_ASSIGNMENTS + " WHERE " + COLUMN_SUBJECT + " = ? ORDER BY " + COLUMN_UPLOAD_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{subject});

        if (cursor.moveToFirst()) {
            do {
                AssignmentModel assignment = new AssignmentModel();
                assignment.setId(cursor.getInt(0));
                assignment.setTitle(cursor.getString(1));
                assignment.setSubject(cursor.getString(2));
                assignment.setDescription(cursor.getString(3));
                assignment.setFileName(cursor.getString(4));
                assignment.setFilePath(cursor.getString(5));
                assignment.setUploadDate(cursor.getLong(6));
                assignment.setFileSize(cursor.getLong(7));
                assignment.setStatus(cursor.getString(8));

                assignments.add(assignment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return assignments;
    }

    public int updateAssignment(AssignmentModel assignment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, assignment.getTitle());
        values.put(COLUMN_SUBJECT, assignment.getSubject());
        values.put(COLUMN_DESCRIPTION, assignment.getDescription());
        values.put(COLUMN_FILE_NAME, assignment.getFileName());
        values.put(COLUMN_FILE_PATH, assignment.getFilePath());
        values.put(COLUMN_FILE_SIZE, assignment.getFileSize());
        values.put(COLUMN_STATUS, assignment.getStatus());

        int result = db.update(TABLE_ASSIGNMENTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(assignment.getId())});
        db.close();

        return result;
    }

    public int deleteAssignment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ASSIGNMENTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return result;
    }

    public int getAssignmentCount() {
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_ASSIGNMENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }

    public int getAssignmentCountBySubject(String subject) {
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_ASSIGNMENTS + " WHERE " + COLUMN_SUBJECT + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[]{subject});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }

    public long getTotalFileSize() {
        String sumQuery = "SELECT SUM(" + COLUMN_FILE_SIZE + ") FROM " + TABLE_ASSIGNMENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sumQuery, null);

        long totalSize = 0;
        if (cursor.moveToFirst()) {
            totalSize = cursor.getLong(0);
        }

        cursor.close();
        db.close();

        return totalSize;
    }

    public List<String> getAllSubjects() {
        List<String> subjects = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + COLUMN_SUBJECT + " FROM " + TABLE_ASSIGNMENTS + " ORDER BY " + COLUMN_SUBJECT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                subjects.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return subjects;
    }

    public boolean isAssignmentExists(String title, String subject) {
        String selectQuery = "SELECT COUNT(*) FROM " + TABLE_ASSIGNMENTS + " WHERE " + COLUMN_TITLE + " = ? AND " + COLUMN_SUBJECT + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{title, subject});

        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0;
        }

        cursor.close();
        db.close();

        return exists;
    }

    public void deleteAllAssignments() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ASSIGNMENTS, null, null);
        db.close();
    }
}