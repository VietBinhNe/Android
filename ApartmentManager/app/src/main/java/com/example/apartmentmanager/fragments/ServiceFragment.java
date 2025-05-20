package com.example.apartmentmanager.fragments;

import android.app.Dialog;
import android.os.Bundle;
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
import com.example.apartmentmanager.adapters.ServiceAdapter;
import com.example.apartmentmanager.models.Booking;
import com.example.apartmentmanager.models.Service;
import com.example.apartmentmanager.network.FirebaseService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServiceFragment extends Fragment {
    private RecyclerView recyclerView;
    private ServiceAdapter serviceAdapter;
    private FirebaseService firebaseService;
    private List<Booking> bookings;
    private String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);

        userRole = requireActivity().getIntent().getStringExtra("userRole");
        recyclerView = view.findViewById(R.id.service_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseService = new FirebaseService();
        bookings = new ArrayList<>();

        firebaseService.getBookings(bookingsResult -> {
            bookings = bookingsResult;
            firebaseService.getServices(services -> {
                if ("admin".equals(userRole)) {
                    serviceAdapter = new ServiceAdapter(services, this::showServiceDetailsDialogForAdmin);
                } else {
                    serviceAdapter = new ServiceAdapter(services, this::showServiceDetailsDialogForResident);
                }
                recyclerView.setAdapter(serviceAdapter);
            });
        });

        return view;
    }

    private void showServiceDetailsDialogForAdmin(Service service) {
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.dialog_service_details);

        // Tùy chỉnh chiều ngang dialog
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95); // 95% chiều ngang màn hình
            window.setAttributes(params);
        }

        TextView nameTextView = dialog.findViewById(R.id.service_name);
        TextView priceLabelTextView = dialog.findViewById(R.id.price_label);
        TextView priceTextView = dialog.findViewById(R.id.service_price);
        TextView remainingSlotsLabelTextView = dialog.findViewById(R.id.remaining_slots_label);
        TextView remainingSlotsTextView = dialog.findViewById(R.id.service_remaining_slots);
        TextView descriptionLabelTextView = dialog.findViewById(R.id.description_label);
        TextView descriptionTextView = dialog.findViewById(R.id.service_description);
        TextView detailsLabelTextView = dialog.findViewById(R.id.details_label);
        TextView detailsTextView = dialog.findViewById(R.id.service_details);
        MaterialButton bookButton = dialog.findViewById(R.id.book_button);
        MaterialButton closeButton = dialog.findViewById(R.id.close_button);

        nameTextView.setText(service.getName());
        priceLabelTextView.setText("Giá:");
        priceTextView.setText(String.format("%.2f", service.getPrice()));
        remainingSlotsLabelTextView.setText("Chỗ còn lại:");
        descriptionLabelTextView.setText("Mô tả:");
        descriptionTextView.setText(service.getDescription());
        detailsLabelTextView.setText("Chi tiết:");
        detailsTextView.setText(service.getDetails());

        int bookedCount = 0;
        for (Booking booking : bookings) {
            if (booking.getServiceId().equals(service.getId()) && booking.getStatus().equals("confirmed")) {
                bookedCount++;
            }
        }
        int remainingSlots = service.getCapacity() - bookedCount;
        remainingSlotsTextView.setText(String.valueOf(remainingSlots));

        bookButton.setVisibility(View.GONE);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showServiceDetailsDialogForResident(Service service) {
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.dialog_service_details);

        // Tùy chỉnh chiều ngang dialog
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95); // 95% chiều ngang màn hình
            window.setAttributes(params);
        }

        TextView nameTextView = dialog.findViewById(R.id.service_name);
        TextView priceLabelTextView = dialog.findViewById(R.id.price_label);
        TextView priceTextView = dialog.findViewById(R.id.service_price);
        TextView remainingSlotsLabelTextView = dialog.findViewById(R.id.remaining_slots_label);
        TextView remainingSlotsTextView = dialog.findViewById(R.id.service_remaining_slots);
        TextView descriptionLabelTextView = dialog.findViewById(R.id.description_label);
        TextView descriptionTextView = dialog.findViewById(R.id.service_description);
        TextView detailsLabelTextView = dialog.findViewById(R.id.details_label);
        TextView detailsTextView = dialog.findViewById(R.id.service_details);
        MaterialButton bookButton = dialog.findViewById(R.id.book_button);
        MaterialButton closeButton = dialog.findViewById(R.id.close_button);

        nameTextView.setText(service.getName());
        priceLabelTextView.setText("Giá:");
        priceTextView.setText(String.format("%.2f", service.getPrice()));
        remainingSlotsLabelTextView.setText("Chỗ còn lại:");
        descriptionLabelTextView.setText("Mô tả:");
        descriptionTextView.setText(service.getDescription());
        detailsLabelTextView.setText("Chi tiết:");
        detailsTextView.setText(service.getDetails());

        int bookedCount = 0;
        for (Booking booking : bookings) {
            if (booking.getServiceId().equals(service.getId()) && booking.getStatus().equals("confirmed")) {
                bookedCount++;
            }
        }
        int remainingSlots = service.getCapacity() - bookedCount;
        remainingSlotsTextView.setText(String.valueOf(remainingSlots));

        bookButton.setOnClickListener(v -> showBookServiceDialog(service.getId()));
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showBookServiceDialog(String serviceId) {
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.dialog_book_service);

        // Tùy chỉnh chiều ngang dialog
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95); // 95% chiều ngang màn hình
            window.setAttributes(params);
        }

        TextInputEditText dateEditText = dialog.findViewById(R.id.booking_date_input);
        MaterialButton bookButton = dialog.findViewById(R.id.book_button);
        MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);

        bookButton.setOnClickListener(v -> {
            String date = dateEditText.getText().toString().trim();
            if (date.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập ngày đặt", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Booking booking = new Booking(UUID.randomUUID().toString(), userId, serviceId, date, "pending", "unpaid");
            firebaseService.bookService(booking, success -> {
                if (success) {
                    Toast.makeText(getContext(), "Đặt dịch vụ thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Đặt dịch vụ thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}