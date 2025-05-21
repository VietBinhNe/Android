package com.example.apartmentmanager.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.adapters.NotificationAdapter;
import com.example.apartmentmanager.models.Notification;
import com.example.apartmentmanager.network.FirebaseService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationFragment extends Fragment {
    private static final String TAG = "NotificationFragment";
    private RecyclerView recyclerView;
    private View progressBar;
    private NotificationAdapter notificationAdapter;
    private FirebaseService firebaseService;
    private FloatingActionButton addNotificationFab;
    private String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_notification, container, false);
            Log.d(TAG, "Inflated layout for NotificationFragment");

            recyclerView = view.findViewById(R.id.notification_recycler_view);
            progressBar = view.findViewById(R.id.progress_bar);
            addNotificationFab = view.findViewById(R.id.add_notification_fab);
            Log.d(TAG, "Views initialized: recyclerView=" + (recyclerView != null) + ", progressBar=" + (progressBar != null) + ", addNotificationFab=" + (addNotificationFab != null));

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            firebaseService = new FirebaseService();
            notificationAdapter = new NotificationAdapter(new ArrayList<>());
            recyclerView.setAdapter(notificationAdapter);

            // Lấy userRole từ arguments
            if (getArguments() != null) {
                userRole = getArguments().getString("userRole");
                Log.d(TAG, "User role: " + userRole);
            } else {
                userRole = "resident";
                Log.w(TAG, "User role not found in arguments, defaulting to resident");
            }

            // Hiển thị nút "Thêm thông báo" chỉ cho admin
            if ("admin".equals(userRole)) {
                if (addNotificationFab != null) {
                    addNotificationFab.setVisibility(View.VISIBLE);
                    addNotificationFab.setOnClickListener(v -> showAddNotificationDialog());
                    Log.d(TAG, "Add notification FAB set to VISIBLE for admin");
                } else {
                    Log.e(TAG, "addNotificationFab is null, cannot set visibility");
                }
            } else {
                if (addNotificationFab != null) {
                    addNotificationFab.setVisibility(View.GONE);
                    Log.d(TAG, "Add notification FAB set to GONE for non-admin");
                } else {
                    Log.e(TAG, "addNotificationFab is null, cannot set visibility");
                }
            }

            // Hiển thị ProgressBar khi tải dữ liệu
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            firebaseService.getNotifications(notifications -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    List<Notification> filteredNotifications = new ArrayList<>();
                    if (notifications != null) {
                        for (Notification notification : notifications) {
                            filteredNotifications.add(notification);
                        }
                        Log.d(TAG, "Loaded " + filteredNotifications.size() + " notifications");
                    } else {
                        Log.w(TAG, "No notifications found or error occurred");
                        Toast.makeText(getContext(), "Không có thông báo hoặc lỗi khi tải dữ liệu", Toast.LENGTH_LONG).show();
                    }
                    notificationAdapter = new NotificationAdapter(filteredNotifications);
                    recyclerView.setAdapter(notificationAdapter);
                } catch (Exception e) {
                    Log.e(TAG, "Error displaying notifications", e);
                    Toast.makeText(getContext(), "Lỗi khi tải thông báo: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView", e);
            Toast.makeText(getContext(), "Lỗi khi tải fragment: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void showAddNotificationDialog() {
        try {
            Dialog dialog = new Dialog(requireContext());
            dialog.setContentView(R.layout.dialog_add_notification);

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
                window.setAttributes(params);
            }

            TextInputEditText titleEditText = dialog.findViewById(R.id.notification_title_input);
            TextInputEditText contentEditText = dialog.findViewById(R.id.notification_content_input);
            TextInputEditText dateEditText = dialog.findViewById(R.id.notification_date_input);
            MaterialButton sendButton = dialog.findViewById(R.id.send_button);
            MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);

            if (titleEditText == null || contentEditText == null || dateEditText == null ||
                    sendButton == null || cancelButton == null) {
                Log.e(TAG, "showAddNotificationDialog: One or more views in dialog are null");
                Toast.makeText(getContext(), "Lỗi giao diện: Không tìm thấy view", Toast.LENGTH_LONG).show();
                return;
            }

            sendButton.setOnClickListener(v -> {
                String title = titleEditText.getText().toString().trim();
                String content = contentEditText.getText().toString().trim();
                String date = dateEditText.getText().toString().trim();

                if (title.isEmpty() || content.isEmpty() || date.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                Notification notification = new Notification(
                        UUID.randomUUID().toString(),
                        title,
                        content,
                        date
                );
                firebaseService.addNotification(notification, success -> {
                    if (success) {
                        Toast.makeText(getContext(), "Thêm thông báo thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        // Cập nhật lại danh sách thông báo
                        progressBar.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        firebaseService.getNotifications(notifications -> {
                            notificationAdapter = new NotificationAdapter(new ArrayList<>(notifications));
                            recyclerView.setAdapter(notificationAdapter);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        });
                    } else {
                        Toast.makeText(getContext(), "Thêm thông báo thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            cancelButton.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "showAddNotificationDialog: Error showing dialog", e);
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}