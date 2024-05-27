package com.example.ecoventur.ui.greenspace;

import static com.example.ecoventur.ui.greenspace.approxDistanceBetweenLocation.HaversineFormula;

import android.media.Image;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GreenEvent {
    private String eventId = null;
    private String name = "Unspecified Event Name";
    private String date = "Unspecified Date";
    private String venue = "Unspecified Event Venue";
    private int ecoCoins = -1;
    private String imageLink = null;
    private String duration = "Unspecified Duration";
    private double registrationFee = -1.0;
    private String venueAddress = "Unspecified Venue Address";
    private String venueLink = null;
    private LatLng venueLatLng = null;
    private double approxDistance = -1.0;
    private int going = -1;
    private int interested = -1;
    private String tncLink = null;
    private String detailsLink = null;
    private boolean savedToWishlist = false;

    public GreenEvent(){
        // empty constructor required for Firestore (retrieving list of GreenEvents)
    }
    public GreenEvent (String eventName, String date, String venue, int ecoCoins) {
        // for hard coded data
        this.name = eventName;
        this.date = date;
        this.venue = venue;
        this.ecoCoins = ecoCoins;
    }
    public GreenEvent (String eventId, String UID, LatLng currentLatLng, Callback callback) {
        // for Firestore (retrieving details of specified GreenEvent)
        this.eventId = eventId;
        fetchDetailsFromFirestore(UID, currentLatLng, callback);
    }
    private void fetchDetailsFromFirestore(String UID, LatLng currentLatLng, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("greenEvents").document(eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.contains("name")) this.setName(document.getString("name"));
                        if (document.contains("date")) {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
                            this.setDate(formatter.format(document.getTimestamp("date").toDate()));
                        }
                        if (document.contains("venue")) this.setVenue(document.getString("venue"));
                        if (document.contains("ecoCoins")) this.setEcoCoins(document.getLong("ecoCoins").intValue());
                        if (document.contains("imageLink")) this.setImageLink(document.getString("imageLink"));
                        if (document.contains("duration")) this.setDuration(document.getString("duration"));
                        if (document.contains("registrationFee")) this.setRegistrationFee(document.getDouble("registrationFee"));
                        if (document.contains("venueAddress")) this.setVenueAddress(document.getString("venueAddress"));
                        this.setVenueLink(document.getString("venueLink"));
                        GeoPoint venueGeoPoint = document.getGeoPoint("venueLatLng");
                        if (venueGeoPoint != null) {
                            this.setVenueLatLng(new LatLng(venueGeoPoint.getLatitude(), venueGeoPoint.getLongitude()));
                        }
                        this.setApproxDistance(HaversineFormula(currentLatLng,venueLatLng));
                        if (document.contains("going")) this.setGoing(document.getLong("going").intValue());
                        if (document.contains("interested")) this.setInterested(document.getLong("interested").intValue());
                        this.setTncLink(document.getString("tncLink"));
                        this.setDetailsLink(document.getString("detailsLink"));
                        callback.onDataLoaded(this);
                    }
                    else {
                        callback.onFailure(new Exception("Document does not exist."));
                    }
                });
        db.collection("users").document(UID).
                collection("eventsWishlist")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DocumentReference eventRef = document.getDocumentReference("eventId");
                            if (eventRef != null) {
                                eventRef.get()
                                        .addOnCompleteListener(eventTask -> {
                                            if (eventTask.isSuccessful()) {
                                                String eventRefId = eventTask.getResult().getId();
                                                if (eventRefId.equals(eventId)) {
                                                    this.setSavedToWishlist(true);
                                                    callback.onDataLoaded(this);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                    else {
                        savedToWishlist = false;
                        callback.onDataLoaded(this);
                    }
                });
    }
    public String getEventId() {
        return eventId;
    }
    public String getName() {
        return name;
    }
    public String getDate() {
        return date;
    }
    public String getVenue() {
        return venue;
    }
    public int getEcoCoins() {
        return ecoCoins;
    }
    public String getImageLink() {
        return imageLink;
    }
    public String getDuration() {
        return duration;
    }
    public double getRegistrationFee() {
        return registrationFee;
    }
    public String getVenueAddress() {
        return venueAddress;
    }
    public String getVenueLink() {
        return venueLink;
    }
    public LatLng getVenueLatLng() {
        return venueLatLng;
    }
    public double getApproxDistance() {
        return approxDistance;
    }
    public int getGoing() {
        return going;
    }
    public int getInterested() {
        return interested;
    }
    public String getTncLink() {
        return tncLink;
    }
    public String getDetailsLink() {
        return detailsLink;
    }
    public boolean isSavedToWishlist() {
        return savedToWishlist;
    }
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setVenue(String venue) {
        this.venue = venue;
    }
    public void setEcoCoins(int ecoCoins) {
        this.ecoCoins = ecoCoins;
    }
    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
    public void setRegistrationFee(double registrationFee) {
        this.registrationFee = registrationFee;
    }
    public void setVenueAddress(String venueAddress) {
        this.venueAddress = venueAddress;
    }
    public void setVenueLink(String venueLink) {
        this.venueLink = venueLink;
    }
    public void setVenueLatLng(LatLng venueLatLng) {
        this.venueLatLng = venueLatLng;
    }
    public void setApproxDistance(double approxDistance) {
        this.approxDistance = approxDistance;
    }
    public void setGoing(int going) {
        this.going = going;
    }
    public void setInterested(int interested) {
        this.interested = interested;
    }
    public void setTncLink(String tncLink) {
        this.tncLink = tncLink;
    }
    public void setDetailsLink(String detailsLink) {
        this.detailsLink = detailsLink;
    }
    public void setSavedToWishlist(boolean savedToWishlist) {
        this.savedToWishlist = savedToWishlist;
    }
}
