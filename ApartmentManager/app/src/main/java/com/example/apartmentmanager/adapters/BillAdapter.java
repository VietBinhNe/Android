package com.example.apartmentmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.models.Bill;
import com.example.apartmentmanager.models.BillItem;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.function.Consumer;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
    private List<Bill> billList;
    private Consumer<String> onDeleteClick;

    public BillAdapter(List<Bill> billList, Consumer<String> onDeleteClick) {
        this.billList = billList;
        this.onDeleteClick = onDeleteClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bill, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bill bill = billList.get(position);
        holder.titleTextView.setText(bill.getTitle());
        holder.apartmentIdTextView.setText("Phòng: " + bill.getApartmentId());

        StringBuilder itemsText = new StringBuilder();
        for (BillItem item : bill.getItems()) {
            itemsText.append(item.getName()).append(": ").append(String.format("%.2f", item.getAmount())).append("\n");
        }
        holder.itemsTextView.setText(itemsText.toString());

        holder.totalAmountTextView.setText("Tổng tiền: " + String.format("%.2f", bill.getTotalAmount()));
        holder.dueDateTextView.setText("Ngày đến hạn: " + bill.getDueDate());

        if (onDeleteClick != null) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> onDeleteClick.accept(bill.getId()));
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, apartmentIdTextView, itemsTextView, totalAmountTextView, dueDateTextView;
        MaterialButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.bill_title);
            apartmentIdTextView = itemView.findViewById(R.id.bill_apartment_id);
            itemsTextView = itemView.findViewById(R.id.bill_items);
            totalAmountTextView = itemView.findViewById(R.id.bill_total_amount);
            dueDateTextView = itemView.findViewById(R.id.bill_due_date);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}