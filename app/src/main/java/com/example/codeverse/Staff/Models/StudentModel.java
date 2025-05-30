package com.example.codeverse.Staff.Models;

/**
 * Model class representing a student
 */
public class StudentModel {
    private String studentId;
    private String name;
    private String department;
    private String year;
    private String badge;
    private int imageResource;

    /**
     * Constructor with all properties
     */
    public StudentModel(String studentId, String name, String department,
                        String year, String badge, int imageResource) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.year = year;
        this.badge = badge;
        this.imageResource = imageResource;
    }

    /**
     * Get the student ID
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * Set the student ID
     */
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    /**
     * Get the student name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the student name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Set the department
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Get the year
     */
    public String getYear() {
        return year;
    }

    /**
     * Set the year
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Get the badge
     */
    public String getBadge() {
        return badge;
    }

    /**
     * Set the badge
     */
    public void setBadge(String badge) {
        this.badge = badge;
    }

    /**
     * Get the image resource ID
     */
    public int getImageResource() {
        return imageResource;
    }

    /**
     * Set the image resource ID
     */
    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}