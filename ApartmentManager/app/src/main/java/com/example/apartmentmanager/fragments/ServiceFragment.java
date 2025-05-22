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
import com.example.apartmentmanager.adapters.ServiceAdapter;
import com.example.apartmentmanager.models.Booking;
import com.example.apartmentmanager.models.Service;
import com.example.apartmentmanager.network.FirebaseService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServiceFragment extends Fragment {
    private static final String TAG = "ServiceFragment";
    private RecyclerView servicesRecyclerView, bookingsRecyclerView;
    private View progressBar;
    private ServiceAdapter serviceAdapter;
    private BookingAdapter bookingAdapter;
    private FirebaseService firebaseService;
    private FloatingActionButton addServiceFab;
    private String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating ServiceFragment view");
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_service, container, false);
            Log.d(TAG, "Inflated layout for ServiceFragment");

            servicesRecyclerView = view.findViewById(R.id.services_list);
            bookingsRecyclerView = view.findViewById(R.id.bookings_recycler_view);
            progressBar = view.findViewById(R.id.progress_bar);
            addServiceFab = view.findViewById(R.id.fab_add_service);
            Log.d(TAG, "Views initialized: servicesRecyclerView=" + (servicesRecyclerView != null) + ", bookingsRecyclerView=" + (bookingsRecyclerView != null));

            servicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            firebaseService = new FirebaseService();
            userRole = getArguments() != null ? getArguments().getString("userRole") : "resident";
            Log.d(TAG, "User role: " + userRole);

            progressBar.setVisibility(View.VISIBLE);
            servicesRecyclerView.setVisibility(View.GONE);
            bookingsRecyclerView.setVisibility(View.GONE);

            if ("admin".equals(userRole)) {
                if (addServiceFab != null) {
                    addServiceFab.setVisibility(View.VISIBLE);
                    addServiceFab.setOnClickListener(v -> showAddServiceDialog());
                    Log.d(TAG, "Add service FAB set to VISIBLE for admin");
                } else {
                    Log.e(TAG, "addServiceFab is null, cannot set visibility");
                }
            } else {
                if (addServiceFab != null) {
                    addServiceFab.setVisibility(View.GONE);
                    Log.d(TAG, "Add service FAB set to GONE for non-admin");
                } else {
                    Log.e(TAG, "addServiceFab is null, cannot set visibility");
                }
            }

            firebaseService.getServices(services -> {
                try {
                    List<Service> serviceList = services != null ? services : new ArrayList<>();
                    Log.d(TAG, "Loaded " + serviceList.size() + " services");
                    for (Service service : serviceList) {
                        Log.d(TAG, "Service: " + service.getId() + ", Name: " + service.getName());
                    }
                    serviceAdapter = new ServiceAdapter(serviceList, this::showServiceDetailsDialog);
                    servicesRecyclerView.setAdapter(serviceAdapter);

                    firebaseService.getBookings(bookings -> {
                        try {
                            List<Booking> filteredBookings = new ArrayList<>();
                            if ("resident".equals(userRole)) {
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                for (Booking booking : bookings) {
                                    if (booking.getUserId().equals(userId)) {
                                        filteredBookings.add(booking);
                                    }
                                }
                            } else {
                                filteredBookings.addAll(bookings);
                            }
                            Log.d(TAG, "Loaded " + filteredBookings.size() + " bookings");
                            for (Booking booking : filteredBookings) {
                                Log.d(TAG, "Booking: " + booking.getId() + ", Service ID: " + booking.getServiceId());
                            }
                            bookingAdapter = new BookingAdapter(filteredBookings);
                            bookingsRecyclerView.setAdapter(bookingAdapter);

                            progressBar.setVisibility(View.GONE);
                            servicesRecyclerView.setVisibility(View.VISIBLE);
                            bookingsRecyclerView.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            Log.e(TAG, "Error loading bookings: " + e.getMessage(), e);
                            Toast.makeText(getContext(), "Lỗi khi tải lịch sử đặt dịch vụ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error loading services: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Lỗi khi tải dịch vụ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khi tải fragment: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void showAddServiceDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_book_service);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
            window.setAttributes(params);
        }

        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        dialogTitle.setText("Thêm dịch vụ");

        TextView serviceName = dialog.findViewById(R.id.service_name);
        serviceName.setVisibility(View.GONE);

        TextView servicePrice = dialog.findViewById(R.id.service_price);
        servicePrice.setVisibility(View.GONE);

        TextInputEditText nameInput = dialog.findViewById(R.id.booking_date_input);
        nameInput.setHint("Tên dịch vụ (gym hoặc pool)");
        TextInputEditText priceInput = new TextInputEditText(getContext());
        priceInput.setHint("Giá dịch vụ");
        priceInput.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextInputEditText capacityInput = new TextInputEditText(getContext());
        capacityInput.setHint("Sức chứa");
        capacityInput.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextInputEditText descriptionInput = new TextInputEditText(getContext());
        descriptionInput.setHint("Mô tả");
        descriptionInput.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextInputEditText detailsInput = new TextInputEditText(getContext());
        detailsInput.setHint("Chi tiết");
        detailsInput.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ViewGroup container = (ViewGroup) dialog.findViewById(R.id.booking_date_layout).getParent();
        container.addView(priceInput);
        container.addView(capacityInput);
        container.addView(descriptionInput);
        container.addView(detailsInput);

        MaterialButton bookButton = dialog.findViewById(R.id.book_button);
        bookButton.setText("Thêm");
        MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);

        bookButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String priceStr = priceInput.getText().toString().trim();
            String capacityStr = capacityInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String details = detailsInput.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty() || capacityStr.isEmpty() || description.isEmpty() || details.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            int capacity;
            try {
                price = Double.parseDouble(priceStr);
                capacity = Integer.parseInt(capacityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Giá và sức chứa phải là số", Toast.LENGTH_SHORT).show();
                return;
            }

            String serviceId = name.toLowerCase();
            Service service = new Service(serviceId, name, description, details, price, capacity);
            firebaseService.addService(service, success -> {
                if (success) {
                    Toast.makeText(getContext(), "Thêm dịch vụ thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    progressBar.setVisibility(View.VISIBLE);
                    servicesRecyclerView.setVisibility(View.GONE);
                    firebaseService.getServices(services -> {
                        serviceAdapter = new ServiceAdapter(services, this::showServiceDetailsDialog);
                        servicesRecyclerView.setAdapter(serviceAdapter);
                        progressBar.setVisibility(View.GONE);
                        servicesRecyclerView.setVisibility(View.VISIBLE);
                    });
                } else {
                    Toast.makeText(getContext(), "Thêm dịch vụ thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showServiceDetailsDialog(Service service) {
        try {
            Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
            dialog.setContentView(R.layout.dialog_service_details);

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
                window.setAttributes(params);
            }

            TextView serviceName = dialog.findViewById(R.id.service_name);
            TextView servicePrice = dialog.findViewById(R.id.service_price);
            TextView serviceRemainingSlots = dialog.findViewById(R.id.service_remaining_slots);
            TextView serviceDescription = dialog.findViewById(R.id.service_description);
            TextView serviceDetails = dialog.findViewById(R.id.service_details);
            RecyclerView bookingsRecyclerViewDialog = dialog.findViewById(R.id.bookings_recycler_view);
            MaterialButton bookButton = dialog.findViewById(R.id.book_button);
            MaterialButton cancelButton = dialog.findViewById(R.id.close_button);

            String name = service.getName() != null ? service.getName() : "Không xác định";
            String description = service.getDescription() != null ? service.getDescription() : "Không có mô tả";
            String details = service.getDetails() != null ? service.getDetails() : "Không có chi tiết";

            serviceName.setText("Dịch vụ: " + name);
            servicePrice.setText(String.format("%.2f", service.getPrice()));
            serviceRemainingSlots.setText(String.valueOf(service.getCapacity()));
            serviceDescription.setText(description);
            serviceDetails.setText(details);

            // Tải lịch sử booking cho dịch vụ
            firebaseService.getBookings(bookings -> {
                List<Booking> serviceBookings = new ArrayList<>();
                for (Booking booking : bookings) {
                    if (booking.getServiceId().equals(service.getId())) {
                        serviceBookings.add(booking);
                    }
                }
                Log.d(TAG, "Loaded " + serviceBookings.size() + " bookings for service: " + service.getId());
                BookingAdapter dialogBookingAdapter = new BookingAdapter(serviceBookings);
                bookingsRecyclerViewDialog.setLayoutManager(new LinearLayoutManager(getContext()));
                bookingsRecyclerViewDialog.setAdapter(dialogBookingAdapter);
            });

            if ("resident".equals(userRole)) {
                bookButton.setVisibility(View.VISIBLE);
                TextInputEditText dateInput = dialog.findViewById(R.id.booking_date_input);
                dateInput.setVisibility(View.VISIBLE);
                bookButton.setOnClickListener(v -> {
                    String date = dateInput.getText().toString().trim();
                    if (date.isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng nhập ngày đặt", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Booking booking = new Booking(
                            UUID.randomUUID().toString(),
                            service.getId(),
                            userId,
                            date,
                            "unpaid",
                            "pending"
                    );
                    firebaseService.bookService(booking, success -> {
                        if (success) {
                            Toast.makeText(getContext(), "Đặt dịch vụ thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            progressBar.setVisibility(View.VISIBLE);
                            bookingsRecyclerView.setVisibility(View.GONE);
                            firebaseService.getBookings(bookings -> {
                                List<Booking> filteredBookings = new ArrayList<>();
                                if ("resident".equals(userRole)) {
                                    for (Booking b : bookings) {
                                        if (b.getUserId().equals(userId)) {
                                            filteredBookings.add(b);
                                        }
                                    }
                                } else {
                                    filteredBookings.addAll(bookings);
                                }
                                bookingAdapter = new BookingAdapter(filteredBookings);
                                bookingsRecyclerView.setAdapter(bookingAdapter);
                                progressBar.setVisibility(View.GONE);
                                bookingsRecyclerView.setVisibility(View.VISIBLE);
                            });
                        } else {
                            Toast.makeText(getContext(), "Đặt dịch vụ thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            } else {
                bookButton.setVisibility(View.GONE);
                TextInputEditText dateInput = dialog.findViewById(R.id.booking_date_input);
                dateInput.setVisibility(View.GONE);
            }

            cancelButton.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing service details dialog: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khi hiển thị chi tiết dịch vụ", Toast.LENGTH_SHORT).show();
        }
    }
}