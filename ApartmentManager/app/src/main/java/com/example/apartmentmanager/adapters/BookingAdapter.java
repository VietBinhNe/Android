package com.example.apartmentmanager.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.models.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    private static final String TAG = "BookingAdapter";
    private List<Booking> bookingList;

    public BookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
        Log.d(TAG, "BookingAdapter initialized with " + bookingList.size() + " bookings");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            Booking booking = bookingList.get(position);
            Log.d(TAG, "Binding booking: " + booking.getId());
            holder.serviceIdTextView.setText("Dịch vụ: " + booking.getServiceId());
            holder.bookingDateTextView.setText("Ngày đặt: " + booking.getBookingDate());
        } catch (Exception e) {
            Log.e(TAG, "Error binding booking at position " + position, e);
        }
    }

    @Override
    public int getItemCount() {
        int count = bookingList.size();
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceIdTextView, bookingDateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            serviceIdTextView = itemView.findViewById(R.id.booking_service_id);
            bookingDateTextView = itemView.findViewById(R.id.booking_date);
        }
    }
}