package com.example.ecoventur.ui.greenspace;

public class Review {
    private String reviewerUID = null;
    private String reviewerName = "Unknown";
    private String description = null;
    private String imageLink = null;
    private float rating = -1.0f;
    private String timestamp = null;
    public String getReviewerUID() {
        return reviewerUID;
    }
    public String getReviewerName() {
        return reviewerName;
    }
    public String getDescription() {
        return description;
    }
    public String getImageLink() {
        return imageLink;
    }
    public float getRating() {
        return rating;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setReviewerUID(String reviewerUID) {
        this.reviewerUID = reviewerUID;
    }
    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
    public void setRating(float rating) {
        this.rating = rating;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
