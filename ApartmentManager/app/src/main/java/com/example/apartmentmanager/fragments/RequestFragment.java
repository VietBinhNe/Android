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
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RequestFragment extends Fragment {
    private static final String TAG = "RequestFragment";
    private RecyclerView recyclerView;
    private View progressBar;
    private RequestAdapter requestAdapter;
    private FirebaseService firebaseService;
    private List<Request> requestList;
    private String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating RequestFragment view");
        View view = inflater.inflate(R.layout.fragment_request, container, false);

        try {
            recyclerView = view.findViewById(R.id.request_recycler_view);
            progressBar = view.findViewById(R.id.progress_bar);
            if (recyclerView == null || progressBar == null) {
                Log.e(TAG, "RecyclerView or ProgressBar is null");
                Toast.makeText(getContext(), "Lỗi giao diện", Toast.LENGTH_SHORT).show();
                return view;
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            firebaseService = new FirebaseService();
            userRole = getArguments() != null ? getArguments().getString("userRole") : "resident";
            Log.d(TAG, "User role: " + userRole);

            requestList = new ArrayList<>();
            // Khởi tạo RequestAdapter với cả requestList và OnItemClickListener
            requestAdapter = new RequestAdapter(requestList, this::showRequestDetailsDialog);
            recyclerView.setAdapter(requestAdapter);

            loadRequests();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khi tải danh sách yêu cầu", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadRequests() {
        Log.d(TAG, "Loading requests");
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        firebaseService.getRequests(requests -> {
            try {
                requestList.clear();
                if (requests == null || requests.isEmpty()) {
                    Log.e(TAG, "No requests found");
                    Toast.makeText(getContext(), "Không có dữ liệu yêu cầu", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    return;
                }
                Log.d(TAG, "Total requests loaded: " + requests.size());
                if ("admin".equals(userRole)) {
                    // Admin thấy tất cả yêu cầu
                    requestList.addAll(requests);
                } else {
                    // Resident chỉ thấy yêu cầu của mình
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    for (Request request : requests) {
                        if (userId.equals(request.getUserId())) {
                            requestList.add(request);
                        }
                    }
                }
                requestAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e(TAG, "Error loading requests: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi khi tải danh sách yêu cầu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRequestDetailsDialog(Request request) {
        try {
            if (request == null) {
                Log.e(TAG, "Request is null in showRequestDetailsDialog");
                Toast.makeText(getContext(), "Lỗi: Không tìm thấy thông tin yêu cầu", Toast.LENGTH_SHORT).show();
                return;
            }

            Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
            dialog.setContentView(R.layout.dialog_request_details);

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
            MaterialButton approveButton = dialog.findViewById(R.id.approve_button);
            MaterialButton rejectButton = dialog.findViewById(R.id.reject_button);
            MaterialButton closeButton = dialog.findViewById(R.id.close_button);

            if (titleTextView == null || descriptionTextView == null || dateTextView == null ||
                    statusTextView == null || userIdTextView == null || approveButton == null ||
                    rejectButton == null || closeButton == null) {
                Log.e(TAG, "One or more views in dialog_request_details are null");
                Toast.makeText(getContext(), "Lỗi giao diện", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            titleTextView.setText("Tiêu đề: " + (request.getTitle() != null ? request.getTitle() : "Không xác định"));
            descriptionTextView.setText("Mô tả: " + (request.getContent() != null ? request.getContent() : "Không xác định"));
            dateTextView.setText("Ngày gửi: " + (request.getDate() != null ? request.getDate() : "Không xác định"));
            statusTextView.setText("Trạng thái: " + (request.getStatus() != null ? request.getStatus() : "Không xác định"));

            String userId = request.getUserId();
            if (userId != null) {
                firebaseService.getUser(userId, user -> {
                    if (user != null) {
                        userIdTextView.setText("Người gửi: " + (user.getName() != null ? user.getName() : "Không xác định"));
                    } else {
                        userIdTextView.setText("Người gửi: Không xác định");
                    }
                });
            } else {
                userIdTextView.setText("Người gửi: Không xác định");
            }

            if ("admin".equals(userRole) && "pending".equals(request.getStatus())) {
                approveButton.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.VISIBLE);
                approveButton.setOnClickListener(v -> {
                    request.setStatus("approved");
                    firebaseService.updateRequest(request, success -> {
                        if (success) {
                            Toast.makeText(getContext(), "Phê duyệt yêu cầu thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            loadRequests();
                        } else {
                            Toast.makeText(getContext(), "Cập nhật yêu cầu thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                rejectButton.setOnClickListener(v -> {
                    request.setStatus("rejected");
                    firebaseService.updateRequest(request, success -> {
                        if (success) {
                            Toast.makeText(getContext(), "Từ chối yêu cầu thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            loadRequests();
                        } else {
                            Toast.makeText(getContext(), "Cập nhật yêu cầu thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            } else {
                approveButton.setVisibility(View.GONE);
                rejectButton.setVisibility(View.GONE);
            }

            closeButton.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing request details dialog: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}