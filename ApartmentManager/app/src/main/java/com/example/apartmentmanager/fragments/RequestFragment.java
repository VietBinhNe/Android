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
import com.example.apartmentmanager.adapters.RequestAdapter;
import com.example.apartmentmanager.viewmodels.RequestViewModel;
import java.util.ArrayList;

public class RequestFragment extends Fragment {
    private RecyclerView recyclerView;
    private RequestAdapter requestAdapter;
    private RequestViewModel requestViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, container, false);

        recyclerView = view.findViewById(R.id.request_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        requestAdapter = new RequestAdapter(new ArrayList<>());
        recyclerView.setAdapter(requestAdapter);

        requestViewModel = new ViewModelProvider(this).get(RequestViewModel.class);
        requestViewModel.getRequests().observe(getViewLifecycleOwner(), requests -> {
            requestAdapter = new RequestAdapter(requests);
            recyclerView.setAdapter(requestAdapter);
        });

        return view;
    }
}