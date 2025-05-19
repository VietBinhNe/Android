package com.example.apartmentmanager.models;

public class Visitor {
    private String id;
    private String name;
    private String visitDate;
    private String apartmentId;

    public Visitor() {}

    public Visitor(String id, String name, String visitDate, String apartmentId) {
        this.id = id;
        this.name = name;
        this.visitDate = visitDate;
        this.apartmentId = apartmentId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVisitDate() { return visitDate; }
    public void setVisitDate(String visitDate) { this.visitDate = visitDate; }
    public String getApartmentId() { return apartmentId; }
    public void setApartmentId(String apartmentId) { this.apartmentId = apartmentId; }
}