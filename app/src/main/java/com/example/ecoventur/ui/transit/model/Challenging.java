package com.example.ecoventur.ui.transit.model;

import java.util.Date;

public class Challenging {

    String imageUrl;
    String title;
    Date endDate;

    // Make sure to import the correct DocumentReference class
    private String challengingID;

    //Constructor
    public Challenging() {
    }

    public Challenging(String imageUrl, String title, Date endDate, String challengingID) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.endDate = endDate;
        this.challengingID = challengingID;
    }

    //Getter and Setter
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    // Getter and setter for challengingID
    public String getChallengingID() {
        return challengingID;
    }

    public void setChallengingID(String challengingID) {
        this.challengingID = challengingID;
    }
}
