package com.example.apartmentmanager.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.adapters.BillAdapter;
import com.example.apartmentmanager.models.Bill;
import com.example.apartmentmanager.models.BillItem;
import com.example.apartmentmanager.network.FirebaseService;
import com.example.apartmentmanager.viewmodels.BillViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BillFragment extends Fragment {
    private RecyclerView recyclerView;
    private BillAdapter billAdapter;
    private BillViewModel billViewModel;
    private FirebaseService firebaseService;
    private String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill, container, false);

        userRole = requireActivity().getIntent().getStringExtra("userRole");
        recyclerView = view.findViewById(R.id.bill_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseService = new FirebaseService();
        billViewModel = new ViewModelProvider(this).get(BillViewModel.class);

        if ("admin".equals(userRole)) {
            // Admin: Quản lý hóa đơn (xem tất cả, thêm, xóa)
            billAdapter = new BillAdapter(new ArrayList<>(), billId -> {
                firebaseService.deleteBill(billId, success -> {
                    if (success) {
                        Toast.makeText(getContext(), "Xóa hóa đơn thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Xóa hóa đơn thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            recyclerView.setAdapter(billAdapter);
            billViewModel.getBills().observe(getViewLifecycleOwner(), bills -> {
                if (bills != null) {
                    billAdapter = new BillAdapter(bills, billId -> {
                        firebaseService.deleteBill(billId, success -> {
                            if (success) {
                                Toast.makeText(getContext(), "Xóa hóa đơn thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Xóa hóa đơn thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                    recyclerView.setAdapter(billAdapter);
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách hóa đơn", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Resident: Chỉ xem hóa đơn của phòng mình
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            firebaseService.getApartmentByResident(userId, apartment -> {
                if (apartment != null) {
                    firebaseService.getBillsByApartment(apartment.getId(), bills -> {
                        if (bills != null) {
                            billAdapter = new BillAdapter(bills, null);
                            recyclerView.setAdapter(billAdapter);
                        } else {
                            billAdapter = new BillAdapter(new ArrayList<>(), null);
                            recyclerView.setAdapter(billAdapter);
                            Toast.makeText(getContext(), "Không có hóa đơn nào cho phòng của bạn", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    billAdapter = new BillAdapter(new ArrayList<>(), null);
                    recyclerView.setAdapter(billAdapter);
                    Toast.makeText(getContext(), "Không tìm thấy phòng của bạn", Toast.LENGTH_SHORT).show();
                }
            });
        }

        FloatingActionButton addBillFab = view.findViewById(R.id.add_bill_fab);
        if ("admin".equals(userRole)) {
            addBillFab.setVisibility(View.VISIBLE);
            addBillFab.setOnClickListener(v -> showAddBillDialog());
        } else {
            addBillFab.setVisibility(View.GONE);
        }

        return view;
    }

    private void showAddBillDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_bill);

        TextInputEditText titleEditText = dialog.findViewById(R.id.bill_title_input);
        TextInputEditText apartmentIdEditText = dialog.findViewById(R.id.apartment_id_input);
        TextInputEditText item1NameEditText = dialog.findViewById(R.id.item1_name_input);
        TextInputEditText item1AmountEditText = dialog.findViewById(R.id.item1_amount_input);
        TextInputEditText item2NameEditText = dialog.findViewById(R.id.item2_name_input);
        TextInputEditText item2AmountEditText = dialog.findViewById(R.id.item2_amount_input);
        TextInputEditText dueDateEditText = dialog.findViewById(R.id.bill_due_date_input);
        MaterialButton saveButton = dialog.findViewById(R.id.save_button);
        MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);

        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String apartmentId = apartmentIdEditText.getText().toString().trim();
            String item1Name = item1NameEditText.getText().toString().trim();
            String item1AmountStr = item1AmountEditText.getText().toString().trim();
            String item2Name = item2NameEditText.getText().toString().trim();
            String item2AmountStr = item2AmountEditText.getText().toString().trim();
            String dueDate = dueDateEditText.getText().toString().trim();

            if (title.isEmpty() || apartmentId.isEmpty() || item1Name.isEmpty() || item1AmountStr.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double item1Amount = Double.parseDouble(item1AmountStr);
                List<BillItem> items = new ArrayList<>();
                items.add(new BillItem(item1Name, item1Amount));

                double totalAmount = item1Amount;
                if (!item2Name.isEmpty() && !item2AmountStr.isEmpty()) {
                    double item2Amount = Double.parseDouble(item2AmountStr);
                    items.add(new BillItem(item2Name, item2Amount));
                    totalAmount += item2Amount;
                }

                Bill bill = new Bill(UUID.randomUUID().toString(), title, apartmentId, items, totalAmount, dueDate);
                firebaseService.addBill(bill, success -> {
                    if (success) {
                        Toast.makeText(getContext(), "Thêm hóa đơn thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Thêm hóa đơn thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}