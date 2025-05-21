package com.example.apartmentmanager.models;

public class Request {
    private String id;
    private String userId;
    private String apartmentId;
    private String title;
    private String description;
    private String date;
    private String status;

    public Request() {
    }

    public Request(String id, String userId, String apartmentId, String description, String status) {
        this.id = id;
        this.userId = userId;
        this.apartmentId = apartmentId;
        this.description = description;
        this.status = status;
    }

    public Request(String id, String title, String description, String date, String status, String userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.status = status;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(String apartmentId) {
        this.apartmentId = apartmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}