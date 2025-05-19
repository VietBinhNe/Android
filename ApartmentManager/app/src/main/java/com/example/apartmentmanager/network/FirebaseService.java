package com.example.apartmentmanager.network;

import com.example.apartmentmanager.models.Amenity;
import com.example.apartmentmanager.models.Bill;
import com.example.apartmentmanager.models.Notification;
import com.example.apartmentmanager.models.Request;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseService {
    private FirebaseFirestore db;

    public FirebaseService() {
        db = FirebaseFirestore.getInstance();
    }

    public void getBills(FirestoreCallback<Bill> callback) {
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

    public void getNotifications(FirestoreCallback<Notification> callback) {
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

    public void getRequests(FirestoreCallback<Request> callback) {
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

    public void getAmenities(FirestoreCallback<Amenity> callback) {
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

    public interface FirestoreCallback<T> {
        void onCallback(List<T> list);
    }
}