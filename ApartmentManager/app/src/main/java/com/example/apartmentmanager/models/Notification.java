package com.example.apartmentmanager.models;

public class Notification {
    private String id;
    private String title;
    private String message;
    private String date;
    private String apartmentId; // null nếu gửi chung

    public Notification() {}

    public Notification(String id, String title, String message, String date, String apartmentId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
        this.apartmentId = apartmentId;
    }

    // Thêm constructor mới cho 4 tham số
    public Notification(String id, String title, String message, String date) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
        this.apartmentId = null;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getApartmentId() { return apartmentId; }
    public void setApartmentId(String apartmentId) { this.apartmentId = apartmentId; }
}