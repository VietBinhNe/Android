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
    private View progressBar;
    private RequestAdapter requestAdapter;
    private FirebaseService firebaseService;
    private FloatingActionButton addRequestFab;
    private String userRole;
    private List<Request> requestList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating RequestFragment view");
        View view = inflater.inflate(R.layout.fragment_request, container, false);

        recyclerView = view.findViewById(R.id.request_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        addRequestFab = view.findViewById(R.id.add_request_fab);
        Log.d(TAG, "Views initialized: recyclerView=" + (recyclerView != null) + ", addRequestFab=" + (addRequestFab != null));

        if (recyclerView == null || progressBar == null || addRequestFab == null) {
            Log.e(TAG, "One or more views are null");
            Toast.makeText(getContext(), "Lỗi giao diện", Toast.LENGTH_SHORT).show();
            return view;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseService = new FirebaseService();
        userRole = getArguments() != null ? getArguments().getString("userRole") : "resident";
        Log.d(TAG, "User role: " + userRole);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        requestList = new ArrayList<>();
        requestAdapter = new RequestAdapter(requestList);
        recyclerView.setAdapter(requestAdapter);

        if ("resident".equals(userRole)) {
            addRequestFab.setVisibility(View.VISIBLE);
            addRequestFab.setOnClickListener(v -> showAddRequestDialog());
            Log.d(TAG, "Add request FAB set to VISIBLE for resident");
        } else {
            addRequestFab.setVisibility(View.GONE);
            Log.d(TAG, "Add request FAB set to GONE for admin");
        }

        loadRequests();

        return view;
    }

    private void loadRequests() {
        Log.d(TAG, "Loading requests");
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        firebaseService.getRequests(requests -> {
            requestList.clear();
            if (requests == null || requests.isEmpty()) {
                Log.e(TAG, "No requests found");
                Toast.makeText(getContext(), "Không có yêu cầu nào", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                return;
            }
            Log.d(TAG, "Total requests loaded: " + requests.size());
            requestList.addAll(requests);
            requestAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });
    }

    private void showAddRequestDialog() {
        try {
            Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
            dialog.setContentView(R.layout.dialog_add_request);

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
                window.setAttributes(params);
            }

            TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
            TextInputEditText titleInput = dialog.findViewById(R.id.request_title_input);
            TextInputEditText contentInput = dialog.findViewById(R.id.request_content_input);
            MaterialButton addButton = dialog.findViewById(R.id.add_button);
            MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);

            if (dialogTitle == null || titleInput == null || contentInput == null ||
                    addButton == null || cancelButton == null) {
                Log.e(TAG, "One or more views in dialog_add_request are null");
                Toast.makeText(getContext(), "Lỗi giao diện", Toast.LENGTH_SHORT).show();
                return;
            }

            dialogTitle.setText("Thêm yêu cầu");

            addButton.setOnClickListener(v -> {
                String title = titleInput.getText().toString().trim();
                String content = contentInput.getText().toString().trim();

                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                String requestId = UUID.randomUUID().toString();
                Request request = new Request(requestId, title, content, FirebaseAuth.getInstance().getCurrentUser().getUid());
                firebaseService.addRequest(request, success -> {
                    if (success) {
                        Toast.makeText(getContext(), "Gửi yêu cầu thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadRequests();
                    } else {
                        Toast.makeText(getContext(), "Gửi yêu cầu thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            cancelButton.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing add request dialog: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}