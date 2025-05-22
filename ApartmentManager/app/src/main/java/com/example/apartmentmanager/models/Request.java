package com.example.apartmentmanager.models;

public class Request {
    private String id;
    private String title;
    private String content;
    private String userId;
    private String apartmentId;
    private String status;
    private String date;

    public Request() {}

    public Request(String id, String title, String content, String userId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.status = "pending";
        this.date = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
    }

    public Request(String id, String title, String content, String userId, String apartmentId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.apartmentId = apartmentId;
        this.status = "pending";
        this.date = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
    }

    public Request(String id, String title, String content, String userId, String apartmentId, String status) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.apartmentId = apartmentId;
        this.status = status;
        this.date = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
    }

    public Request(String id, String title, String content, String userId, String apartmentId, String status, String date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.apartmentId = apartmentId;
        this.status = status;
        this.date = date;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getApartmentId() { return apartmentId; }
    public void setApartmentId(String apartmentId) { this.apartmentId = apartmentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}