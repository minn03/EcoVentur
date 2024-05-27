package com.example.ecoventur.ui.transit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecoventur.R;
import com.example.ecoventur.ui.transit.model.Submitted;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class SubmissionFormFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    private Spinner challengeSpinner;
    private FrameLayout imageUploadLayout;
    private View boxView;
    private ImageView cameraIcon;
    private GridLayout imagePreviewLayout;
    private TextInputEditText descriptionEditText;
    private Button submitButton;
    private List<String> challengingTitleList;
    List<String> challengeIDList;
    List <String> ecocoinsList;

    List<Bitmap> imageBitmapList;
    Submitted submitted;
    List<Uri> imageUrlList;
    List<String> imageUrls;



    private FirebaseFirestore db;

    private FirebaseStorage firebaseStorage;

    private String userID;
    ProgressDialog progressDialog;

    ProgressDialog progressDialog1;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_submission_form, container, false);

        //Firebase instance
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        // Initialize UI components
        challengeSpinner = rootView.findViewById(R.id.challengeSpinner);
        imageUploadLayout = rootView.findViewById(R.id.imageUploadLayout);
        boxView = rootView.findViewById(R.id.boxView);
        cameraIcon = rootView.findViewById(R.id.cameraIcon);
        imagePreviewLayout = rootView.findViewById(R.id.imagePreviewLayout);
        descriptionEditText = rootView.findViewById(R.id.descriptionEditText);
        submitButton = rootView.findViewById(R.id.submitButton);

        //Initialize Progress Dialog
        progressDialog1 = new ProgressDialog(requireContext());
        progressDialog1.setMessage("Loading...");
        progressDialog1.setCancelable(false);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Submitting...");
        progressDialog.setCancelable(false);

        //Initialize submitted object
        submitted = new Submitted();


        // Set up camera icon click listener
        cameraIcon.setOnClickListener(v -> showImageSourceDialog());


        // Set up submit button click listener
        submitButton.setOnClickListener(v -> handleSubmitButtonClick());

        // Set the initial hint
        descriptionEditText.setHint("Please provide all the relevant details.");

        // Add OnFocusChangeListener to dynamically change the hint
        descriptionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Update hint based on whether the TextInputEditText has focus
                if (hasFocus) {
                    descriptionEditText.setHint("");
                } else {
                    descriptionEditText.setHint("Please provide all the relevant details.");
                }
            }
        });

        challengingTitleList = new ArrayList<>();
        challengeIDList = new ArrayList<>();
        ecocoinsList = new ArrayList<>();
        imageBitmapList = new ArrayList<>();
        imageUrlList = new ArrayList<>();
        imageUrls = new ArrayList<>();

        fetchAllChallenging(userID);

        challengeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected title
                String selectedTitle = (String) parentView.getItemAtPosition(position);
                String challengingID = challengeIDList.get(position);
                String ecocoins = ecocoinsList.get(position);

                // Log selected title
                Log.d("SpinnerItemSelected", "Selected Title: " + selectedTitle);

                // Log Ecocoins
                Log.d("SpinnerItemSelected", "Ecocoins: " + ecocoins);

                // Log Ecocoins
                Log.d("SpinnerItemSelected", "Challenging ID: " + challengingID);

                // Set the selected title as the text in the Spinner itself
                ((TextView) parentView.getSelectedView()).setText(selectedTitle);
                Log.d("SpinnerItemSelected", "Text set in Spinner");

                submitted.setChallengeTitle(selectedTitle);
                submitted.setChallengingID(challengingID);
                submitted.setEcocoins(ecocoins);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing if nothing is selected
            }
        });

        return rootView;
    }

    // Method to load challenging challenges into the Spinner
    private void fetchAllChallenging(String userID) {
        progressDialog1.show();

        Log.d("Submission Form Fragment", "Fetching challenging title from Firestore for user: " + userID);

        submitted.setUserID(userID);

        db.collection("users").document(userID).collection("challenging")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        try {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            int recordCount = documents.size(); // Get the number of records
                            Log.d("Submission Form Fragment", "Number of records in 'challenging' collection: " + recordCount);

                            // Initialize a counter for completed fetch tasks
                            AtomicInteger completedTasks = new AtomicInteger(0);

                            for (DocumentSnapshot document : documents) {
                                String challengingID = document.getId();
                                challengeIDList.add(challengingID);
                                Log.d("Submission Form Fragment",challengingID + "is fetched");

                                DocumentReference challengingIDRef = document.getDocumentReference("challengingID");
                                fetchChallengingTitle(challengingIDRef, completedTasks, recordCount);
                            }

                        } catch (Exception e) {
                            Log.e("Submission Form Fragment", "Exception while processing documents: " + e.getMessage());
                        }
                    } else {
                        Log.e("Submission Form Fragment", "Error getting documents: ", task.getException());
                        Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to add the challenging title
    private void fetchChallengingTitle(DocumentReference challengingIDRef, AtomicInteger completedTasks, int totalTasks) {
        Log.d("Submission Form Fragment", "Fetching specific challenging item from Firestore with ID: " + challengingIDRef);

        challengingIDRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    // Get the title of the challenging
                    String title = document.getString("title");
                    Log.d("Submission Form Fragment", title);

                    if (!title.equals(null) && !title.isEmpty()) {
                        challengingTitleList.add(title);

                    } else {
                        Log.d("Submission Form Fragment", "No such challenging document");
                    }

                    //Fetch the ecocoins of the challenge
                    List<String> tags = (List<String>) document.get("tags");

                    if (tags != null && tags.size() > 2) {
                        String ecocoins = tags.get(2);
                        ecocoinsList.add(ecocoins);
                        // Your logic here
                        Log.d("Submission Form Fragment", ecocoins);
                    } else {
                        Log.d("Submission Form Fragment", "Invalid or missing 'tags' array in the document");
                    }
                }
            } else {
                Log.e("Submission Form Fragment", "Error getting challenging document: ", task.getException());
                Toast.makeText(getActivity(), "Error fetching challenging data", Toast.LENGTH_SHORT).show();
            }

            // Check if all tasks are completed
            if (completedTasks.incrementAndGet() == totalTasks) {
                progressDialog1.dismiss();

                // If all tasks are completed, populate the spinner
                populateSpinner(challengingTitleList);
                Log.d("SpinnerItemSelected", "The spinner is set up successfully");
            }
        });
    }


    // Method to populate the Spinner with challenging titles
    private void populateSpinner(List<String> challengingTitles) {

        Log.d("Spinner Debug", "Challenging Titles (Passed): " + challengingTitles.size());

        // Convert List<String> to String array
        String[] challengingTitleArray = challengingTitles.toArray(new String[0]);

        // Log the challenging titles for debugging
        Log.d("SpinnerDebug", "Challenging Titles: " + Arrays.toString(challengingTitleArray));

        // Create an ArrayAdapter for the Spinner with String items
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_list,
                challengingTitleArray
        );

        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(R.layout.spinner_list);

        // Set the adapter to the Spinner
        challengeSpinner.setAdapter(adapter);
    }

    // Method to show a dialog for choosing image source (camera or gallery)
    private void showImageSourceDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose Image Source")
                .setItems(new CharSequence[]{"Camera", "Gallery"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            dispatchTakePictureIntent(); // Choose Camera
                            break;
                        case 1:
                            dispatchChooseFromGalleryIntent(); // Choose Gallery
                            break;
                    }
                });
        builder.show();
    }

    // Method to handle the camera intent
    private void dispatchTakePictureIntent() {
        Log.d("Submission Form Fragment", "dispatchTakePictureIntent: Triggered");

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) == null) {
            Log.e("Submission Form Fragment", "dispatchTakePictureIntent: No camera app found");
            return;
        }

        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    // Method to handle the gallery intent
    private void dispatchChooseFromGalleryIntent() {
        Log.d("Submission Form Fragment", "dispatchChooseFromGalleryIntent: Triggered");

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }


    // Method to handle the result from the camera or gallery intent
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Submission Form Fragment", "onActivityResult: Triggered");
        Log.d("Submission Form Fragment", "requestCode: " + requestCode + ", resultCode: " + resultCode);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Log.d("Submission Form Fragment", "Image captured from camera");
                // Process result for image captured from the camera
                // Check if data extras contain the full-size image Uri
                if (data != null && data.getData() != null) {
                    Uri fullSizeImageUri = data.getData();
                    imageUrlList.add(fullSizeImageUri);

                    // Optionally, you can use the Uri for further processing
                    Log.d("Submission Form Fragment", "Full-size image URI: " + fullSizeImageUri.toString());

                    Bitmap fullSizeBitmap = (Bitmap) data.getExtras().get("data");
                    Uri fullSizeBitmapUri = getImageUri(requireContext(), fullSizeBitmap, "full_size_image");
                    imageUrlList.add(fullSizeBitmapUri);

                    imageBitmapList.add(fullSizeBitmap);
                    displayImages();
                } else if (data != null && data.hasExtra("data")) {
                    // Image captured from camera as a thumbnail
                    Bitmap thumbnailBitmap = (Bitmap) data.getExtras().get("data");

                    // Convert the thumbnail to Uri
                    Uri thumbnailUri = getImageUri(requireContext(), thumbnailBitmap, "thumbnail_image");
                    imageUrlList.add(thumbnailUri);

                    // Optionally, you can use the Uri for further processing
                    Log.d("Submission Form Fragment", "Thumbnail image URI: " + thumbnailUri.toString());

                    imageBitmapList.add(thumbnailBitmap);
                    displayImages();
                }
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                Log.d("Submission Form Fragment", "Image selected from gallery");
                if (data != null) {
                    if (data.getClipData() != null) {
                        // Multiple images selected from the gallery
                        int count = data.getClipData().getItemCount();
                        Log.d("Submission Form Fragment", "Number of selected images: " + count);
                        for (int i = 0; i < count; i++) {
                            Uri selectedImageUri = data.getClipData().getItemAt(i).getUri();
                            imageUrlList.add(selectedImageUri);
                            Log.d("Submission Form Fragment", "Selected image URI " + (i + 1) + ": " + selectedImageUri.toString());
                            try {
                                Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                                imageBitmapList.add(selectedBitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e("Submission Form Fragment", "Error loading image from gallery: " + e.getMessage());
                            }
                        }
                        // Display the selected images
                        displayImages();
                    } else if (data.getData() != null) {
                        // Single image selected from the gallery
                        Uri selectedImageUri = data.getData();
                        imageUrlList.add(selectedImageUri);
                        Log.d("Submission Form Fragment", "Selected image URI: " + selectedImageUri.toString());
                        try {
                            Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                            imageBitmapList.add(selectedBitmap);
                            // Display the selected image
                            displayImages();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("Submission Form Fragment", "Error loading image from gallery: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    // Helper method to convert Bitmap to Uri
    private Uri getImageUri(Context context, Bitmap bitmap, String title) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String path = MediaStore.Images.Media.insertImage(
                context.getContentResolver(),
                bitmap,
                title + "_" + timeStamp,
                null
        );
        return Uri.parse(path);
    }

    //Method to display the images
    private void displayImages() {
        Log.d("Submission Form Fragment", "displayImages: Triggered");

        // Clear existing images
        imagePreviewLayout.removeAllViews();

        Log.d("Submission Form Fragment", "Size of imageBitmapList before adding images: " + imageBitmapList.size());

        // Display the captured or selected images using GridLayout
        int columnCount = 5; // You can adjust the number of columns as per your preference
        for (int i = 0; i < imageBitmapList.size(); i++) {
            Bitmap bitmap = imageBitmapList.get(i);

            // Create a FrameLayout to hold the image and close icon
            FrameLayout imageContainer = new FrameLayout(requireContext());

            // Create an ImageView for the image
            ImageView imageView = new ImageView(requireContext());
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // Calculate width and height based on the screen width and number of columns
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int imageWidth = screenWidth / columnCount;
            int imageHeight = imageWidth;

            FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(imageWidth, imageHeight);
            imageView.setLayoutParams(imageParams);

            // Create a close icon as an ImageButton
            ImageButton closeIcon = new ImageButton(requireContext());
            closeIcon.setImageResource(R.drawable.cross); // You need to have an 'ic_close' drawable in your resources
            closeIcon.setBackgroundColor(Color.WHITE);

            // Set a fixed size for the close icon (e.g., 24dp x 24dp)
            int closeIconSize = 14; // Adjust the size based on your preference
            FrameLayout.LayoutParams closeIconParams = new FrameLayout.LayoutParams(
                    convertDpToPx(requireContext(), closeIconSize),
                    convertDpToPx(requireContext(), closeIconSize)
            );

            // Set the close icon to be positioned at the top right corner of the image
            closeIconParams.gravity = Gravity.TOP | Gravity.END;
            closeIcon.setLayoutParams(closeIconParams);

            // Create a final copy of 'i' for use in lambda expression
            final int imageIndex = i;

            // Set a click listener to remove the image on click
            closeIcon.setOnClickListener(v -> removeImage(imageIndex));

            // Add the image and close icon to the container
            imageContainer.addView(imageView);
            imageContainer.addView(closeIcon);

            imagePreviewLayout.addView(imageContainer);
        }

        Log.d("Submission Form Fragment", "Size of imageBitmapList after adding images: " + imageBitmapList.size());
    }

    private int convertDpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // Add a method to remove an image from the list and update the UI
    private void removeImage(int position) {
        if (position >= 0 && position < imageBitmapList.size()) {
            imageBitmapList.remove(position);
            imageUrlList.remove(position);
            displayImages(); // Update the UI after removing the image
        }
    }

    //Submission Logic
    private void submitChallenge() {

        // Check if imageUrlList is not null and not empty
        if (imageUrlList != null && !imageUrlList.isEmpty()) {
            Log.d("Submission Form Fragment", "The size of the imageUrlList is " + imageUrlList.size());

            // Create a counter to keep track of completed uploads
            AtomicInteger completedUploads = new AtomicInteger(0);

            for (Uri url : imageUrlList) {
                // Upload the image to Firebase Storage
                uploadImageToStorage(url, completedUploads);
            }
        } else {
            // Log or handle the case when imageBitmapList is null or empty
            Log.e("Submission Form Fragment", "imageBitmapList is null or empty");
        }
    }

    //Method to upload the proof to Storage & Get and Set the download url of the image
    private void uploadImageToStorage(Uri url, AtomicInteger completedUploads) {
        Log.d("Submission Form Fragment", "Trying to upload the image to Storage");

        // Specify the folder ("submitted") where you want to store the images
        String folderName = "submitted";

        // Generate a unique identifier for each image
        String imageName = UUID.randomUUID().toString() + ".jpg";

        // Create a reference to the Firebase Storage location
        StorageReference storageRef = firebaseStorage.getReference().child(folderName).child(imageName);

        // Upload the image with onProgressListener and onPausedListener
        UploadTask uploadTask = storageRef.putFile(url);

        // Add onProgressListener to track the upload progress
        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            Log.d("Submission Form Fragment", "Upload is " + progress + "% done");
        });

        // Add onPausedListener to track if the upload is paused
        uploadTask.addOnPausedListener(taskSnapshot -> {
            Log.d("Submission Form Fragment", "Upload is paused");
        });

        // Continue with the other listeners
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully, get the download URL
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Handle the download URL, for example, add it to your list of image URLs
                String downloadUrl = uri.toString();
                Log.d("Submission Form Fragment", "Download URL: " + downloadUrl);
                imageUrls.add(downloadUrl);
                Log.d("Submission Form Fragment", "The size of imageUrls now is " + imageUrls.size());

                // Check if all uploads are completed
                if (completedUploads.incrementAndGet() == imageUrlList.size()) {
                    // Get other form data
                    String description = descriptionEditText.getText().toString().trim();
                    String userID = submitted.getUserID();
                    String challengingID = submitted.getChallengingID(); // Get the challenging ID
                    String challengingTitle = submitted.getChallengeTitle();
                    String ecocoins = submitted.getEcocoins(); // Get the ecocoins

                    Submitted submitted1 = new Submitted(userID, challengingID, challengingTitle, imageUrls, description, ecocoins);
                    Log.d("Submission Form Fragment", String.valueOf(submitted1));

                    addSubmissionToFirestore(submitted1);
                }

            }).addOnFailureListener(e -> {
                // Handle the failure to get the download URL
                Log.e("Submission Form Fragment", "Error getting download URL: " + e.getMessage());
                e.printStackTrace();
            });
        }).addOnFailureListener(e -> {
            // Handle the failure
            Log.e("Submission Form Fragment", "Error uploading image: " + e.getMessage());
            e.printStackTrace(); // Print the exception stack trace for debugging
        }).addOnCompleteListener(task -> {
            Log.d("Submission Form Fragment", "onComplete callback executed");
            if (task.isSuccessful()) {
                Log.d("Submission Form Fragment", "Upload successful");

            } else {
                Log.e("Submission Form Fragment", "Upload failed");
                task.getException().printStackTrace();
            }
        });
    }

    // Method to handle the submit button click
    private void handleSubmitButtonClick() {
        // Validate form data
        if (validateForm()) {
            //Show Progress Dialog
            progressDialog.show();

            // Submission Logic
            submitChallenge();
        }
    }

    // Method to validate form data
    private boolean validateForm() {
        // You can add additional validation logic here
        // For simplicity, let's assume the description is required

        String description = descriptionEditText.getText().toString().trim();

        if (description.isEmpty()) {
            // Show an error message
            descriptionEditText.setError("Description is required");
            return false;
        }

        return true;
    }

    //Method to add a document into 'submissions' collection in Firestore
    private void addSubmissionToFirestore(Submitted submission) {
        // Get the Firestore reference for submissions collection
        CollectionReference submissionsRef = db.collection("submissions");

        // Add the submission to Firestore
        submissionsRef.add(submission)
                .addOnSuccessListener(documentReference -> {
                    //Dismiss the Progress Dialog if the document is added
                    progressDialog.dismiss();

                    // Document added successfully
                    String submissionId = documentReference.getId();
                    Log.d("Submission Form Fragment", "Submission added with ID: " + submissionId);

                    // For now, show a toast as a placeholder
                    Toast.makeText(requireContext(), "Submission Successful!", Toast.LENGTH_SHORT).show();

                    navigateBackToSubmitChallengeDetail();
                })
                .addOnFailureListener(e -> {
                    // Handle the failure
                    Log.e("Submission Form Fragment", "Error adding submission: " + e.getMessage());

                    // Show an error message
                    Toast.makeText(requireContext(), "Submission failed. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateBackToSubmitChallengeDetail() {
        // Use FragmentManager to pop the current fragment from the back stack
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}