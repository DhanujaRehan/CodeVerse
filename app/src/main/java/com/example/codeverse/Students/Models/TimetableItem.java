package com.example.codeverse.Students.Models;

public class TimetableItem {
    public int id;
    public String weekTitle;
    public String startDate;
    public String endDate;
    public String pdfPath;
    public String fileSize;
    public String status;

    public TimetableItem() {
    }

    public TimetableItem(int id, String weekTitle, String startDate, String endDate, String pdfPath, String fileSize, String status) {
        this.id = id;
        this.weekTitle = weekTitle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pdfPath = pdfPath;
        this.fileSize = fileSize;
        this.status = status;
    }
}