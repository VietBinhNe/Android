package com.example.apartmentmanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.adapters.BillAdapter;
import com.example.apartmentmanager.viewmodels.BillViewModel;
import java.util.ArrayList;

public class BillFragment extends Fragment {
    private RecyclerView recyclerView;
    private BillAdapter billAdapter;
    private BillViewModel billViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill, container, false);

        recyclerView = view.findViewById(R.id.bill_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        billAdapter = new BillAdapter(new ArrayList<>());
        recyclerView.setAdapter(billAdapter);

        billViewModel = new ViewModelProvider(this).get(BillViewModel.class);
        billViewModel.getBills().observe(getViewLifecycleOwner(), bills -> {
            billAdapter = new BillAdapter(bills);
            recyclerView.setAdapter(billAdapter);
        });

        return view;
    }
}