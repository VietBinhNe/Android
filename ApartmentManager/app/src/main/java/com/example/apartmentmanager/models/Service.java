package com.example.apartmentmanager.models;

public class Service {
    private String id;
    private String name;
    private String description;
    private String details;
    private double price;
    private int capacity;

    public Service() {}

    public Service(String id, String name, String description, String details, double price, int capacity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.details = details;
        this.price = price;
        this.capacity = capacity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}