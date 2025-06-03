package com.example.codeverse.Students.Models;

import java.io.Serializable;

public class Student implements Serializable {

    private long id;
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

    // Account Information
    private String email;
    private String username;
    private String password;
    private boolean termsAccepted;

    // Timestamps
    private String createdAt;
    private String updatedAt;

    // Default constructor
    public Student() {
        this.termsAccepted = false;
    }

    // Constructor with basic information
    public Student(String fullName, String universityId, String nicNumber,
                   String dateOfBirth, String gender) {
        this();
        this.fullName = fullName;
        this.universityId = universityId;
        this.nicNumber = nicNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUniversityId() { return universityId; }
    public void setUniversityId(String universityId) { this.universityId = universityId; }

    public String getNicNumber() { return nicNumber; }
    public void setNicNumber(String nicNumber) { this.nicNumber = nicNumber; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }

    // Academic Information
    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(String enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    // Contact Information
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getAlternateNumber() { return alternateNumber; }
    public void setAlternateNumber(String alternateNumber) { this.alternateNumber = alternateNumber; }

    public String getPermanentAddress() { return permanentAddress; }
    public void setPermanentAddress(String permanentAddress) { this.permanentAddress = permanentAddress; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    // Emergency Contact Information
    public String getEmergencyName() { return emergencyName; }
    public void setEmergencyName(String emergencyName) { this.emergencyName = emergencyName; }

    public String getEmergencyRelationship() { return emergencyRelationship; }
    public void setEmergencyRelationship(String emergencyRelationship) { this.emergencyRelationship = emergencyRelationship; }

    public String getEmergencyNumber() { return emergencyNumber; }
    public void setEmergencyNumber(String emergencyNumber) { this.emergencyNumber = emergencyNumber; }

    // Account Information
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isTermsAccepted() { return termsAccepted; }
    public void setTermsAccepted(boolean termsAccepted) { this.termsAccepted = termsAccepted; }

    // Timestamps
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Helper method to check if basic information is complete
    public boolean isBasicInfoComplete() {
        return fullName != null && !fullName.trim().isEmpty() &&
                universityId != null && !universityId.trim().isEmpty() &&
                nicNumber != null && !nicNumber.trim().isEmpty() &&
                dateOfBirth != null && !dateOfBirth.trim().isEmpty() &&
                gender != null && !gender.trim().isEmpty();
    }

    // Helper method to check if academic information is complete
    public boolean isAcademicInfoComplete() {
        return faculty != null && !faculty.trim().isEmpty() &&
                department != null && !department.trim().isEmpty() &&
                batch != null && !batch.trim().isEmpty() &&
                semester != null && !semester.trim().isEmpty();
    }

    // Helper method to check if contact information is complete
    public boolean isContactInfoComplete() {
        return mobileNumber != null && !mobileNumber.trim().isEmpty() &&
                permanentAddress != null && !permanentAddress.trim().isEmpty() &&
                city != null && !city.trim().isEmpty() &&
                province != null && !province.trim().isEmpty();
    }

    // Helper method to check if account information is complete
    public boolean isAccountInfoComplete() {
        return email != null && !email.trim().isEmpty() &&
                username != null && !username.trim().isEmpty() &&
                password != null && !password.trim().isEmpty() &&
                termsAccepted;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", universityId='" + universityId + '\'' +
                ", nicNumber='" + nicNumber + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}