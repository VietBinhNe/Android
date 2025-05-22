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
import com.example.apartmentmanager.models.User;
import com.example.apartmentmanager.network.FirebaseService;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ResidentManagementFragment extends Fragment {
    private static final String TAG = "ResidentManagement";
    private TextView nameTextView, emailTextView, phoneTextView, idNumberTextView, totalPaidTextView, apartmentNumberTextView, statusTextView, leaseDurationTextView;
    private View progressBar, userInfoCard;
    private RecyclerView recyclerView;
    private ApartmentAdapter apartmentAdapter;
    private FirebaseService firebaseService;
    private String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resident_management, container, false);

        recyclerView = view.findViewById(R.id.resident_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        userInfoCard = view.findViewById(R.id.user_info_card);
        nameTextView = view.findViewById(R.id.user_name);
        emailTextView = view.findViewById(R.id.user_email);
        phoneTextView = view.findViewById(R.id.user_phone);
        idNumberTextView = view.findViewById(R.id.user_id_number);
        totalPaidTextView = view.findViewById(R.id.user_total_paid);
        apartmentNumberTextView = view.findViewById(R.id.user_apartment_number);
        statusTextView = view.findViewById(R.id.user_status);
        leaseDurationTextView = view.findViewById(R.id.user_lease_duration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseService = new FirebaseService();
        userRole = getArguments() != null ? getArguments().getString("userRole") : "resident";
        Log.d(TAG, "User role: " + userRole);

        if ("admin".equals(userRole)) {
            recyclerView.setVisibility(View.VISIBLE);
            userInfoCard.setVisibility(View.GONE);
            loadAllApartments();
        } else {
            recyclerView.setVisibility(View.GONE);
            userInfoCard.setVisibility(View.VISIBLE);
            loadResidentInfo();
        }

        return view;
    }

    private void loadAllApartments() {
        Log.d(TAG, "Loading all apartments for admin");
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        firebaseService.getApartments(apartments -> {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (apartments == null || apartments.isEmpty()) {
                Log.e(TAG, "loadAllApartments: No apartments found");
                Toast.makeText(getContext(), "Không có dữ liệu phòng", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "loadAllApartments: Found " + apartments.size() + " apartments");
            for (Apartment apartment : apartments) {
                Log.d(TAG, "Apartment: " + apartment.getId() + ", Number: " + apartment.getNumber() + ", Status: " + apartment.getStatus());
            }
            apartmentAdapter = new ApartmentAdapter(apartments, this::showApartmentDetailsDialog);
            recyclerView.setAdapter(apartmentAdapter);
        });
    }

    private void loadResidentInfo() {
        Log.d(TAG, "Loading resident info");
        progressBar.setVisibility(View.VISIBLE);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseService.getUser(userId, user -> {
            progressBar.setVisibility(View.GONE);
            if (user == null) {
                Log.e(TAG, "loadResidentInfo: User not found for ID: " + userId);
                Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                return;
            }

            nameTextView.setText("Tên: " + (user.getName() != null ? user.getName() : "Không xác định"));
            emailTextView.setText("Email: " + (user.getEmail() != null ? user.getEmail() : "Không xác định"));
            phoneTextView.setText("Số điện thoại: " + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "Không xác định"));
            idNumberTextView.setText("Số căn cước: " + (user.getIdNumber() != null ? user.getIdNumber() : "Không xác định"));
            totalPaidTextView.setText("Số tiền đã trả: " + String.format("%.2f", user.getTotalPaid()));
            apartmentNumberTextView.setText("Số nhà: " + (user.getApartmentNumber() != null ? user.getApartmentNumber() : "Không có"));
            statusTextView.setText("Trạng thái: " + (user.getStatus() != null ? user.getStatus() : "Không xác định"));
            leaseDurationTextView.setText("Thời gian thuê: " + (user.getLeaseDuration() != null ? user.getLeaseDuration() : "Không xác định"));
        });
    }

    private void showApartmentDetailsDialog(Apartment apartment) {
        try {
            if (apartment == null) {
                Log.e(TAG, "Apartment is null in showApartmentDetailsDialog");
                Toast.makeText(getContext(), "Lỗi: Không tìm thấy thông tin phòng", Toast.LENGTH_SHORT).show();
                return;
            }

            Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
            dialog.setContentView(R.layout.dialog_apartment_details);

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
                window.setAttributes(params);
            }

            TextView numberTextView = dialog.findViewById(R.id.apartment_number);
            TextView statusTextView = dialog.findViewById(R.id.apartment_status);
            TextView tenantTextView = dialog.findViewById(R.id.tenant_name);
            TextView leaseDurationTextView = dialog.findViewById(R.id.lease_duration);
            TextView priceTextView = dialog.findViewById(R.id.price);
            TextView descriptionTextView = dialog.findViewById(R.id.description);
            TextView detailsTextView = dialog.findViewById(R.id.details);
            MaterialButton closeButton = dialog.findViewById(R.id.close_button);

            if (numberTextView == null || statusTextView == null || tenantTextView == null ||
                    leaseDurationTextView == null || priceTextView == null || descriptionTextView == null ||
                    detailsTextView == null || closeButton == null) {
                Log.e(TAG, "One or more views in dialog_apartment_details are null");
                Toast.makeText(getContext(), "Lỗi giao diện", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            numberTextView.setText("Phòng: " + (apartment.getNumber() != null ? apartment.getNumber() : "Không xác định"));
            statusTextView.setText("Trạng thái: " + ("available".equals(apartment.getStatus()) ? "Chưa thuê" : "Đang thuê"));
            priceTextView.setText("Giá: " + String.format("%.2f", apartment.getPrice()));
            descriptionTextView.setText("Mô tả: " + (apartment.getDescription() != null ? apartment.getDescription() : "Không có mô tả"));
            detailsTextView.setText("Chi tiết: " + (apartment.getDetails() != null ? apartment.getDetails() : "Không có chi tiết"));

            String residentId = apartment.getResidentId();
            if (residentId != null) {
                firebaseService.getUser(residentId, user -> {
                    if (user != null) {
                        tenantTextView.setText("Chủ hộ: " + (user.getName() != null ? user.getName() : "Không xác định"));
                        leaseDurationTextView.setText("Thời gian thuê: " + (user.getLeaseDuration() != null ? user.getLeaseDuration() : "Không xác định"));
                    } else {
                        tenantTextView.setText("Chủ hộ: Không xác định");
                        leaseDurationTextView.setText("Thời gian thuê: Không xác định");
                    }
                });
            } else {
                tenantTextView.setText("Chủ hộ: Chưa có");
                leaseDurationTextView.setText("Thời gian thuê: Chưa xác định");
            }

            closeButton.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing apartment details dialog: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}