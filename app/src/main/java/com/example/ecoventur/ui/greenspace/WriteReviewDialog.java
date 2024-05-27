package com.example.ecoventur.ui.greenspace;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.example.ecoventur.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class WriteReviewDialog {
    private Activity activity;
    private ActivityResultLauncher<Intent> ARL;
    private String placeId;
    private String placeName;
    private String UID;
    private View dialogView;
    private ImageView IVReviewImage;
    private LinearProgressIndicator progressBar;
    private Button btnTakePhoto, btnSelectPhoto, btnRemovePhoto, btnCancel, btnSubmit;
    private RatingBar ratingBar;
    private EditText ETReview;
    private Uri reviewImageUri = null; // assign to IVReviewImage
    private float rating;
    private String review;
    private String reviewImage = null; // save to Firestore
    public interface WriteReviewDialogListener {
        void onCancelClicked();
        void onSubmitClicked();
    }
    private WriteReviewDialogListener listener;
    public WriteReviewDialog (Activity activity, ActivityResultLauncher<Intent> ARL, WriteReviewDialogListener listener, String placeId, String placeName, String UID) {
        this.activity = activity;
        this.ARL = ARL;
        this.listener = listener;
        this.placeId = placeId;
        this.placeName = placeName;
        this.UID = UID;
        show();
    }
    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = LayoutInflater.from(activity);
        dialogView = inflater.inflate(R.layout.dialog_write_review, null);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();

        initializeWidgets();

        btnTakePhoto.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ARL.launch(intent);
        });
        btnSelectPhoto.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            ARL.launch(intent);
        });
        btnRemovePhoto.setOnClickListener(view -> {
            removeReviewImage();
        });

        btnCancel.setOnClickListener(view -> {
            if (listener != null) listener.onCancelClicked();
            alertDialog.dismiss();
        });
        btnSubmit.setOnClickListener(view -> {
            rating = ratingBar.getRating();
            review = ETReview.getText().toString();
            if (reviewImageUri != null) uploadImage(new Callback() {
                @Override
                public void onDataLoaded(Object object) {
                    submitReview();
                    if (listener != null) listener.onSubmitClicked();
                    alertDialog.dismiss();
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(activity, "Failed to upload image!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            else {
                submitReview();
                if (listener != null) listener.onSubmitClicked();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
    private void initializeWidgets() {
        IVReviewImage = dialogView.findViewById(R.id.IVReviewImage);
        progressBar = dialogView.findViewById(R.id.progressBar);
        btnTakePhoto = dialogView.findViewById(R.id.btnTakePhoto);
        btnSelectPhoto = dialogView.findViewById(R.id.btnSelectPhoto);
        btnRemovePhoto = dialogView.findViewById(R.id.btnRemovePhoto);
        ratingBar = dialogView.findViewById(R.id.ratingBar);
        ETReview = dialogView.findViewById(R.id.ETReview);
        btnCancel = dialogView.findViewById(R.id.btnCancel);
        btnSubmit = dialogView.findViewById(R.id.btnSubmit);
    }
    public void handleReviewImage(Intent data) {
        if (data != null) {
            reviewImageUri = data.getData();
            if (reviewImageUri == null) {
                Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                String capturedImageFileName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + "_";
                reviewImageUri = Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), capturedImage, capturedImageFileName, null));
                if (reviewImageUri == null) {
                    Toast.makeText(activity, "Failed to save captured image! Try selecting photo instead.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            Glide.with(dialogView).load(reviewImageUri).into(IVReviewImage);
        }
    }
    private void removeReviewImage() {
        reviewImageUri = null;
        IVReviewImage.setImageResource(R.drawable.upload_placeholder);
    }
    private void uploadImage(Callback callback) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("reviewImages/" + UUID.randomUUID().toString());
        storageReference.putFile(reviewImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                reviewImage = uri.toString();
                                callback.onDataLoaded(reviewImage);
                                Toast.makeText(activity, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                callback.onFailure(e);
                                Toast.makeText(activity, "Failed to get download Url!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                    Toast.makeText(activity, "Image upload failed!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setMax(Math.toIntExact(taskSnapshot.getTotalByteCount()));
                        progressBar.setProgress(Math.toIntExact(taskSnapshot.getBytesTransferred()));
                    }
                });
    }
    private void submitReview() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reviewsRef = db.collection("greenSpaces").document(placeId).collection("reviews");

        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("description", review);
        if (reviewImage != null){
            reviewData.put("imageLink", reviewImage);
        }
        reviewData.put("rating", rating);
        reviewData.put("reviewerUID", UID);
        reviewData.put("time", FieldValue.serverTimestamp());

        reviewsRef.add(reviewData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(activity, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activity, "Review submission failed!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        ecoCoinsManager.addEcoCoins(UID, String.format("Wrote a review on %s", placeName), 10, new Callback() {
            @Override
            public void onDataLoaded(Object object) {
                Toast.makeText(activity, "You have earned 10 ecoCoins! Current balance: " + (int) object, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Exception e) {
                System.out.println("Error adding EcoCoins to user's profile: " + e);
            }
        });
    }
}
