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
import com.example.apartmentmanager.adapters.UserAdapter;
import com.example.apartmentmanager.models.User;
import com.example.apartmentmanager.network.FirebaseService;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class UserManagementFragment extends Fragment {
    private static final String TAG = "UserManagementFragment";
    private RecyclerView recyclerView;
    private View progressBar;
    private UserAdapter userAdapter;
    private FirebaseService firebaseService;
    private List<User> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating UserManagementFragment view");
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);

        try {
            recyclerView = view.findViewById(R.id.user_recycler_view);
            progressBar = view.findViewById(R.id.progress_bar);
            if (recyclerView == null || progressBar == null) {
                Log.e(TAG, "RecyclerView or ProgressBar is null");
                Toast.makeText(getContext(), "Lỗi giao diện", Toast.LENGTH_SHORT).show();
                return view;
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            firebaseService = new FirebaseService();
            userList = new ArrayList<>();
            userAdapter = new UserAdapter(userList, this::showUserDetailsDialog);
            recyclerView.setAdapter(userAdapter);

            loadAllUsers();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khi tải danh sách người dùng", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadAllUsers() {
        Log.d(TAG, "Loading all users");
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        firebaseService.getAllUsers(users -> {
            try {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (users == null || users.isEmpty()) {
                    Log.e(TAG, "loadAllUsers: No users found");
                    Toast.makeText(getContext(), "Không có dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG, "loadAllUsers: Found " + users.size() + " users");
                userList.clear();
                userList.addAll(users);
                userAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.e(TAG, "Error loading users: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi khi tải danh sách người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserDetailsDialog(User user) {
        try {
            if (user == null) {
                Log.e(TAG, "User is null in showUserDetailsDialog");
                Toast.makeText(getContext(), "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                return;
            }

            Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
            dialog.setContentView(R.layout.dialog_user_details);

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
                window.setAttributes(params);
            }

            TextView nameTextView = dialog.findViewById(R.id.user_name);
            TextView emailTextView = dialog.findViewById(R.id.user_email);
            TextView phoneTextView = dialog.findViewById(R.id.user_phone);
            TextView idNumberTextView = dialog.findViewById(R.id.user_id_number);
            TextView totalPaidTextView = dialog.findViewById(R.id.user_total_paid);
            TextView apartmentNumberTextView = dialog.findViewById(R.id.user_apartment_number);
            TextView statusTextView = dialog.findViewById(R.id.user_status);
            TextView leaseDurationTextView = dialog.findViewById(R.id.user_lease_duration);
            MaterialButton closeButton = dialog.findViewById(R.id.close_button);

            if (nameTextView == null || emailTextView == null || phoneTextView == null ||
                    idNumberTextView == null || totalPaidTextView == null || apartmentNumberTextView == null ||
                    statusTextView == null || leaseDurationTextView == null || closeButton == null) {
                Log.e(TAG, "One or more views in dialog_user_details are null");
                Toast.makeText(getContext(), "Lỗi giao diện", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
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

            closeButton.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing user details dialog: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khi hiển thị thông tin người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}