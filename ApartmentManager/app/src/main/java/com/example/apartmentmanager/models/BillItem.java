package com.example.apartmentmanager.models;

public class BillItem {
    private String name;
    private double amount;

    public BillItem() {}

    public BillItem(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}