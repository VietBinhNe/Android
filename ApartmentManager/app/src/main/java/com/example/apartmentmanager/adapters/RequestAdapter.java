package com.example.apartmentmanager.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.models.Request;

import java.util.List;
import java.util.function.Consumer;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<Request> requestList;
    private Consumer<Request> onRequestClickListener;

    public RequestAdapter(List<Request> requestList) {
        this(requestList, null);
    }

    public RequestAdapter(List<Request> requestList, Consumer<Request> onRequestClickListener) {
        this.requestList = requestList;
        this.onRequestClickListener = onRequestClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Request request = requestList.get(position);
        holder.titleTextView.setText(request.getTitle());
        holder.descriptionTextView.setText("Mô tả: " + request.getDescription());
        holder.dateTextView.setText("Ngày gửi: " + request.getDate());
        holder.statusTextView.setText("Trạng thái: " + request.getStatus());

        // Đặt màu cho trạng thái
        if ("pending".equals(request.getStatus())) {
            holder.statusTextView.setTextColor(Color.RED);
        } else if ("completed".equals(request.getStatus())) {
            holder.statusTextView.setTextColor(Color.GREEN);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onRequestClickListener != null) {
                onRequestClickListener.accept(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, dateTextView, statusTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.request_title);
            descriptionTextView = itemView.findViewById(R.id.request_description);
            dateTextView = itemView.findViewById(R.id.request_date);
            statusTextView = itemView.findViewById(R.id.request_status);
        }
    }
}