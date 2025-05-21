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
import com.example.apartmentmanager.adapters.RequestAdapter;
import com.example.apartmentmanager.models.Request;
import com.example.apartmentmanager.network.FirebaseService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RequestFragment extends Fragment {
    private static final String TAG = "RequestFragment";
    private RecyclerView recyclerView;
    private RequestAdapter requestAdapter;
    private FirebaseService firebaseService;
    private String userRole;
    private FloatingActionButton addRequestFab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, container, false);

        // Lấy userRole từ arguments (nếu có), hoặc từ intent (tạm thời giữ lại để tương thích)
        if (getArguments() != null) {
            userRole = getArguments().getString("userRole");
        } else {
            userRole = requireActivity().getIntent().getStringExtra("userRole");
        }

        recyclerView = view.findViewById(R.id.request_recycler_view);
        addRequestFab = view.findViewById(R.id.add_request_fab);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseService = new FirebaseService();

        if ("admin".equals(userRole)) {
            addRequestFab.setVisibility(View.GONE);
            firebaseService.getRequests(requests -> {
                requestAdapter = new RequestAdapter(requests, this::showRequestDetailsDialog);
                recyclerView.setAdapter(requestAdapter);
            });
        } else {
            addRequestFab.setVisibility(View.VISIBLE);
            addRequestFab.setOnClickListener(v -> showAddRequestDialog());
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d(TAG, "Loading requests for user: " + userId);
            firebaseService.getRequests(requests -> {
                List<Request> filteredRequests = new ArrayList<>();
                for (Request request : requests) {
                    Log.d(TAG, "Request: " + request.getTitle() + ", User ID: " + request.getUserId());
                    if (request.getUserId().equals(userId)) {
                        filteredRequests.add(request);
                    }
                }
                Log.d(TAG, "Filtered requests count: " + filteredRequests.size());
                requestAdapter = new RequestAdapter(filteredRequests);
                recyclerView.setAdapter(requestAdapter);
            });
        }

        return view;
    }

    private void showAddRequestDialog() {
        try {
            Dialog dialog = new Dialog(requireContext());
            dialog.setContentView(R.layout.dialog_add_request);

            // Tùy chỉnh chiều ngang dialog
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
                window.setAttributes(params);
            }

            TextInputEditText titleEditText = dialog.findViewById(R.id.request_title_input);
            TextInputEditText descriptionEditText = dialog.findViewById(R.id.request_description_input);
            TextInputEditText dateEditText = dialog.findViewById(R.id.request_date_input);
            MaterialButton sendButton = dialog.findViewById(R.id.send_button);
            MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);

            if (titleEditText == null || descriptionEditText == null || dateEditText == null ||
                    sendButton == null || cancelButton == null) {
                Log.e(TAG, "showAddRequestDialog: One or more views in dialog are null");
                Toast.makeText(getContext(), "Lỗi giao diện: Không tìm thấy view", Toast.LENGTH_LONG).show();
                return;
            }

            sendButton.setOnClickListener(v -> {
                String title = titleEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String date = dateEditText.getText().toString().trim();

                if (title.isEmpty() || description.isEmpty() || date.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Request request = new Request(UUID.randomUUID().toString(), title, description, date, "pending", userId);
                firebaseService.addRequest(request, success -> {
                    if (success) {
                        Toast.makeText(getContext(), "Gửi yêu cầu thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Gửi yêu cầu thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            cancelButton.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "showAddRequestDialog: Error showing dialog", e);
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showRequestDetailsDialog(Request request) {
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.dialog_request_details);

        // Tùy chỉnh chiều ngang dialog
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
            window.setAttributes(params);
        }

        TextView titleTextView = dialog.findViewById(R.id.request_title);
        TextView descriptionTextView = dialog.findViewById(R.id.request_description);
        TextView dateTextView = dialog.findViewById(R.id.request_date);
        TextView statusTextView = dialog.findViewById(R.id.request_status);
        TextView userIdTextView = dialog.findViewById(R.id.user_id);
        MaterialButton resolveButton = dialog.findViewById(R.id.resolve_button);
        MaterialButton closeButton = dialog.findViewById(R.id.close_button);

        titleTextView.setText("Tiêu đề: " + request.getTitle());
        descriptionTextView.setText("Mô tả: " + request.getDescription());
        dateTextView.setText("Ngày gửi: " + request.getDate());
        statusTextView.setText("Trạng thái: " + request.getStatus());
        userIdTextView.setText("Người gửi: " + request.getUserId());

        // Đặt màu cho trạng thái trong dialog
        if ("pending".equals(request.getStatus())) {
            statusTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if ("completed".equals(request.getStatus())) {
            statusTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }

        // Ẩn nút "Đã giải quyết" nếu trạng thái đã là "completed"
        resolveButton.setVisibility("pending".equals(request.getStatus()) ? View.VISIBLE : View.GONE);
        resolveButton.setOnClickListener(v -> {
            request.setStatus("completed");
            firebaseService.updateRequest(request, success -> {
                if (success) {
                    Toast.makeText(getContext(), "Yêu cầu đã được giải quyết", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    // Cập nhật lại danh sách yêu cầu
                    firebaseService.getRequests(requests -> {
                        requestAdapter = new RequestAdapter(requests, this::showRequestDetailsDialog);
                        recyclerView.setAdapter(requestAdapter);
                    });
                } else {
                    Toast.makeText(getContext(), "Cập nhật trạng thái thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}