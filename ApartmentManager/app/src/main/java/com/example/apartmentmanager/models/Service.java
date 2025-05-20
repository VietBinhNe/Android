package com.example.apartmentmanager.models;

public class Service {
    private String id;
    private String name;
    private int capacity;
    private String description;
    private double price;
    private String details;

    public Service() {}

    public Service(String id, String name, int capacity, String description, double price, String details) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.description = description;
        this.price = price;
        this.details = details;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}