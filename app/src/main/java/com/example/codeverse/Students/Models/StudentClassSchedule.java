package com.example.codeverse.Students.Models;

public class StudentClassSchedule {
    private int id;
    private String subjectName;
    private String moduleNumber;
    private String lecturerName;
    private String classroom;
    private String startTime;
    private String endTime;
    private String amPm;
    private String status;
    private String date;

    public StudentClassSchedule() {
    }

    public StudentClassSchedule(String subjectName, String moduleNumber, String lecturerName,
                                String classroom, String startTime, String endTime, String amPm,
                                String status, String date) {
        this.subjectName = subjectName;
        this.moduleNumber = moduleNumber;
        this.lecturerName = lecturerName;
        this.classroom = classroom;
        this.startTime = startTime;
        this.endTime = endTime;
        this.amPm = amPm;
        this.status = status;
        this.date = date;
    }

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

    public String getModuleNumber() {
        return moduleNumber;
    }

    public void setModuleNumber(String moduleNumber) {
        this.moduleNumber = moduleNumber;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
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

    public String getAmPm() {
        return amPm;
    }

    public void setAmPm(String amPm) {
        this.amPm = amPm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}