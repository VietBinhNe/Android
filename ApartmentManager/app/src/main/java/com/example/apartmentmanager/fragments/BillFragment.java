package com.example.apartmentmanager.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.adapters.BillAdapter;
import com.example.apartmentmanager.models.Bill;
import com.example.apartmentmanager.models.BillItem;
import com.example.apartmentmanager.network.FirebaseService;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class BillFragment extends Fragment {
    private static final String TAG = "BillFragment";
    private RecyclerView recyclerView;
    private View progressBar;
    private BillAdapter billAdapter;
    private FirebaseService firebaseService;
    private String userRole;
    private List<Bill> filteredBills;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill, container, false);

        recyclerView = view.findViewById(R.id.bill_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseService = new FirebaseService();
        userRole = getArguments() != null ? getArguments().getString("userRole") : "resident";
        Log.d(TAG, "User role: " + userRole);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        filteredBills = new ArrayList<>();
        billAdapter = new BillAdapter(filteredBills, this::showBillDetailsDialog);
        recyclerView.setAdapter(billAdapter);

        String userId = "resident".equals(userRole) ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        Log.d(TAG, "Loading bills for user: " + (userId != null ? userId : "admin"));
        firebaseService.getBills(bills -> {
            filteredBills.clear();
            if (bills == null || bills.isEmpty()) {
                Log.e(TAG, "No bills found");
                Toast.makeText(getContext(), "Không có dữ liệu hóa đơn", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                return;
            }
            Log.d(TAG, "Loaded " + bills.size() + " bills");
            for (Bill bill : bills) {
                Log.d(TAG, "Bill: " + bill.getId() + ", Apartment ID: " + bill.getApartmentId() + ", Status: " + bill.getStatus());
            }

            if ("resident".equals(userRole) && userId != null) {
                for (Bill bill : bills) {
                    firebaseService.getApartmentByResident(userId, apartment -> {
                        if (apartment != null && bill.getApartmentId().equals(apartment.getId())) {
                            filteredBills.add(bill);
                            Log.d(TAG, "Added bill for resident: " + bill.getId());
                        }
                        billAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    });
                }
            } else {
                filteredBills.addAll(bills);
                Log.d(TAG, "Added all bills for admin: " + filteredBills.size());
                billAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private void showBillDetailsDialog(Bill bill) {
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.dialog_bill_details);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
            window.setAttributes(params);
        }

        TextView titleTextView = dialog.findViewById(R.id.bill_title);
        TextView apartmentIdTextView = dialog.findViewById(R.id.apartment_id);
        TextView dueDateTextView = dialog.findViewById(R.id.due_date);
        TextView statusTextView = dialog.findViewById(R.id.status);
        LinearLayout billItemsContainer = dialog.findViewById(R.id.bill_items_container);
        TextView totalTextView = dialog.findViewById(R.id.total);
        MaterialButton payButton = dialog.findViewById(R.id.pay_button);
        MaterialButton closeButton = dialog.findViewById(R.id.close_button);

        titleTextView.setText("Hóa đơn: " + bill.getTitle());
        apartmentIdTextView.setText("Phòng: " + bill.getApartmentId());
        dueDateTextView.setText("Hạn: " + bill.getDueDate());
        statusTextView.setText("Trạng thái: " + bill.getStatus());

        billItemsContainer.removeAllViews();
        for (BillItem item : bill.getItems()) {
            TextView itemView = new TextView(getContext());
            itemView.setText(item.getDescription() + ": " + String.format("%.2f", item.getAmount()));
            itemView.setTextSize(14);
            billItemsContainer.addView(itemView);
        }

        double total = 0;
        for (BillItem item : bill.getItems()) {
            total += item.getAmount();
        }
        totalTextView.setText("Tổng: " + String.format("%.2f", total));

        if ("unpaid".equals(bill.getStatus())) {
            payButton.setVisibility(View.VISIBLE);
            payButton.setOnClickListener(v -> {
                bill.setStatus("paid");
                Log.d(TAG, "Updating bill: " + bill.getId() + " with new status: " + bill.getStatus());
                firebaseService.updateBill(bill, success -> {
                    if (success) {
                        Toast.makeText(getContext(), "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        progressBar.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        String userId = "resident".equals(userRole) ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
                        firebaseService.getBills(bills -> {
                            filteredBills.clear();
                            if ("resident".equals(userRole) && userId != null) {
                                for (Bill b : bills) {
                                    firebaseService.getApartmentByResident(userId, apartment -> {
                                        if (apartment != null && b.getApartmentId().equals(apartment.getId())) {
                                            filteredBills.add(b);
                                        }
                                        billAdapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    });
                                }
                            } else {
                                filteredBills.addAll(bills);
                                billAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        });
                        showQRCodeDialog();
                    } else {
                        Toast.makeText(getContext(), "Cập nhật trạng thái thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else {
            payButton.setVisibility(View.GONE);
        }

        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showQRCodeDialog() {
        Dialog qrDialog = new Dialog(requireContext(), R.style.CustomDialog);
        qrDialog.setContentView(R.layout.dialog_qr_code);

        Window window = qrDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
            window.setAttributes(params);
        }

        MaterialButton closeButton = qrDialog.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> qrDialog.dismiss());

        qrDialog.show();
    }
}