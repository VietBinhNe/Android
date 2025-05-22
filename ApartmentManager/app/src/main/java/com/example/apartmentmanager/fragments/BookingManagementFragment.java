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
import com.example.apartmentmanager.adapters.BookingAdapter;
import com.example.apartmentmanager.models.Booking;
import com.example.apartmentmanager.network.FirebaseService;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class BookingManagementFragment extends Fragment {
    private static final String TAG = "BookingManagementFragment";
    private RecyclerView recyclerView;
    private View progressBar;
    private BookingAdapter bookingAdapter;
    private FirebaseService firebaseService;
    private List<Booking> bookingList;
    private String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating BookingManagementFragment view");
        View view = inflater.inflate(R.layout.fragment_booking_management, container, false);

        try {
            recyclerView = view.findViewById(R.id.booking_recycler_view);
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

            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            bookingList = new ArrayList<>();
            bookingAdapter = new BookingAdapter(bookingList, this::showBookingDetailsDialog);
            recyclerView.setAdapter(bookingAdapter);

            loadBookings();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khi tải danh sách đặt dịch vụ", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadBookings() {
        Log.d(TAG, "Loading bookings");
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        firebaseService.getBookings(bookings -> {
            try {
                bookingList.clear();
                if (bookings == null || bookings.isEmpty()) {
                    Log.e(TAG, "No bookings found");
                    Toast.makeText(getContext(), "Không có dữ liệu đặt dịch vụ", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    return;
                }
                Log.d(TAG, "Total bookings loaded: " + bookings.size());
                bookingList.addAll(bookings);
                bookingAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e(TAG, "Error loading bookings: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi khi tải danh sách đặt dịch vụ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBookingDetailsDialog(Booking booking) {
        try {
            if (booking == null) {
                Log.e(TAG, "Booking is null in showBookingDetailsDialog");
                Toast.makeText(getContext(), "Lỗi: Không tìm thấy thông tin đặt dịch vụ", Toast.LENGTH_SHORT).show();
                return;
            }

            Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
            dialog.setContentView(R.layout.dialog_booking_details);

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
                window.setAttributes(params);
            }

            TextView bookingIdTextView = dialog.findViewById(R.id.booking_id);
            TextView serviceNameTextView = dialog.findViewById(R.id.service_name);
            TextView bookingDateTextView = dialog.findViewById(R.id.booking_date);
            TextView bookingStatusTextView = dialog.findViewById(R.id.booking_status);
            MaterialButton closeButton = dialog.findViewById(R.id.close_button);

            if (bookingIdTextView == null || serviceNameTextView == null || bookingDateTextView == null ||
                    bookingStatusTextView == null || closeButton == null) {
                Log.e(TAG, "One or more views in dialog_booking_details are null");
                Toast.makeText(getContext(), "Lỗi giao diện", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            bookingIdTextView.setText("ID Đặt: " + (booking.getId() != null ? booking.getId() : "Không xác định"));
            serviceNameTextView.setText("Dịch vụ: " + (booking.getServiceId() != null ? booking.getServiceId() : "Không xác định"));
            bookingDateTextView.setText("Ngày đặt: " + (booking.getBookingDate() != null ? booking.getBookingDate() : "Không xác định"));
            bookingStatusTextView.setText("Trạng thái: " + (booking.getStatus() != null ? booking.getStatus() : "Không xác định"));

            closeButton.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing booking details dialog: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khi hiển thị thông tin đặt dịch vụ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}