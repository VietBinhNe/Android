package com.example.apartmentmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.models.Amenity;
import java.util.List;

public class AmenityAdapter extends RecyclerView.Adapter<AmenityAdapter.ViewHolder> {
    private List<Amenity> amenityList;

    public AmenityAdapter(List<Amenity> amenityList) {
        this.amenityList = amenityList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_amenity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Amenity amenity = amenityList.get(position);
        holder.nameTextView.setText(amenity.getName());
        holder.descriptionTextView.setText(amenity.getDescription());
    }

    @Override
    public int getItemCount() {
        return amenityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, descriptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.amenity_name);
            descriptionTextView = itemView.findViewById(R.id.amenity_description);
        }
    }
}