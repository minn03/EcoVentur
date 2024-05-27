package com.example.ecoventur.ui.user;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ecoventur.R;
import com.example.ecoventur.ui.greenspace.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class EditProfilePictureActivity extends AppCompatActivity {
    private Uri profilePictureUri = null;
    ActivityResultLauncher<Intent> ARL = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                handleProfilePicture(data);
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_picture);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                // Access the profilePicUrl field
                                if (document.contains("profilePicUrl")){
                                    String profilePicUrl = document.getString("profilePicUrl");
                                    if (profilePicUrl != null && !profilePicUrl.isEmpty()){
                                        profilePictureUri = Uri.parse(profilePicUrl);
                                        Glide.with(EditProfilePictureActivity.this).load(profilePictureUri).into((ImageView) findViewById(R.id.picture));
                                    }
                                    else {
                                        Glide.with(EditProfilePictureActivity.this).load(R.drawable.user_big).into((ImageView) findViewById(R.id.picture));
                                    }
                                }
                            }
                        }
                    });
        }

        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                ARL.launch(intent);
            }
        });

        Button btnChoosePhoto = findViewById(R.id.btnChoosePhoto);
        btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                ARL.launch(intent);
            }
        });

        Button btnRemove = findViewById(R.id.btnRemovePhoto);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeProfilePicture();
            }
        });

        Button btnApplyChanges = findViewById(R.id.btnApplyChanges);
        btnApplyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfilePicture();
            }
        });

        ImageView back = findViewById(R.id.backprofile);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void removeProfilePicture() {
        profilePictureUri = null;
        Glide.with(EditProfilePictureActivity.this).load(R.drawable.user_big).into((ImageView) findViewById(R.id.picture));
    }
    private void handleProfilePicture(Intent data) {
        if (data != null) {
            profilePictureUri = data.getData();
            if (profilePictureUri == null) {
                Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                String capturedImageFileName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + "_";
                profilePictureUri = Uri.parse(MediaStore.Images.Media.insertImage(EditProfilePictureActivity.this.getContentResolver(), capturedImage, capturedImageFileName, null));
                if (profilePictureUri == null) {
                    Toast.makeText(EditProfilePictureActivity.this, "Failed to save captured image! Try selecting photo instead.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            Glide.with(EditProfilePictureActivity.this).load(profilePictureUri).into((ImageView) findViewById(R.id.picture));
        }
    }
    private void updateProfilePicture() {
        // Update firebase storage and firestore
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            deletePreviousProfilePicture(userId, db, new Callback() {
                @Override
                public void onDataLoaded(Object object) {
                    if (profilePictureUri != null) {
                        uploadNewProfilePicture(userId, db);
                    }
                }
                @Override
                public void onFailure(Exception e) {

                }
            });
        }
    }
    private void deletePreviousProfilePicture(String userId, FirebaseFirestore db, Callback callback) {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            if (!document.contains("profilePicUrl")) {
                                callback.onDataLoaded(null);
                                return;
                            }
                            String previousProfilePicUrl = document.getString("profilePicUrl");
                            if (previousProfilePicUrl != null && !previousProfilePicUrl.isEmpty()) {
                                StorageReference previousImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(previousProfilePicUrl);
                                previousImageRef.delete().addOnSuccessListener(aVoid -> {
                                    // File deleted successfully
                                    Log.d("ProfilePictureUpdate", "Previous profile picture deleted successfully.");

                                    // Clear the profilePicUrl field in user document
                                    Map<String, Object> updateData = new HashMap<>();
                                    updateData.put("profilePicUrl", FieldValue.delete());

                                    db.collection("users").document(userId)
                                            .update(updateData)
                                            .addOnSuccessListener(aVoid1 -> {
                                                Log.d("ProfilePictureUpdate", "Profile picture URL removed from user document.");
                                                callback.onDataLoaded(null);
                                                Toast.makeText(EditProfilePictureActivity.this, "Profile picture removed successfully.", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(EditProfilePictureActivity.this, "Failed to remove profile picture URL from user document.", Toast.LENGTH_SHORT).show();
                                            });
                                }).addOnFailureListener(exception -> {
                                    // Uh-oh, an error occurred!
                                    Log.d("ProfilePictureUpdate", "Failed to delete previous profile picture: " + exception.getMessage());
                                });
                            }
                        }
                    }
                });
    }
    private void uploadNewProfilePicture(String userId, FirebaseFirestore db) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("profilePictures/" + userId);
        // Upload the file and add an onSuccessListener to get the download URL
        imageRef.putFile(profilePictureUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Store the download URL in Firestore user document
                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("profilePicUrl", downloadUri.toString());

                        db.collection("users").document(userId)
                                .update(updateData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(EditProfilePictureActivity.this, "Profile picture updated successfully.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(EditProfilePictureActivity.this, "Failed to update profile picture URL in user document.", Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfilePictureActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                });
    }
}