package com.example.apartmentmanager.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        userRole = requireActivity().getIntent().getStringExtra("userRole");
        recyclerView = view.findViewById(R.id.request_recycler_view);
        addRequestFab = view.findViewById(R.id.add_request_fab);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseService = new FirebaseService();
        requestAdapter = new RequestAdapter(new ArrayList<>());
        recyclerView.setAdapter(requestAdapter);

        if ("admin".equals(userRole)) {
            addRequestFab.setVisibility(View.GONE);
            firebaseService.getRequests(requests -> {
                requestAdapter = new RequestAdapter(requests);
                recyclerView.setAdapter(requestAdapter);
            });
        } else {
            addRequestFab.setVisibility(View.VISIBLE);
            addRequestFab.setOnClickListener(v -> showAddRequestDialog());
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            firebaseService.getRequests(requests -> {
                List<Request> filteredRequests = new ArrayList<>();
                for (Request request : requests) {
                    if (request.getUserId().equals(userId)) {
                        filteredRequests.add(request);
                    }
                }
                requestAdapter = new RequestAdapter(filteredRequests);
                recyclerView.setAdapter(requestAdapter);
            });
        }

        return view;
    }

    private void showAddRequestDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_request);

        TextInputEditText titleEditText = dialog.findViewById(R.id.request_title_input);
        TextInputEditText descriptionEditText = dialog.findViewById(R.id.request_description_input);
        TextInputEditText dateEditText = dialog.findViewById(R.id.request_date_input);
        MaterialButton sendButton = dialog.findViewById(R.id.send_button);
        MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);

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
    }
}