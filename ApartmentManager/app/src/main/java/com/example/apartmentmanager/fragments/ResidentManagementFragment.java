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
import com.example.apartmentmanager.adapters.ApartmentAdapter;
import com.example.apartmentmanager.adapters.ResidentAdapter;
import com.example.apartmentmanager.models.Apartment;
import com.example.apartmentmanager.models.User;
import com.example.apartmentmanager.network.FirebaseService;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ResidentManagementFragment extends Fragment {
    private static final String TAG = "ResidentManagement";
    private RecyclerView recyclerView;
    private ResidentAdapter residentAdapter;
    private ApartmentAdapter apartmentAdapter;
    private FirebaseService firebaseService;
    private List<ResidentAdapter.ResidentInfo> residentList;
    private List<Apartment> apartmentList;
    private String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resident_management, container, false);

        userRole = requireActivity().getIntent().getStringExtra("userRole");
        recyclerView = view.findViewById(R.id.resident_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseService = new FirebaseService();

        if ("admin".equals(userRole)) {
            apartmentList = new ArrayList<>();
            apartmentAdapter = new ApartmentAdapter(apartmentList, this::showApartmentDetailsDialog);
            recyclerView.setAdapter(apartmentAdapter);
            loadAllApartments();
        } else {
            residentList = new ArrayList<>();
            residentAdapter = new ResidentAdapter(residentList);
            recyclerView.setAdapter(residentAdapter);
            loadResidentApartment();
        }

        return view;
    }

    private void loadAllApartments() {
        firebaseService.getApartments(apartments -> {
            if (apartments == null || apartments.isEmpty()) {
                Log.e(TAG, "loadAllApartments: No apartments found");
                Toast.makeText(getContext(), "Không có dữ liệu phòng", Toast.LENGTH_SHORT).show();
                return;
            }
            apartmentList.clear();
            apartmentList.addAll(apartments);
            apartmentAdapter.notifyDataSetChanged();
        });
    }

    private void loadResidentApartment() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseService.getApartmentByResident(userId, apartment -> {
            if (apartment == null) {
                Log.e(TAG, "loadResidentApartment: No apartment found for user: " + userId);
                Toast.makeText(getContext(), "Không tìm thấy phòng của bạn", Toast.LENGTH_SHORT).show();
                return;
            }

            String residentId = apartment.getResidentId();
            String status = "available".equals(apartment.getStatus()) ? "Chưa thuê" : "Đang thuê";
            Log.d(TAG, "loadResidentApartment: Processing apartment: " + apartment.getNumber() + ", Resident ID: " + residentId);

            if (residentId != null) {
                firebaseService.getUser(residentId, user -> {
                    if (user == null) {
                        Log.w(TAG, "loadResidentApartment: User not found for residentId: " + residentId);
                        ResidentAdapter.ResidentInfo residentInfo = new ResidentAdapter.ResidentInfo(
                                apartment.getNumber(),
                                "Không xác định",
                                status,
                                "Không xác định"
                        );
                        residentList.add(residentInfo);
                        residentAdapter.notifyDataSetChanged();
                        return;
                    }

                    String tenantName = user.getEmail() != null ? user.getEmail() : "Không xác định";
                    String leaseDuration = user.getLeaseDuration() != null ? user.getLeaseDuration() : "Không xác định";
                    Log.d(TAG, "loadResidentApartment: User found - Name: " + tenantName + ", Lease Duration: " + leaseDuration);

                    ResidentAdapter.ResidentInfo residentInfo = new ResidentAdapter.ResidentInfo(
                            apartment.getNumber(),
                            tenantName,
                            status,
                            leaseDuration
                    );
                    residentList.add(residentInfo);
                    residentAdapter.notifyDataSetChanged();
                });
            } else {
                Log.d(TAG, "loadResidentApartment: No resident for apartment: " + apartment.getNumber());
                ResidentAdapter.ResidentInfo residentInfo = new ResidentAdapter.ResidentInfo(
                        apartment.getNumber(),
                        "Chưa có",
                        status,
                        "Chưa xác định"
                );
                residentList.add(residentInfo);
                residentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showApartmentDetailsDialog(Apartment apartment) {
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.dialog_apartment_details);

        // Tùy chỉnh chiều ngang dialog
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
            window.setAttributes(params);
        }

        TextView numberTextView = dialog.findViewById(R.id.apartment_number);
        TextView statusTextView = dialog.findViewById(R.id.apartment_status);
        TextView tenantTextView = dialog.findViewById(R.id.tenant_name);
        TextView leaseDurationTextView = dialog.findViewById(R.id.lease_duration);
        MaterialButton closeButton = dialog.findViewById(R.id.close_button);

        numberTextView.setText("Phòng: " + apartment.getNumber());
        statusTextView.setText("Trạng thái: " + ("available".equals(apartment.getStatus()) ? "Chưa thuê" : "Đang thuê"));

        String residentId = apartment.getResidentId();
        if (residentId != null) {
            firebaseService.getUser(residentId, user -> {
                if (user != null) {
                    tenantTextView.setText("Chủ hộ: " + (user.getEmail() != null ? user.getEmail() : "Không xác định"));
                    leaseDurationTextView.setText("Thời gian thuê: " + (user.getLeaseDuration() != null ? user.getLeaseDuration() : "Không xác định"));
                } else {
                    tenantTextView.setText("Chủ hộ: Không xác định");
                    leaseDurationTextView.setText("Thời gian thuê: Không xác định");
                }
            });
        } else {
            tenantTextView.setText("Chủ hộ: Chưa có");
            leaseDurationTextView.setText("Thời gian thuê: Chưa xác định");
        }

        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}