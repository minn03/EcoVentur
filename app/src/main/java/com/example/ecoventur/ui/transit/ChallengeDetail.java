package com.example.ecoventur.ui.transit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ecoventur.R;
import com.example.ecoventur.ui.transit.model.AllChallenges;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChallengeDetail extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_challenge_detail, container, false);

        // Retrieve challenge details from arguments
        AllChallenges challenge = getArguments().getParcelable("challenge");

        // Log the value of challenge for debugging purposes
        Log.d("ChallengeDetail", "Challenge: " + challenge);

        // Use the challenge details to populate the UI
        populateChallengeDetails(rootView, challenge);

        return rootView;
    }

    private void populateChallengeDetails(View rootView,AllChallenges challenge) {
        // Find views by their IDs
        RoundedImageView challengeImage = rootView.findViewById(R.id.challengeImage);
        TextView challengeTitle = rootView.findViewById(R.id.challengeTitle);
        TextView ecocoinsTextView = rootView.findViewById(R.id.ecocoins);
        ImageView ecocoinsImage = rootView.findViewById(R.id.ecocoinsImage);
        TextView dateTextView = rootView.findViewById(R.id.date);
        TextView descriptionTextView = rootView.findViewById(R.id.description);
        TextView rulesTextView = rootView.findViewById(R.id.rules);
        TextView rule1TextView = rootView.findViewById(R.id.rule1);
        TextView rule2TextView = rootView.findViewById(R.id.rule2);
        TextView rule3TextView = rootView.findViewById(R.id.rule3);
        Button joinButton = rootView.findViewById(R.id.joinButton);

        // Populate challenge details
        challengeTitle.setText(challenge.getTitle());
        ecocoinsTextView.setText(String.valueOf(challenge.getTags().get(2)));
        dateTextView.setText(formatDate(challenge.getStartDate()) + " - " + formatDate(challenge.getEndDate()));
        descriptionTextView.setText(challenge.getDescription());
        rulesTextView.setText("Rules:");
        rule1TextView.setText("1. " + challenge.getRules().get(0));
        rule2TextView.setText("2. " + challenge.getRules().get(1));
        rule3TextView.setText("3. " + challenge.getRules().get(2));

        // Load the ecocoins image from the drawable resource
        ecocoinsImage.setImageResource(R.drawable.ecocoin);

        // You can use Glide or any other library to load the challenge image
        Glide.with(this).load(challenge.getImageUrl()).into(challengeImage);

        // Set click listener for the join button
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event, e.g., navigate to join challenge activity
                // You can start a new activity or perform any other action as needed
                String id = challenge.getId();
                joinChallenge(id);

            }
        });
    }

    private void joinChallenge(String challengeId) {
        // Check if challengeId is null
        if (challengeId == null) {
            // Log an error or handle the case where challengeId is null
            Log.e("joinChallenge", "challengeId is null");
            return; // Exit the method early if challengeId is null
        }

        // Assuming you have a Firebase Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assuming userID is the ID of the current user, replace it with your logic to get the user ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();


        // Create a reference to the challenging document for the current challenge and user
        DocumentReference challengingRef = db.collection("users").document(userID)
                .collection("challenging").document();

        // Get a reference to the challenged challenge document
        DocumentReference challengedChallengeRef = db.collection("challenges").document(challengeId);

        // Set the data to Firestore
        Map<String, Object> challengingData = new HashMap<>();
        challengingData.put("challengingID", challengedChallengeRef); // Store the reference instead of the ID

        challengingRef.set(challengingData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Successfully joined the challenge
                        // You can show a success message or navigate to another screen if needed

                        // Show a success message (You can use Toast or Snackbar)
                        Toast.makeText(getActivity(), "Joined the challenge successfully", Toast.LENGTH_SHORT).show();

                        // Navigate back to the home page
                        NavController navController = Navigation.findNavController(getView());
                        navController.popBackStack(R.id.navigation_transit, false); // Replace with your home destination ID
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to join the challenge
                        // You can show an error message or handle the failure as needed
                        // Show an error message
                        Toast.makeText(getActivity(), "Failed to join the challenge", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Helper method to format date
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}