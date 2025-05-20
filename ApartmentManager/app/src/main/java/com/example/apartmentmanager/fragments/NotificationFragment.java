package com.example.apartmentmanager.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.adapters.NotificationAdapter;
import com.example.apartmentmanager.models.Notification;
import com.example.apartmentmanager.network.FirebaseService;
import com.example.apartmentmanager.viewmodels.NotificationViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationFragment extends Fragment {
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private NotificationViewModel notificationViewModel;
    private FirebaseService firebaseService;
    private String userRole;
    private FloatingActionButton addNotificationFab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        userRole = requireActivity().getIntent().getStringExtra("userRole");
        recyclerView = view.findViewById(R.id.notification_recycler_view);
        addNotificationFab = view.findViewById(R.id.add_notification_fab);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notificationAdapter = new NotificationAdapter(new ArrayList<>());
        recyclerView.setAdapter(notificationAdapter);

        firebaseService = new FirebaseService();
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        if ("admin".equals(userRole)) {
            addNotificationFab.setVisibility(View.VISIBLE);
            addNotificationFab.setOnClickListener(v -> showAddNotificationDialog());
            notificationViewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
                notificationAdapter = new NotificationAdapter(notifications);
                recyclerView.setAdapter(notificationAdapter);
            });
        } else {
            addNotificationFab.setVisibility(View.GONE);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            firebaseService.getApartmentByResident(userId, apartment -> {
                if (apartment != null) {
                    String apartmentId = apartment.getId();
                    firebaseService.getNotifications(notifications -> {
                        List<Notification> filteredNotifications = new ArrayList<>();
                        for (Notification notification : notifications) {
                            if (notification.getApartmentId() == null || notification.getApartmentId().equals(apartmentId)) {
                                filteredNotifications.add(notification);
                            }
                        }
                        notificationAdapter = new NotificationAdapter(filteredNotifications);
                        recyclerView.setAdapter(notificationAdapter);
                    });
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy phòng của bạn", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }

    public void showAddNotificationDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_notification);

        // Tùy chỉnh chiều ngang dialog
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95); // 95% chiều ngang màn hình
            window.setAttributes(params);
        }

        TextInputEditText titleEditText = dialog.findViewById(R.id.notification_title_input);
        TextInputEditText messageEditText = dialog.findViewById(R.id.notification_message_input);
        TextInputEditText dateEditText = dialog.findViewById(R.id.notification_date_input);
        RadioGroup sendToRadioGroup = dialog.findViewById(R.id.send_to_radio_group);
        RadioButton sendToAll = dialog.findViewById(R.id.send_to_all);
        RadioButton sendToRoom = dialog.findViewById(R.id.send_to_room);
        TextInputLayout roomIdLayout = dialog.findViewById(R.id.room_id_layout);
        TextInputEditText roomIdEditText = dialog.findViewById(R.id.room_id_input);
        MaterialButton sendButton = dialog.findViewById(R.id.send_button);
        MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);

        sendToRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.send_to_room) {
                roomIdLayout.setVisibility(View.VISIBLE);
            } else {
                roomIdLayout.setVisibility(View.GONE);
            }
        });

        sendButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String message = messageEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();
            String roomId = sendToRoom.isChecked() ? roomIdEditText.getText().toString().trim() : null;

            if (title.isEmpty() || message.isEmpty() || date.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (sendToRoom.isChecked() && roomId.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập ID phòng", Toast.LENGTH_SHORT).show();
                return;
            }

            Notification notification = new Notification(UUID.randomUUID().toString(), title, message, date, roomId);
            firebaseService.addNotification(notification, success -> {
                if (success) {
                    Toast.makeText(getContext(), "Gửi thông báo thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Gửi thông báo thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}