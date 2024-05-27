package com.example.ecoventur.ui.transit.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;

public class AllChallenges implements Parcelable {

    String id;
    String imageUrl;
    List<String> tags;
    String title;
    Date startDate;
    Date endDate;
    String description;
    List <String> rules;

    //Constructor
    public AllChallenges() {
    }

    public AllChallenges(String id,String imageUrl, List<String> tags, String title, Date startDate, Date endDate, String description, List<String> rules) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.tags = tags;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.rules = rules;
    }

    protected AllChallenges(Parcel in) {
        id = in.readString();
        imageUrl = in.readString();
        tags = in.createStringArrayList();
        title = in.readString();
        description = in.readString();
        rules = in.createStringArrayList();
    }

    public static final Creator<AllChallenges> CREATOR = new Creator<AllChallenges>() {
        @Override
        public AllChallenges createFromParcel(Parcel in) {
            return new AllChallenges(in);
        }

        @Override
        public AllChallenges[] newArray(int size) {
            return new AllChallenges[size];
        }
    };

    //Getter & Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(imageUrl);
        dest.writeStringList(tags);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeStringList(rules);
    }
}
