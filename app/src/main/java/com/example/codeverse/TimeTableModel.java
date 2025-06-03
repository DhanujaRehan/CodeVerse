package com.example.codeverse;

public class TimeTableModel {
    private int id;
    private String subjectName;
    private String teacherName;
    private String classroom;
    private String date;
    private String startTime;
    private String endTime;
    private String pdfUrl;
    private String status;

    // Default constructor
    public TimeTableModel() {
    }

    // Constructor with parameters
    public TimeTableModel(int id, String subjectName, String teacherName, String classroom,
                          String date, String startTime, String endTime, String pdfUrl, String status) {
        this.id = id;
        this.subjectName = subjectName;
        this.teacherName = teacherName;
        this.classroom = classroom;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pdfUrl = pdfUrl;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TimeTableModel{" +
                "id=" + id +
                ", subjectName='" + subjectName + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", classroom='" + classroom + '\'' +
                ", date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", pdfUrl='" + pdfUrl + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}