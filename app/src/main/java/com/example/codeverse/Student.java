package com.example.codeverse;

public class Student {
    private int id;
    private String fullName;
    private String universityId;
    private String nicNumber;
    private String gender;
    private String dateOfBirth;
    private String photoPath;

    // Academic Details
    private String faculty;
    private String department;
    private String batch;
    private String semester;
    private String enrollmentDate;

    // Account Details
    private String email;
    private String username;
    private String password;

    // Contact Details
    private String mobileNumber;
    private String alternateNumber;
    private String permanentAddress;
    private String city;
    private String province;
    private String postalCode;

    // Emergency Contact
    private String emergencyContactName;
    private String emergencyRelationship;
    private String emergencyContactNumber;

    private String createdAt;
    private String updatedAt;

    // Default constructor
    public Student() {
    }

    // Full constructor
    public Student(String fullName, String universityId, String nicNumber, String gender,
                   String dateOfBirth, String photoPath, String faculty, String department,
                   String batch, String semester, String enrollmentDate, String email,
                   String username, String password, String mobileNumber, String alternateNumber,
                   String permanentAddress, String city, String province, String postalCode,
                   String emergencyContactName, String emergencyRelationship, String emergencyContactNumber) {
        this.fullName = fullName;
        this.universityId = universityId;
        this.nicNumber = nicNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.photoPath = photoPath;
        this.faculty = faculty;
        this.department = department;
        this.batch = batch;
        this.semester = semester;
        this.enrollmentDate = enrollmentDate;
        this.email = email;
        this.username = username;
        this.password = password;
        this.mobileNumber = mobileNumber;
        this.alternateNumber = alternateNumber;
        this.permanentAddress = permanentAddress;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.emergencyContactName = emergencyContactName;
        this.emergencyRelationship = emergencyRelationship;
        this.emergencyContactNumber = emergencyContactNumber;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUniversityId() { return universityId; }
    public void setUniversityId(String universityId) { this.universityId = universityId; }

    public String getNicNumber() { return nicNumber; }
    public void setNicNumber(String nicNumber) { this.nicNumber = nicNumber; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

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

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

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

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyRelationship() { return emergencyRelationship; }
    public void setEmergencyRelationship(String emergencyRelationship) { this.emergencyRelationship = emergencyRelationship; }

    public String getEmergencyContactNumber() { return emergencyContactNumber; }
    public void setEmergencyContactNumber(String emergencyContactNumber) { this.emergencyContactNumber = emergencyContactNumber; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", universityId='" + universityId + '\'' +
                ", email='" + email + '\'' +
                ", faculty='" + faculty + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}