package com.example.ecoventur.ui.greenspace;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReviewsList {
    private final ArrayList<Review> reviews = new ArrayList<>();
    FirebaseFirestore db;
    String placeId;
    Callback callback;
    public ReviewsList(String placeId, Callback callback) {
        db = FirebaseFirestore.getInstance();
        this.placeId = placeId;
        this.callback = callback;
        retrieveFirestoreData();
    }
    private void retrieveFirestoreData() {
        //retrieve data from firestore
        db.collection("greenSpaces").document(placeId)
                .collection("reviews")
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    List<Task<DocumentSnapshot>> userTasks = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                        Review review = new Review();
                        if (documentSnapshot.contains("reviewerUID")) {
                            review.setReviewerUID(documentSnapshot.getString("reviewerUID"));
                            Task<DocumentSnapshot> userTask = db.collection("users").document(review.getReviewerUID()).get();
                            userTasks.add(userTask);
                        }
                        if (documentSnapshot.contains("description")) review.setDescription(documentSnapshot.getString("description"));
                        if (documentSnapshot.contains("imageLink")) review.setImageLink(documentSnapshot.getString("imageLink"));
                        if (documentSnapshot.contains("rating")) {
                            double rating = documentSnapshot.getDouble("rating");
                            review.setRating((float) rating);
                        }
                        if (documentSnapshot.contains("time")) {
                            Timestamp time = documentSnapshot.getTimestamp("time");
                            if (time != null) {
                                String timeStamp = dateFormat.format(time.toDate());
                                review.setTimestamp(timeStamp);
                            }
                        }
                        reviews.add(review);
                    }
                    Tasks.whenAllComplete(userTasks)
                            .addOnSuccessListener(taskSnapshots -> {
                                for (int i = 0; i < userTasks.size(); i++) {
                                    DocumentSnapshot userSnapshot = (DocumentSnapshot) taskSnapshots.get(i).getResult();
                                    if (userSnapshot != null && userSnapshot.contains("username")) {
                                        reviews.get(i).setReviewerName(userSnapshot.getString("username"));
                                    }
                                }
                                callback.onDataLoaded(reviews);
                            })
                            .addOnFailureListener(e -> callback.onFailure(e));
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }
    public ArrayList<Review> getReviews() {
        return reviews;
    }
}
