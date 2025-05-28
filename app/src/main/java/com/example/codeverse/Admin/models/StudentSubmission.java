package com.example.codeverse.Admin.models;

public class StudentSubmission {
    private String studentId;
    private String studentName;
    private String submissionDate;
    private String status;

    public StudentSubmission(String studentId, String studentName, String submissionDate, String status) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.submissionDate = submissionDate;
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

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}