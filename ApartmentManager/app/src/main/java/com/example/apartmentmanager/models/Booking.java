package com.example.apartmentmanager.models;

public class Booking {
    private String id;
    private String userId;
    private String serviceId;
    private String bookingDate;
    private String status;
    private String paymentStatus;

    public Booking() {}

    public Booking(String id, String userId, String serviceId, String bookingDate, String status, String paymentStatus) {
        this.id = id;
        this.userId = userId;
        this.serviceId = serviceId;
        this.bookingDate = bookingDate;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}