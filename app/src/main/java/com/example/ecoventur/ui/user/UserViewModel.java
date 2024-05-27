package com.example.ecoventur.ui.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public UserViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Profile Management & User Authentication module contents...\n[GAN SERENE]");
    }

    public LiveData<String> getText() {
        return mText;
    }
}