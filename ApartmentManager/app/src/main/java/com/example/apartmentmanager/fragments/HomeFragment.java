package com.example.apartmentmanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.apartmentmanager.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {
    private TextView welcomeTextView;
    private ImageView mainImageView;
    private MaterialButton bookingManagementButton;
    private MaterialButton roomAvailabilityButton;
    private MaterialButton residentManagementButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        welcomeTextView = view.findViewById(R.id.welcome_text);
        mainImageView = view.findViewById(R.id.main_image);
        bookingManagementButton = view.findViewById(R.id.booking_management_button);
        roomAvailabilityButton = view.findViewById(R.id.room_availability_button);
        residentManagementButton = view.findViewById(R.id.resident_management_button);

        String userRole = requireActivity().getIntent().getStringExtra("userRole");

        String userEmail = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getEmail()
                : "Khách";
        welcomeTextView.setText("Chào mừng, " + userEmail);

        mainImageView.setImageResource(R.drawable.main_image);

        bookingManagementButton.setVisibility("admin".equals(userRole) ? View.VISIBLE : View.GONE);
        bookingManagementButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_booking_management));

        roomAvailabilityButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_room_availability));

        residentManagementButton.setVisibility("admin".equals(userRole) ? View.VISIBLE : View.GONE);
        residentManagementButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_resident_management));

        return view;
    }
}