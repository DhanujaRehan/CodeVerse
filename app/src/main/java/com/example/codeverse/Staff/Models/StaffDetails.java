package com.example.codeverse.Staff.Models;

public class StaffDetails {
    private int id;
    private String staffId;
    private String name;
    private String department;
    private String position;
    private String email;
    private String phone;
    private String officeHours;
    private String officeLocation;
    private String specialization;
    private int yearsService;
    private String education;
    private String researchTags;
    private String researchDescription;
    private int publications;
    private String linkedin;
    private String researchGate;
    private String github;
    private String profileImagePath;
    private String createdDate;
    private String updatedDate;

    // Default constructor
    public StaffDetails() {
        // Initialize with default values
        this.yearsService = 0;
        this.publications = 0;
    }

    // Constructor with required fields
    public StaffDetails(String staffId, String name, String department, String position) {
        this();
        this.staffId = staffId;
        this.name = name;
        this.department = department;
        this.position = position;
    }

    // Full constructor
    public StaffDetails(String staffId, String name, String department, String position,
                        String email, String phone, String officeHours, String officeLocation,
                        String specialization, int yearsService, String education) {
        this(staffId, name, department, position);
        this.email = email;
        this.phone = phone;
        this.officeHours = officeHours;
        this.officeLocation = officeLocation;
        this.specialization = specialization;
        this.yearsService = yearsService;
        this.education = education;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOfficeHours() {
        return officeHours;
    }

    public void setOfficeHours(String officeHours) {
        this.officeHours = officeHours;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public int getYearsService() {
        return yearsService;
    }

    public void setYearsService(int yearsService) {
        this.yearsService = yearsService;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getResearchTags() {
        return researchTags;
    }

    public void setResearchTags(String researchTags) {
        this.researchTags = researchTags;
    }

    public String getResearchDescription() {
        return researchDescription;
    }

    public void setResearchDescription(String researchDescription) {
        this.researchDescription = researchDescription;
    }

    public int getPublications() {
        return publications;
    }

    public void setPublications(int publications) {
        this.publications = publications;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getResearchGate() {
        return researchGate;
    }

    public void setResearchGate(String researchGate) {
        this.researchGate = researchGate;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    // Helper methods
    public String getDisplayName() {
        return name != null ? name : "Unknown";
    }

    public String getFormattedYearsService() {
        return yearsService + " years";
    }

    public String getFormattedPublications() {
        return publications + " publications";
    }

    // Validation methods
    public boolean isValidEmail() {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidPhone() {
        return phone != null && !phone.trim().isEmpty();
    }

    public boolean hasRequiredFields() {
        return staffId != null && !staffId.trim().isEmpty() &&
                name != null && !name.trim().isEmpty() &&
                department != null && !department.trim().isEmpty() &&
                position != null && !position.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "StaffDetails{" +
                "id=" + id +
                ", staffId='" + staffId + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StaffDetails that = (StaffDetails) o;

        return staffId != null ? staffId.equals(that.staffId) : that.staffId == null;
    }

    @Override
    public int hashCode() {
        return staffId != null ? staffId.hashCode() : 0;
    }
}