package com.example.codeverse.Lecturer.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.codeverse.Lecturer.Models.LecturerNotes;

import java.util.ArrayList;
import java.util.List;

public class LectureNotesHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LecturerNotes.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_LECTURER_NOTES = "lecturer_notes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LECTURER_NAME = "lecturer_name";
    private static final String COLUMN_SUBJECT = "subject";
    private static final String COLUMN_LECTURE_TITLE = "lecture_title";
    private static final String COLUMN_LECTURE_DATE = "lecture_date";
    private static final String COLUMN_CHAPTER = "chapter";
    private static final String COLUMN_NOTES_FILE_PATH = "notes_file_path";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE_CREATED = "date_created";
    private static final String COLUMN_TIME_CREATED = "time_created";

    public LectureNotesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LECTURER_NOTES_TABLE = "CREATE TABLE " + TABLE_LECTURER_NOTES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_LECTURER_NAME + " TEXT,"
                + COLUMN_SUBJECT + " TEXT,"
                + COLUMN_LECTURE_TITLE + " TEXT,"
                + COLUMN_LECTURE_DATE + " TEXT,"
                + COLUMN_CHAPTER + " TEXT,"
                + COLUMN_NOTES_FILE_PATH + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_DATE_CREATED + " TEXT,"
                + COLUMN_TIME_CREATED + " TEXT" + ")";
        db.execSQL(CREATE_LECTURER_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LECTURER_NOTES);
        onCreate(db);
    }

    public long addLecturerNotes(LecturerNotes notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_LECTURER_NAME, notes.getLecturerName());
        values.put(COLUMN_SUBJECT, notes.getSubject());
        values.put(COLUMN_LECTURE_TITLE, notes.getLectureTitle());
        values.put(COLUMN_LECTURE_DATE, notes.getLectureDate());
        values.put(COLUMN_CHAPTER, notes.getChapter());
        values.put(COLUMN_NOTES_FILE_PATH, notes.getNotesFilePath());
        values.put(COLUMN_DESCRIPTION, notes.getDescription());
        values.put(COLUMN_DATE_CREATED, notes.getDateCreated());
        values.put(COLUMN_TIME_CREATED, notes.getTimeCreated());

        long id = db.insert(TABLE_LECTURER_NOTES, null, values);
        db.close();
        return id;
    }

    public List<LecturerNotes> getAllLecturerNotes() {
        List<LecturerNotes> notesList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_LECTURER_NOTES + " ORDER BY " + COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                LecturerNotes notes = new LecturerNotes();
                notes.setId(cursor.getInt(0));
                notes.setLecturerName(cursor.getString(1));
                notes.setSubject(cursor.getString(2));
                notes.setLectureTitle(cursor.getString(3));
                notes.setLectureDate(cursor.getString(4));
                notes.setChapter(cursor.getString(5));
                notes.setNotesFilePath(cursor.getString(6));
                notes.setDescription(cursor.getString(7));
                notes.setDateCreated(cursor.getString(8));
                notes.setTimeCreated(cursor.getString(9));

                notesList.add(notes);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notesList;
    }

    public LecturerNotes getLecturerNotesById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LECTURER_NOTES,
                new String[]{COLUMN_ID, COLUMN_LECTURER_NAME, COLUMN_SUBJECT, COLUMN_LECTURE_TITLE,
                        COLUMN_LECTURE_DATE, COLUMN_CHAPTER, COLUMN_NOTES_FILE_PATH, COLUMN_DESCRIPTION,
                        COLUMN_DATE_CREATED, COLUMN_TIME_CREATED},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            LecturerNotes notes = new LecturerNotes();
            notes.setId(cursor.getInt(0));
            notes.setLecturerName(cursor.getString(1));
            notes.setSubject(cursor.getString(2));
            notes.setLectureTitle(cursor.getString(3));
            notes.setLectureDate(cursor.getString(4));
            notes.setChapter(cursor.getString(5));
            notes.setNotesFilePath(cursor.getString(6));
            notes.setDescription(cursor.getString(7));
            notes.setDateCreated(cursor.getString(8));
            notes.setTimeCreated(cursor.getString(9));

            cursor.close();
            db.close();
            return notes;
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return null;
    }

    public int updateLecturerNotes(LecturerNotes notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_LECTURER_NAME, notes.getLecturerName());
        values.put(COLUMN_SUBJECT, notes.getSubject());
        values.put(COLUMN_LECTURE_TITLE, notes.getLectureTitle());
        values.put(COLUMN_LECTURE_DATE, notes.getLectureDate());
        values.put(COLUMN_CHAPTER, notes.getChapter());
        values.put(COLUMN_NOTES_FILE_PATH, notes.getNotesFilePath());
        values.put(COLUMN_DESCRIPTION, notes.getDescription());
        values.put(COLUMN_DATE_CREATED, notes.getDateCreated());
        values.put(COLUMN_TIME_CREATED, notes.getTimeCreated());

        int rowsAffected = db.update(TABLE_LECTURER_NOTES, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(notes.getId())});
        db.close();
        return rowsAffected;
    }

    public void deleteLecturerNotes(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LECTURER_NOTES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<LecturerNotes> searchLecturerNotes(String searchQuery) {
        List<LecturerNotes> notesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_LECTURER_NOTES +
                " WHERE " + COLUMN_LECTURER_NAME + " LIKE ? OR " +
                COLUMN_SUBJECT + " LIKE ? OR " +
                COLUMN_LECTURE_TITLE + " LIKE ? OR " +
                COLUMN_CHAPTER + " LIKE ?" +
                " ORDER BY " + COLUMN_ID + " DESC";

        String searchPattern = "%" + searchQuery + "%";
        Cursor cursor = db.rawQuery(query, new String[]{searchPattern, searchPattern, searchPattern, searchPattern});

        if (cursor.moveToFirst()) {
            do {
                LecturerNotes notes = new LecturerNotes();
                notes.setId(cursor.getInt(0));
                notes.setLecturerName(cursor.getString(1));
                notes.setSubject(cursor.getString(2));
                notes.setLectureTitle(cursor.getString(3));
                notes.setLectureDate(cursor.getString(4));
                notes.setChapter(cursor.getString(5));
                notes.setNotesFilePath(cursor.getString(6));
                notes.setDescription(cursor.getString(7));
                notes.setDateCreated(cursor.getString(8));
                notes.setTimeCreated(cursor.getString(9));

                notesList.add(notes);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notesList;
    }

    public int getNotesCount() {
        String countQuery = "SELECT * FROM " + TABLE_LECTURER_NOTES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
}