package com.example.apartmentmanager.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class NotificationViewModelFactory implements ViewModelProvider.Factory {
    private final String userRole;

    public NotificationViewModelFactory(String userRole) {
        this.userRole = userRole;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (NotificationViewModel.class.isAssignableFrom(modelClass)) {
            @SuppressWarnings("unchecked")
            T viewModel = (T) new NotificationViewModel(userRole);
            return viewModel;
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}