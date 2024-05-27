package com.example.ecoventur.ui.ecorewards.viewModels;

import android.util.Log;

import com.example.ecoventur.ui.ecorewards.models.Transaction;
import com.example.ecoventur.ui.greenspace.Callback;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ViewEcocoinsBalanceViewModel {
    private String UID;
    private int ecocoinsBalance = -1;
    private ArrayList<Transaction> earningList = new ArrayList<>();
    private ArrayList<Transaction> spendingList = new ArrayList<>();
    public ViewEcocoinsBalanceViewModel(String UID) {
        this.UID = UID;
    }
    public void retrieveEcocoinsBalance(Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(UID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("ecocoin")) {
                    ecocoinsBalance = documentSnapshot.getLong("ecocoin").intValue();
                    callback.onDataLoaded(ecocoinsBalance);
                }
                else {
                    ecocoinsBalance = -1;
                    callback.onDataLoaded(ecocoinsBalance);
                }
            }
            else {
                ecocoinsBalance = -1;
                callback.onDataLoaded(ecocoinsBalance);
            }
        }).addOnFailureListener(e -> {
            Log.e("ViewEcocoinsBalanceViewModel", "Error retrieving ecocoins balance: " + e.getMessage());
            callback.onFailure(e);
        });
    }
    public void retrieveEarningList(Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(UID)
                .collection("earning").orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.contains("title") && documentSnapshot.contains("ecoCoin")) {
                            Transaction transaction = new Transaction();
                            transaction.setTransactionId(documentSnapshot.getId());
                            transaction.setTransactionTitle(documentSnapshot.getString("title"));
                            transaction.setEcoCoins(documentSnapshot.getLong("ecoCoin").intValue());
                            earningList.add(transaction);
                        }
                    }
                    callback.onDataLoaded(earningList);
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewEcocoinsBalanceViewModel", "Error retrieving earning list: " + e.getMessage());
                    callback.onFailure(e);
                });
    }
    public void retrieveSpendingList(Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(UID)
                .collection("spending").orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.contains("title") && documentSnapshot.contains("ecoCoin")) {
                            Transaction transaction = new Transaction();
                            transaction.setTransactionId(documentSnapshot.getId());
                            transaction.setTransactionTitle(documentSnapshot.getString("title"));
                            transaction.setEcoCoins(documentSnapshot.getLong("ecoCoin").intValue());
                            spendingList.add(transaction);
                        }
                    }
                    callback.onDataLoaded(spendingList);
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewEcocoinsBalanceViewModel", "Error retrieving spending list: " + e.getMessage());
                    callback.onFailure(e);
                });
    }
    public int getEcocoinsBalance() {
        return ecocoinsBalance;
    }
    public ArrayList<Transaction> getEarningList() {
        return earningList;
    }
    public ArrayList<Transaction> getSpendingList() {
        return spendingList;
    }
}
