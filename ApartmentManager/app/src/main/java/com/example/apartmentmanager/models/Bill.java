package com.example.apartmentmanager.models;

import java.util.List;

public class Bill {
    private String id;
    private String title;
    private String apartmentId;
    private List<BillItem> items;
    private String dueDate;
    private String status;

    public Bill() {}

    public Bill(String id, String title, String apartmentId, List<BillItem> items, String dueDate, String status) {
        this.id = id;
        this.title = title;
        this.apartmentId = apartmentId;
        this.items = items;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getApartmentId() { return apartmentId; }
    public void setApartmentId(String apartmentId) { this.apartmentId = apartmentId; }
    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}