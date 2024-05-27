package com.example.ecoventur.ui.greenspace;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class FirebasePlaceAdder extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addNewPlace();
    }
    private void addNewPlace() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> place = new HashMap<>();

//        place.put("address", "Forest Research Institute Malaysia (FRIM), 68100 Kuala Lumpur, Federal Territory of Kuala Lumpur");
//        place.put("entryFee", 40);
//        place.put("latLng", new GeoPoint(3.2337728497175044, 101.63358740876234));
//        place.put("link", "https://maps.app.goo.gl/bCRha8Q7Hm5LLHEE7");
//        place.put("name", "FRIM Tropical Forest Research Institute");
//        place.put("openingHours", "");
//
//        db.collection("greenSpaces")
//                .add(place)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        System.out.println("DocumentSnapshot added with ID: " + documentReference.getId());
//                        String placeDocumentId = documentReference.getId();
//                        db.collection("greenSpaces")
//                                .document(placeDocumentId)
//                                .collection("reviews")
//                                .document("placeholder")
//                                .set(new HashMap<>())
//                                .addOnSuccessListener(aVoid -> {
//                                    System.out.println("Empty 'reviews' collection created");
//                                })
//                                .addOnFailureListener(e -> System.out.println("Error adding document: " + e));
//                    }
//                })
//                .addOnFailureListener(e -> System.out.println("Error adding document: " + e));
    }
}
