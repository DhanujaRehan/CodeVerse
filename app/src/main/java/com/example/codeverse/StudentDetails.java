package com.example.codeverse;

import android.os.Parcel;
import android.os.Parcelable;

public class StudentDetails implements Parcelable {

    // Primary Key
    private long id;

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

    // Timestamps
    private String createdAt;
    private String updatedAt;

    // Default constructor
    public StudentDetails() {
        this.id = 0;
        this.termsAccepted = false;
    }

    // Constructor with basic information
    public StudentDetails(String fullName, String universityId, String nicNumber, String gender, String dateOfBirth) {
        this();
        this.fullName = fullName;
        this.universityId = universityId;
        this.nicNumber = nicNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    // Constructor with all basic and academic info
    public StudentDetails(String fullName, String universityId, String nicNumber, String gender,
                          String dateOfBirth, String faculty, String department, String batch, String semester) {
        this(fullName, universityId, nicNumber, gender, dateOfBirth);
        this.faculty = faculty;
        this.department = department;
        this.batch = batch;
        this.semester = semester;
    }

    // Parcelable implementation
    protected StudentDetails(Parcel in) {
        id = in.readLong();
        fullName = in.readString();
        universityId = in.readString();
        nicNumber = in.readString();
        gender = in.readString();
        dateOfBirth = in.readString();
        studentPhoto = in.readString();
        faculty = in.readString();
        department = in.readString();
        batch = in.readString();
        semester = in.readString();
        enrollmentDate = in.readString();
        email = in.readString();
        username = in.readString();
        password = in.readString();
        termsAccepted = in.readByte() != 0;
        mobileNumber = in.readString();
        alternateNumber = in.readString();
        permanentAddress = in.readString();
        city = in.readString();
        province = in.readString();
        postalCode = in.readString();
        emergencyName = in.readString();
        emergencyRelationship = in.readString();
        emergencyNumber = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(fullName);
        dest.writeString(universityId);
        dest.writeString(nicNumber);
        dest.writeString(gender);
        dest.writeString(dateOfBirth);
        dest.writeString(studentPhoto);
        dest.writeString(faculty);
        dest.writeString(department);
        dest.writeString(batch);
        dest.writeString(semester);
        dest.writeString(enrollmentDate);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeByte((byte) (termsAccepted ? 1 : 0));
        dest.writeString(mobileNumber);
        dest.writeString(alternateNumber);
        dest.writeString(permanentAddress);
        dest.writeString(city);
        dest.writeString(province);
        dest.writeString(postalCode);
        dest.writeString(emergencyName);
        dest.writeString(emergencyRelationship);
        dest.writeString(emergencyNumber);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StudentDetails> CREATOR = new Creator<StudentDetails>() {
        @Override
        public StudentDetails createFromParcel(Parcel in) {
            return new StudentDetails(in);
        }

        @Override
        public StudentDetails[] newArray(int size) {
            return new StudentDetails[size];
        }
    };

    // =========================
    // GETTERS AND SETTERS
    // =========================

    // Primary Key
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // Basic Information
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName != null ? fullName.trim() : null;
    }

    public String getUniversityId() {
        return universityId;
    }

    public void setUniversityId(String universityId) {
        this.universityId = universityId != null ? universityId.trim().toUpperCase() : null;
    }

    public String getNicNumber() {
        return nicNumber;
    }

    public void setNicNumber(String nicNumber) {
        this.nicNumber = nicNumber != null ? nicNumber.trim() : null;
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

    public String getStudentPhoto() {
        return studentPhoto;
    }

    public void setStudentPhoto(String studentPhoto) {
        this.studentPhoto = studentPhoto;
    }

    // Academic Details
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
        this.batch = batch != null ? batch.trim() : null;
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

    // Account Details
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username != null ? username.trim() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isTermsAccepted() {
        return termsAccepted;
    }

    public void setTermsAccepted(boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }

    // Contact Details
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber != null ? mobileNumber.trim().replaceAll("[^0-9]", "") : null;
    }

    public String getAlternateNumber() {
        return alternateNumber;
    }

    public void setAlternateNumber(String alternateNumber) {
        this.alternateNumber = alternateNumber != null ? alternateNumber.trim().replaceAll("[^0-9]", "") : null;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress != null ? permanentAddress.trim() : null;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city != null ? city.trim() : null;
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
        this.postalCode = postalCode != null ? postalCode.trim().replaceAll("[^0-9]", "") : null;
    }

    // Emergency Contact
    public String getEmergencyName() {
        return emergencyName;
    }

    public void setEmergencyName(String emergencyName) {
        this.emergencyName = emergencyName != null ? emergencyName.trim() : null;
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
        this.emergencyNumber = emergencyNumber != null ? emergencyNumber.trim().replaceAll("[^0-9]", "") : null;
    }

    // Timestamps
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

    // =========================
    // UTILITY METHODS
    // =========================

    /**
     * Check if basic information is complete
     */
    public boolean isBasicInfoComplete() {
        return isNotEmpty(fullName) &&
                isNotEmpty(universityId) &&
                isNotEmpty(nicNumber) &&
                isNotEmpty(gender) &&
                isNotEmpty(dateOfBirth);
    }

    /**
     * Check if academic information is complete
     */
    public boolean isAcademicInfoComplete() {
        return isNotEmpty(faculty) &&
                isNotEmpty(department) &&
                isNotEmpty(batch) &&
                isNotEmpty(semester) &&
                isNotEmpty(enrollmentDate);
    }

    /**
     * Check if account information is complete
     */
    public boolean isAccountInfoComplete() {
        return isNotEmpty(email) &&
                isNotEmpty(username) &&
                isNotEmpty(password) &&
                termsAccepted;
    }

    /**
     * Check if contact information is complete
     */
    public boolean isContactInfoComplete() {
        return isNotEmpty(mobileNumber) &&
                isNotEmpty(permanentAddress) &&
                isNotEmpty(city) &&
                isNotEmpty(province) &&
                isNotEmpty(postalCode) &&
                isNotEmpty(emergencyName) &&
                isNotEmpty(emergencyRelationship) &&
                isNotEmpty(emergencyNumber);
    }

    /**
     * Check if entire profile is complete
     */
    public boolean isComplete() {
        return isBasicInfoComplete() &&
                isAcademicInfoComplete() &&
                isAccountInfoComplete() &&
                isContactInfoComplete();
    }

    /**
     * Get completion percentage (0-100)
     */
    public int getCompletionPercentage() {
        int totalSections = 4;
        int completedSections = 0;

        if (isBasicInfoComplete()) completedSections++;
        if (isAcademicInfoComplete()) completedSections++;
        if (isAccountInfoComplete()) completedSections++;
        if (isContactInfoComplete()) completedSections++;

        return (completedSections * 100) / totalSections;
    }

    /**
     * Get display name (formatted)
     */
    public String getDisplayName() {
        if (isNotEmpty(fullName)) {
            return capitalizeWords(fullName);
        }
        return "Unknown Student";
    }

    /**
     * Get full academic info as string
     */
    public String getAcademicInfo() {
        StringBuilder info = new StringBuilder();

        if (isNotEmpty(faculty)) {
            info.append(faculty);
        }

        if (isNotEmpty(department)) {
            if (info.length() > 0) info.append(" - ");
            info.append(department);
        }

        if (isNotEmpty(batch)) {
            if (info.length() > 0) info.append(" (");
            info.append("Batch ").append(batch);
            if (info.toString().contains("(")) info.append(")");
        }

        return info.length() > 0 ? info.toString() : "Academic info not available";
    }

    /**
     * Get contact summary
     */
    public String getContactSummary() {
        StringBuilder contact = new StringBuilder();

        if (isNotEmpty(mobileNumber)) {
            contact.append("Mobile: ").append(formatPhoneNumber(mobileNumber));
        }

        if (isNotEmpty(email)) {
            if (contact.length() > 0) contact.append("\n");
            contact.append("Email: ").append(email);
        }

        if (isNotEmpty(city) && isNotEmpty(province)) {
            if (contact.length() > 0) contact.append("\n");
            contact.append("Location: ").append(city).append(", ").append(province);
        }

        return contact.length() > 0 ? contact.toString() : "Contact info not available";
    }

    /**
     * Validate student data
     */
    public ValidationResult validate() {
        ValidationResult result = new ValidationResult();

        // Validate basic info
        if (!isNotEmpty(fullName) || fullName.length() < 2) {
            result.addError("Full name must be at least 2 characters");
        }

        if (!isNotEmpty(universityId) || !universityId.matches("\\d{4}[A-Z]{2}\\d{3}")) {
            result.addError("University ID must follow format: YYYYDD000 (e.g., 2023CS001)");
        }

        if (!isNotEmpty(nicNumber)) {
            result.addError("NIC number is required");
        }

        // Validate contact info
        if (isNotEmpty(mobileNumber) && !mobileNumber.matches("\\d{10}")) {
            result.addError("Mobile number must be 10 digits");
        }

        if (isNotEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            result.addError("Invalid email format");
        }

        if (isNotEmpty(postalCode) && !postalCode.matches("\\d{5}")) {
            result.addError("Postal code must be 5 digits");
        }

        // Validate emergency contact
        if (isNotEmpty(emergencyNumber) && !emergencyNumber.matches("\\d{10}")) {
            result.addError("Emergency contact number must be 10 digits");
        }

        return result;
    }

    /**
     * Copy data from another StudentDetails object
     */
    public void copyFrom(StudentDetails other) {
        if (other == null) return;

        // Copy all non-null fields
        if (other.getFullName() != null) this.fullName = other.getFullName();
        if (other.getUniversityId() != null) this.universityId = other.getUniversityId();
        if (other.getNicNumber() != null) this.nicNumber = other.getNicNumber();
        if (other.getGender() != null) this.gender = other.getGender();
        if (other.getDateOfBirth() != null) this.dateOfBirth = other.getDateOfBirth();
        if (other.getStudentPhoto() != null) this.studentPhoto = other.getStudentPhoto();

        if (other.getFaculty() != null) this.faculty = other.getFaculty();
        if (other.getDepartment() != null) this.department = other.getDepartment();
        if (other.getBatch() != null) this.batch = other.getBatch();
        if (other.getSemester() != null) this.semester = other.getSemester();
        if (other.getEnrollmentDate() != null) this.enrollmentDate = other.getEnrollmentDate();

        if (other.getEmail() != null) this.email = other.getEmail();
        if (other.getUsername() != null) this.username = other.getUsername();
        if (other.getPassword() != null) this.password = other.getPassword();
        this.termsAccepted = other.isTermsAccepted();

        if (other.getMobileNumber() != null) this.mobileNumber = other.getMobileNumber();
        if (other.getAlternateNumber() != null) this.alternateNumber = other.getAlternateNumber();
        if (other.getPermanentAddress() != null) this.permanentAddress = other.getPermanentAddress();
        if (other.getCity() != null) this.city = other.getCity();
        if (other.getProvince() != null) this.province = other.getProvince();
        if (other.getPostalCode() != null) this.postalCode = other.getPostalCode();

        if (other.getEmergencyName() != null) this.emergencyName = other.getEmergencyName();
        if (other.getEmergencyRelationship() != null) this.emergencyRelationship = other.getEmergencyRelationship();
        if (other.getEmergencyNumber() != null) this.emergencyNumber = other.getEmergencyNumber();
    }

    // =========================
    // HELPER METHODS
    // =========================

    private boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    private String capitalizeWords(String text) {
        if (!isNotEmpty(text)) return text;

        String[] words = text.trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            if (i > 0) result.append(" ");

            String word = words[i];
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
            }
        }

