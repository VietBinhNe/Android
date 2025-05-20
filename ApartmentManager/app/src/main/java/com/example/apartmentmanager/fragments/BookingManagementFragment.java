package com.example.apartmentmanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.adapters.BookingAdapter;
import com.example.apartmentmanager.models.Booking;
import com.example.apartmentmanager.models.Service;
import com.example.apartmentmanager.network.FirebaseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingManagementFragment extends Fragment {
    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private FirebaseService firebaseService;
    private Map<String, Service> serviceMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_management, container, false);

        recyclerView = view.findViewById(R.id.booking_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseService = new FirebaseService();
        serviceMap = new HashMap<>();

        firebaseService.getServices(services -> {
            for (Service service : services) {
                serviceMap.put(service.getId(), service);
            }
            firebaseService.getBookings(bookings -> {
                bookingAdapter = new BookingAdapter(bookings, serviceMap);
                recyclerView.setAdapter(bookingAdapter);
            });
        });

        return view;
    }
}