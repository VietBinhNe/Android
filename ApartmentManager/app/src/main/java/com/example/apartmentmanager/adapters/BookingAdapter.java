package com.example.apartmentmanager.adapters;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.models.Booking;
import com.example.apartmentmanager.models.Service;
import com.example.apartmentmanager.network.FirebaseService;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Map;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    private List<Booking> bookingList;
    private Map<String, Service> serviceMap;
    private FirebaseService firebaseService;

    public BookingAdapter(List<Booking> bookingList, Map<String, Service> serviceMap) {
        this.bookingList = bookingList;
        this.serviceMap = serviceMap;
        this.firebaseService = new FirebaseService();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        Service service = serviceMap.get(booking.getServiceId());
        holder.serviceNameTextView.setText(service != null ? service.getName() : "Unknown Service");
        holder.userIdTextView.setText("Người dùng: " + booking.getUserId());
        holder.dateTextView.setText("Ngày đặt: " + booking.getBookingDate());
        holder.statusTextView.setText("Trạng thái: " + booking.getStatus());
        holder.paymentStatusTextView.setText("Thanh toán: " + booking.getPaymentStatus());

        int capacity = service != null ? service.getCapacity() : 0;
        int bookedCount = 0;
        for (Booking b : bookingList) {
            if (b.getServiceId().equals(booking.getServiceId()) && b.getBookingDate().equals(booking.getBookingDate()) && b.getStatus().equals("confirmed")) {
                bookedCount++;
            }
        }
        int remainingSlots = capacity - bookedCount;
        holder.remainingSlotsTextView.setText("Chỗ còn lại: " + remainingSlots);

        holder.itemView.setOnClickListener(v -> showBookingDetailsDialog(booking, holder));
    }

    private void showBookingDetailsDialog(Booking booking, ViewHolder holder) {
        Dialog dialog = new Dialog(holder.itemView.getContext());
        dialog.setContentView(R.layout.dialog_booking_details);

        TextView serviceNameTextView = dialog.findViewById(R.id.booking_service_name);
        TextView userIdTextView = dialog.findViewById(R.id.booking_user_id);
        TextView dateTextView = dialog.findViewById(R.id.booking_date);
        TextView paymentStatusTextView = dialog.findViewById(R.id.booking_payment_status);
        MaterialButton closeButton = dialog.findViewById(R.id.close_button);

        Service service = serviceMap.get(booking.getServiceId());
        serviceNameTextView.setText(service != null ? service.getName() : "Unknown Service");
        userIdTextView.setText("Người thuê: " + booking.getUserId());
        dateTextView.setText("Ngày đặt: " + booking.getBookingDate());
        paymentStatusTextView.setText("Trạng thái thanh toán: " + booking.getPaymentStatus());

        firebaseService.getUser(booking.getUserId(), user -> {
            if (user != null) {
                userIdTextView.setText("Người thuê: " + user.getEmail() + " (" + booking.getUserId() + ")");
            }
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameTextView, userIdTextView, dateTextView, statusTextView, paymentStatusTextView, remainingSlotsTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.service_name);
            userIdTextView = itemView.findViewById(R.id.user_id);
            dateTextView = itemView.findViewById(R.id.booking_date);
            statusTextView = itemView.findViewById(R.id.booking_status);
            paymentStatusTextView = itemView.findViewById(R.id.payment_status);
            remainingSlotsTextView = itemView.findViewById(R.id.remaining_slots);
        }
    }
}