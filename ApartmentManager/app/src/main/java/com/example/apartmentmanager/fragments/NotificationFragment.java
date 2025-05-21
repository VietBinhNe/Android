package com.example.apartmentmanager.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
    private List<Notification> notificationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Inflated layout for NotificationFragment");
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.notification_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        addNotificationFab = view.findViewById(R.id.add_notification_fab);
        Log.d(TAG, "Views initialized: recyclerView=" + (recyclerView != null) + ", progressBar=" + (progressBar != null) + ", addNotificationFab=" + (addNotificationFab != null));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseService = new FirebaseService();
        userRole = getArguments() != null ? getArguments().getString("userRole") : "resident";
        Log.d(TAG, "User role: " + userRole);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(notificationAdapter);

        if ("admin".equals(userRole)) {
            addNotificationFab.setVisibility(View.VISIBLE);
            addNotificationFab.setOnClickListener(v -> showAddNotificationDialog());
            Log.d(TAG, "Add notification FAB set to VISIBLE for admin");
        } else {
            addNotificationFab.setVisibility(View.GONE);
            Log.d(TAG, "Add notification FAB set to GONE for non-admin");
        }

        firebaseService.getNotifications(notifications -> {
            notificationList.clear();
            if (notifications == null || notifications.isEmpty()) {
                Log.e(TAG, "No notifications found");
                Toast.makeText(getContext(), "Không có thông báo nào", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                return;
            }
            Log.d(TAG, "Total notifications loaded: " + notifications.size());
            for (Notification notification : notifications) {
                Log.d(TAG, "Loaded notification: " + notification.getId());
            }
            notificationList.addAll(notifications);
            notificationAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });

        return view;
    }

    private void showAddNotificationDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_notification);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
            window.setAttributes(params);
        }

        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        dialogTitle.setText("Thêm thông báo");

        TextInputEditText titleInput = dialog.findViewById(R.id.notification_title_input);
        TextInputEditText contentInput = dialog.findViewById(R.id.notification_content_input);
        RadioGroup sendOptionsGroup = dialog.findViewById(R.id.send_options_group);
        RadioButton sendToAllOption = dialog.findViewById(R.id.send_to_all_option);
        RadioButton sendToSpecificOption = dialog.findViewById(R.id.send_to_specific_option);
        TextInputEditText apartmentIdInput = dialog.findViewById(R.id.apartment_id_input);
        MaterialButton addButton = dialog.findViewById(R.id.add_button);
        MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);

        // Ẩn ô nhập apartmentId ban đầu
        apartmentIdInput.setVisibility(View.GONE);

        // Xử lý khi chọn tùy chọn gửi
        sendOptionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.send_to_specific_option) {
                apartmentIdInput.setVisibility(View.VISIBLE);
            } else {
                apartmentIdInput.setVisibility(View.GONE);
            }
        });

        addButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String content = contentInput.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            String notificationId = UUID.randomUUID().toString();
            Notification notification = new Notification(notificationId, title, content);

            if (sendToAllOption.isChecked()) {
                // Gửi đến tất cả resident
                firebaseService.sendNotificationToAllResidents(notification, success -> {
                    if (success) {
                        Toast.makeText(getContext(), "Gửi thông báo đến tất cả resident thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        progressBar.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        firebaseService.getNotifications(notifications -> {
                            notificationList.clear();
                            notificationList.addAll(notifications);
                            notificationAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        });
                    } else {
                        Toast.makeText(getContext(), "Gửi thông báo thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (sendToSpecificOption.isChecked()) {
                // Gửi đến phòng cụ thể
                String apartmentId = apartmentIdInput.getText().toString().trim();
                if (apartmentId.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập ID phòng", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseService.sendNotificationToApartment(notification, apartmentId, success -> {
                    if (success) {
                        Toast.makeText(getContext(), "Gửi thông báo đến phòng " + apartmentId + " thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        progressBar.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        firebaseService.getNotifications(notifications -> {
                            notificationList.clear();
                            notificationList.addAll(notifications);
                            notificationAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        });
                    } else {
                        Toast.makeText(getContext(), "Gửi thông báo thất bại. Kiểm tra ID phòng!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}