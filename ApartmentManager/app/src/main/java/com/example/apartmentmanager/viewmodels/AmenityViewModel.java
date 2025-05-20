package com.example.apartmentmanager.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.apartmentmanager.models.Amenity;
import com.example.apartmentmanager.network.FirebaseService;

import java.util.List;

public class AmenityViewModel extends ViewModel {
    private MutableLiveData<List<Amenity>> amenities;
    private FirebaseService firebaseService;

    public AmenityViewModel() {
        amenities = new MutableLiveData<>();
        firebaseService = new FirebaseService();
    }

    public LiveData<List<Amenity>> getAmenities() {
        firebaseService.getAmenities(result -> amenities.setValue(result));
        return amenities;
    }
}