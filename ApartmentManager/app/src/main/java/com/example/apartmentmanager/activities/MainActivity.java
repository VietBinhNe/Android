package com.example.apartmentmanager.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.apartmentmanager.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private NavController navController;
    private Handler navigationHandler = new Handler(Looper.getMainLooper());
    private static final long NAVIGATION_DEBOUNCE_TIME = 200;
    private boolean isNavigating = false;
    private String userRole;
    private Bundle args;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(navView, navController);
        } else {
            Log.e(TAG, "NavHostFragment is null");
            return;
        }

        userRole = getIntent() != null ? getIntent().getStringExtra("userRole") : "resident";
        Log.d(TAG, "User role set to: " + userRole);
        args = new Bundle();
        args.putString("userRole", userRole);
        navController.setGraph(R.navigation.nav_graph, args);

        findViewById(R.id.notification_icon).setOnClickListener(v -> {
            Log.d(TAG, "Notification icon clicked");
            if (!isNavigating) {
                isNavigating = true;
                navigationHandler.postDelayed(() -> isNavigating = false, NAVIGATION_DEBOUNCE_TIME);
                if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() != R.id.nav_notification) {
                    Log.d(TAG, "Navigating to NotificationFragment");
                    navController.popBackStack(R.id.nav_home, false);
                    navController.navigate(R.id.nav_notification, args); // Truyền userRole vào fragment
                } else {
                    Log.w(TAG, "Already on NotificationFragment or destination is null");
                    Toast.makeText(this, "Đã ở màn hình thông báo", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w(TAG, "Navigation debounce active, ignoring click");
                Toast.makeText(this, "Đang xử lý, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
            }
        });

        navView.setOnItemSelectedListener(item -> {
            if (navController.getCurrentDestination() == null) {
                Log.e(TAG, "Current destination is null");
                return false;
            }
            int currentDestinationId = navController.getCurrentDestination().getId();
            int selectedDestinationId = item.getItemId();

            if (currentDestinationId == selectedDestinationId) {
                Log.d(TAG, "Already on destination: " + selectedDestinationId);
                return true;
            }

            try {
                navController.navigate(selectedDestinationId, args); // Truyền userRole khi điều hướng
                return true;
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Navigation error: " + e.getMessage(), e);
                Toast.makeText(this, "Không thể điều hướng đến mục này", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (navController.getCurrentDestination().getId() == R.id.nav_home) {
                    finish();
                } else {
                    navController.popBackStack();
                }
            }
        });
    }
}