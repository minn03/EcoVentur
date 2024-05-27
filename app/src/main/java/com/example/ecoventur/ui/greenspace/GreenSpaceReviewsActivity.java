package com.example.ecoventur.ui.greenspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoventur.R;
import com.example.ecoventur.ui.greenspace.adapter.ReviewsAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class GreenSpaceReviewsActivity extends AppCompatActivity {
    private String placeId;
    private String placeName;
    private String UID;
    private ReviewsList reviewsList;
    private ArrayList<Review> reviews;
    private TextView TVSpaceName, TVTotalRatings, TVAverageRating;
    private ProgressBar PB5Star, PB4Star, PB3Star, PB2Star, PB1Star;
    private RecyclerView RVReviews;
    private CardView CVWriteReview;
    WriteReviewDialog writeReviewDialog;
    ActivityResultLauncher<Intent> ARL = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (writeReviewDialog != null) writeReviewDialog.handleReviewImage(data);
            }
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greenspace_reviews);

        Intent intent = getIntent();
        if (intent != null) {
            this.placeId = intent.getStringExtra("placeId");
            this.UID = intent.getStringExtra("UID");
        }
        if (placeId != null) {
            initializeWidgets();
            reviewsList = new ReviewsList(placeId, new Callback() {
                @Override
                public void onDataLoaded(Object object) {
                    assignUIWidgets();
                }
                @Override
                public void onFailure(Exception e) {
                    System.out.println("Error retrieving green space reviews: " + e.getMessage());
                }
            });
            reviews = reviewsList.getReviews();
        }
    }
    private void initializeWidgets() {
        TVSpaceName = findViewById(R.id.TVSpaceName);
        TVTotalRatings = findViewById(R.id.TVTotalRatings);
        TVAverageRating = findViewById(R.id.TVAverageRating);
        PB5Star = findViewById(R.id.PB5Star);
        PB4Star = findViewById(R.id.PB4Star);
        PB3Star = findViewById(R.id.PB3Star);
        PB2Star = findViewById(R.id.PB2Star);
        PB1Star = findViewById(R.id.PB1Star);
        RVReviews = findViewById(R.id.RVReviews);
        CVWriteReview = findViewById(R.id.CVWriteReview);
    }
    private void assignUIWidgets() {
        getSpaceName(new Callback() {
            @Override
            public void onDataLoaded(Object object) {
                placeName = (String) object;
                TVSpaceName.setText(placeName);
            }
            @Override
            public void onFailure(Exception e) {
                System.out.println("Error retrieving green space name: " + e.getMessage());
            }
        });
        TVTotalRatings.setText(String.format("Average (%d)", getNoOfRatings(reviews)));
        if (getNoOfRatings(reviews) > 0) TVAverageRating.setText(String.format("%.1f", calcAverageRating(reviews)));
        float[] ratingsCount = countRatingPercentages();
        PB5Star.setProgress(Math.round(ratingsCount[4]));
        PB4Star.setProgress(Math.round(ratingsCount[3]));
        PB3Star.setProgress(Math.round(ratingsCount[2]));
        PB2Star.setProgress(Math.round(ratingsCount[1]));
        PB1Star.setProgress(Math.round(ratingsCount[0]));
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviewsList.getReviews());
        RVReviews.setLayoutManager(new LinearLayoutManager(this));
        RVReviews.setAdapter(reviewsAdapter);
        CVWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeReviewDialog = new WriteReviewDialog(GreenSpaceReviewsActivity.this, ARL,
                        new WriteReviewDialog.WriteReviewDialogListener() {
                            @Override
                            public void onCancelClicked() {
                                Toast.makeText(GreenSpaceReviewsActivity.this, "Review cancelled.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSubmitClicked() {
                            }
                        },
                        placeId, placeName, UID);
            }
        });
    }
    private void getSpaceName(Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("greenSpaces").document(placeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.contains("name")) {
                            String spaceName = document.getString("name");
                            callback.onDataLoaded(spaceName);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }
    private static int getNoOfRatings(ArrayList<Review> reviews) {
        int totalRatings = 0;
        for (Review review: reviews) {
            if (review.getRating() != -1.0f) {
                totalRatings++;
            }
        }
        return totalRatings;
    }
    public static float calcAverageRating(ArrayList<Review> reviews) {
        float n = getNoOfRatings(reviews);
        if (n == 0) return -1;
        float totalRating = 0;
        for (Review review: reviews) {
            if (review.getRating() != -1.0f) {
                totalRating += review.getRating();
            }
        }
        return totalRating / n;
    }
    private float[] countRatingPercentages() {
        float[] ratingPercentages = new float[5];
        float totalRatings = getNoOfRatings(reviews);
        if (totalRatings == 0) return ratingPercentages;
        for (Review review: reviewsList.getReviews()) {
            if (review.getRating() >= 0 && review.getRating() <= 1){
                ratingPercentages[0]++;
            }
            else if (review.getRating() <= 2){
                ratingPercentages[1]++;
            }
            else if (review.getRating() <= 3){
                ratingPercentages[2]++;
            }
            else if (review.getRating() <= 4){
                ratingPercentages[3]++;
            }
            else if (review.getRating() <= 5){
                ratingPercentages[4]++;
            }
        }
        for (int i = 0; i < ratingPercentages.length; i++) {
            ratingPercentages[i] = (ratingPercentages[i] / totalRatings) * 100;
        }
        return ratingPercentages;
    }
}
