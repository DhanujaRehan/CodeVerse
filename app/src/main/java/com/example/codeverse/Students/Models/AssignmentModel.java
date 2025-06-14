package com.example.codeverse.Students.Models;

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


    private String studentId;
    private String studentName;
    private String batch;
    private String programme;
    private String module;
    private double marks;
    private String grade;
    private String feedback;
    private boolean isGraded;
    private long gradedDate;


    public AssignmentModel() {
        this.status = "uploaded";
        this.isGraded = false;
        this.marks = 0.0;
    }


    public AssignmentModel(String title, String subject, String description, String fileName,
                           String filePath, long uploadDate, long fileSize) {
        this.title = title;
        this.subject = subject;
        this.description = description;
        this.fileName = fileName;
        this.filePath = filePath;
        this.uploadDate = uploadDate;
        this.fileSize = fileSize;
        this.status = "uploaded";
        this.isGraded = false;
        this.marks = 0.0;
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

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public boolean isGraded() {
        return isGraded;
    }

    public void setGraded(boolean graded) {
        isGraded = graded;
    }

    public long getGradedDate() {
        return gradedDate;
    }

    public void setGradedDate(long gradedDate) {
        this.gradedDate = gradedDate;
    }


    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }


    public String getFormattedUploadDate() {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", java.util.Locale.getDefault());
        return dateFormat.format(new java.util.Date(uploadDate));
    }


    public boolean isPdfFile() {
        return fileName != null && fileName.toLowerCase().endsWith(".pdf");
    }


    public boolean isWordFile() {
        return fileName != null && (fileName.toLowerCase().endsWith(".doc") || fileName.toLowerCase().endsWith(".docx"));
    }


    public boolean isOverdue() {

        return false;
    }

    @Override
    public String toString() {
        return "AssignmentModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", subject='" + subject + '\'' +
                ", studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", batch='" + batch + '\'' +
                ", programme='" + programme + '\'' +
                ", module='" + module + '\'' +
                ", isGraded=" + isGraded +
                ", marks=" + marks +
                ", grade='" + grade + '\'' +
                '}';
    }
}