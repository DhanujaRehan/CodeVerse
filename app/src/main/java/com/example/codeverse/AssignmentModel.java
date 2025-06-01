package com.example.codeverse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AssignmentModel {

    private int id;
    private String title;
    private String subject;
    private String description;
    private String fileName;
    private String filePath;
    private long uploadDate;
    private long fileSize;
    private String status;

    public AssignmentModel() {
        this.uploadDate = System.currentTimeMillis();
        this.status = "uploaded";
        this.fileSize = 0;
    }

    public AssignmentModel(String title, String subject, String description, String fileName, String filePath) {
        this.title = title;
        this.subject = subject;
        this.description = description;
        this.fileName = fileName;
        this.filePath = filePath;
        this.uploadDate = System.currentTimeMillis();
        this.status = "uploaded";
        this.fileSize = 0;
    }

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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(long uploadDate) {
        this.uploadDate = uploadDate;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormattedUploadDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(uploadDate));
    }

    public String getFormattedFileSize() {
        if (fileSize <= 0) return "Unknown size";

        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double size = fileSize;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format(Locale.getDefault(), "%.1f %s", size, units[unitIndex]);
    }

    public String getFileExtension() {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
        }
        return "FILE";
    }

    public boolean isPdfFile() {
        return fileName != null && fileName.toLowerCase().endsWith(".pdf");
    }

    public boolean isWordFile() {
        return fileName != null && (fileName.toLowerCase().endsWith(".doc") || fileName.toLowerCase().endsWith(".docx"));
    }

    @Override
    public String toString() {
        return "AssignmentModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", subject='" + subject + '\'' +
                ", fileName='" + fileName + '\'' +
                ", uploadDate=" + uploadDate +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AssignmentModel that = (AssignmentModel) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}