package com.example.ecoventur.ui.transit;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.ecoventur.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DigitalPassportFragment extends DialogFragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID;
    TextView titleTextView;
    ImageView cyclingIcon;
    ImageView closeButton;
    ImageView userImage;
    TextView usernameTextView;
    TextView emailTextView;
    GridLayout badgeSection;
    TextView appreciationMessage;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.TranslucentDialog);

        // Set width to MATCH_PARENT and height to WRAP_CONTENT
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setAttributes(params);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.digital_passport, container, false);

        // Obtain references to UI components
        titleTextView = rootView.findViewById(R.id.titleTextView);
        cyclingIcon = rootView.findViewById(R.id.cyclingIcon);
        closeButton = rootView.findViewById(R.id.closeButton);
        userImage = rootView.findViewById(R.id.userImage);
        usernameTextView = rootView.findViewById(R.id.usernameTextView);
        emailTextView = rootView.findViewById(R.id.emailTextView);
        badgeSection = rootView.findViewById(R.id.badgeSection);
        appreciationMessage = rootView.findViewById(R.id.appreciationMessage);

        // Set title
        titleTextView.setText("Eco-friendly Digital Passport");

        // Set cycling icon (assuming you have a drawable resource named bicycle)
        cyclingIcon.setImageResource(R.drawable.bicycle);

        // Set close button icon (assuming you have a drawable resource named close)
        closeButton.setImageResource(R.drawable.close);

        // Set appreciation message
        appreciationMessage.setText("\"Thanks for your commitment to sustainable commuting.");

        String userID = getArguments().getString("userID", "");

        fetchUserDetails(userID);

        // Set an OnClickListener for the close button
        closeButton.setOnClickListener(view -> {
            // Dismiss the DialogFragment when the close button is clicked
            dismiss();
        });

        return rootView;
    }

    public void fetchUserDetails(String userID) {
        db.collection("users").document(userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Access user details directly from the document
                            String userName = document.getString("username");
                            String userEmail = document.getString("email");
                            String userImageUrl = document.getString("profilepic_url");

                            // Now you can display the user details and badges
                            displayUserDetails(userID,userName, userEmail,userImageUrl);
                        } else {
                            Log.e("FirebaseFetch (Digital Passport)", "User document does not exist");
                            Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("FirebaseFetch (Digital Passport)", "Error getting user document: " + task.getException());
                        Toast.makeText(getActivity(), "Error fetching user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayUserDetails(String userID, String userName, String userEmail, String userImageUrl) {
        // Update the UI with the fetched user details
        usernameTextView.setText(userName);
        emailTextView.setText(userEmail);

        if (userImageUrl != null && !userImageUrl.isEmpty()) {
            Glide.with(this).load(userImageUrl).into(userImage);
        } else {
            // Set the user image to the default image
            userImage.setImageResource(R.drawable.user_big);
        }

        // Assume badgeSection is the LinearLayout for badges
        badgeSection.removeAllViews(); // Clear existing badges (if any)

        // Get the transitAwards subcollection reference
        CollectionReference transitAwardsCollection = db.collection("users").document(userID).collection("transitAwards");

        // Fetch and display badge details for each document in the subcollection
        transitAwardsCollection.get().addOnCompleteListener(subcollectionTask -> {
            if (subcollectionTask.isSuccessful()) {
                for (QueryDocumentSnapshot badgeDocument : subcollectionTask.getResult()) {
                    // Access badge details from the badge document
                    DocumentReference awardsIDRef = badgeDocument.getDocumentReference("awardsID");

                    // Use awardsIDRef to fetch details from transitAwardsList collection
                    fetchBadgeDetails(awardsIDRef);
                }
            } else {
                Log.e("FirebaseFetch (Digital Passport)", "Error getting transitAwards subcollection: " + subcollectionTask.getException());
                Toast.makeText(getActivity(), "Error fetching badge data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchBadgeDetails(DocumentReference awardsIDRef) {
        // Fetch details using awardsIDRef
        if (awardsIDRef != null) {
            awardsIDRef.get().addOnCompleteListener(badgeTask -> {
                if (badgeTask.isSuccessful()) {
                    DocumentSnapshot badgeDocument = badgeTask.getResult();
                    if (badgeDocument.exists()) {
                        // Access badge details from the badge document
                        String badgeName = badgeDocument.getString("name");
                        String imageUrl = badgeDocument.getString("image_Url");

                        // Log badge details
                        Log.d("FirebaseFetch (Digital Passport)", "BadgeName: " + badgeName + ", ImageUrl: " + imageUrl);

                        // Create a view for the badge (inflate badge_item.xml)
                        View badgeView = LayoutInflater.from(getActivity()).inflate(R.layout.badge_item, null);

                        // Access views in the inflated layout
                        ImageView badgeImage = badgeView.findViewById(R.id.badgeImage);
                        TextView badgeNameTextView = badgeView.findViewById(R.id.badgeName);
                        badgeNameTextView.setTextColor(Color.GRAY);

                        // Set badge details
                        badgeNameTextView.setText(badgeName);
                        // Load badgeImage using Glide or any other image loading library
                        Glide.with(this).load(imageUrl).into(badgeImage);

                        // Set layout parameters with margins to add spacing
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                        // Add margins to create spacing between badges
                        layoutParams.setMargins(0, 0, 24, 0); // Adjust the right margin as needed

                        // Apply layout parameters
                        badgeView.setLayoutParams(layoutParams);

                        // Add the badge view to the badgeSection
                        badgeSection.addView(badgeView);
                    }
                } else {
                    Log.e("FirebaseFetch (Digital Passport)", "Error getting badge document: " + badgeTask.getException());
                    Toast.makeText(getActivity(), "Error fetching badge data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
