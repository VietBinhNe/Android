package com.example.apartmentmanager.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.apartmentmanager.models.Request;
import com.example.apartmentmanager.network.FirebaseService;

import java.util.List;

public class RequestViewModel extends ViewModel {
    private MutableLiveData<List<Request>> requests;
    private FirebaseService firebaseService;

    public RequestViewModel() {
        requests = new MutableLiveData<>();
        firebaseService = new FirebaseService();
    }

    public LiveData<List<Request>> getRequests() {
        firebaseService.getRequests(requests::setValue);
        return requests;
    }
}