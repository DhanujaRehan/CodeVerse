package com.example.codeverse.Admin.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.codeverse.Admin.Models.Notification;
import com.example.codeverse.Admin.Models.NotificationHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notifications.db";
    private static final int DATABASE_VERSION = 4;


    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_RECIPIENTS = "recipients";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_PUSH_ENABLED = "push_enabled";
    public static final String COLUMN_EMAIL_ENABLED = "email_enabled";
    public static final String COLUMN_SMS_ENABLED = "sms_enabled";
    public static final String COLUMN_SCHEDULED_TIME = "scheduled_time";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_ATTACHMENT_PATH = "attachment_path";


    public static final String TABLE_NOTIFICATION_HISTORY = "notification_history";
    public static final String COLUMN_NOTIFICATION_ID = "notification_id";
    public static final String COLUMN_NOTIFICATION_ACTION = "notification_action";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_DETAILS = "details";


    private static final String CREATE_NOTIFICATIONS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATIONS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_MESSAGE + " TEXT NOT NULL, " +
                    COLUMN_PRIORITY + " TEXT NOT NULL, " +
                    COLUMN_CATEGORY + " TEXT NOT NULL, " +
                    COLUMN_RECIPIENTS + " TEXT NOT NULL, " +
                    COLUMN_STATUS + " TEXT NOT NULL, " +
                    COLUMN_PUSH_ENABLED + " INTEGER DEFAULT 1, " +
                    COLUMN_EMAIL_ENABLED + " INTEGER DEFAULT 0, " +
                    COLUMN_SMS_ENABLED + " INTEGER DEFAULT 0, " +
                    COLUMN_SCHEDULED_TIME + " INTEGER, " +
                    COLUMN_CREATED_AT + " INTEGER NOT NULL, " +
                    COLUMN_ATTACHMENT_PATH + " TEXT" +
                    ")";


    private static final String CREATE_NOTIFICATION_HISTORY_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION_HISTORY + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOTIFICATION_ID + " INTEGER NOT NULL, " +
                    COLUMN_NOTIFICATION_ACTION + " TEXT NOT NULL, " +
                    COLUMN_TIMESTAMP + " TEXT NOT NULL, " +
                    COLUMN_DETAILS + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_NOTIFICATION_ID + ") REFERENCES " +
                    TABLE_NOTIFICATIONS + "(" + COLUMN_ID + ")" +
                    ")";

    public NotificationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);
        db.execSQL(CREATE_NOTIFICATION_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {

            db.execSQL(CREATE_NOTIFICATIONS_TABLE);


            if (tableExists(db, TABLE_NOTIFICATION_HISTORY)) {

                if (!columnExists(db, TABLE_NOTIFICATION_HISTORY, COLUMN_NOTIFICATION_ACTION)) {
                    try {

                        db.execSQL("ALTER TABLE " + TABLE_NOTIFICATION_HISTORY +
                                " ADD COLUMN " + COLUMN_NOTIFICATION_ACTION + " TEXT DEFAULT 'UNKNOWN'");
                        Log.d("DatabaseUpgrade", "Added notification_action column to existing table");
                    } catch (Exception e) {
                        Log.e("DatabaseUpgrade", "Failed to add column, recreating table: " + e.getMessage());

                        recreateNotificationHistoryTable(db);
                    }
                }
            } else {

                db.execSQL(CREATE_NOTIFICATION_HISTORY_TABLE);
            }
        }
    }


    private boolean tableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{tableName}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


    private boolean columnExists(SQLiteDatabase db, String tableName, String columnName) {
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        boolean exists = false;

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                if (name.equals(columnName)) {
                    exists = true;
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return exists;
    }


    private void recreateNotificationHistoryTable(SQLiteDatabase db) {
        try {

            List<String> backupData = new ArrayList<>();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATION_HISTORY, null);

            if (cursor.moveToFirst()) {
                do {
                    String notificationId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_ID));
                    String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                    String details = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DETAILS));

                    backupData.add(notificationId + "|" + timestamp + "|" + details);
                } while (cursor.moveToNext());
            }
            cursor.close();


            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION_HISTORY);
            db.execSQL(CREATE_NOTIFICATION_HISTORY_TABLE);


            for (String data : backupData) {
                String[] parts = data.split("\\|", 3);
                if (parts.length == 3) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_NOTIFICATION_ID, Integer.parseInt(parts[0]));
                    values.put(COLUMN_NOTIFICATION_ACTION, "SENT");
                    values.put(COLUMN_TIMESTAMP, parts[1]);
                    values.put(COLUMN_DETAILS, parts[2]);

                    db.insert(TABLE_NOTIFICATION_HISTORY, null, values);
                }
            }

            Log.d("DatabaseUpgrade", "Successfully recreated notification_history table with " + backupData.size() + " records");

        } catch (Exception e) {
            Log.e("DatabaseUpgrade", "Error recreating table: " + e.getMessage());

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION_HISTORY);
            db.execSQL(CREATE_NOTIFICATION_HISTORY_TABLE);
        }
    }


    public long insertNotification(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, notification.getTitle());
        values.put(COLUMN_MESSAGE, notification.getMessage());
        values.put(COLUMN_PRIORITY, notification.getPriority());
        values.put(COLUMN_CATEGORY, notification.getCategory());
        values.put(COLUMN_RECIPIENTS, notification.getRecipients());
        values.put(COLUMN_STATUS, notification.getStatus());
        values.put(COLUMN_PUSH_ENABLED, notification.isPushEnabled() ? 1 : 0);
        values.put(COLUMN_EMAIL_ENABLED, notification.isEmailEnabled() ? 1 : 0);
        values.put(COLUMN_SMS_ENABLED, notification.isSmsEnabled() ? 1 : 0);
        values.put(COLUMN_SCHEDULED_TIME, notification.getScheduledTime());
        values.put(COLUMN_CREATED_AT, notification.getCreatedAt());
        values.put(COLUMN_ATTACHMENT_PATH, notification.getAttachmentPath());

        long result = db.insert(TABLE_NOTIFICATIONS, null, values);
        db.close();
        return result;
    }


    public long insertHistory(int notificationId, String notification_action, String details) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NOTIFICATION_ID, notificationId);
        values.put(COLUMN_NOTIFICATION_ACTION, notification_action);
        values.put(COLUMN_TIMESTAMP, getCurrentTimestamp());
        values.put(COLUMN_DETAILS, details);

        long result = db.insert(TABLE_NOTIFICATION_HISTORY, null, values);
        db.close();
        return result;
    }

    public List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATIONS + " ORDER BY " + COLUMN_CREATED_AT + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                notification.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                notification.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE)));
                notification.setPriority(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)));
                notification.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
                notification.setRecipients(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPIENTS)));
                notification.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                notification.setPushEnabled(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PUSH_ENABLED)) == 1);
                notification.setEmailEnabled(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_ENABLED)) == 1);
                notification.setSmsEnabled(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SMS_ENABLED)) == 1);
                notification.setScheduledTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULED_TIME)));
                notification.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));

                int attachmentIndex = cursor.getColumnIndex(COLUMN_ATTACHMENT_PATH);
                if (attachmentIndex != -1 && !cursor.isNull(attachmentIndex)) {
                    notification.setAttachmentPath(cursor.getString(attachmentIndex));
                }

                notifications.add(notification);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notifications;
    }

    public List<NotificationHistory> getAllNotificationHistory() {
        List<NotificationHistory> historyList = new ArrayList<>();
        String selectQuery = "SELECT h.*, n." + COLUMN_TITLE + ", n." + COLUMN_MESSAGE +
                ", n." + COLUMN_PRIORITY + ", n." + COLUMN_CATEGORY + ", n." + COLUMN_RECIPIENTS +
                " FROM " + TABLE_NOTIFICATION_HISTORY + " h " +
                " LEFT JOIN " + TABLE_NOTIFICATIONS + " n ON h." + COLUMN_NOTIFICATION_ID + " = n." + COLUMN_ID +
                " ORDER BY h." + COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                NotificationHistory history = new NotificationHistory();
                history.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                history.setNotificationId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_ID)));


                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                if (titleIndex != -1 && !cursor.isNull(titleIndex)) {
                    history.setTitle(cursor.getString(titleIndex));
                }

                int messageIndex = cursor.getColumnIndex(COLUMN_MESSAGE);
                if (messageIndex != -1 && !cursor.isNull(messageIndex)) {
                    history.setMessage(cursor.getString(messageIndex));
                }

                int priorityIndex = cursor.getColumnIndex(COLUMN_PRIORITY);
                if (priorityIndex != -1 && !cursor.isNull(priorityIndex)) {
                    history.setPriority(cursor.getString(priorityIndex));
                }

                int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
                if (categoryIndex != -1 && !cursor.isNull(categoryIndex)) {
                    history.setCategory(cursor.getString(categoryIndex));
                }

                int recipientsIndex = cursor.getColumnIndex(COLUMN_RECIPIENTS);
                if (recipientsIndex != -1 && !cursor.isNull(recipientsIndex)) {
                    history.setRecipients(cursor.getString(recipientsIndex));
                }


                history.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_ACTION)));

                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                history.setSentAt(parseTimestamp(timestamp));

                String details = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DETAILS));
                parseDetailsForStats(history, details);

                historyList.add(history);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return historyList;
    }


    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private long parseTimestamp(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(timestamp);
            return date != null ? date.getTime() : System.currentTimeMillis();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }

    private void parseDetailsForStats(NotificationHistory history, String details) {
        history.setDeliveredCount(0);
        history.setReadCount(0);

        if (details != null && details.contains("delivered:")) {
            try {
                if (details.contains("delivered:")) {
                    String[] parts = details.split("delivered:");
                    if (parts.length > 1) {
                        String deliveredPart = parts[1].split(",")[0].trim();
                        history.setDeliveredCount(Integer.parseInt(deliveredPart));
                    }
                }

                if (details.contains("read:")) {
                    String[] parts = details.split("read:");
                    if (parts.length > 1) {
                        String readPart = parts[1].split(",")[0].trim();
                        history.setReadCount(Integer.parseInt(readPart));
                    }
                }
            } catch (NumberFormatException e) {

            }
        }
    }

    public int getNotificationCount() {
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_NOTIFICATIONS;
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

    public int getNotificationCountByStatus(String status) {
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_NOTIFICATIONS +
                " WHERE " + COLUMN_STATUS + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[]{status});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }
}