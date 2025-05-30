package com.example.codeverse;

public class ScheduleClassModel {
    private long id;
    private String subjectName;
    private String moduleNumber;
    private String lecturerName;
    private String classroom;
    private String startTime;
    private String endTime;
    private String amPm;
    private boolean isStudentSchedule;
    private String status;

    public ScheduleClassModel() {
    }

    public ScheduleClassModel(String subjectName, String moduleNumber, String lecturerName,
                              String classroom, String startTime, String endTime, String amPm,
                              boolean isStudentSchedule, String status) {
        this.subjectName = subjectName;
        this.moduleNumber = moduleNumber;
        this.lecturerName = lecturerName;
        this.classroom = classroom;
        this.startTime = startTime;
        this.endTime = endTime;
        this.amPm = amPm;
        this.isStudentSchedule = isStudentSchedule;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public boolean isStudentSchedule() {
        return isStudentSchedule;
    }

    public void setStudentSchedule(boolean studentSchedule) {
        isStudentSchedule = studentSchedule;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}