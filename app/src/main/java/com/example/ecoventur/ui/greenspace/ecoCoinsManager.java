package com.example.ecoventur.ui.greenspace;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ecoCoinsManager {
    public static void addEcoCoins(String UID, String title, int ecoCoins, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        addEarningTransaction(UID, title, ecoCoins, new Callback() {
            @Override
            public void onDataLoaded(Object data) {
                db.collection("users").document(UID)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                if (documentSnapshot.contains("ecocoin")) {
                                    int currentEcoCoins = documentSnapshot.getLong("ecocoin").intValue();
                                    int updatedEcoCoins = currentEcoCoins + ecoCoins;
                                    db.collection("users").document(UID)
                                            .update("ecocoin", updatedEcoCoins)
                                            .addOnSuccessListener(aVoid -> callback.onDataLoaded(updatedEcoCoins))
                                            .addOnFailureListener(callback::onFailure);
                                }
                                else {
                                    db.collection("users").document(UID).update("ecocoin", ecoCoins);
                                }
                            }
                            else {
                                callback.onFailure(new Exception("User does not exist."));
                            }
                        })
                        .addOnFailureListener(callback::onFailure);
            }
            @Override
            public void onFailure(Exception exception) {
                callback.onFailure(exception);
            }
        });

    }
    public static void deductEcoCoins(String UID, String title, int ecoCoins, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        addSpendingTransaction(UID, title, ecoCoins, new Callback() {
            @Override
            public void onDataLoaded(Object data) {
                db.collection("users").document(UID)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                if (documentSnapshot.contains("ecocoin")) {
                                    int currentEcoCoins = documentSnapshot.getLong("ecocoin").intValue();
                                    int updatedEcoCoins = currentEcoCoins - ecoCoins;
                                    db.collection("users").document(UID)
                                            .update("ecocoin", updatedEcoCoins)
                                            .addOnSuccessListener(aVoid -> callback.onDataLoaded(updatedEcoCoins))
                                            .addOnFailureListener(callback::onFailure);;
                                }
                                else {
                                    db.collection("users").document(UID).update("ecocoin", -ecoCoins);
                                }
                            }
                            else {
                                callback.onFailure(new Exception("User does not exist."));
                            }
                        })
                        .addOnFailureListener(callback::onFailure);
            }
            @Override
            public void onFailure(Exception exception) {
                callback.onFailure(exception);
            }
        });
    }
    public static void updateEcoCoins(String UID, int ecoCoins, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(UID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        db.collection("users").document(UID)
                                .update("ecocoin", ecoCoins)
                                .addOnSuccessListener(aVoid -> callback.onDataLoaded(ecoCoins))
                                .addOnFailureListener(callback::onFailure);;
                    }
                    else {
                        callback.onFailure(new Exception("User does not exist."));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }
    private static void addEarningTransaction(String UID, String title, int ecoCoins, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> earningData = new HashMap<>();
        earningData.put("ecoCoin", ecoCoins);
        earningData.put("title", title);
        earningData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users").document(UID)
                .collection("earning")
                .add(earningData)
                .addOnSuccessListener(documentReference -> callback.onDataLoaded(documentReference.getId()))
                .addOnFailureListener(callback::onFailure);
    }
    private static void addSpendingTransaction(String UID, String title, int ecoCoins, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> spendingData = new HashMap<>();
        spendingData.put("ecoCoin", ecoCoins);
        spendingData.put("title", title);
        spendingData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users").document(UID)
                .collection("spending")
                .add(spendingData)
                .addOnSuccessListener(documentReference -> callback.onDataLoaded(documentReference.getId()))
                .addOnFailureListener(callback::onFailure);
    }
}
