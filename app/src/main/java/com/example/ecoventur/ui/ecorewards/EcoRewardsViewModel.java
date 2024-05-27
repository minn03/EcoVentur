package com.example.ecoventur.ui.ecorewards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EcoRewardsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EcoRewardsViewModel() {
        mText = new MutableLiveData<>();
//        mText.setValue("EcoCoins & EcoRewards module contents...\n[TOH YAN XIN]");
    }

    public LiveData<String> getText() {
        return mText;
    }
}