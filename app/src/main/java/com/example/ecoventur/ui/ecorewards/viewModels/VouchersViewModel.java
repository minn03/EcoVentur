package com.example.ecoventur.ui.ecorewards.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecoventur.ui.ecorewards.models.Voucher;

import java.util.List;

public class VouchersViewModel extends ViewModel {
    private MutableLiveData<List<Voucher>> activeVouchers = new MutableLiveData<>();
    private MutableLiveData<List<Voucher>> pastVouchers = new MutableLiveData<>();
    private MutableLiveData<String> redeemedVoucherName = new MutableLiveData<>();
    private MutableLiveData<String> redeemedVoucherImageURL = new MutableLiveData<>();
    private MutableLiveData<Integer> redeemedVoucherEcoCoins = new MutableLiveData<>();

    public LiveData<List<Voucher>> getActiveVouchers() {
        return activeVouchers;
    }

    // Update the value of activeVouchers
    public void setActiveVouchers(List<Voucher> activeVouchersList) {
        activeVouchers.setValue(activeVouchersList);
    }

    public LiveData<List<Voucher>> getPastVouchers() {
        return pastVouchers;
    }

    // Update the value of pastVouchers
    public void setPastVouchers(List<Voucher> pastVouchersList) {
        pastVouchers.setValue(pastVouchersList);
    }

    // Set the details of the redeemed voucher
    public void setRedeemedVoucherDetails(String voucherName, String imageURL, int ecoCoins) {
        redeemedVoucherName.setValue(voucherName);
        redeemedVoucherImageURL.setValue(imageURL);
        redeemedVoucherEcoCoins.setValue(ecoCoins);
    }

    public LiveData<String> getRedeemedVoucherName() {
        return redeemedVoucherName;
    }

    public LiveData<String> getRedeemedVoucherImageURL() {
        return redeemedVoucherImageURL;
    }

    public LiveData<Integer> getRedeemedVoucherEcoCoins() {
        return redeemedVoucherEcoCoins;
    }
}
