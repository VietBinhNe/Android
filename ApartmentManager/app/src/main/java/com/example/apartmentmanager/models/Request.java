package com.example.apartmentmanager.models;

public class Request {
    private String id;
    private String title;
    private String description;
    private String requestDate;
    private String status;
    private String userId;

    public Request() {}

    public Request(String id, String title, String description, String requestDate, String status, String userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requestDate = requestDate;
        this.status = status;
        this.userId = userId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequestDate() { return requestDate; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}