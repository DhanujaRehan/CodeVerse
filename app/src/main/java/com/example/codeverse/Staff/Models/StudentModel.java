package com.example.codeverse.Staff.Models;


public class StudentModel {
    private String studentId;
    private String name;
    private String department;
    private String year;
    private String badge;
    private int imageResource;


    public StudentModel(String studentId, String name, String department,
                        String year, String badge, int imageResource) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.year = year;
        this.badge = badge;
        this.imageResource = imageResource;
    }


    public String getStudentId() {
        return studentId;
    }


    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDepartment() {
        return department;
    }


    public void setDepartment(String department) {
        this.department = department;
    }


    public String getYear() {
        return year;
    }


    public void setYear(String year) {
        this.year = year;
    }


    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }


    public int getImageResource() {
        return imageResource;
    }


    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}