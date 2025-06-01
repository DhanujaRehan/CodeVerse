package com.example.codeverse.Students.Models;

public class PaymentDetail {

    private int id;
    private String studentId;
    private String universityNumber;
    private String paymentDate;
    private String paymentAmount;
    private String receiptPath;
    private String remarks;
    private String status;
    private String createdAt;

    public PaymentDetail() {
    }

    public PaymentDetail(String studentId, String universityNumber, String paymentDate,
                         String paymentAmount, String receiptPath, String remarks) {
        this.studentId = studentId;
        this.universityNumber = universityNumber;
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
        this.receiptPath = receiptPath;
        this.remarks = remarks;
        this.status = "PENDING";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getUniversityNumber() {
        return universityNumber;
    }

    public void setUniversityNumber(String universityNumber) {
        this.universityNumber = universityNumber;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getReceiptPath() {
        return receiptPath;
    }

    public void setReceiptPath(String receiptPath) {
        this.receiptPath = receiptPath;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}