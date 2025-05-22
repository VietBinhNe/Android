package com.example.apartmentmanager.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.adapters.ApartmentAdapter;
import com.example.apartmentmanager.models.Apartment;
import com.example.apartmentmanager.network.FirebaseService;

import java.util.ArrayList;
import java.util.List;

public class RoomAvailabilityFragment extends Fragment {
    private static final String TAG = "RoomAvailabilityFragment";
    private RecyclerView recyclerView;
    private View progressBar;
    private ApartmentAdapter apartmentAdapter;
    private FirebaseService firebaseService;
    private List<Apartment> apartmentList;
    private String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating RoomAvailabilityFragment view");
        View view = inflater.inflate(R.layout.fragment_room_availability, container, false);

        try {
            recyclerView = view.findViewById(R.id.room_recycler_view);
            progressBar = view.findViewById(R.id.progress_bar);
            if (recyclerView == null || progressBar == null) {
                Log.e(TAG, "RecyclerView or ProgressBar is null");
                Toast.makeText(getContext(), "Lỗi giao diện", Toast.LENGTH_SHORT).show();
                return view;
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            firebaseService = new FirebaseService();
            userRole = getArguments() != null ? getArguments().getString("userRole") : "resident";
            Log.d(TAG, "User role: " + userRole);

            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            apartmentList = new ArrayList<>();
            apartmentAdapter = new ApartmentAdapter(apartmentList, apartment -> {});
            recyclerView.setAdapter(apartmentAdapter);

            loadRooms();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khi tải danh sách phòng", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadRooms() {
        Log.d(TAG, "Loading rooms");
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        firebaseService.getApartments(apartments -> {
            try {
                apartmentList.clear();
                if (apartments == null || apartments.isEmpty()) {
                    Log.e(TAG, "No rooms found");
                    Toast.makeText(getContext(), "Không có dữ liệu phòng", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    return;
                }
                Log.d(TAG, "Total rooms loaded: " + apartments.size());
                apartmentList.addAll(apartments);
                apartmentAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e(TAG, "Error loading rooms: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi khi tải danh sách phòng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}