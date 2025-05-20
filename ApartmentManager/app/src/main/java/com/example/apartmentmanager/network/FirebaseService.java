package com.example.apartmentmanager.network;

import com.example.apartmentmanager.models.Amenity;
import com.example.apartmentmanager.models.Apartment;
import com.example.apartmentmanager.models.Bill;
import com.example.apartmentmanager.models.Booking;
import com.example.apartmentmanager.models.Notification;
import com.example.apartmentmanager.models.Request;
import com.example.apartmentmanager.models.Service;
import com.example.apartmentmanager.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseService {
    private FirebaseFirestore db;

    public FirebaseService() {
        db = FirebaseFirestore.getInstance();
    }

    public void getBills(FirestoreCallback<List<Bill>> callback) {
        db.collection("bills")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Bill> bills = new ArrayList<>();
                        QuerySnapshot snapshot = task.getResult();
                        for (var doc : snapshot.getDocuments()) {
                            bills.add(doc.toObject(Bill.class));
                        }
                        callback.onCallback(bills);
                    } else {
                        callback.onCallback(new ArrayList<>());
                    }
                });
    }

    public void getBillsByApartment(String apartmentId, FirestoreCallback<List<Bill>> callback) {
        db.collection("bills")
                .whereEqualTo("apartmentId", apartmentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Bill> bills = new ArrayList<>();
                        QuerySnapshot snapshot = task.getResult();
                        for (var doc : snapshot.getDocuments()) {
                            bills.add(doc.toObject(Bill.class));
                        }
                        callback.onCallback(bills);
                    } else {
                        callback.onCallback(new ArrayList<>());
                    }
                });
    }

    public void addBill(Bill bill, FirestoreCallback<Boolean> callback) {
        db.collection("bills").document(bill.getId())
                .set(bill)
                .addOnSuccessListener(aVoid -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }

    public void deleteBill(String billId, FirestoreCallback<Boolean> callback) {
        db.collection("bills").document(billId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }

    public void getNotifications(FirestoreCallback<List<Notification>> callback) {
        db.collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Notification> notifications = new ArrayList<>();
                        QuerySnapshot snapshot = task.getResult();
                        for (var doc : snapshot.getDocuments()) {
                            notifications.add(doc.toObject(Notification.class));
                        }
                        callback.onCallback(notifications);
                    } else {
                        callback.onCallback(new ArrayList<>());
                    }
                });
    }

    public void addNotification(Notification notification, FirestoreCallback<Boolean> callback) {
        db.collection("notifications").document(notification.getId())
                .set(notification)
                .addOnSuccessListener(aVoid -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }

    public void getRequests(FirestoreCallback<List<Request>> callback) {
        db.collection("requests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Request> requests = new ArrayList<>();
                        QuerySnapshot snapshot = task.getResult();
                        for (var doc : snapshot.getDocuments()) {
                            requests.add(doc.toObject(Request.class));
                        }
                        callback.onCallback(requests);
                    } else {
                        callback.onCallback(new ArrayList<>());
                    }
                });
    }

    public void addRequest(Request request, FirestoreCallback<Boolean> callback) {
        db.collection("requests").document(request.getId())
                .set(request)
                .addOnSuccessListener(aVoid -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }

    public void updateRequestStatus(String requestId, String status, FirestoreCallback<Boolean> callback) {
        db.collection("requests").document(requestId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }

    public void getAmenities(FirestoreCallback<List<Amenity>> callback) {
        db.collection("amenities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Amenity> amenities = new ArrayList<>();
                        QuerySnapshot snapshot = task.getResult();
                        for (var doc : snapshot.getDocuments()) {
                            amenities.add(doc.toObject(Amenity.class));
                        }
                        callback.onCallback(amenities);
                    } else {
                        callback.onCallback(new ArrayList<>());
                    }
                });
    }

    public void addAmenity(Amenity amenity, FirestoreCallback<Boolean> callback) {
        db.collection("amenities").document(amenity.getId())
                .set(amenity)
                .addOnSuccessListener(aVoid -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }

    public void getUser(String userId, FirestoreCallback<User> callback) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        callback.onCallback(document.toObject(User.class));
                    } else {
                        callback.onCallback(null);
                    }
                })
                .addOnFailureListener(e -> callback.onCallback(null));
    }

    public void getServices(FirestoreCallback<List<Service>> callback) {
        db.collection("services")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Service> services = new ArrayList<>();
                        QuerySnapshot snapshot = task.getResult();
                        for (var doc : snapshot.getDocuments()) {
                            services.add(doc.toObject(Service.class));
                        }
                        callback.onCallback(services);
                    } else {
                        callback.onCallback(new ArrayList<>());
                    }
                });
    }

    public void bookService(Booking booking, FirestoreCallback<Boolean> callback) {
        db.collection("bookings").document(booking.getId())
                .set(booking)
                .addOnSuccessListener(aVoid -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }

    public void getBookings(FirestoreCallback<List<Booking>> callback) {
        db.collection("bookings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Booking> bookings = new ArrayList<>();
                        QuerySnapshot snapshot = task.getResult();
                        for (var doc : snapshot.getDocuments()) {
                            bookings.add(doc.toObject(Booking.class));
                        }
                        callback.onCallback(bookings);
                    } else {
                        callback.onCallback(new ArrayList<>());
                    }
                });
    }

    public void getBookingsByService(String serviceId, FirestoreCallback<List<Booking>> callback) {
        db.collection("bookings")
                .whereEqualTo("serviceId", serviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Booking> bookings = new ArrayList<>();
                        QuerySnapshot snapshot = task.getResult();
                        for (var doc : snapshot.getDocuments()) {
                            bookings.add(doc.toObject(Booking.class));
                        }
                        callback.onCallback(bookings);
                    } else {
                        callback.onCallback(new ArrayList<>());
                    }
                });
    }

    public void getApartments(FirestoreCallback<List<Apartment>> callback) {
        db.collection("apartments")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Apartment> apartments = new ArrayList<>();
                        QuerySnapshot snapshot = task.getResult();
                        for (var doc : snapshot.getDocuments()) {
                            apartments.add(doc.toObject(Apartment.class));
                        }
                        callback.onCallback(apartments);
                    } else {
                        callback.onCallback(new ArrayList<>());
                    }
                });
    }

    public void getApartmentByResident(String residentId, FirestoreCallback<Apartment> callback) {
        db.collection("apartments")
                .whereEqualTo("residentId", residentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (!snapshot.isEmpty()) {
                            callback.onCallback(snapshot.getDocuments().get(0).toObject(Apartment.class));
                        } else {
                            callback.onCallback(null);
                        }
                    } else {
                        callback.onCallback(null);
                    }
                });
    }

    public interface FirestoreCallback<T> {
        void onCallback(T result);
    }
}