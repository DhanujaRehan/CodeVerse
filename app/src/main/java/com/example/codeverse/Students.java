package com.example.codeverse;

public class Students {
    private long id;

    private String fullName;
    private String universityId;
    private String nicNumber;
    private String dateOfBirth;
    private String gender;
    private String photoUri;


    private String faculty;
    private String department;
    private String batch;
    private String semester;
    private String enrollmentDate;



    private String createdAt;
    private String updatedAt;


    public Students() {
    }

    public Students(String fullName, String universityId, String nicNumber,
                   String dateOfBirth, String gender, String photoUri,
                   String faculty, String department, String batch,
                   String semester, String enrollmentDate) {
        this.fullName = fullName;
        this.universityId = universityId;
        this.nicNumber = nicNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.photoUri = photoUri;
        this.faculty = faculty;
        this.department = department;
        this.batch = batch;
        this.semester = semester;
        this.enrollmentDate = enrollmentDate;
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

    public String getUniversityId() {
        return universityId;
    }

    public void setUniversityId(String universityId) {
        this.universityId = universityId;
    }

    public String getNicNumber() {
        return nicNumber;
    }

    public void setNicNumber(String nicNumber) {
        this.nicNumber = nicNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
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

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", universityId='" + universityId + '\'' +
                ", nicNumber='" + nicNumber + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", photoUri='" + photoUri + '\'' +
                ", faculty='" + faculty + '\'' +
                ", department='" + department + '\'' +
                ", batch='" + batch + '\'' +
                ", semester='" + semester + '\'' +
                ", enrollmentDate='" + enrollmentDate + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}