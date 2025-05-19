package com.example.apartmentmanager.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.apartmentmanager.models.Bill;
import com.example.apartmentmanager.network.FirebaseService;

import java.util.List;

public class BillViewModel extends ViewModel {
    private MutableLiveData<List<Bill>> bills;
    private FirebaseService firebaseService;

    public BillViewModel() {
        bills = new MutableLiveData<>();
        firebaseService = new FirebaseService();
    }

    public LiveData<List<Bill>> getBills() {
        firebaseService.getBills(bills::setValue);
        return bills;
    }
}