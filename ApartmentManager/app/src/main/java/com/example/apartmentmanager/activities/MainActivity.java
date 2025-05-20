package com.example.apartmentmanager.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.apartmentmanager.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String userRole = getIntent().getStringExtra("userRole");
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        ImageButton notificationIcon = findViewById(R.id.notification_icon);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bottomNav, navController);

        notificationIcon.setOnClickListener(v -> {
            navController.navigate(R.id.nav_notification);
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (navController.getCurrentDestination().getId() != R.id.nav_home) {
                    navController.navigate(R.id.nav_home);
                } else {
                    finish();
                }
            }
        });
    }
}