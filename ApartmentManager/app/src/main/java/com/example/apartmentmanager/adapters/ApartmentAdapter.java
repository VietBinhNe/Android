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

public class ApartmentAdapter extends RecyclerView.Adapter<ApartmentAdapter.ViewHolder> {
    private List<Apartment> apartmentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Apartment apartment);
    }

    public ApartmentAdapter(List<Apartment> apartmentList, OnItemClickListener listener) {
        this.apartmentList = apartmentList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apartment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Apartment apartment = apartmentList.get(position);
        holder.apartmentName.setText("Phòng " + apartment.getNumber());
        holder.apartmentImage.setImageResource(R.drawable.main_image); // Thay bằng hình ảnh thực tế nếu có
        holder.itemView.setOnClickListener(v -> listener.onItemClick(apartment));
    }

    @Override
    public int getItemCount() {
        return apartmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView apartmentImage;
        TextView apartmentName;

        public ViewHolder(View itemView) {
            super(itemView);
            apartmentImage = itemView.findViewById(R.id.apartment_image);
            apartmentName = itemView.findViewById(R.id.apartment_name);
        }
    }
}