package com.example.codeverse.Admin.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.codeverse.Admin.models.Notification;
import com.example.codeverse.Admin.models.NotificationHistory;

public class NotificationDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notification_database";
    private static final int DATABASE_VERSION = 1;

    // Notifications table
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

    // History table
    private static final String TABLE_HISTORY = "notification_history";
    private static final String COLUMN_HISTORY_ID = "history_id";
    private static final String COLUMN_NOTIFICATION_ID = "notification_id";
    private static final String COLUMN_ACTION = "action";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_DETAILS = "details";

    // Create table queries
    private static final String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_MESSAGE + " TEXT,"
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
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    // CRUD Operations for Notifications
    public long insertNotification(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
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
        values.put(COLUMN_CREATED_DATE, notification.getCreatedDate());
        values.put(COLUMN_CREATED_TIME, notification.getCreatedTime());
        values.put(COLUMN_ATTACHMENT_PATH, notification.getAttachmentPath());
        values.put(COLUMN_ATTACHMENT_NAME, notification.getAttachmentName());

        long id = db.insert(TABLE_NOTIFICATIONS, null, values);
        db.close();
        return id;
    }

    public List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATIONS + " ORDER BY " + COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
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

                notifications.add(notification);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notifications;
    }

    public List<Notification> getNotificationsByStatus(String status) {
        List<Notification> notifications = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE " + COLUMN_STATUS + " = ? ORDER BY " + COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{status});

        if (cursor.moveToFirst()) {
            do {
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

                notifications.add(notification);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notifications;
    }

    public Notification getNotificationById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTIFICATIONS, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
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

            cursor.close();
            db.close();
            return notification;
        }

        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    public int updateNotification(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
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

        int result = db.update(TABLE_NOTIFICATIONS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(notification.getId())});
        db.close();
        return result;
    }

    public void deleteNotification(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // First delete related history records
        db.delete(TABLE_HISTORY, COLUMN_NOTIFICATION_ID + " = ?", new String[]{String.valueOf(id)});
        // Then delete the notification
        db.delete(TABLE_NOTIFICATIONS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // History operations
    public long insertHistory(NotificationHistory history) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NOTIFICATION_ID, history.getNotificationId());
        values.put(COLUMN_ACTION, history.getAction());
        values.put(COLUMN_TIMESTAMP, history.getTimestamp());
        values.put(COLUMN_DETAILS, history.getDetails());

        long id = db.insert(TABLE_HISTORY, null, values);
        db.close();
        return id;
    }

    public List<NotificationHistory> getHistoryByNotificationId(int notificationId) {
        List<NotificationHistory> historyList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY + " WHERE " + COLUMN_NOTIFICATION_ID + " = ? ORDER BY " + COLUMN_HISTORY_ID + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(notificationId)});

        if (cursor.moveToFirst()) {
            do {
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

                historyList.add(history);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return historyList;
    }

    public List<NotificationHistory> getAllHistory() {
        List<NotificationHistory> historyList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + COLUMN_HISTORY_ID + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
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

                historyList.add(history);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return historyList;
    }

    // Utility methods
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
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_NOTIFICATIONS + " WHERE " + COLUMN_STATUS + " = ?";
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

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }
}