package com.example.codeverse.Admin;

public class NotificationHistory {
    private int id;
    private int notificationId;
    private String title;
    private String message;
    private String priority;
    private String category;
    private String recipients;
    private String status;
    private long sentAt;
    private int deliveredCount;
    private int readCount;
    private boolean pushSent;
    private boolean emailSent;
    private boolean smsSent;

    // Constructors
    public NotificationHistory() {
    }

    public NotificationHistory(int notificationId, String title, String message,
                               String priority, String category, String recipients, String status) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.priority = priority;
        this.category = category;
        this.recipients = recipients;
        this.status = status;
        this.sentAt = System.currentTimeMillis();
    }

    // Constructor from Notification object
    public NotificationHistory(com.example.codeverse.Admin.Notification notification) {
        this.notificationId = notification.getId();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.priority = notification.getPriority();
        this.category = notification.getCategory();
        this.recipients = notification.getRecipients();
        this.status = notification.getStatus();
        this.sentAt = System.currentTimeMillis();
        this.pushSent = notification.isPushEnabled();
        this.emailSent = notification.isEmailEnabled();
        this.smsSent = notification.isSmsEnabled();
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

    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }

    public int getDeliveredCount() {
        return deliveredCount;
    }

    public void setDeliveredCount(int deliveredCount) {
        this.deliveredCount = deliveredCount;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public boolean isPushSent() {
        return pushSent;
    }

    public void setPushSent(boolean pushSent) {
        this.pushSent = pushSent;
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public boolean isSmsSent() {
        return smsSent;
    }

    public void setSmsSent(boolean smsSent) {
        this.smsSent = smsSent;
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
            case "sent":
                return "Delivered";
            case "failed":
                return "Failed";
            case "pending":
                return "Pending";
            default:
                return status;
        }
    }

    public double getReadPercentage() {
        if (deliveredCount == 0) return 0.0;
        return (double) readCount / deliveredCount * 100;
    }

    public String getDeliveryMethodsText() {
        StringBuilder methods = new StringBuilder();
        if (pushSent) methods.append("Push ");
        if (emailSent) methods.append("Email ");
        if (smsSent) methods.append("SMS ");
        return methods.toString().trim();
    }

    @Override
    public String toString() {
        return "NotificationHistory{" +
                "id=" + id +
                ", notificationId=" + notificationId +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", priority='" + priority + '\'' +
                ", category='" + category + '\'' +
                ", recipients='" + recipients + '\'' +
                ", status='" + status + '\'' +
                ", sentAt=" + sentAt +
                ", deliveredCount=" + deliveredCount +
                ", readCount=" + readCount +
                ", pushSent=" + pushSent +
                ", emailSent=" + emailSent +
                ", smsSent=" + smsSent +
                '}';
    }
}