package com.example.codeverse.Admin.Models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Notification {
    private int id;
    private String title;
    private String message;
    private String priority;
    private String category;
    private String recipients;
    private String scheduledDate;
    private String scheduledTime;
    private boolean isScheduled;
    private boolean isPushEnabled;
    private boolean isEmailEnabled;
    private boolean isSMSEnabled;
    private String status; // DRAFT, SENT, SCHEDULED
    private String createdDate;
    private String createdTime;
    private String attachmentPath;
    private String attachmentName;

    // Constructors
    public Notification() {
        this.createdDate = getCurrentDate();
        this.createdTime = getCurrentTime();
        this.status = "DRAFT";
        this.isPushEnabled = true;
        this.isEmailEnabled = false;
        this.isSMSEnabled = false;
        this.isScheduled = false;
    }

    public Notification(String title, String message, String priority, String category,
                        String recipients, String status) {
        this();
        this.title = title;
        this.message = message;
        this.priority = priority;
        this.category = category;
        this.recipients = recipients;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public boolean isPushEnabled() {
        return isPushEnabled;
    }

    public void setPushEnabled(boolean pushEnabled) {
        isPushEnabled = pushEnabled;
    }

    public boolean isEmailEnabled() {
        return isEmailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        isEmailEnabled = emailEnabled;
    }

    public boolean isSMSEnabled() {
        return isSMSEnabled;
    }

    public void setSMSEnabled(boolean SMSEnabled) {
        isSMSEnabled = SMSEnabled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    // Helper methods
    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", priority='" + priority + '\'' +
                ", category='" + category + '\'' +
                ", recipients='" + recipients + '\'' +
                ", status='" + status + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", createdTime='" + createdTime + '\'' +
                '}';
    }
}