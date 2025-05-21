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
    private static final String TAG = "RoomAvailability";
    private RecyclerView recyclerView;
    private ApartmentAdapter apartmentAdapter;
    private FirebaseService firebaseService;
    private String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Initializing RoomAvailabilityFragment");
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_room_availability, container, false);
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: Failed to inflate layout", e);
            Toast.makeText(getContext(), "Lỗi giao diện: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }

        if (view == null) {
            Log.e(TAG, "onCreateView: View is null after inflation");
            return null;
        }

        userRole = requireActivity().getIntent().getStringExtra("userRole");
        recyclerView = view.findViewById(R.id.room_recycler_view);
        if (recyclerView == null) {
            Log.e(TAG, "onCreateView: RecyclerView is null");
            Toast.makeText(getContext(), "Không tìm thấy RecyclerView", Toast.LENGTH_LONG).show();
            return view;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseService = new FirebaseService();
        Log.d(TAG, "onCreateView: Fetching apartments from Firestore");
        firebaseService.getApartments(apartments -> {
            if (apartments == null) {
                Log.e(TAG, "onCreateView: Apartments list is null");
                Toast.makeText(getContext(), "Không thể tải danh sách phòng", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Apartment> availableApartments = new ArrayList<>();
            for (Apartment apartment : apartments) {
                if (apartment == null) {
                    Log.w(TAG, "onCreateView: Found null apartment in list");
                    continue;
                }
                if (apartment.getStatus() == null) {
                    Log.w(TAG, "onCreateView: Apartment status is null for ID: " + apartment.getId());
                    continue;
                }
                if ("available".equals(apartment.getStatus())) {
                    availableApartments.add(apartment);
                }
            }
            Log.d(TAG, "onCreateView: Available apartments count: " + availableApartments.size());
            apartmentAdapter = new ApartmentAdapter(availableApartments, this::showRoomDetailsDialog);
            recyclerView.setAdapter(apartmentAdapter);
            if (availableApartments.isEmpty()) {
                Toast.makeText(getContext(), "Không có phòng trống", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showRoomDetailsDialog(Apartment apartment) {
        if (apartment == null) {
            Log.e(TAG, "showRoomDetailsDialog: Apartment is null");
            Toast.makeText(getContext(), "Không thể hiển thị thông tin phòng", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "showRoomDetailsDialog: Showing details for apartment: " + apartment.getId());
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
        try {
            dialog.setContentView(R.layout.dialog_room_details);

            // Tùy chỉnh chiều ngang dialog
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
                window.setAttributes(params);
            }
        } catch (Exception e) {
            Log.e(TAG, "showRoomDetailsDialog: Failed to set content view for dialog", e);
            Toast.makeText(getContext(), "Lỗi giao diện dialog: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        TextView numberTextView = dialog.findViewById(R.id.room_number);
        TextView statusLabelTextView = dialog.findViewById(R.id.status_label);
        TextView statusTextView = dialog.findViewById(R.id.room_status);
        TextView priceLabelTextView = dialog.findViewById(R.id.price_label);
        TextView priceTextView = dialog.findViewById(R.id.room_price);
        TextView descriptionLabelTextView = dialog.findViewById(R.id.description_label);
        TextView descriptionTextView = dialog.findViewById(R.id.room_description);
        TextView detailsLabelTextView = dialog.findViewById(R.id.details_label);
        TextView detailsTextView = dialog.findViewById(R.id.room_details);
        MaterialButton requestButton = dialog.findViewById(R.id.request_button);
        MaterialButton closeButton = dialog.findViewById(R.id.close_button);

        if (numberTextView == null || statusLabelTextView == null || statusTextView == null ||
                priceLabelTextView == null || priceTextView == null ||
                descriptionLabelTextView == null || descriptionTextView == null ||
                detailsLabelTextView == null || detailsTextView == null ||
                requestButton == null || closeButton == null) {
            Log.e(TAG, "showRoomDetailsDialog: One or more views in dialog are null");
            Toast.makeText(getContext(), "Lỗi giao diện dialog: View không tìm thấy", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            return;
        }

        numberTextView.setText("Phòng: " + (apartment.getNumber() != null ? apartment.getNumber() : "Không có số phòng"));
        statusLabelTextView.setText("Trạng thái:");
        statusTextView.setText(apartment.getStatus() != null ? apartment.getStatus() : "Không xác định");
        priceLabelTextView.setText("Giá:");
        priceTextView.setText(String.format("%.2f", apartment.getPrice() != 0.0 ? apartment.getPrice() : 0.0));
        descriptionLabelTextView.setText("Mô tả:");
        descriptionTextView.setText(apartment.getDescription() != null ? apartment.getDescription() : "Không có mô tả");
        detailsLabelTextView.setText("Chi tiết:");
        detailsTextView.setText(apartment.getDetails() != null ? apartment.getDetails() : "Không có chi tiết");

        // Ẩn nút "Yêu cầu" đối với admin
        requestButton.setVisibility("resident".equals(userRole) ? View.VISIBLE : View.GONE);
        if ("resident".equals(userRole)) {
            requestButton.setOnClickListener(v -> {
                String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                        FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
                if (userId == null) {
                    Log.e(TAG, "showRoomDetailsDialog: User ID is null");
                    Toast.makeText(getContext(), "Lỗi: Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                Request request = new Request(
                        UUID.randomUUID().toString(),
                        "Yêu cầu thuê phòng " + (apartment.getNumber() != null ? apartment.getNumber() : "Không xác định"),
                        "Người dùng " + userId + " yêu cầu thuê phòng " + apartment.getId(),
                        "2025-05-20",
                        "pending",
                        userId
                );
                Log.d(TAG, "showRoomDetailsDialog: Sending rent request for apartment: " + apartment.getId());
                firebaseService.addRequest(request, success -> {
                    if (success) {
                        Toast.makeText(getContext(), "Yêu cầu thuê đã được gửi", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Gửi yêu cầu thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}