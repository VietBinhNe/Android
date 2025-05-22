package com.example.apartmentmanager.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.models.Apartment;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.function.Consumer;

public class ApartmentAdapter extends RecyclerView.Adapter<ApartmentAdapter.ViewHolder> {
    private static final String TAG = "ApartmentAdapter";
    private List<Apartment> apartmentList;
    private Consumer<Apartment> onRentRequestClick;

    public ApartmentAdapter(List<Apartment> apartmentList, Consumer<Apartment> onRentRequestClick) {
        this.apartmentList = apartmentList;
        this.onRentRequestClick = onRentRequestClick;
        Log.d(TAG, "ApartmentAdapter initialized with " + apartmentList.size() + " apartments");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apartment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            Apartment apartment = apartmentList.get(position);
            Log.d(TAG, "Binding apartment: " + apartment.getId());
            holder.numberTextView.setText("Phòng: " + (apartment.getNumber() != null ? apartment.getNumber() : "Không xác định"));
            holder.statusTextView.setText("Trạng thái: " + ("available".equals(apartment.getStatus()) ? "Chưa thuê" : "Đang thuê"));
            holder.priceTextView.setText("Giá: " + String.format("%.2f", apartment.getPrice()));
            holder.descriptionTextView.setText("Mô tả: " + (apartment.getDescription() != null ? apartment.getDescription() : "Không có mô tả"));

            holder.rentButton.setOnClickListener(v -> onRentRequestClick.accept(apartment));
        } catch (Exception e) {
            Log.e(TAG, "Error binding apartment at position " + position + ": " + e.getMessage(), e);
        }
    }

    @Override
    public int getItemCount() {
        int count = apartmentList.size();
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView numberTextView, statusTextView, priceTextView, descriptionTextView;
        MaterialButton rentButton;

        public ViewHolder(View itemView) {
            super(itemView);
            numberTextView = itemView.findViewById(R.id.apartment_number);
            statusTextView = itemView.findViewById(R.id.apartment_status);
            priceTextView = itemView.findViewById(R.id.apartment_price);
            descriptionTextView = itemView.findViewById(R.id.apartment_description);
            rentButton = itemView.findViewById(R.id.rent_button);
        }
    }
}