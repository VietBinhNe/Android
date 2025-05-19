package com.example.apartmentmanager.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.apartmentmanager.models.Notification;
import com.example.apartmentmanager.network.FirebaseService;

import java.util.List;

public class NotificationViewModel extends ViewModel {
    private MutableLiveData<List<Notification>> notifications;
    private FirebaseService firebaseService;

    public NotificationViewModel() {
        notifications = new MutableLiveData<>();
        firebaseService = new FirebaseService();
    }

    public LiveData<List<Notification>> getNotifications() {
        firebaseService.getNotifications(notifications::setValue);
        return notifications;
    }
}