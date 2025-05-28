package com.example.codeverse.Admin.Models;

public class Notification {
    private int id;
    private String title;
    private String message;
    private String priority;
    private String category;
    private String recipients;
    private String status; // draft, scheduled, sent
    private boolean pushEnabled;
    private boolean emailEnabled;
    private boolean smsEnabled;
    private long scheduledTime;
    private long createdAt;
    private String attachmentPath;

    // Constructors
    public Notification() {
    }

    public Notification(String title, String message, String priority, String category,
                        String recipients, String status) {
        this.title = title;
        this.message = message;
        this.priority = priority;
        this.category = category;
        this.recipients = recipients;
        this.status = status;
        this.createdAt = System.currentTimeMillis();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPushEnabled() {
        return pushEnabled;
    }

    public void setPushEnabled(boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public boolean isSmsEnabled() {
        return smsEnabled;
    }

    public void setSmsEnabled(boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public long getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    // Utility methods
    public String getPriorityColor() {
        switch (priority.toLowerCase()) {
            case "urgent":
                return "#F44336"; // Red
            case "high":
                return "#FF9800"; // Orange
            case "medium":
                return "#2196F3"; // Blue
            case "low":
                return "#4CAF50"; // Green
            default:
                return "#757575"; // Grey
        }
    }

    public String getStatusDisplayText() {
        switch (status.toLowerCase()) {
            case "draft":
                return "Draft";
            case "scheduled":
                return "Scheduled";
            case "sent":
                return "Sent";
            default:
                return "Unknown";
        }
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
                ", pushEnabled=" + pushEnabled +
                ", emailEnabled=" + emailEnabled +
                ", smsEnabled=" + smsEnabled +
                ", scheduledTime=" + scheduledTime +
                ", createdAt=" + createdAt +
                ", attachmentPath='" + attachmentPath + '\'' +
                '}';
    }
}