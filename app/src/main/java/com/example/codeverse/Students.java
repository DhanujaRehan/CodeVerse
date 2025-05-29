package com.example.codeverse;

public class Students {
    private long id;
    // Basic Information
    private String fullName;
    private String universityId;
    private String nicNumber;
    private String dateOfBirth;
    private String gender;
    private String photoUri;

    // Academic Information
    private String faculty;
    private String department;
    private String batch;
    private String semester;
    private String enrollmentDate;

    // Contact Information
    private String mobileNumber;
    private String alternateNumber;
    private String permanentAddress;
    private String city;
    private String province;
    private String postalCode;

    // Emergency Contact Information
    private String emergencyName;
    private String emergencyRelationship;
    private String emergencyNumber;

    // Timestamps
    private String createdAt;
    private String updatedAt;

    // Default constructor
    public Students() {
    }

    // Constructor with all parameters
    public Students(String fullName, String universityId, String nicNumber,
                   String dateOfBirth, String gender, String photoUri,
                   String faculty, String department, String batch,
                   String semester, String enrollmentDate,
                   String mobileNumber, String alternateNumber, String permanentAddress,
                   String city, String province, String postalCode,
                   String emergencyName, String emergencyRelationship, String emergencyNumber) {
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
        this.mobileNumber = mobileNumber;
        this.alternateNumber = alternateNumber;
        this.permanentAddress = permanentAddress;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.emergencyName = emergencyName;
        this.emergencyRelationship = emergencyRelationship;
        this.emergencyNumber = emergencyNumber;
    }

    // Getters and Setters
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

    // Academic Information Getters and Setters
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

    // Contact Information Getters and Setters
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAlternateNumber() {
        return alternateNumber;
    }

    public void setAlternateNumber(String alternateNumber) {
        this.alternateNumber = alternateNumber;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    // Emergency Contact Information Getters and Setters
    public String getEmergencyName() {
        return emergencyName;
    }

    public void setEmergencyName(String emergencyName) {
        this.emergencyName = emergencyName;
    }

    public String getEmergencyRelationship() {
        return emergencyRelationship;
    }

    public void setEmergencyRelationship(String emergencyRelationship) {
        this.emergencyRelationship = emergencyRelationship;
    }

    public String getEmergencyNumber() {
        return emergencyNumber;
    }

    public void setEmergencyNumber(String emergencyNumber) {
        this.emergencyNumber = emergencyNumber;
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
                ", mobileNumber='" + mobileNumber + '\'' +
                ", alternateNumber='" + alternateNumber + '\'' +
                ", permanentAddress='" + permanentAddress + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", emergencyName='" + emergencyName + '\'' +
                ", emergencyRelationship='" + emergencyRelationship + '\'' +
                ", emergencyNumber='" + emergencyNumber + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}