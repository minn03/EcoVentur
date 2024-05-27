package com.example.ecoventur.ui.greenspace;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class FirebaseEventAdder extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addNewEvent();
    }
    private void addNewEvent() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> event = new HashMap<>();

//        event.put("date", "24 Jan 2024");
//        event.put("duration", "10:00 AM - 2:00 PM");
//        event.put("ecoCoins", 40);
//        event.put("going", 20);
//        event.put("interested", 30);
//        event.put("name", "Mangrove Conservation Day");
//        event.put("registrationFee", 0);
//        event.put("venue", "Bako National Park");
//        event.put("venueAddress", "Bako National Park, Sarawak");
//        // Assuming venueLatLng is a GeoPoint object with latitude and longitude
//        event.put("venueLatLng", new GeoPoint(1.7168061105422379, 110.46739625498715));
//        event.put("venueLink", "https://maps.app.goo.gl/W8s8p45qQTvCK9gr8");
//        event.put("tncLink", "https://skyandtelescope.org/astronomy-resources/stargazing-basics/");
//        event.put("detailsLink", "https://www.conservation.org/priorities/mangroves");

        db.collection("greenEvents")
                .add(event)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        System.out.println("DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(e -> System.out.println("Error adding document: " + e));
    }
}
