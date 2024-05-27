package com.example.ecoventur.ui.greenspace;

import static com.example.ecoventur.ui.greenspace.approxDistanceBetweenLocation.HaversineFormula;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class GreenEventsList {
    private ArrayList<GreenEvent> greenEvents = new ArrayList<>();
    private Date today;
    public GreenEventsList() {
        //hardcoded data
        greenEvents.add(new GreenEvent("Tree Planting Day", "10 Nov 2023", "Botanical Garden KL", 50));
        GreenEvent event = new GreenEvent("Nature Walks", "11 Nov 2023 - 13 Nov 2023", "Lake Garden KL", 10);
        event.setImageLink(null);
        event.setDuration("All day");
        event.setRegistrationFee(0.00);
        event.setVenueAddress("Perdana Botanical Gardens, 50480 Kuala Lumpur, Federal Territory of Kuala Lumpur");
        event.setVenueLink("https://maps.app.goo.gl/rfVADHhb32FYZBkWA");
        event.setGoing(23);
        event.setInterested(67);
        event.setTncLink("https://www.google.com");
        event.setDetailsLink("https://www.google.com");
        greenEvents.add(event);
        greenEvents.add(new GreenEvent("Beach Cleanup", "11 Nov 2023", "Pantai Tanjung Piai", 50));
        greenEvents.add(new GreenEvent("Eco-Workshops", "15 Nov 2023", "Online", 5));
    }
    public GreenEventsList(FirebaseFirestore db, Callback callback) {
        //retrieve all upcoming green events from firestore
        today = new Date();
        retrieveFirestoreData(db, callback);
    }
    public GreenEventsList(FirebaseFirestore db, String UID, Callback callback) {
        //retrieve user's saved green events wishlist from firestore
        today = new Date();
        retrieveUserWishlist(db, UID, callback);
    }
    private void retrieveFirestoreData(FirebaseFirestore db, Callback callback) {
        //retrieve data from firestore
        db.collection("greenEvents")
                .whereGreaterThanOrEqualTo("date", today)
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            GreenEvent event = new GreenEvent();
                            event.setEventId(document.getId());
                            if (document.contains("name")) event.setName(document.getString("name"));
                            if (document.contains("date")) {
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
                                event.setDate(formatter.format(document.getTimestamp("date").toDate()));
                            }
                            if (document.contains("venue")) event.setVenue(document.getString("venue"));
                            if (document.contains("ecoCoins")) event.setEcoCoins(document.getLong("ecoCoins").intValue());
                            greenEvents.add(event);
                        }
                        callback.onDataLoaded(greenEvents);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }
    private void retrieveUserWishlist(FirebaseFirestore db, String UID, Callback callback) {
        //retrieve user's wishlist from firestore
        //first retrieve all upcoming green events from firestore then filter out the ones in user's wishlist
        retrieveFirestoreData(db, new Callback() {
            @Override
            public void onDataLoaded(Object object) {
                List<String> eventIds = new ArrayList<>();
                db.collection("users").document(UID).collection("eventsWishlist")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document: task.getResult()) {
                                    if (document.contains("eventId")) {
                                        String eventId = document.getDocumentReference("eventId").getId();
                                        eventIds.add(eventId);
                                    }
                                }
                                ArrayList<GreenEvent> filteredEvents = new ArrayList<>();
                                for (GreenEvent event: greenEvents) {
                                    if (eventIds.contains(event.getEventId())) {
                                        filteredEvents.add(event);
                                    }
                                }
                                callback.onDataLoaded(filteredEvents);
                            }
                            else {
                                callback.onFailure(task.getException());
                            }
                        });
            }
            @Override
            public void onFailure(Exception e) {
                System.out.println("Error retrieving green events: " + e);
            }
        });
    }
    public ArrayList<GreenEvent> getGreenEvents() {
        return greenEvents;
    }
}
