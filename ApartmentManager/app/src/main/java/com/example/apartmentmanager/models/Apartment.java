package com.example.apartmentmanager.models;

public class Apartment {
    private String id;
    private String number;
    private String ownerId;

    public Apartment() {}

    public Apartment(String id, String number, String ownerId) {
        this.id = id;
        this.number = number;
        this.ownerId = ownerId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
}