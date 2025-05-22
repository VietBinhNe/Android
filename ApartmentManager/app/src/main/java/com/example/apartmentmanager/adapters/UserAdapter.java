package com.example.apartmentmanager.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.models.User;

import java.util.List;
import java.util.function.Consumer;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static final String TAG = "UserAdapter";
    private List<User> userList;
    private Consumer<User> onUserClick;

    public UserAdapter(List<User> userList, Consumer<User> onUserClick) {
        this.userList = userList;
        this.onUserClick = onUserClick;
        Log.d(TAG, "UserAdapter initialized with " + userList.size() + " users");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            User user = userList.get(position);
            Log.d(TAG, "Binding user: " + user.getId());
            holder.nameTextView.setText("Tên: " + (user.getName() != null ? user.getName() : "Không xác định"));
            holder.emailTextView.setText("Email: " + (user.getEmail() != null ? user.getEmail() : "Không xác định"));
            String status = user.getStatus() != null ? user.getStatus() : "Không xác định";
            holder.statusTextView.setText("Trạng thái: " + status);
            if ("Đang thuê".equals(status)) {
                holder.statusTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_rented));
            } else if ("Chưa thuê".equals(status)) {
                holder.statusTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_not_rented));
            } else {
                holder.statusTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black));
            }

            holder.itemView.setOnClickListener(v -> onUserClick.accept(user));
        } catch (Exception e) {
            Log.e(TAG, "Error binding user at position " + position + ": " + e.getMessage(), e);
        }
    }

    @Override
    public int getItemCount() {
        int count = userList.size();
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView, statusTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.user_name);
            emailTextView = itemView.findViewById(R.id.user_email);
            statusTextView = itemView.findViewById(R.id.user_status);
        }
    }
}