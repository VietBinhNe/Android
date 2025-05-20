package com.example.apartmentmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;

import java.util.List;

public class ResidentAdapter extends RecyclerView.Adapter<ResidentAdapter.ViewHolder> {
    private List<ResidentInfo> residentList;

    public ResidentAdapter(List<ResidentInfo> residentList) {
        this.residentList = residentList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resident, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ResidentInfo residentInfo = residentList.get(position);
        holder.apartmentNumberTextView.setText("Phòng: " + residentInfo.apartmentNumber);
        holder.tenantNameTextView.setText("Tên chủ hộ: " + (residentInfo.tenantName != null ? residentInfo.tenantName : "Chưa có"));
        holder.tenantStatusTextView.setText("Trạng thái: " + residentInfo.status);
        holder.leaseDurationTextView.setText("Thời gian thuê: " + (residentInfo.leaseDuration != null ? residentInfo.leaseDuration : "Chưa xác định"));
    }

    @Override
    public int getItemCount() {
        return residentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView apartmentNumberTextView, tenantNameTextView, tenantStatusTextView, leaseDurationTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            apartmentNumberTextView = itemView.findViewById(R.id.apartment_number);
            tenantNameTextView = itemView.findViewById(R.id.tenant_name);
            tenantStatusTextView = itemView.findViewById(R.id.tenant_status);
            leaseDurationTextView = itemView.findViewById(R.id.lease_duration);
        }
    }

    public static class ResidentInfo {
        String apartmentNumber;
        String tenantName;
        String status;
        String leaseDuration;

        public ResidentInfo(String apartmentNumber, String tenantName, String status, String leaseDuration) {
            this.apartmentNumber = apartmentNumber;
            this.tenantName = tenantName;
            this.status = status;
            this.leaseDuration = leaseDuration;
        }
    }
}