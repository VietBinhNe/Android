package com.example.apartmentmanager.models;

public class Apartment {
    private String id;
    private String number;
    private String status;
    private String residentId;
    private String description;
    private double price;
    private String details;

    public Apartment() {}

    public Apartment(String id, String number, String status, String residentId, String description, double price, String details) {
        this.id = id;
        this.number = number;
        this.status = status;
        this.residentId = residentId;
        this.description = description;
        this.price = price;
        this.details = details;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getResidentId() { return residentId; }
    public void setResidentId(String residentId) { this.residentId = residentId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}