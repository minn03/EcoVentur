package com.example.ecoventur.ui.ecorewards.models;

import com.google.firebase.Timestamp;

public class Voucher {
    private String voucherTitle;
    private Timestamp expiryDate; // Store expiry date as Timestamp
    private String imageUrl;

    public Voucher(String voucherTitle, Timestamp expiryDate, String imageUrl) {
        this.voucherTitle = voucherTitle;
        this.expiryDate = expiryDate;
        this.imageUrl = imageUrl;
    }

    public String getVoucherTitle() {
        return voucherTitle;
    }

    public void setVoucherTitle(String voucherTitle) {
        this.voucherTitle = voucherTitle;
    }

    public Timestamp getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
