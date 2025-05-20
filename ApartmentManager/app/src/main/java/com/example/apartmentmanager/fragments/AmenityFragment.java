package com.example.apartmentmanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.adapters.AmenityAdapter;
import com.example.apartmentmanager.viewmodels.AmenityViewModel;

import java.util.ArrayList;

public class AmenityFragment extends Fragment {
    private RecyclerView recyclerView;
    private AmenityAdapter amenityAdapter;
    private AmenityViewModel amenityViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_amenity, container, false);

        recyclerView = view.findViewById(R.id.amenity_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        amenityAdapter = new AmenityAdapter(new ArrayList<>());
        recyclerView.setAdapter(amenityAdapter);

        amenityViewModel = new ViewModelProvider(this).get(AmenityViewModel.class);
        amenityViewModel.getAmenities().observe(getViewLifecycleOwner(), amenities -> {
            amenityAdapter = new AmenityAdapter(amenities);
            recyclerView.setAdapter(amenityAdapter);
        });

        return view;
    }
}