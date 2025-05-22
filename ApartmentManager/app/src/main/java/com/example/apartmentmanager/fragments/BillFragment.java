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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BillFragment extends Fragment {
    private static final String TAG = "BillFragment";
    private RecyclerView recyclerView;
    private View progressBar;
    private BillAdapter billAdapter;
    private FirebaseService firebaseService;
    private String userRole;
    private List<Bill> filteredBills;
    private FloatingActionButton addBillFab;
    private String userId; // Khai báo userId ở cấp độ class

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill, container, false);

        recyclerView = view.findViewById(R.id.bill_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        addBillFab = view.findViewById(R.id.add_bill_fab);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseService = new FirebaseService();
        userRole = getArguments() != null ? getArguments().getString("userRole") : "resident";
        Log.d(TAG, "User role: " + userRole);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        filteredBills = new ArrayList<>();
        billAdapter = new BillAdapter(filteredBills, this::showBillDetailsDialog);
        recyclerView.setAdapter(billAdapter);

        userId = "resident".equals(userRole) ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null; // Khởi tạo userId

        if ("admin".equals(userRole)) {
            addBillFab.setVisibility(View.VISIBLE);
            addBillFab.setOnClickListener(v -> showAddBillDialog());
        } else {
            addBillFab.setVisibility(View.GONE);
        }

        Log.d(TAG, "Loading bills for user: " + (userId != null ? userId : "admin"));
        loadBills(userId);

        return view;
    }

    private void showAddBillDialog() {
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.dialog_add_bill);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
            window.setAttributes(params);
        }

        TextInputEditText titleInput = dialog.findViewById(R.id.bill_title_input);
        TextInputEditText apartmentIdInput = dialog.findViewById(R.id.apartment_id_input);
        TextInputEditText item1NameInput = dialog.findViewById(R.id.item1_name_input);
        TextInputEditText item1AmountInput = dialog.findViewById(R.id.item1_amount_input);
        TextInputEditText item2NameInput = dialog.findViewById(R.id.item2_name_input);
        TextInputEditText item2AmountInput = dialog.findViewById(R.id.item2_amount_input);
        TextInputEditText dueDateInput = dialog.findViewById(R.id.bill_due_date_input);
        MaterialButton saveButton = dialog.findViewById(R.id.save_button);
        MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);

        saveButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String apartmentId = apartmentIdInput.getText().toString().trim();
            String item1Name = item1NameInput.getText().toString().trim();
            String item1AmountStr = item1AmountInput.getText().toString().trim();
            String item2Name = item2NameInput.getText().toString().trim();
            String item2AmountStr = item2AmountInput.getText().toString().trim();
            String dueDate = dueDateInput.getText().toString().trim();

            if (title.isEmpty() || apartmentId.isEmpty() || dueDate.isEmpty() ||
                    item1Name.isEmpty() || item1AmountStr.isEmpty() ||
                    item2Name.isEmpty() || item2AmountStr.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double item1Amount, item2Amount;
            try {
                item1Amount = Double.parseDouble(item1AmountStr);
                item2Amount = Double.parseDouble(item2AmountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Số tiền phải là số hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            List<BillItem> items = new ArrayList<>();
            items.add(new BillItem(item1Name, item1Amount));
            items.add(new BillItem(item2Name, item2Amount));

            String billId = UUID.randomUUID().toString();
            Bill bill = new Bill(billId, title, apartmentId, items, dueDate, "unpaid");

            firebaseService.getBills(bills -> {
                if (bills != null) {
                    for (Bill existingBill : bills) {
                        if (existingBill.getApartmentId().equals(apartmentId) && existingBill.getStatus().equals("unpaid")) {
                            Toast.makeText(getContext(), "Phòng này đã có hóa đơn chưa thanh toán", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }
                    }

                    firebaseService.updateBill(bill, success -> {
                        if (success) {
                            Toast.makeText(getContext(), "Tạo hóa đơn thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            loadBills(userId); // Sử dụng userId đã khai báo ở cấp độ class
                        } else {
                            Toast.makeText(getContext(), "Tạo hóa đơn thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Lỗi khi kiểm tra hóa đơn", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
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

        if ("unpaid".equals(bill.getStatus()) && "resident".equals(userRole)) { // Chỉ resident được thanh toán
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
                        loadBills(userId); // Sử dụng userId đã khai báo ở cấp độ class
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

    private void loadBills(String userId) {
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