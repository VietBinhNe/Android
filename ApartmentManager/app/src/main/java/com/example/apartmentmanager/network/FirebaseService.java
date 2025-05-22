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
    private final FirebaseFirestore db;

    public FirebaseService() {
        db = FirebaseFirestore.getInstance();
    }

    public void getUser(String userId, Consumer<User> callback) {
        Log.d(TAG, "Attempting to load user: " + userId);
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        Log.d(TAG, "Loaded user: " + userId + ", Name: " + (user != null ? user.getName() : "null"));
                        callback.accept(user);
                    } else {
                        Log.w(TAG, "User not found: " + userId);
                        callback.accept(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading user: " + e.getMessage(), e);
                    callback.accept(null);
                });
    }

    public void addUser(User user, Consumer<Boolean> callback) {
        Log.d(TAG, "Attempting to add user: " + user.getId());
        db.collection("users").document(user.getId()).set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User added successfully: " + user.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding user: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void getApartments(Consumer<List<Apartment>> callback) {
        Log.d(TAG, "Attempting to load all apartments");
        db.collection("apartments").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Apartment> apartments = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Apartment apartment = document.toObject(Apartment.class);
                        apartments.add(apartment);
                    }
                    Log.d(TAG, "Loaded " + apartments.size() + " apartments");
                    callback.accept(apartments);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading apartments: " + e.getMessage(), e);
                    callback.accept(null);
                });
    }

    public void getApartmentByResident(String userId, Consumer<Apartment> callback) {
        Log.d(TAG, "Attempting to load apartment for user: " + userId);
        db.collection("apartments")
                .whereEqualTo("residentId", userId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Apartment apartment = document.toObject(Apartment.class);
                            Log.d(TAG, "Loaded apartment for user " + userId + ": " + apartment.getId());
                            callback.accept(apartment);
                            return;
                        }
                    } else {
                        Log.w(TAG, "No apartment found for user: " + userId);
                        callback.accept(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading apartment for user: " + e.getMessage(), e);
                    callback.accept(null);
                });
    }

    public void getBills(Consumer<List<Bill>> callback) {
        Log.d(TAG, "Attempting to load bills from Firestore");
        db.collection("bills").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Bill> bills = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Bill bill = document.toObject(Bill.class);
                        bills.add(bill);
                        Log.d(TAG, "Loaded bill: " + bill.getId());
                    }
                    Log.d(TAG, "Total bills loaded: " + bills.size());
                    callback.accept(bills);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading bills: " + e.getMessage(), e);
                    callback.accept(null);
                });
    }

    public void updateBill(Bill bill, Consumer<Boolean> callback) {
        Log.d(TAG, "Attempting to update bill: " + bill.getId() + ", New status: " + bill.getStatus());
        db.collection("bills").document(bill.getId()).set(bill)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Bill updated successfully: " + bill.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating bill: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void getServices(Consumer<List<Service>> callback) {
        Log.d(TAG, "Attempting to load services from Firestore");
        db.collection("services").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Service> services = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Service service = document.toObject(Service.class);
                        services.add(service);
                        Log.d(TAG, "Loaded service: " + service.getId());
                    }
                    Log.d(TAG, "Total services loaded: " + services.size());
                    callback.accept(services);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading services: " + e.getMessage(), e);
                    callback.accept(null);
                });
    }

    public void addService(Service service, Consumer<Boolean> callback) {
        Log.d(TAG, "Attempting to add service: " + service.getId());
        db.collection("services").document(service.getId()).set(service)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Service added successfully: " + service.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding service: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void getBookings(Consumer<List<Booking>> callback) {
        Log.d(TAG, "Attempting to load bookings from Firestore");
        db.collection("bookings").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Booking> bookings = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Booking booking = document.toObject(Booking.class);
                        bookings.add(booking);
                        Log.d(TAG, "Loaded booking: " + booking.getId());
                    }
                    Log.d(TAG, "Total bookings loaded: " + bookings.size());
                    callback.accept(bookings);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading bookings: " + e.getMessage(), e);
                    callback.accept(null);
                });
    }

    public void bookService(Booking booking, Consumer<Boolean> callback) {
        Log.d(TAG, "Attempting to book service: " + booking.getId());
        db.collection("bookings").document(booking.getId()).set(booking)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Service booked successfully: " + booking.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error booking service: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void getNotifications(String userId, String userRole, Consumer<List<Notification>> callback) {
        Log.d(TAG, "Attempting to load notifications for user: " + userId + ", role: " + userRole);
        if ("admin".equals(userRole)) {
            // Admin lấy tất cả thông báo
            db.collection("notifications").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Notification> notifications = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Notification notification = document.toObject(Notification.class);
                            notifications.add(notification);
                            Log.d(TAG, "Loaded notification: " + notification.getId());
                        }
                        Log.d(TAG, "Total notifications loaded for admin: " + notifications.size());
                        callback.accept(notifications);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading notifications: " + e.getMessage(), e);
                        callback.accept(null);
                    });
        } else {
            // Resident chỉ lấy thông báo theo residentId
            db.collection("notifications")
                    .whereEqualTo("residentId", userId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Notification> notifications = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Notification notification = document.toObject(Notification.class);
                            notifications.add(notification);
                            Log.d(TAG, "Loaded notification: " + notification.getId());
                        }
                        Log.d(TAG, "Total notifications loaded for user " + userId + ": " + notifications.size());
                        callback.accept(notifications);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading notifications: " + e.getMessage(), e);
                        callback.accept(null);
                    });
        }
    }

    public void sendNotificationToAllResidents(Notification notification, Consumer<Boolean> callback) {
        Log.d(TAG, "Attempting to send notification to all residents: " + notification.getId());
        db.collection("users")
                .whereEqualTo("role", "resident")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.w(TAG, "No residents found");
                        callback.accept(false);
                        return;
                    }

                    List<String> residentIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        residentIds.add(document.getId());
                    }

                    List<Notification> notificationsToAdd = new ArrayList<>();
                    for (String residentId : residentIds) {
                        Notification residentNotification = new Notification(
                                notification.getId() + "_" + residentId,
                                notification.getTitle(),
                                notification.getMessage()
                        );
                        residentNotification.setResidentId(residentId);
                        residentNotification.setDate(notification.getDate());
                        notificationsToAdd.add(residentNotification);
                    }

                    int[] successCount = {0};
                    int total = notificationsToAdd.size();
                    for (Notification notif : notificationsToAdd) {
                        db.collection("notifications").document(notif.getId()).set(notif)
                                .addOnSuccessListener(aVoid -> {
                                    successCount[0]++;
                                    if (successCount[0] == total) {
                                        Log.d(TAG, "Sent notification to all residents successfully");
                                        callback.accept(true);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error sending notification to resident: " + notif.getResidentId() + ", " + e.getMessage(), e);
                                    callback.accept(false);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching residents: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void sendNotificationToApartment(Notification notification, String apartmentId, Consumer<Boolean> callback) {
        Log.d(TAG, "Attempting to send notification to apartment: " + apartmentId);
        db.collection("apartments")
                .whereEqualTo("id", apartmentId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.w(TAG, "Apartment not found: " + apartmentId);
                        callback.accept(false);
                        return;
                    }

                    QueryDocumentSnapshot document = queryDocumentSnapshots.iterator().next();
                    Apartment apartment = document.toObject(Apartment.class);
                    String residentId = apartment.getResidentId();

                    if (residentId == null || residentId.isEmpty()) {
                        Log.w(TAG, "No resident found for apartment: " + apartmentId);
                        callback.accept(false);
                        return;
                    }

                    Notification residentNotification = new Notification(
                            notification.getId() + "_" + residentId,
                            notification.getTitle(),
                            notification.getMessage()
                    );
                    residentNotification.setResidentId(residentId);
                    residentNotification.setDate(notification.getDate());

                    db.collection("notifications").document(residentNotification.getId()).set(residentNotification)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Sent notification to resident " + residentId + " of apartment " + apartmentId);
                                callback.accept(true);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error sending notification to resident: " + residentId + ", " + e.getMessage(), e);
                                callback.accept(false);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching apartment: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void getRequests(Consumer<List<Request>> callback) {
        Log.d(TAG, "Attempting to load requests from Firestore");
        db.collection("requests").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Request> requests = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Request request = document.toObject(Request.class);
                        requests.add(request);
                        Log.d(TAG, "Loaded request: " + request.getId());
                    }
                    Log.d(TAG, "Total requests loaded: " + requests.size());
                    callback.accept(requests);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading requests: " + e.getMessage(), e);
                    callback.accept(null);
                });
    }

    public void addRequest(Request request, Consumer<Boolean> callback) {
        Log.d(TAG, "Attempting to add request: " + request.getId());
        db.collection("requests").document(request.getId()).set(request)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Request added successfully: " + request.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding request: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void updateRequest(Request request, Consumer<Boolean> callback) {
        Log.d(TAG, "Attempting to update request: " + request.getId());
        db.collection("requests").document(request.getId()).set(request)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Request updated successfully: " + request.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating request: " + e.getMessage(), e);
                    callback.accept(false);
                });
    }

    public void getAllUsers(Consumer<List<User>> callback) {
        Log.d(TAG, "Attempting to load all users");
        db.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        users.add(user);
                        Log.d(TAG, "Loaded user: " + user.getId());
                    }
                    Log.d(TAG, "Total users loaded: " + users.size());
                    callback.accept(users);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading users: " + e.getMessage(), e);
                    callback.accept(null);
                });
    }
}