package com.example.ecoventur.ui.ecoeducation.models;

public class QuizResult {
    private String ecoCoins, timestamp, title;

    public String getEcoCoins() {
        return ecoCoins;
    }

    public void setEcoCoins(String ecoCoins) {
        this.ecoCoins = ecoCoins;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = String.valueOf(System.currentTimeMillis());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
