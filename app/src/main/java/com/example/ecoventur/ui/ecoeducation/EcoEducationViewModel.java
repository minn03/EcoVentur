package com.example.ecoventur.ui.ecoeducation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EcoEducationViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EcoEducationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("EcoEducation module contents...\n[TAY MIN EN]");
    }

    public LiveData<String> getText() {
        return mText;
    }
}