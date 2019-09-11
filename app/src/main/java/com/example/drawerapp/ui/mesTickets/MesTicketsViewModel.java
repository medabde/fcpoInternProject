package com.example.drawerapp.ui.mesTickets;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MesTicketsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MesTicketsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("tickets...");
    }

    public LiveData<String> getText() {
        return mText;
    }
}