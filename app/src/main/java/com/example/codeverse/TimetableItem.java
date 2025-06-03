package com.example.codeverse;

public class TimetableItem {

    public int id;
    public String weekTitle;
    public String startDate;
    public String endDate;
    public String pdfPath;
    public String fileSize;
    public String status;
    public String createdAt;
    public int downloadCount;
    public boolean isDownloaded;
    public String localPath;

    public TimetableItem() {
        this.status = "Available";
        this.downloadCount = 0;
        this.isDownloaded = false;
    }

    public TimetableItem(int id, String weekTitle, String startDate, String endDate,
                         String pdfPath, String fileSize, String status) {
        this.id = id;
        this.weekTitle = weekTitle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pdfPath = pdfPath;
        this.fileSize = fileSize;
        this.status = status != null ? status : "Available";
        this.downloadCount = 0;
        this.isDownloaded = false;
    }

    public boolean isAvailable() {
        return "Available".equalsIgnoreCase(status);
    }

    public String getStatusColor() {
        if (status == null) return "#666666";
        switch (status.toLowerCase()) {
            case "available":
                return "#4CAF50";
            case "unavailable":
                return "#F44336";
            case "pending":
                return "#FF9800";
            case "downloaded":
                return "#2196F3";
            default:
                return "#666666";
        }
    }

    public String getStatusBackgroundColor() {
        if (status == null) return "#F5F5F5";
        switch (status.toLowerCase()) {
            case "available":
                return "#E8F5E9";
            case "unavailable":
                return "#FFEBEE";
            case "pending":
                return "#FFF3E0";
            case "downloaded":
                return "#E3F2FD";
            default:
                return "#F5F5F5";
        }
    }

    public void incrementDownloadCount() {
        this.downloadCount++;
    }

    public void markAsDownloaded(String localPath) {
        this.isDownloaded = true;
        this.localPath = localPath;
        incrementDownloadCount();
    }

    public boolean hasValidPdfPath() {
        return pdfPath != null && !pdfPath.isEmpty() &&
                (pdfPath.startsWith("http") || pdfPath.startsWith("file://"));
    }

    @Override
    public String toString() {
        return "TimetableItem{" +
                "id=" + id +
                ", weekTitle='" + weekTitle + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", status='" + status + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", isDownloaded=" + isDownloaded +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TimetableItem that = (TimetableItem) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(id).hashCode();
    }
}