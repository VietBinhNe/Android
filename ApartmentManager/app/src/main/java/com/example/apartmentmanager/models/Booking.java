package com.example.apartmentmanager.models;

public class Booking {
    private String id;
    private String serviceId;
    private String userId;
    private String bookingDate;
    private String paymentStatus;
    private String status;

    public Booking() {}

    public Booking(String id, String serviceId, String userId, String bookingDate, String paymentStatus, String status) {
        this.id = id;
        this.serviceId = serviceId;
        this.userId = userId;
        this.bookingDate = bookingDate;
        this.paymentStatus = paymentStatus;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}