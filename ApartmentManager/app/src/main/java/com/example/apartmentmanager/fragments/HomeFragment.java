package com.example.apartmentmanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.apartmentmanager.R;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {
    private TextView welcomeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        welcomeTextView = view.findViewById(R.id.welcome_text);
        String userEmail = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getEmail()
                : "Khách";
        welcomeTextView.setText("Chào mừng, " + userEmail);

        return view;
    }
}