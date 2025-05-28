package com.example.codeverse.Admin.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationHistory {
    private int id;
    private int notificationId;
    private String action; // CREATED, SENT, SCHEDULED, EDITED, DELETED
    private String timestamp;
    private String details;

    // Constructors
    public NotificationHistory() {
        this.timestamp = getCurrentTimestamp();
    }

    public NotificationHistory(int notificationId, String action, String details) {
        this();
        this.notificationId = notificationId;
        this.action = action;
        this.details = details;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    // Helper method
    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    @Override
    public String toString() {
        return "NotificationHistory{" +
                "id=" + id +
                ", notificationId=" + notificationId +
                ", action='" + action + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}