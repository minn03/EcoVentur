package com.example.ecoventur.ui.transit.model;

import java.util.List;

public class Submitted {

    //instance variables
    String userID;
    String challengingID;
    String challengeTitle;
    List<String> imageUrl;
    String description;
    String ecocoins;

    //Constructor
    public Submitted() {
    }

    public Submitted(String userID, String challengingID, String challengeTitle, List<String> imageUrl, String description, String ecocoins) {
        this.userID = userID;
        this.challengingID = challengingID;
        this.challengeTitle = challengeTitle;
        this.imageUrl = imageUrl;
        this.description = description;
        this.ecocoins = ecocoins;
    }

    //Getter and Setter
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getChallengingID(){
        return challengingID;
    }

    public void setChallengingID(String challengingID){
        this.challengingID = challengingID;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEcocoins() {
        return ecocoins;
    }

    public void setEcocoins(String ecocoins) {
        this.ecocoins = ecocoins;
    }

}
