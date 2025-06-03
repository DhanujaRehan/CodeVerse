package com.example.codeverse.Admin.Models;

import java.io.Serializable;

public class Staff implements Serializable {
    private long id;
    private String fullName;
    private String email;
    private String contactNumber;
    private String nicNumber;
    private String gender;
    private String dateOfBirth;
    private String photoUri;
    private String position;
    private String department;
    private String teachingSubject;
    private String programCoordinating;
    private String password;
    private String highestQualification;
    private String fieldOfStudy;
    private String university;
    private String graduationYear;
    private String experienceYears;
    private String createdAt;
    private String updatedAt;

    public Staff() {
    }

    public Staff(String fullName, String email, String contactNumber, String nicNumber,
                 String gender, String dateOfBirth, String photoUri, String position,
                 String department, String teachingSubject, String programCoordinating,
                 String password, String highestQualification, String fieldOfStudy,
                 String university, String graduationYear, String experienceYears) {
        this.fullName = fullName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.nicNumber = nicNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.photoUri = photoUri;
        this.position = position;
        this.department = department;
        this.teachingSubject = teachingSubject;
        this.programCoordinating = programCoordinating;
        this.password = password;
        this.highestQualification = highestQualification;
        this.fieldOfStudy = fieldOfStudy;
        this.university = university;
        this.graduationYear = graduationYear;
        this.experienceYears = experienceYears;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getNicNumber() {
        return nicNumber;
    }

    public void setNicNumber(String nicNumber) {
        this.nicNumber = nicNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTeachingSubject() {
        return teachingSubject;
    }

    public void setTeachingSubject(String teachingSubject) {
        this.teachingSubject = teachingSubject;
    }

    public String getProgramCoordinating() {
        return programCoordinating;
    }

    public void setProgramCoordinating(String programCoordinating) {
        this.programCoordinating = programCoordinating;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHighestQualification() {
        return highestQualification;
    }

    public void setHighestQualification(String highestQualification) {
        this.highestQualification = highestQualification;
    }

    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(String experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", nicNumber='" + nicNumber + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", photoUri='" + photoUri + '\'' +
                ", position='" + position + '\'' +
                ", department='" + department + '\'' +
                ", teachingSubject='" + teachingSubject + '\'' +
                ", programCoordinating='" + programCoordinating + '\'' +
                ", password='" + password + '\'' +
                ", highestQualification='" + highestQualification + '\'' +
                ", fieldOfStudy='" + fieldOfStudy + '\'' +
                ", university='" + university + '\'' +
                ", graduationYear='" + graduationYear + '\'' +
                ", experienceYears='" + experienceYears + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}