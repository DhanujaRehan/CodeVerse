package com.example.codeverse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class EventHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Events.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_EVENTS = "Events";

    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_VENUE = "venue";
    private static final String COL_IMAGE = "image";

    public EventHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_EVENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_TIME + " TEXT, " +
                COL_VENUE + " TEXT, " +
                COL_IMAGE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public boolean insertEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, event.getTitle());
        values.put(COL_DESCRIPTION, event.getDescription());
        values.put(COL_DATE, event.getDate());
        values.put(COL_TIME, event.getTime());
        values.put(COL_VENUE, event.getVenue());
        values.put(COL_IMAGE, event.getImage());

        long result = db.insert(TABLE_EVENTS, null, values);
        return result != -1;
    }

    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_EVENTS;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setId(cursor.getInt(0));
                event.setTitle(cursor.getString(1));
                event.setDescription(cursor.getString(2));
                event.setDate(cursor.getString(3));
                event.setTime(cursor.getString(4));
                event.setVenue(cursor.getString(5));
                event.setImage(cursor.getString(6));
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventList;
    }

    public Event getEventById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Event event = new Event();
            event.setId(cursor.getInt(0));
            event.setTitle(cursor.getString(1));
            event.setDescription(cursor.getString(2));
            event.setDate(cursor.getString(3));
            event.setTime(cursor.getString(4));
            event.setVenue(cursor.getString(5));
            event.setImage(cursor.getString(6));
            cursor.close();
            return event;
        }
        return null;
    }
}