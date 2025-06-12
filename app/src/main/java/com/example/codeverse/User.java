package com.example.codeverse;

public class User {
    private long id;
    private String name;
    private String identifier;
    private String department;
    private String photoUri;
    private UserType userType;
    private String email;
    private String contactNumber;
    private String gender;
    private String dateOfBirth;

    public enum UserType {
        STUDENT,
        STAFF
    }

    public User() {}

    public User(long id, String name, String identifier, String department,
                String photoUri, UserType userType, String email,
                String contactNumber, String gender, String dateOfBirth) {
        this.id = id;
        this.name = name;
        this.identifier = identifier;
        this.department = department;
        this.photoUri = photoUri;
        this.userType = userType;
        this.email = email;
        this.contactNumber = contactNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
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
}