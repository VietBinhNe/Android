package com.example.apartmentmanager.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
    private MaterialButton roomAvailabilityButton;
    private FirebaseService firebaseService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        welcomeTextView = view.findViewById(R.id.welcome_text);
        mainImageView = view.findViewById(R.id.main_image);
        roomAvailabilityButton = view.findViewById(R.id.room_availability_button);

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

        // Chỉ hiển thị nút "Xem phòng trống" cho resident
        roomAvailabilityButton.setVisibility("resident".equals(userRole) ? View.VISIBLE : View.GONE);
        roomAvailabilityButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_room_availability));

        return view;
    }
}