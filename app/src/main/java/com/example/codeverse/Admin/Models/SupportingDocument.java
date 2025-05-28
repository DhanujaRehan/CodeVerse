package com.example.codeverse.Admin.Models;

public class SupportingDocument {
    private String name;
    private String url;
    private String type;
    private String size;

    public SupportingDocument(String name, String url, String type, String size) {
        this.name = name;
        this.url = url;
        this.type = type;
        this.size = size;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
}
