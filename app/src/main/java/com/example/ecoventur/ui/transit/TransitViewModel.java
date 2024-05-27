package com.example.ecoventur.ui.transit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TransitViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TransitViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Green Transit module contents...\n[CHONG BOON PING]");
    }

    public LiveData<String> getText() {
        return mText;
    }
}