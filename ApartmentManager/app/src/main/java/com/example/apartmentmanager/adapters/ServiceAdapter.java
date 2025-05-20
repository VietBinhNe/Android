package com.example.apartmentmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.models.Service;
import java.util.List;
import java.util.function.Consumer;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {
    private List<Service> serviceList;
    private Consumer<Service> onClick;

    public ServiceAdapter(List<Service> serviceList, Consumer<Service> onClick) {
        this.serviceList = serviceList;
        this.onClick = onClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.nameTextView.setText(service.getName());
        holder.descriptionTextView.setText(service.getDescription());
        holder.capacityTextView.setText("Số chỗ: " + service.getCapacity());

        if ("pool".equals(service.getId())) {
            holder.imageView.setImageResource(R.drawable.pool);
        } else if ("gym".equals(service.getId())) {
            holder.imageView.setImageResource(R.drawable.gym);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_service);
        }

        holder.itemView.setOnClickListener(v -> onClick.accept(service));
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, descriptionTextView, capacityTextView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.service_name);
            descriptionTextView = itemView.findViewById(R.id.service_description);
            capacityTextView = itemView.findViewById(R.id.service_capacity);
            imageView = itemView.findViewById(R.id.service_image);
        }
    }
}