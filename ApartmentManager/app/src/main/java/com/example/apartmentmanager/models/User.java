package com.example.apartmentmanager.models;

public class User {
    private String id;
    private String email;
    private String name;
    private String leaseDuration;

    public User() {}

    public User(String id, String email, String name, String leaseDuration) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.leaseDuration = leaseDuration;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLeaseDuration() { return leaseDuration; }
    public void setLeaseDuration(String leaseDuration) { this.leaseDuration = leaseDuration; }
}