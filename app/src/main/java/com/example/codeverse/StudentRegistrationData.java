package com.example.codeverse;

/**
 * Singleton class to hold student registration data across fragments
 * This class is used as a temporary data holder during the registration process
 */
public class StudentRegistrationData {

    private static StudentRegistrationData instance;

    // Basic Information
    private String fullName;
    private String universityId;
    private String nicNumber;
    private String gender;
    private String dateOfBirth;
    private String studentPhoto;

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
    private boolean termsAccepted;

    // Contact Details
    private String mobileNumber;
    private String alternateNumber;
    private String permanentAddress;
    private String city;
    private String province;
    private String postalCode;

    // Emergency Contact
    private String emergencyName;
    private String emergencyRelationship;
    private String emergencyNumber;

    // Private constructor to prevent direct instantiation
    private StudentRegistrationData() {
        resetData();
    }

    // Get singleton instance
    public static synchronized StudentRegistrationData getInstance() {
        if (instance == null) {
            instance = new StudentRegistrationData();
        }
        return instance;
    }

    // Reset all data
    public void resetData() {
        // Basic Information
        fullName = null;
        universityId = null;
        nicNumber = null;
        gender = null;
        dateOfBirth = null;
        studentPhoto = null;

        // Academic Details
        faculty = null;
        department = null;
        batch = null;
        semester = null;
        enrollmentDate = null;

        // Account Details
        email = null;
        username = null;
        password = null;
        termsAccepted = false;

        // Contact Details
        mobileNumber = null;
        alternateNumber = null;
        permanentAddress = null;
        city = null;
        province = null;
        postalCode = null;

        // Emergency Contact
        emergencyName = null;
        emergencyRelationship = null;
        emergencyNumber = null;
    }

    // Convert to StudentDetails object
    public Student toStudentDetails() {
        Student student = new Student();

        // Basic Information
        student.setFullName(fullName);
        student.setUniversityId(universityId);
        student.setNicNumber(nicNumber);
        student.setGender(gender);
        student.setDateOfBirth(dateOfBirth);
        student.setPhotoUri(studentPhoto);

        // Academic Details
        student.setFaculty(faculty);
        student.setDepartment(department);
        student.setBatch(batch);
        student.setSemester(semester);
        student.setEnrollmentDate(enrollmentDate);

        // Account Details
        student.setEmail(email);
        student.setUsername(username);
        student.setPassword(password);
        student.setTermsAccepted(termsAccepted);

        // Contact Details
        student.setMobileNumber(mobileNumber);
        student.setAlternateNumber(alternateNumber);
        student.setPermanentAddress(permanentAddress);
        student.setCity(city);
        student.setProvince(province);
        student.setPostalCode(postalCode);

        // Emergency Contact
        student.setEmergencyName(emergencyName);
        student.setEmergencyRelationship(emergencyRelationship);
        student.setEmergencyNumber(emergencyNumber);

        return student;
    }

    // Load from StudentDetails object
    public void fromStudentDetails(Student student) {
        if (student == null) return;

        // Basic Information
        fullName = student.getFullName();
        universityId = student.getUniversityId();
        nicNumber = student.getNicNumber();
        gender = student.getGender();
        dateOfBirth = student.getDateOfBirth();
        studentPhoto = student.getPhotoUri();

        // Academic Details
        faculty = student.getFaculty();
        department = student.getDepartment();
        batch = student.getBatch();
        semester = student.getSemester();
        enrollmentDate = student.getEnrollmentDate();

        // Account Details
        email = student.getEmail();
        username = student.getUsername();
        password = student.getPassword();
        termsAccepted = student.isTermsAccepted();

        // Contact Details
        mobileNumber = student.getMobileNumber();
        alternateNumber = student.getAlternateNumber();
        permanentAddress = student.getPermanentAddress();
        city = student.getCity();
        province = student.getProvince();
        postalCode = student.getPostalCode();

        // Emergency Contact
        emergencyName = student.getEmergencyName();
        emergencyRelationship = student.getEmergencyRelationship();
        emergencyNumber = student.getEmergencyNumber();
    }

    // Getters and Setters

    // Basic Information
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

    public String getStudentPhoto() { return studentPhoto; }
    public void setStudentPhoto(String studentPhoto) { this.studentPhoto = studentPhoto; }

    // Academic Details
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

    // Account Details
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isTermsAccepted() { return termsAccepted; }
    public void setTermsAccepted(boolean termsAccepted) { this.termsAccepted = termsAccepted; }

    // Contact Details
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

    // Emergency Contact
    public String getEmergencyName() { return emergencyName; }
    public void setEmergencyName(String emergencyName) { this.emergencyName = emergencyName; }

    public String getEmergencyRelationship() { return emergencyRelationship; }
    public void setEmergencyRelationship(String emergencyRelationship) { this.emergencyRelationship = emergencyRelationship; }

    public String getEmergencyNumber() { return emergencyNumber; }
    public void setEmergencyNumber(String emergencyNumber) { this.emergencyNumber = emergencyNumber; }

    // Utility methods
    public boolean isBasicInfoComplete() {
        return fullName != null && !fullName.trim().isEmpty() &&
                universityId != null && !universityId.trim().isEmpty() &&
                nicNumber != null && !nicNumber.trim().isEmpty() &&
                gender != null && !gender.trim().isEmpty() &&
                dateOfBirth != null && !dateOfBirth.trim().isEmpty();
    }

    public boolean isAcademicInfoComplete() {
        return faculty != null && !faculty.trim().isEmpty() &&
                department != null && !department.trim().isEmpty() &&
                batch != null && !batch.trim().isEmpty() &&
                semester != null && !semester.trim().isEmpty() &&
                enrollmentDate != null && !enrollmentDate.trim().isEmpty();
    }

    public boolean isAccountInfoComplete() {
        return email != null && !email.trim().isEmpty() &&
                username != null && !username.trim().isEmpty() &&
                password != null && !password.trim().isEmpty() &&
                termsAccepted;
    }

    public boolean isContactInfoComplete() {
        return mobileNumber != null && !mobileNumber.trim().isEmpty() &&
                permanentAddress != null && !permanentAddress.trim().isEmpty() &&
                city != null && !city.trim().isEmpty() &&
                province != null && !province.trim().isEmpty() &&
                postalCode != null && !postalCode.trim().isEmpty() &&
                emergencyName != null && !emergencyName.trim().isEmpty() &&
                emergencyRelationship != null && !emergencyRelationship.trim().isEmpty() &&
                emergencyNumber != null && !emergencyNumber.trim().isEmpty();
    }

    public boolean isComplete() {
        return isBasicInfoComplete() && isAcademicInfoComplete() &&
                isAccountInfoComplete() && isContactInfoComplete();
    }
}