package com.example.apartmentmanager.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.adapters.ApartmentAdapter;
import com.example.apartmentmanager.models.Apartment;
import com.example.apartmentmanager.models.Request;
import com.example.apartmentmanager.network.FirebaseService;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
            apartmentAdapter = new ApartmentAdapter(apartmentList, this::showRoomDetailsDialog);
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
                for (Apartment apartment : apartments) {
                    if ("available".equals(apartment.getStatus())) {
                        apartmentList.add(apartment);
                    }
                }
                apartmentAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e(TAG, "Error loading rooms: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi khi tải danh sách phòng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRoomDetailsDialog(Apartment apartment) {
        try {
            if (apartment == null) {
                Log.e(TAG, "Apartment is null in showRoomDetailsDialog");
                Toast.makeText(getContext(), "Lỗi: Không tìm thấy thông tin phòng", Toast.LENGTH_SHORT).show();
                return;
            }

            Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
            dialog.setContentView(R.layout.dialog_room_details);

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
                window.setAttributes(params);
            }

            TextView numberTextView = dialog.findViewById(R.id.room_number);
            TextView statusTextView = dialog.findViewById(R.id.room_status);
            TextView priceTextView = dialog.findViewById(R.id.room_price);
            TextView descriptionTextView = dialog.findViewById(R.id.room_description);
            TextView detailsTextView = dialog.findViewById(R.id.room_details);
            MaterialButton requestButton = dialog.findViewById(R.id.request_button);
            MaterialButton closeButton = dialog.findViewById(R.id.close_button);

            if (numberTextView == null || statusTextView == null || priceTextView == null ||
                    descriptionTextView == null || detailsTextView == null || requestButton == null ||
                    closeButton == null) {
                Log.e(TAG, "One or more views in dialog_room_details are null");
                Toast.makeText(getContext(), "Lỗi giao diện", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            numberTextView.setText("Phòng: " + (apartment.getNumber() != null ? apartment.getNumber() : "Không xác định"));
            statusTextView.setText("Chưa thuê");
            priceTextView.setText("Giá: " + String.format("%.2f", apartment.getPrice()));
            descriptionTextView.setText("Mô tả: " + (apartment.getDescription() != null ? apartment.getDescription() : "Không có mô tả"));
            detailsTextView.setText("Chi tiết: " + (apartment.getDetails() != null ? apartment.getDetails() : "Không có chi tiết"));

            if ("resident".equals(userRole)) {
                requestButton.setVisibility(View.VISIBLE);
                requestButton.setOnClickListener(v -> {
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String requestId = UUID.randomUUID().toString();
                    Request request = new Request(
                            requestId,
                            "Yêu cầu thuê phòng " + apartment.getNumber(),
                            "Cư dân muốn thuê phòng " + apartment.getNumber(),
                            userId,
                            apartment.getId()
                    );
                    firebaseService.addRentRequest(request, success -> {
                        if (success) {
                            Toast.makeText(getContext(), "Gửi yêu cầu thuê phòng thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Gửi yêu cầu thuê phòng thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            } else {
                requestButton.setVisibility(View.GONE);
            }

            closeButton.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing room details dialog: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}