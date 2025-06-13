package com.example.codeverse.Staff.Models;

public class Assignment {
    private int id;
    private String module;
    private String title;
    private String description;
    private String releaseDate;
    private String dueDate;
    private int weighting;
    private String filePath;
    private String targetBatch;
    private String status;
    private String createdAt;

    public Assignment() {}

    public Assignment(String module, String title, String description, String releaseDate,
                      String dueDate, int weighting, String filePath, String targetBatch, String status) {
        this.module = module;
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.dueDate = dueDate;
        this.weighting = weighting;
        this.filePath = filePath;
        this.targetBatch = targetBatch;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public int getWeighting() { return weighting; }
    public void setWeighting(int weighting) { this.weighting = weighting; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getTargetBatch() { return targetBatch; }
    public void setTargetBatch(String targetBatch) { this.targetBatch = targetBatch; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}