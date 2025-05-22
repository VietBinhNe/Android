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
import com.example.apartmentmanager.models.Apartment;
import com.example.apartmentmanager.models.Request;
import com.example.apartmentmanager.network.FirebaseService;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ApartmentManagementFragment extends Fragment {
    private static final String TAG = "ApartmentManagementFragment";
    private RecyclerView recyclerView;
    private View progressBar;
    private ApartmentAdapter apartmentAdapter;
    private FirebaseService firebaseService;
    private List<Apartment> apartmentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating ApartmentManagementFragment view");
        View view = inflater.inflate(R.layout.fragment_apartment_management, container, false);

        try {
            recyclerView = view.findViewById(R.id.apartment_recycler_view);
            progressBar = view.findViewById(R.id.progress_bar);
            if (recyclerView == null || progressBar == null) {
                Log.e(TAG, "RecyclerView or ProgressBar is null");
                Toast.makeText(getContext(), "Lỗi giao diện", Toast.LENGTH_SHORT).show();
                return view;
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            firebaseService = new FirebaseService();
            apartmentList = new ArrayList<>();
            apartmentAdapter = new ApartmentAdapter(apartmentList, this::showApartmentDetailsDialog);
            recyclerView.setAdapter(apartmentAdapter);

            loadApartments();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khi tải danh sách phòng", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadApartments() {
        Log.d(TAG, "Loading apartments");
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        firebaseService.getApartments(apartments -> {
            try {
                apartmentList.clear();
                if (apartments == null || apartments.isEmpty()) {
                    Log.e(TAG, "No apartments found");
                    Toast.makeText(getContext(), "Không có dữ liệu phòng", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    return;
                }
                Log.d(TAG, "Total apartments loaded: " + apartments.size());
                apartmentList.addAll(apartments);
                apartmentAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e(TAG, "Error loading apartments: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi khi tải danh sách phòng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showApartmentDetailsDialog(Apartment apartment) {
        try {
            if (apartment == null) {
                Log.e(TAG, "Apartment is null in showApartmentDetailsDialog");
                Toast.makeText(getContext(), "Lỗi: Không tìm thấy thông tin phòng", Toast.LENGTH_SHORT).show();
                return;
            }

            Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
            dialog.setContentView(R.layout.dialog_apartment_details);

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
            TextView priceTextView = dialog.findViewById(R.id.price);
            TextView descriptionTextView = dialog.findViewById(R.id.description);
            TextView detailsTextView = dialog.findViewById(R.id.details);
            TextView requestsTextView = dialog.findViewById(R.id.requests_recycler_view); // Tạm dùng TextView thay RecyclerView
            MaterialButton closeButton = dialog.findViewById(R.id.close_button);

            if (numberTextView == null || statusTextView == null || tenantTextView == null ||
                    leaseDurationTextView == null || priceTextView == null || descriptionTextView == null ||
                    detailsTextView == null || requestsTextView == null || closeButton == null) {
                Log.e(TAG, "One or more views in dialog_apartment_details are null");
                Toast.makeText(getContext(), "Lỗi giao diện", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            numberTextView.setText("Phòng: " + (apartment.getNumber() != null ? apartment.getNumber() : "Không xác định"));
            statusTextView.setText("Trạng thái: " + ("available".equals(apartment.getStatus()) ? "Chưa thuê" : "Đang thuê"));
            priceTextView.setText("Giá: " + String.format("%.2f", apartment.getPrice()));
            descriptionTextView.setText("Mô tả: " + (apartment.getDescription() != null ? apartment.getDescription() : "Không có mô tả"));
            detailsTextView.setText("Chi tiết: " + (apartment.getDetails() != null ? apartment.getDetails() : "Không có chi tiết"));

            String residentId = apartment.getResidentId();
            if (residentId != null) {
                firebaseService.getUser(residentId, user -> {
                    if (user != null) {
                        tenantTextView.setText("Chủ hộ: " + (user.getName() != null ? user.getName() : "Không xác định"));
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

            // Tạm thời hiển thị yêu cầu thuê dưới dạng TextView
            firebaseService.getRequests(requests -> {
                StringBuilder requestsText = new StringBuilder();
                if (requests != null) {
                    for (Request request : requests) {
                        if (request.getApartmentId() != null && request.getApartmentId().equals(apartment.getId())) {
                            requestsText.append("Yêu cầu: ")
                                    .append(request.getTitle())
                                    .append(" (Trạng thái: ")
                                    .append(request.getStatus())
                                    .append(")\n");
                        }
                    }
                }
                requestsTextView.setText(requestsText.length() > 0 ? requestsText.toString() : "Không có yêu cầu thuê");
            });

            closeButton.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing apartment details dialog: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showRequestDetailsDialog(Request request, Apartment apartment) {
        try {
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

            if ("pending".equals(request.getStatus())) {
                approveButton.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.VISIBLE);
                approveButton.setOnClickListener(v -> {
                    request.setStatus("approved");
                    firebaseService.updateRequest(request, success -> {
                        if (success) {
                            // Update apartment and user
                            apartment.setStatus("rented");
                            apartment.setResidentId(userId);
                            firebaseService.updateApartment(apartment, aptSuccess -> {
                                if (aptSuccess) {
                                    firebaseService.getUser(userId, user -> {
                                        if (user != null) {
                                            user.setApartmentNumber(apartment.getNumber());
                                            user.setStatus("Đang thuê");
                                            firebaseService.addUser(user, userSuccess -> {
                                                if (userSuccess) {
                                                    Toast.makeText(getContext(), "Phê duyệt yêu cầu thuê thành công", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    loadApartments();
                                                } else {
                                                    Toast.makeText(getContext(), "Cập nhật thông tin người dùng thất bại", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), "Cập nhật phòng thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Cập nhật yêu cầu thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                rejectButton.setOnClickListener(v -> {
                    request.setStatus("rejected");
                    firebaseService.updateRequest(request, success -> {
                        if (success) {
                            Toast.makeText(getContext(), "Từ chối yêu cầu thuê thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            loadApartments();
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