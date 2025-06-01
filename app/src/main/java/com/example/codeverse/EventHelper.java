package com.example.codeverse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class EventHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Events.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_EVENTS = "Events";
    private static final String TABLE_EVENT_REGISTER = "EventRegister";

    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_VENUE = "venue";
    private static final String COL_IMAGE = "image";

    private static final String COL_EVENT_ID = "event_id";
    private static final String COL_STUDENT_ID = "student_id";
    private static final String COL_FULL_NAME = "full_name";
    private static final String COL_EMAIL = "email";
    private static final String COL_PHONE = "phone";
    private static final String COL_DEPARTMENT = "department";
    private static final String COL_DESC = "desc_text";

    public EventHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createEventsTable = "CREATE TABLE " + TABLE_EVENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_TIME + " TEXT, " +
                COL_VENUE + " TEXT, " +
                COL_IMAGE + " TEXT)";
        db.execSQL(createEventsTable);

        String createRegisterTable = "CREATE TABLE " + TABLE_EVENT_REGISTER + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EVENT_ID + " INTEGER, " +
                COL_STUDENT_ID + " TEXT, " +
                COL_FULL_NAME + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_DEPARTMENT + " TEXT, " +
                COL_DESC + " TEXT)";
        db.execSQL(createRegisterTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            String createRegisterTable = "CREATE TABLE " + TABLE_EVENT_REGISTER + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_EVENT_ID + " INTEGER, " +
                    COL_STUDENT_ID + " TEXT, " +
                    COL_FULL_NAME + " TEXT, " +
                    COL_EMAIL + " TEXT, " +
                    COL_PHONE + " TEXT, " +
                    COL_DEPARTMENT + " TEXT, " +
                    COL_DESC + " TEXT)";
            db.execSQL(createRegisterTable);
        }
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
        db.close();
        return result != -1;
    }

    public boolean insertEventRegister(EventRegister eventRegister) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_EVENT_ID, eventRegister.getEventId());
            values.put(COL_STUDENT_ID, eventRegister.getStudentId());
            values.put(COL_FULL_NAME, eventRegister.getFullName());
            values.put(COL_EMAIL, eventRegister.getEmail());
            values.put(COL_PHONE, eventRegister.getPhone());
            values.put(COL_DEPARTMENT, eventRegister.getDepartment());
            values.put(COL_DESC, eventRegister.getDescription());

            long result = db.insert(TABLE_EVENT_REGISTER, null, values);
            Log.d("EventHelper", "Insert result: " + result);
            return result != -1;
        } catch (Exception e) {
            Log.e("EventHelper", "Error inserting registration: " + e.getMessage());
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
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
        db.close();
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
            db.close();
            return event;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    public List<EventRegister> getRegistrationsByEventId(int eventId) {
        List<EventRegister> registerList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EVENT_REGISTER, null, COL_EVENT_ID + "=?",
                new String[]{String.valueOf(eventId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                EventRegister register = new EventRegister();
                register.setId(cursor.getInt(0));
                register.setEventId(cursor.getInt(1));
                register.setStudentId(cursor.getString(2));
                register.setFullName(cursor.getString(3));
                register.setEmail(cursor.getString(4));
                register.setPhone(cursor.getString(5));
                register.setDepartment(cursor.getString(6));
                register.setDescription(cursor.getString(7));
                registerList.add(register);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return registerList;
    }
}