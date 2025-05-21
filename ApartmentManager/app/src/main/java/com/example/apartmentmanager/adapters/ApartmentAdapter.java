package com.example.apartmentmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.models.Apartment;

import java.util.List;
import java.util.function.Consumer;

public class ApartmentAdapter extends RecyclerView.Adapter<ApartmentAdapter.ViewHolder> {
    private List<Apartment> apartmentList;
    private Consumer<Apartment> onApartmentClickListener;

    public ApartmentAdapter(List<Apartment> apartmentList, Consumer<Apartment> onApartmentClickListener) {
        this.apartmentList = apartmentList;
        this.onApartmentClickListener = onApartmentClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apartment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Apartment apartment = apartmentList.get(position);
        holder.roomNumberTextView.setText("Phòng " + apartment.getNumber());
        holder.statusTextView.setText("available".equals(apartment.getStatus()) ? "Chưa thuê" : "Đang thuê");

        // Hiển thị hình ảnh dựa trên apartment ID
        switch (apartment.getId()) {
            case "apartment101":
                holder.apartmentImageView.setImageResource(R.drawable.apartment101);
                break;
            case "apartment102":
                holder.apartmentImageView.setImageResource(R.drawable.apartment102);
                break;
            case "apartment103":
                holder.apartmentImageView.setImageResource(R.drawable.apartment103);
                break;
            case "apartment201":
                holder.apartmentImageView.setImageResource(R.drawable.apartment201);
                break;
            case "apartment202":
                holder.apartmentImageView.setImageResource(R.drawable.apartment202);
                break;
            default:
                holder.apartmentImageView.setImageResource(android.R.drawable.ic_menu_info_details); // Hình ảnh mặc định từ Android
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (onApartmentClickListener != null) {
                onApartmentClickListener.accept(apartment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return apartmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView apartmentImageView;
        TextView roomNumberTextView, statusTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            apartmentImageView = itemView.findViewById(R.id.apartment_image);
            roomNumberTextView = itemView.findViewById(R.id.room_number);
            statusTextView = itemView.findViewById(R.id.status);
        }
    }
}