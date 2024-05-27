package com.example.ecoventur.ui.transit.model;

import java.util.Date;
import java.util.List;

public class Completed {
    String imageUrl;
    List<String> tags;
    String title;
    Date startDate;
    Date endDate;


    //Constructor
    public Completed() {
    }

    public Completed(String imageUrl, List<String> tags, String title, Date startDate, Date endDate) {
        this.imageUrl = imageUrl;
        this.tags = tags;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    //Getter & Setter
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
