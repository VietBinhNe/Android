package com.example.apartmentmanager.models;

public class Bill {
    private String id;
    private String title;
    private double amount;
    private String dueDate;

    public Bill() {}

    public Bill(String id, String title, double amount, String dueDate) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
}