package com.example.codeverse.Admin.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.codeverse.Admin.Models.Notification;
import com.example.codeverse.Admin.Models.NotificationHistory;

public class NotificationDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "NotificationDB";
    private static final String DATABASE_NAME = "notification_database";
    private static final int DATABASE_VERSION = 1;


    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_PRIORITY = "priority";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_RECIPIENTS = "recipients";
    private static final String COLUMN_SCHEDULED_DATE = "scheduled_date";
    private static final String COLUMN_SCHEDULED_TIME = "scheduled_time";
    private static final String COLUMN_IS_SCHEDULED = "is_scheduled";
    private static final String COLUMN_IS_PUSH_ENABLED = "is_push_enabled";
    private static final String COLUMN_IS_EMAIL_ENABLED = "is_email_enabled";
    private static final String COLUMN_IS_SMS_ENABLED = "is_sms_enabled";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_CREATED_DATE = "created_date";
    private static final String COLUMN_CREATED_TIME = "created_time";
    private static final String COLUMN_ATTACHMENT_PATH = "attachment_path";
    private static final String COLUMN_ATTACHMENT_NAME = "attachment_name";

    private static final String TABLE_HISTORY = "notification_history";
    private static final String COLUMN_HISTORY_ID = "history_id";
    private static final String COLUMN_NOTIFICATION_ID = "notification_id";
    private static final String COLUMN_ACTION = "notification_action";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_DETAILS = "details";

    // Create table queries
    private static final String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT NOT NULL,"
            + COLUMN_MESSAGE + " TEXT NOT NULL,"
            + COLUMN_PRIORITY + " TEXT,"
            + COLUMN_CATEGORY + " TEXT,"
            + COLUMN_RECIPIENTS + " TEXT,"
            + COLUMN_SCHEDULED_DATE + " TEXT,"
            + COLUMN_SCHEDULED_TIME + " TEXT,"
            + COLUMN_IS_SCHEDULED + " INTEGER DEFAULT 0,"
            + COLUMN_IS_PUSH_ENABLED + " INTEGER DEFAULT 1,"
            + COLUMN_IS_EMAIL_ENABLED + " INTEGER DEFAULT 0,"
            + COLUMN_IS_SMS_ENABLED + " INTEGER DEFAULT 0,"
            + COLUMN_STATUS + " TEXT DEFAULT 'DRAFT',"
            + COLUMN_CREATED_DATE + " TEXT,"
            + COLUMN_CREATED_TIME + " TEXT,"
            + COLUMN_ATTACHMENT_PATH + " TEXT,"
            + COLUMN_ATTACHMENT_NAME + " TEXT"
            + ")";

    private static final String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
            + COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NOTIFICATION_ID + " INTEGER,"
            + COLUMN_ACTION + " TEXT,"
            + COLUMN_TIMESTAMP + " TEXT,"
            + COLUMN_DETAILS + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_NOTIFICATION_ID + ") REFERENCES " + TABLE_NOTIFICATIONS + "(" + COLUMN_ID + ")"
            + ")";

    public NotificationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "Database helper initialized");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database tables");
        try {
            db.execSQL(CREATE_NOTIFICATIONS_TABLE);
            Log.d(TAG, "Notifications table created successfully");

            db.execSQL(CREATE_HISTORY_TABLE);
            Log.d(TAG, "History table created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating tables: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    public long insertNotification(Notification notification) {
        Log.d(TAG, "Attempting to insert notification: " + notification.getTitle());

        // Validate required fields
        if (notification.getTitle() == null || notification.getTitle().trim().isEmpty()) {
            Log.e(TAG, "Cannot insert notification: Title is null or empty");
            return -1;
        }

        if (notification.getMessage() == null || notification.getMessage().trim().isEmpty()) {
            Log.e(TAG, "Cannot insert notification: Message is null or empty");
            return -1;
        }

        SQLiteDatabase db = null;
        long id = -1;

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            // Set current date/time if not provided
            String currentDate = notification.getCreatedDate();
            String currentTime = notification.getCreatedTime();

            if (currentDate == null || currentDate.isEmpty()) {
                currentDate = getCurrentDate();
                notification.setCreatedDate(currentDate);
            }

            if (currentTime == null || currentTime.isEmpty()) {
                currentTime = getCurrentTime();
                notification.setCreatedTime(currentTime);
            }

            values.put(COLUMN_TITLE, notification.getTitle().trim());
            values.put(COLUMN_MESSAGE, notification.getMessage().trim());
            values.put(COLUMN_PRIORITY, notification.getPriority() != null ? notification.getPriority() : "MEDIUM");
            values.put(COLUMN_CATEGORY, notification.getCategory() != null ? notification.getCategory() : "GENERAL");
            values.put(COLUMN_RECIPIENTS, notification.getRecipients() != null ? notification.getRecipients() : "ALL");
            values.put(COLUMN_SCHEDULED_DATE, notification.getScheduledDate());
            values.put(COLUMN_SCHEDULED_TIME, notification.getScheduledTime());
            values.put(COLUMN_IS_SCHEDULED, notification.isScheduled() ? 1 : 0);
            values.put(COLUMN_IS_PUSH_ENABLED, notification.isPushEnabled() ? 1 : 0);
            values.put(COLUMN_IS_EMAIL_ENABLED, notification.isEmailEnabled() ? 1 : 0);
            values.put(COLUMN_IS_SMS_ENABLED, notification.isSMSEnabled() ? 1 : 0);
            values.put(COLUMN_STATUS, notification.getStatus() != null ? notification.getStatus() : "DRAFT");
            values.put(COLUMN_CREATED_DATE, currentDate);
            values.put(COLUMN_CREATED_TIME, currentTime);
            values.put(COLUMN_ATTACHMENT_PATH, notification.getAttachmentPath());
            values.put(COLUMN_ATTACHMENT_NAME, notification.getAttachmentName());

            id = db.insert(TABLE_NOTIFICATIONS, null, values);

            if (id != -1) {
                Log.d(TAG, "Notification inserted successfully with ID: " + id);
                notification.setId((int) id);

                // Insert history record
                insertHistoryRecord((int) id, "CREATED", "Notification created", currentDate + " " + currentTime);
            } else {
                Log.e(TAG, "Failed to insert notification");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error inserting notification: " + e.getMessage());
            e.printStackTrace();
            id = -1;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return id;
    }

    // Helper method to insert history records
    private void insertHistoryRecord(int notificationId, String action, String details, String timestamp) {
        try {
            NotificationHistory history = new NotificationHistory();
            history.setNotificationId(notificationId);
            history.setAction(action);
            history.setDetails(details);
            history.setTimestamp(timestamp);
            insertHistory(history);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting history record: " + e.getMessage());
        }
    }

    public List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATIONS + " ORDER BY " + COLUMN_ID + " DESC";
            db = this.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            Log.d(TAG, "Found " + cursor.getCount() + " notifications");

            if (cursor.moveToFirst()) {
                do {
                    Notification notification = createNotificationFromCursor(cursor);
                    if (notification != null) {
                        notifications.add(notification);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all notifications: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return notifications;
    }

    public List<Notification> getNotificationsByStatus(String status) {
        List<Notification> notifications = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE " + COLUMN_STATUS + " = ? ORDER BY " + COLUMN_ID + " DESC";
            db = this.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, new String[]{status});

            if (cursor.moveToFirst()) {
                do {
                    Notification notification = createNotificationFromCursor(cursor);
                    if (notification != null) {
                        notifications.add(notification);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting notifications by status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return notifications;
    }

    public Notification getNotificationById(int id) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Notification notification = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_NOTIFICATIONS, null, COLUMN_ID + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                notification = createNotificationFromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting notification by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return notification;
    }

    // Helper method to create Notification object from cursor
    private Notification createNotificationFromCursor(Cursor cursor) {
        try {
            Notification notification = new Notification();

            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            if (idIndex >= 0) notification.setId(cursor.getInt(idIndex));

            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            if (titleIndex >= 0) notification.setTitle(cursor.getString(titleIndex));

            int messageIndex = cursor.getColumnIndex(COLUMN_MESSAGE);
            if (messageIndex >= 0) notification.setMessage(cursor.getString(messageIndex));

            int priorityIndex = cursor.getColumnIndex(COLUMN_PRIORITY);
            if (priorityIndex >= 0) notification.setPriority(cursor.getString(priorityIndex));

            int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
            if (categoryIndex >= 0) notification.setCategory(cursor.getString(categoryIndex));

            int recipientsIndex = cursor.getColumnIndex(COLUMN_RECIPIENTS);
            if (recipientsIndex >= 0) notification.setRecipients(cursor.getString(recipientsIndex));

            int scheduledDateIndex = cursor.getColumnIndex(COLUMN_SCHEDULED_DATE);
            if (scheduledDateIndex >= 0) notification.setScheduledDate(cursor.getString(scheduledDateIndex));

            int scheduledTimeIndex = cursor.getColumnIndex(COLUMN_SCHEDULED_TIME);
            if (scheduledTimeIndex >= 0) notification.setScheduledTime(cursor.getString(scheduledTimeIndex));

            int isScheduledIndex = cursor.getColumnIndex(COLUMN_IS_SCHEDULED);
            if (isScheduledIndex >= 0) notification.setScheduled(cursor.getInt(isScheduledIndex) == 1);

            int isPushEnabledIndex = cursor.getColumnIndex(COLUMN_IS_PUSH_ENABLED);
            if (isPushEnabledIndex >= 0) notification.setPushEnabled(cursor.getInt(isPushEnabledIndex) == 1);

            int isEmailEnabledIndex = cursor.getColumnIndex(COLUMN_IS_EMAIL_ENABLED);
            if (isEmailEnabledIndex >= 0) notification.setEmailEnabled(cursor.getInt(isEmailEnabledIndex) == 1);

            int isSMSEnabledIndex = cursor.getColumnIndex(COLUMN_IS_SMS_ENABLED);
            if (isSMSEnabledIndex >= 0) notification.setSMSEnabled(cursor.getInt(isSMSEnabledIndex) == 1);

            int statusIndex = cursor.getColumnIndex(COLUMN_STATUS);
            if (statusIndex >= 0) notification.setStatus(cursor.getString(statusIndex));

            int createdDateIndex = cursor.getColumnIndex(COLUMN_CREATED_DATE);
            if (createdDateIndex >= 0) notification.setCreatedDate(cursor.getString(createdDateIndex));

            int createdTimeIndex = cursor.getColumnIndex(COLUMN_CREATED_TIME);
            if (createdTimeIndex >= 0) notification.setCreatedTime(cursor.getString(createdTimeIndex));

            int attachmentPathIndex = cursor.getColumnIndex(COLUMN_ATTACHMENT_PATH);
            if (attachmentPathIndex >= 0) notification.setAttachmentPath(cursor.getString(attachmentPathIndex));

            int attachmentNameIndex = cursor.getColumnIndex(COLUMN_ATTACHMENT_NAME);
            if (attachmentNameIndex >= 0) notification.setAttachmentName(cursor.getString(attachmentNameIndex));

            return notification;
        } catch (Exception e) {
            Log.e(TAG, "Error creating notification from cursor: " + e.getMessage());
            return null;
        }
    }

    public int updateNotification(Notification notification) {
        Log.d(TAG, "Updating notification with ID: " + notification.getId());

        SQLiteDatabase db = null;
        int result = 0;

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_TITLE, notification.getTitle());
            values.put(COLUMN_MESSAGE, notification.getMessage());
            values.put(COLUMN_PRIORITY, notification.getPriority());
            values.put(COLUMN_CATEGORY, notification.getCategory());
            values.put(COLUMN_RECIPIENTS, notification.getRecipients());
            values.put(COLUMN_SCHEDULED_DATE, notification.getScheduledDate());
            values.put(COLUMN_SCHEDULED_TIME, notification.getScheduledTime());
            values.put(COLUMN_IS_SCHEDULED, notification.isScheduled() ? 1 : 0);
            values.put(COLUMN_IS_PUSH_ENABLED, notification.isPushEnabled() ? 1 : 0);
            values.put(COLUMN_IS_EMAIL_ENABLED, notification.isEmailEnabled() ? 1 : 0);
            values.put(COLUMN_IS_SMS_ENABLED, notification.isSMSEnabled() ? 1 : 0);
            values.put(COLUMN_STATUS, notification.getStatus());
            values.put(COLUMN_ATTACHMENT_PATH, notification.getAttachmentPath());
            values.put(COLUMN_ATTACHMENT_NAME, notification.getAttachmentName());

            result = db.update(TABLE_NOTIFICATIONS, values, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(notification.getId())});

            if (result > 0) {
                Log.d(TAG, "Notification updated successfully");
                insertHistoryRecord(notification.getId(), "UPDATED", "Notification updated", getCurrentDate() + " " + getCurrentTime());
            } else {
                Log.w(TAG, "No rows updated for notification ID: " + notification.getId());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error updating notification: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (db != null) db.close();
        }

        return result;
    }

    public void deleteNotification(int id) {
        Log.d(TAG, "Deleting notification with ID: " + id);

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();

            // First delete related history records
            int historyDeleted = db.delete(TABLE_HISTORY, COLUMN_NOTIFICATION_ID + " = ?", new String[]{String.valueOf(id)});
            Log.d(TAG, "Deleted " + historyDeleted + " history records");

            // Then delete the notification
            int notificationDeleted = db.delete(TABLE_NOTIFICATIONS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            Log.d(TAG, "Deleted " + notificationDeleted + " notification");

        } catch (Exception e) {
            Log.e(TAG, "Error deleting notification: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (db != null) db.close();
        }
    }

    // History operations
    public long insertHistory(NotificationHistory history) {
        SQLiteDatabase db = null;
        long id = -1;

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_NOTIFICATION_ID, history.getNotificationId());
            values.put(COLUMN_ACTION, history.getAction());
            values.put(COLUMN_TIMESTAMP, history.getTimestamp());
            values.put(COLUMN_DETAILS, history.getDetails());

            id = db.insert(TABLE_HISTORY, null, values);

        } catch (Exception e) {
            Log.e(TAG, "Error inserting history: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (db != null) db.close();
        }

        return id;
    }

    public List<NotificationHistory> getHistoryByNotificationId(int notificationId) {
        List<NotificationHistory> historyList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            String selectQuery = "SELECT * FROM " + TABLE_HISTORY + " WHERE " + COLUMN_NOTIFICATION_ID + " = ? ORDER BY " + COLUMN_HISTORY_ID + " DESC";
            db = this.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(notificationId)});

            if (cursor.moveToFirst()) {
                do {
                    NotificationHistory history = createHistoryFromCursor(cursor);
                    if (history != null) {
                        historyList.add(history);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting history by notification ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return historyList;
    }

    public List<NotificationHistory> getAllHistory() {
        List<NotificationHistory> historyList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            String selectQuery = "SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + COLUMN_HISTORY_ID + " DESC";
            db = this.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    NotificationHistory history = createHistoryFromCursor(cursor);
                    if (history != null) {
                        historyList.add(history);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all history: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return historyList;
    }

    // Helper method to create NotificationHistory object from cursor
    private NotificationHistory createHistoryFromCursor(Cursor cursor) {
        try {
            NotificationHistory history = new NotificationHistory();

            int historyIdIndex = cursor.getColumnIndex(COLUMN_HISTORY_ID);
            if (historyIdIndex >= 0) history.setId(cursor.getInt(historyIdIndex));

            int notificationIdIndex = cursor.getColumnIndex(COLUMN_NOTIFICATION_ID);
            if (notificationIdIndex >= 0) history.setNotificationId(cursor.getInt(notificationIdIndex));

            int actionIndex = cursor.getColumnIndex(COLUMN_ACTION);
            if (actionIndex >= 0) history.setAction(cursor.getString(actionIndex));

            int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);
            if (timestampIndex >= 0) history.setTimestamp(cursor.getString(timestampIndex));

            int detailsIndex = cursor.getColumnIndex(COLUMN_DETAILS);
            if (detailsIndex >= 0) history.setDetails(cursor.getString(detailsIndex));

            return history;
        } catch (Exception e) {
            Log.e(TAG, "Error creating history from cursor: " + e.getMessage());
            return null;
        }
    }

    // Utility methods
    public int getNotificationCount() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        int count = 0;

        try {
            String countQuery = "SELECT COUNT(*) FROM " + TABLE_NOTIFICATIONS;
            db = this.getReadableDatabase();
            cursor = db.rawQuery(countQuery, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting notification count: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return count;
    }

    public int getNotificationCountByStatus(String status) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        int count = 0;

        try {
            String countQuery = "SELECT COUNT(*) FROM " + TABLE_NOTIFICATIONS + " WHERE " + COLUMN_STATUS + " = ?";
            db = this.getReadableDatabase();
            cursor = db.rawQuery(countQuery, new String[]{status});

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting notification count by status: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return count;
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    // Debug method to check if database is accessible
    public boolean testDatabaseConnection() {
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Log.d(TAG, "Database connection test successful");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Database connection test failed: " + e.getMessage());
            return false;
        } finally {
            if (db != null) db.close();
        }
    }
}