package com.example.apartmentmanager.models;

public class User {
    private String id;
    private String email;
    private String name;
    private String leaseDuration;
    private String phoneNumber;
    private String idNumber;
    private double totalPaid;
    private String apartmentNumber;
    private String status;

    public User() {}

    public User(String id, String email, String name, String leaseDuration, String phoneNumber, String idNumber, double totalPaid, String apartmentNumber, String status) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.leaseDuration = leaseDuration;
        this.phoneNumber = phoneNumber;
        this.idNumber = idNumber;
        this.totalPaid = totalPaid;
        this.apartmentNumber = apartmentNumber;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLeaseDuration() { return leaseDuration; }
    public void setLeaseDuration(String leaseDuration) { this.leaseDuration = leaseDuration; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    public double getTotalPaid() { return totalPaid; }
    public void setTotalPaid(double totalPaid) { this.totalPaid = totalPaid; }
    public String getApartmentNumber() { return apartmentNumber; }
    public void setApartmentNumber(String apartmentNumber) { this.apartmentNumber = apartmentNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}