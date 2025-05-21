package com.example.apartmentmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.models.Bill;
import com.example.apartmentmanager.models.BillItem;

import java.util.List;
import java.util.function.Consumer;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
    private List<Bill> billList;
    private Consumer<Bill> onBillClickListener;

    public BillAdapter(List<Bill> billList, Consumer<Bill> onBillClickListener) {
        this.billList = billList;
        this.onBillClickListener = onBillClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bill bill = billList.get(position);
        holder.titleTextView.setText(bill.getTitle());
        holder.apartmentIdTextView.setText("Phòng: " + bill.getApartmentId());
        holder.dueDateTextView.setText("Hạn: " + bill.getDueDate());

        holder.itemView.setOnClickListener(v -> {
            if (onBillClickListener != null) {
                onBillClickListener.accept(bill);
            }
        });
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, apartmentIdTextView, dueDateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.bill_title);
            apartmentIdTextView = itemView.findViewById(R.id.apartment_id);
            dueDateTextView = itemView.findViewById(R.id.due_date);
        }
    }
}