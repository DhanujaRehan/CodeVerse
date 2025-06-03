package com.example.codeverse.Staff.Models;


public class Assignment {
    private int id;
    private String module;
    private String title;
    private String type;
    private String description;
    private String releaseDate;
    private String dueDate;
    private int weighting;
    private String filePath;
    private String targetGroups;
    private boolean notifyStudents;
    private boolean enableReminders;
    private String status;
    private long createdAt;


    public Assignment() {
        this.createdAt = System.currentTimeMillis();
        this.status = "Draft";
        this.notifyStudents = true;
        this.enableReminders = true;
    }


    public Assignment(int id, String module, String title, String type, String description,
                      String releaseDate, String dueDate, int weighting, String filePath,
                      String targetGroups, boolean notifyStudents, boolean enableReminders,
                      String status, long createdAt) {
        this.id = id;
        this.module = module;
        this.title = title;
        this.type = type;
        this.description = description;
        this.releaseDate = releaseDate;
        this.dueDate = dueDate;
        this.weighting = weighting;
        this.filePath = filePath;
        this.targetGroups = targetGroups;
        this.notifyStudents = notifyStudents;
        this.enableReminders = enableReminders;
        this.status = status;
        this.createdAt = createdAt;
    }


    public int getId() { return id; }
    public String getModule() { return module; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getReleaseDate() { return releaseDate; }
    public String getDueDate() { return dueDate; }
    public int getWeighting() { return weighting; }
    public String getFilePath() { return filePath; }
    public String getTargetGroups() { return targetGroups; }
    public boolean isNotifyStudents() { return notifyStudents; }
    public boolean isEnableReminders() { return enableReminders; }
    public String getStatus() { return status; }
    public long getCreatedAt() { return createdAt; }


    public void setId(int id) { this.id = id; }
    public void setModule(String module) { this.module = module; }
    public void setTitle(String title) { this.title = title; }
    public void setType(String type) { this.type = type; }
    public void setDescription(String description) { this.description = description; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setWeighting(int weighting) { this.weighting = weighting; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setTargetGroups(String targetGroups) { this.targetGroups = targetGroups; }
    public void setNotifyStudents(boolean notifyStudents) { this.notifyStudents = notifyStudents; }
    public void setEnableReminders(boolean enableReminders) { this.enableReminders = enableReminders; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", module='" + module + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}