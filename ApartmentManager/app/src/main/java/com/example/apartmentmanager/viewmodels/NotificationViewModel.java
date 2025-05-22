package com.example.apartmentmanager.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.apartmentmanager.models.Notification;
import com.example.apartmentmanager.network.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class NotificationViewModel extends ViewModel {
    private final MutableLiveData<List<Notification>> notifications = new MutableLiveData<>();
    private final FirebaseService firebaseService;
    private final String userRole;

    public NotificationViewModel(String userRole) {
        this.firebaseService = new FirebaseService();
        this.userRole = userRole;
        loadNotifications();
    }

    public LiveData<List<Notification>> getNotifications() {
        return notifications;
    }

    public void loadNotifications() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        if (userId != null) {
            firebaseService.getNotifications(userId, userRole, result -> notifications.setValue(result));
        } else {
            notifications.setValue(null);
        }
    }
}