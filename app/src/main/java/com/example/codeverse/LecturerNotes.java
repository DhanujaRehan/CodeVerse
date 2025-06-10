package com.example.codeverse;

public class LecturerNotes {
    private int id;
    private String lecturerName;
    private String subject;
    private String lectureTitle;
    private String lectureDate;
    private String chapter;
    private String notesFilePath;
    private String description;
    private String dateCreated;
    private String timeCreated;

    public LecturerNotes() {
    }

    public LecturerNotes(String lecturerName, String subject, String lectureTitle,
                         String lectureDate, String chapter, String notesFilePath,
                         String description, String dateCreated, String timeCreated) {
        this.lecturerName = lecturerName;
        this.subject = subject;
        this.lectureTitle = lectureTitle;
        this.lectureDate = lectureDate;
        this.chapter = chapter;
        this.notesFilePath = notesFilePath;
        this.description = description;
        this.dateCreated = dateCreated;
        this.timeCreated = timeCreated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLectureTitle() {
        return lectureTitle;
    }

    public void setLectureTitle(String lectureTitle) {
        this.lectureTitle = lectureTitle;
    }

    public String getLectureDate() {
        return lectureDate;
    }

    public void setLectureDate(String lectureDate) {
        this.lectureDate = lectureDate;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getNotesFilePath() {
        return notesFilePath;
    }

    public void setNotesFilePath(String notesFilePath) {
        this.notesFilePath = notesFilePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }
}