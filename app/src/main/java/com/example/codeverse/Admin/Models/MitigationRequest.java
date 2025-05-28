package com.example.codeverse.Admin.Models;

import java.util.List;

public class MitigationRequest {
    private String id;
    private String studentId;
    private String studentName;
    private String programme;
    private String batch;
    private String module;
    private String assessment;
    private String requestType;
    private String reason;
    private String submittedDate;
    private Priority priority;
    private RequestStatus status;
    private List<SupportingDocument> supportingDocuments;

    public MitigationRequest(String id, String studentId, String studentName, String programme,
                             String batch, String module, String assessment, String requestType,
                             String reason, String submittedDate, Priority priority,
                             RequestStatus status, List<SupportingDocument> supportingDocuments) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.programme = programme;
        this.batch = batch;
        this.module = module;
        this.assessment = assessment;
        this.requestType = requestType;
        this.reason = reason;
        this.submittedDate = submittedDate;
        this.priority = priority;
        this.status = status;
        this.supportingDocuments = supportingDocuments;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getProgramme() { return programme; }
    public void setProgramme(String programme) { this.programme = programme; }

    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }

    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }

    public String getAssessment() { return assessment; }
    public void setAssessment(String assessment) { this.assessment = assessment; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(String submittedDate) { this.submittedDate = submittedDate; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public List<SupportingDocument> getSupportingDocuments() { return supportingDocuments; }
    public void setSupportingDocuments(List<SupportingDocument> supportingDocuments) {
        this.supportingDocuments = supportingDocuments;
    }

    public enum Priority {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
        URGENT("Urgent");

        private final String displayName;

        Priority(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum RequestStatus {
        PENDING("Pending"),
        ACCEPTED("Accepted"),
        REJECTED("Rejected");

        private final String displayName;

        RequestStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}

