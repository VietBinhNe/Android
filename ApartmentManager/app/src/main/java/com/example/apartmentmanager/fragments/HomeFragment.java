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
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.network.FirebaseService;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private TextView welcomeTextView;
    private ImageView mainImageView;
    private MaterialButton infoButton, managementButton;
    private View infoCard, managementCard;
    private FirebaseService firebaseService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating HomeFragment view");
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        try {
            welcomeTextView = view.findViewById(R.id.welcome_text);
            mainImageView = view.findViewById(R.id.main_image);
            infoButton = view.findViewById(R.id.info_button);
            managementButton = view.findViewById(R.id.management_button);
            infoCard = view.findViewById(R.id.info_card);
            managementCard = view.findViewById(R.id.management_card);

            if (welcomeTextView == null || mainImageView == null || infoButton == null ||
                    managementButton == null || infoCard == null || managementCard == null) {
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

            // Set visibility and text based on user role
            if ("admin".equals(userRole)) {
                managementCard.setVisibility(View.VISIBLE);
                managementButton.setVisibility(View.VISIBLE);
                TextView managementTitle = view.findViewById(R.id.management_title);
                managementTitle.setText("Quản lý phòng");
                managementButton.setOnClickListener(v -> {
                    try {
                        Navigation.findNavController(v).navigate(R.id.nav_apartment_management);
                    } catch (Exception e) {
                        Log.e(TAG, "Navigation error: " + e.getMessage(), e);
                        Toast.makeText(getContext(), "Lỗi điều hướng", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d(TAG, "Management card set to VISIBLE for admin (Apartment Management)");
            } else {
                managementCard.setVisibility(View.VISIBLE);
                managementButton.setVisibility(View.VISIBLE);
                TextView managementTitle = view.findViewById(R.id.management_title);
                managementTitle.setText("Xem phòng còn trống");
                managementButton.setOnClickListener(v -> {
                    try {
                        Navigation.findNavController(v).navigate(R.id.nav_room_availability);
                    } catch (Exception e) {
                        Log.e(TAG, "Navigation error: " + e.getMessage(), e);
                        Toast.makeText(getContext(), "Lỗi điều hướng", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d(TAG, "Management card set to VISIBLE for resident (Room Availability)");
            }

            // Force UI refresh
            view.post(() -> view.invalidate());

            infoButton.setOnClickListener(v -> showInfoDialog());
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khi tải giao diện", Toast.LENGTH_LONG).show();
        }

        return view;
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