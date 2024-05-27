package com.example.ecoventur.ui.ecorewards.models;

public class Catalog {
    private String voucherId;
    private String voucherTitle;
    private int ecoCoins;
    private String imgURL1;
    private String imgURL2;

    public Catalog() {
        this.voucherId = "Unspecified Voucher ID";
        this.voucherTitle = "Unspecified Voucher Title";
        this.ecoCoins = 0;
        this.imgURL1 = "Unspecified Image URL 1";
        this.imgURL2 = "Unspecified Image URL 2";
    }

    public Catalog(String voucherTitle, int ecoCoins, String imgURL1, String imgURL2) {
        this.voucherTitle = voucherTitle;
        this.ecoCoins = ecoCoins;
        this.imgURL1 = imgURL1;
        this.imgURL2 = imgURL2;
    }

    public String getVoucherTitle() {
        return voucherTitle;
    }

    public void setVoucherTitle(String voucherTitle) {
        this.voucherTitle = voucherTitle;
    }

    public int getEcoCoins() {
        return ecoCoins;
    }

    public void setEcoCoins(int ecoCoins) {
        this.ecoCoins = ecoCoins;
    }

    public String getImgURL1() {
        return imgURL1;
    }

    public void setImgURL1(String imgURL1) {
        this.imgURL1 = imgURL1;
    }

    public String getImgURL2() {
        return imgURL2;
    }

    public void setImgURL2(String imgURL2) {
        this.imgURL2 = imgURL2;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }
}