        return result.toString();
    }

    private String formatPhoneNumber(String phone) {
        if (!isNotEmpty(phone) || phone.length() != 10) return phone;

        return phone.substring(0, 3) + "-" + phone.substring(3, 6) + "-" + phone.substring(6);
    }

    // =========================
    // OBJECT METHODS
    // =========================

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        StudentDetails that = (StudentDetails) obj;

        // Compare by university ID if both have it
        if (isNotEmpty(this.universityId) && isNotEmpty(that.universityId)) {
            return this.universityId.equals(that.universityId);
        }

        // Compare by ID if both have it
        if (this.id > 0 && that.id > 0) {
            return this.id == that.id;
        }

        // Compare by full name and NIC as fallback
        return isNotEmpty(this.fullName) && isNotEmpty(that.fullName) &&
                isNotEmpty(this.nicNumber) && isNotEmpty(that.nicNumber) &&
                this.fullName.equals(that.fullName) && this.nicNumber.equals(that.nicNumber);
    }

    @Override
    public int hashCode() {
        if (isNotEmpty(universityId)) {
            return universityId.hashCode();
        }

        if (id > 0) {
            return Long.hashCode(id);
        }

        int result = 17;
        if (isNotEmpty(fullName)) result = 31 * result + fullName.hashCode();
        if (isNotEmpty(nicNumber)) result = 31 * result + nicNumber.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "StudentDetails{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", universityId='" + universityId + '\'' +
                ", email='" + email + '\'' +
                ", faculty='" + faculty + '\'' +
                ", department='" + department + '\'' +
                ", batch='" + batch + '\'' +
                ", completionPercentage=" + getCompletionPercentage() + "%" +
                '}';
    }

    // =========================
    // INNER CLASSES
    // =========================

    /**
     * Validation result class
     */
    public static class ValidationResult {
        private java.util.List<String> errors = new java.util.ArrayList<>();

        public void addError(String error) {
            errors.add(error);
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public java.util.List<String> getErrors() {
            return errors;
        }

        public String getErrorMessage() {
            if (errors.isEmpty()) return null;
            return String.join("\n", errors);
        }
    }
}