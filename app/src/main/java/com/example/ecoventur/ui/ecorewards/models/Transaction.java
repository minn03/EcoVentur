package com.example.ecoventur.ui.ecorewards.models;

public class Transaction {
    private String transactionId;
    private String transactionTitle;
    private int ecoCoins;

    public Transaction() {
        this.transactionTitle = "Unspecified Transaction Title";
        this.ecoCoins = 0;
    }

    public Transaction(String voucherTitle, int ecoCoins) {
        this.transactionTitle = voucherTitle;
        this.ecoCoins = ecoCoins;
    }

    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    public String getTransactionTitle() {
        return transactionTitle;
    }
    public void setTransactionTitle(String transactionTitle) {
        this.transactionTitle = transactionTitle;
    }
    public int getEcoCoins() {
        return ecoCoins;
    }
    public void setEcoCoins(int ecoCoins) {
        this.ecoCoins = ecoCoins;
    }
}
