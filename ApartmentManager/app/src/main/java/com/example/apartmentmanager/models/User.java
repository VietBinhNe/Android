package com.example.apartmentmanager.models;

public class User {
    private String id;
    private String email;
    private String role;
    private String leaseDuration;

    public User() {}

    public User(String id, String email, String role, String leaseDuration) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.leaseDuration = leaseDuration;
    }

    // Thêm constructor mới cho 3 tham số
    public User(String id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.leaseDuration = null;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getLeaseDuration() { return leaseDuration; }
    public void setLeaseDuration(String leaseDuration) { this.leaseDuration = leaseDuration; }
}