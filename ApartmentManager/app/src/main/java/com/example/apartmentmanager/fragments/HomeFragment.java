package com.example.apartmentmanager.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
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

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private TextView welcomeTextView;
    private ImageView mainImageView;
    private MaterialButton infoButton, userManagementButton;
    private View userManagementCard;
    private RecyclerView availableRoomsRecyclerView;
    private ApartmentAdapter apartmentAdapter;
    private FirebaseService firebaseService;
    private List<Apartment> availableApartments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating HomeFragment view");
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        try {
            welcomeTextView = view.findViewById(R.id.welcome_text);
            mainImageView = view.findViewById(R.id.main_image);
            infoButton = view.findViewById(R.id.info_button);
            userManagementButton = view.findViewById(R.id.user_management_button);
            userManagementCard = view.findViewById(R.id.user_management_card);
            availableRoomsRecyclerView = view.findViewById(R.id.available_rooms_recycler_view);

            if (welcomeTextView == null || mainImageView == null || infoButton == null ||
                    userManagementButton == null || userManagementCard == null || availableRoomsRecyclerView == null) {
                Log.e(TAG, "One or more views are null");
                Toast.makeText(getContext(), "Lỗi khi tải giao diện", Toast.LENGTH_LONG).show();
                return view;
            }

            firebaseService = new FirebaseService();
            String userRole = getArguments() != null ? getArguments().getString("userRole") : "resident";
            String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                    : null;

            if (userId != null) {
                firebaseService.getUser(userId, user -> {
                    if (user != null && user.getName() != null) {
                        welcomeTextView.setText("Chào mừng, " + user.getName());
                    } else {
                        welcomeTextView.setText("Chào mừng, Khách");
                    }
                });
            } else {
                welcomeTextView.setText("Chào mừng, Khách");
            }

            mainImageView.setImageResource(R.drawable.main_image);

            // Set visibility for user management card (only for admin)
            if ("admin".equals(userRole)) {
                userManagementCard.setVisibility(View.VISIBLE);
                userManagementButton.setVisibility(View.VISIBLE);
                availableRoomsRecyclerView.setVisibility(View.GONE);
                Log.d(TAG, "User management card set to VISIBLE for admin");
            } else {
                userManagementCard.setVisibility(View.GONE);
                userManagementButton.setVisibility(View.GONE);
                availableRoomsRecyclerView.setVisibility(View.VISIBLE);
                Log.d(TAG, "User management card set to GONE, available rooms set to VISIBLE for resident");
                loadAvailableApartments();
            }

            // Force UI refresh
            view.post(() -> view.invalidate());

            infoButton.setOnClickListener(v -> showInfoDialog());
            userManagementButton.setOnClickListener(v -> {
                try {
                    Navigation.findNavController(v).navigate(R.id.nav_resident_management);
                } catch (Exception e) {
                    Log.e(TAG, "Navigation error: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Lỗi điều hướng", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khi tải giao diện", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void loadAvailableApartments() {
        Log.d(TAG, "Loading available apartments for resident");
        availableApartments = new ArrayList<>();
        apartmentAdapter = new ApartmentAdapter(availableApartments, this::sendRentRequest);
        availableRoomsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        availableRoomsRecyclerView.setAdapter(apartmentAdapter);

        firebaseService.getApartments(apartments -> {
            try {
                availableApartments.clear();
                if (apartments != null) {
                    for (Apartment apartment : apartments) {
                        if ("available".equals(apartment.getStatus())) {
                            availableApartments.add(apartment);
                        }
                    }
                }
                if (availableApartments.isEmpty()) {
                    Log.e(TAG, "No available apartments found");
                    Toast.makeText(getContext(), "Không có phòng trống", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Loaded " + availableApartments.size() + " available apartments");
                }
                apartmentAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.e(TAG, "Error loading apartments: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi khi tải danh sách phòng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendRentRequest(Apartment apartment) {
        try {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String requestId = UUID.randomUUID().toString();
            Request request = new Request(
                    requestId,
                    "Yêu cầu thuê phòng " + apartment.getNumber(),
                    "Cư dân muốn thuê phòng " + apartment.getNumber(),
                    userId
            );
            firebaseService.addRequest(request, success -> {
                if (success) {
                    Toast.makeText(getContext(), "Gửi yêu cầu thuê phòng thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Gửi yêu cầu thuê phòng thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error sending rent request: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khi gửi yêu cầu thuê", Toast.LENGTH_SHORT).show();
        }
    }

    private void showInfoDialog() {
        try {
            Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
            dialog.setContentView(R.layout.dialog_info);

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
                window.setAttributes(params);
            }

            MaterialButton closeButton = dialog.findViewById(R.id.close_button);
            if (closeButton != null) {
                closeButton.setOnClickListener(v -> dialog.dismiss());
            } else {
                Log.e(TAG, "Close button is null in dialog_info");
            }

            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing info dialog: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khi hiển thị thông tin", Toast.LENGTH_SHORT).show();
        }
    }
}