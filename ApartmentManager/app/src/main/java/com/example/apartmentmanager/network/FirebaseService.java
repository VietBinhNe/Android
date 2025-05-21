package com.example.apartmentmanager.network;

import android.util.Log;
import com.example.apartmentmanager.models.Apartment;
import com.example.apartmentmanager.models.Bill;
import com.example.apartmentmanager.models.Booking;
import com.example.apartmentmanager.models.Notification;
import com.example.apartmentmanager.models.Request;
import com.example.apartmentmanager.models.Service;
import com.example.apartmentmanager.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FirebaseService {
    private static final String TAG = "FirebaseService";
    private FirebaseFirestore db;

    public FirebaseService() {
        db = FirebaseFirestore.getInstance();
    }

    public void addApartment(Apartment apartment, Consumer<Boolean> callback) {
        db.collection("apartments").document(apartment.getId())
                .set(apartment)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Apartment added successfully: " + apartment.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding apartment: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void getApartments(Consumer<List<Apartment>> callback) {
        Log.d(TAG, "Attempting to load apartments from Firestore");
        db.collection("apartments")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Apartment> apartments = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Apartment apartment = document.toObject(Apartment.class);
                            Log.d(TAG, "Loaded apartment: " + apartment.getId());
                            apartments.add(apartment);
                        }
                        Log.d(TAG, "Total apartments loaded: " + apartments.size());
                        callback.accept(apartments);
                    } else {
                        Log.e(TAG, "Error getting apartments: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), task.getException());
                        callback.accept(null);
                    }
                });
    }

    public void getApartmentByResident(String userId, Consumer<Apartment> callback) {
        Log.d(TAG, "Attempting to load apartment for user: " + userId);
        db.collection("apartments")
                .whereEqualTo("residentId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Apartment apartment = task.getResult().getDocuments().get(0).toObject(Apartment.class);
                        Log.d(TAG, "Loaded apartment for user " + userId + ": " + apartment.getId());
                        callback.accept(apartment);
                    } else {
                        Log.w(TAG, "No apartment found for user: " + userId);
                        callback.accept(null);
                    }
                });
    }

    public void addUser(User user, Consumer<Boolean> callback) {
        db.collection("users").document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User added successfully: " + user.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding user: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void getUser(String userId, Consumer<User> callback) {
        Log.d(TAG, "Attempting to load user: " + userId);
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        User user = task.getResult().toObject(User.class);
                        Log.d(TAG, "Loaded user: " + userId + ", Name: " + user.getName());
                        callback.accept(user);
                    } else {
                        Log.w(TAG, "User not found: " + userId);
                        callback.accept(null);
                    }
                });
    }

    public void addBill(Bill bill, Consumer<Boolean> callback) {
        db.collection("bills").document(bill.getId())
                .set(bill)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Bill added successfully: " + bill.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding bill: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void getBills(Consumer<List<Bill>> callback) {
        Log.d(TAG, "Attempting to load bills from Firestore");
        db.collection("bills")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Bill> bills = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Bill bill = document.toObject(Bill.class);
                            Log.d(TAG, "Loaded bill: " + bill.getId());
                            bills.add(bill);
                        }
                        Log.d(TAG, "Total bills loaded: " + bills.size());
                        callback.accept(bills);
                    } else {
                        Log.e(TAG, "Error getting bills: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), task.getException());
                        callback.accept(null);
                    }
                });
    }

    public void updateBill(Bill bill, Consumer<Boolean> callback) {
        Log.d(TAG, "Attempting to update bill: " + bill.getId() + ", New status: " + bill.getStatus());
        db.collection("bills").document(bill.getId())
                .set(bill)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Bill updated successfully: " + bill.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating bill: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void addService(Service service, Consumer<Boolean> callback) {
        db.collection("services").document(service.getId())
                .set(service)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Service added successfully: " + service.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding service: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void getServices(Consumer<List<Service>> callback) {
        Log.d(TAG, "Attempting to load services from Firestore");
        db.collection("services")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Service> services = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Service service = document.toObject(Service.class);
                            Log.d(TAG, "Loaded service: " + service.getId());
                            services.add(service);
                        }
                        Log.d(TAG, "Total services loaded: " + services.size());
                        callback.accept(services);
                    } else {
                        Log.e(TAG, "Error getting services: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), task.getException());
                        callback.accept(null);
                    }
                });
    }

    public void getBookings(Consumer<List<Booking>> callback) {
        Log.d(TAG, "Attempting to load bookings from Firestore");
        db.collection("bookings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Booking> bookings = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Booking booking = document.toObject(Booking.class);
                            Log.d(TAG, "Loaded booking: " + booking.getId());
                            bookings.add(booking);
                        }
                        Log.d(TAG, "Total bookings loaded: " + bookings.size());
                        callback.accept(bookings);
                    } else {
                        Log.e(TAG, "Error getting bookings: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), task.getException());
                        callback.accept(null);
                    }
                });
    }

    public void bookService(Booking booking, Consumer<Boolean> callback) {
        db.collection("bookings").document(booking.getId())
                .set(booking)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Booking added successfully: " + booking.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding booking: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void addRequest(Request request, Consumer<Boolean> callback) {
        db.collection("requests").document(request.getId())
                .set(request)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Request added successfully: " + request.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding request: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void getRequests(Consumer<List<Request>> callback) {
        Log.d(TAG, "Attempting to load requests from Firestore");
        db.collection("requests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Request> requests = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Request request = document.toObject(Request.class);
                            Log.d(TAG, "Loaded request: " + request.getId());
                            requests.add(request);
                        }
                        Log.d(TAG, "Total requests loaded: " + requests.size());
                        callback.accept(requests);
                    } else {
                        Log.e(TAG, "Error getting requests: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), task.getException());
                        callback.accept(null);
                    }
                });
    }

    public void updateRequest(Request request, Consumer<Boolean> callback) {
        db.collection("requests").document(request.getId())
                .set(request)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Request updated successfully: " + request.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating request: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void addNotification(Notification notification, Consumer<Boolean> callback) {
        db.collection("notifications").document(notification.getId())
                .set(notification)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Notification added successfully: " + notification.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding notification: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void getNotifications(Consumer<List<Notification>> callback) {
        Log.d(TAG, "Attempting to load notifications from Firestore");
        db.collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Notification> notifications = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification notification = document.toObject(Notification.class);
                            Log.d(TAG, "Loaded notification: " + notification.getId());
                            notifications.add(notification);
                        }
                        Log.d(TAG, "Total notifications loaded: " + notifications.size());
                        callback.accept(notifications);
                    } else {
                        Log.e(TAG, "Error getting notifications: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), task.getException());
                        callback.accept(null);
                    }
                });
    }
}