package com.example.apartmentmanager.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.models.Request;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private static final String TAG = "RequestAdapter";
    private List<Request> requestList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Request request);
    }

    public RequestAdapter(List<Request> requestList, OnItemClickListener listener) {
        this.requestList = requestList;
        this.listener = listener;
        Log.d(TAG, "RequestAdapter initialized with " + requestList.size() + " requests");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Request request = requestList.get(position);
            Log.d(TAG, "Binding request: " + request.getId());
            holder.titleTextView.setText("Tiêu đề: " + (request.getTitle() != null ? request.getTitle() : "Không xác định"));
            holder.statusTextView.setText("Trạng thái: " + (request.getStatus() != null ? request.getStatus() : "Không xác định"));
            holder.itemView.setOnClickListener(v -> listener.onItemClick(request));
        } catch (Exception e) {
            Log.e(TAG, "Error binding request at position " + position + ": " + e.getMessage(), e);
        }
    }

    @Override
    public int getItemCount() {
        int count = requestList.size();
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, statusTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.request_title);
            statusTextView = itemView.findViewById(R.id.request_status);
        }
    }
}