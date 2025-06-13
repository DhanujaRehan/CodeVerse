package com.example.codeverse.Staff.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.codeverse.Assignment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignmentHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Assignment.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ASSIGNMENT = "Assignment";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MODULE = "module";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_RELEASE_DATE = "release_date";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_WEIGHTING = "weighting";
    private static final String COLUMN_FILE_PATH = "file_path";
    private static final String COLUMN_TARGET_BATCH = "target_batch";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_CREATED_AT = "created_at";

    public AssignmentHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ASSIGNMENT_TABLE = "CREATE TABLE " + TABLE_ASSIGNMENT + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MODULE + " TEXT NOT NULL,"
                + COLUMN_TITLE + " TEXT NOT NULL,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_RELEASE_DATE + " TEXT,"
                + COLUMN_DUE_DATE + " TEXT,"
                + COLUMN_WEIGHTING + " INTEGER,"
                + COLUMN_FILE_PATH + " TEXT,"
                + COLUMN_TARGET_BATCH + " TEXT,"
                + COLUMN_STATUS + " TEXT,"
                + COLUMN_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_ASSIGNMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNMENT);
        onCreate(db);
    }

    public long addAssignment(Assignment assignment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_MODULE, assignment.getModule());
        values.put(COLUMN_TITLE, assignment.getTitle());
        values.put(COLUMN_DESCRIPTION, assignment.getDescription());
        values.put(COLUMN_RELEASE_DATE, assignment.getReleaseDate());
        values.put(COLUMN_DUE_DATE, assignment.getDueDate());
        values.put(COLUMN_WEIGHTING, assignment.getWeighting());
        values.put(COLUMN_FILE_PATH, assignment.getFilePath());
        values.put(COLUMN_TARGET_BATCH, assignment.getTargetBatch());
        values.put(COLUMN_STATUS, assignment.getStatus());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        values.put(COLUMN_CREATED_AT, sdf.format(new Date()));

        long id = db.insert(TABLE_ASSIGNMENT, null, values);
        db.close();
        return id;
    }

    public List<Assignment> getAllAssignments() {
        List<Assignment> assignments = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ASSIGNMENT + " ORDER BY " + COLUMN_CREATED_AT + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Assignment assignment = new Assignment();
                assignment.setId(cursor.getInt(0));
                assignment.setModule(cursor.getString(1));
                assignment.setTitle(cursor.getString(2));
                assignment.setDescription(cursor.getString(3));
                assignment.setReleaseDate(cursor.getString(4));
                assignment.setDueDate(cursor.getString(5));
                assignment.setWeighting(cursor.getInt(6));
                assignment.setFilePath(cursor.getString(7));
                assignment.setTargetBatch(cursor.getString(8));
                assignment.setStatus(cursor.getString(9));
                assignment.setCreatedAt(cursor.getString(10));

                assignments.add(assignment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return assignments;
    }

    public Assignment getAssignment(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ASSIGNMENT, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Assignment assignment = new Assignment();
            assignment.setId(cursor.getInt(0));
            assignment.setModule(cursor.getString(1));
            assignment.setTitle(cursor.getString(2));
            assignment.setDescription(cursor.getString(3));
            assignment.setReleaseDate(cursor.getString(4));
            assignment.setDueDate(cursor.getString(5));
            assignment.setWeighting(cursor.getInt(6));
            assignment.setFilePath(cursor.getString(7));
            assignment.setTargetBatch(cursor.getString(8));
            assignment.setStatus(cursor.getString(9));
            assignment.setCreatedAt(cursor.getString(10));

            cursor.close();
            db.close();
            return assignment;
        }

        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    public int updateAssignment(Assignment assignment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_MODULE, assignment.getModule());
        values.put(COLUMN_TITLE, assignment.getTitle());
        values.put(COLUMN_DESCRIPTION, assignment.getDescription());
        values.put(COLUMN_RELEASE_DATE, assignment.getReleaseDate());
        values.put(COLUMN_DUE_DATE, assignment.getDueDate());
        values.put(COLUMN_WEIGHTING, assignment.getWeighting());
        values.put(COLUMN_FILE_PATH, assignment.getFilePath());
        values.put(COLUMN_TARGET_BATCH, assignment.getTargetBatch());
        values.put(COLUMN_STATUS, assignment.getStatus());

        int result = db.update(TABLE_ASSIGNMENT, values, COLUMN_ID + "=?",
                new String[]{String.valueOf(assignment.getId())});
        db.close();
        return result;
    }

    public void deleteAssignment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ASSIGNMENT, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Assignment> getAssignmentsByStatus(String status) {
        List<Assignment> assignments = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ASSIGNMENT + " WHERE " + COLUMN_STATUS + "=? ORDER BY " + COLUMN_CREATED_AT + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{status});

        if (cursor.moveToFirst()) {
            do {
                Assignment assignment = new Assignment();
                assignment.setId(cursor.getInt(0));
                assignment.setModule(cursor.getString(1));
                assignment.setTitle(cursor.getString(2));
                assignment.setDescription(cursor.getString(3));
                assignment.setReleaseDate(cursor.getString(4));
                assignment.setDueDate(cursor.getString(5));
                assignment.setWeighting(cursor.getInt(6));
                assignment.setFilePath(cursor.getString(7));
                assignment.setTargetBatch(cursor.getString(8));
                assignment.setStatus(cursor.getString(9));
                assignment.setCreatedAt(cursor.getString(10));

                assignments.add(assignment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return assignments;
    }

    public List<Assignment> getAssignmentsByModule(String module) {
        List<Assignment> assignments = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ASSIGNMENT + " WHERE " + COLUMN_MODULE + "=? ORDER BY " + COLUMN_CREATED_AT + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{module});

        if (cursor.moveToFirst()) {
            do {
                Assignment assignment = new Assignment();
                assignment.setId(cursor.getInt(0));
                assignment.setModule(cursor.getString(1));
                assignment.setTitle(cursor.getString(2));
                assignment.setDescription(cursor.getString(3));
                assignment.setReleaseDate(cursor.getString(4));
                assignment.setDueDate(cursor.getString(5));
                assignment.setWeighting(cursor.getInt(6));
                assignment.setFilePath(cursor.getString(7));
                assignment.setTargetBatch(cursor.getString(8));
                assignment.setStatus(cursor.getString(9));
                assignment.setCreatedAt(cursor.getString(10));

                assignments.add(assignment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return assignments;
    }
}