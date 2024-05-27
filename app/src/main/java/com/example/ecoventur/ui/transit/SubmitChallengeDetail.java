package com.example.ecoventur.ui.transit;

import android.os.Bundle;

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

import com.bumptech.glide.Glide;
import com.example.ecoventur.R;
import com.example.ecoventur.ui.transit.model.AllChallenges;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SubmitChallengeDetail extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_submit_challenge_detail, container, false);


        Bundle args = getArguments();
        if (args != null) {
            String challengeId = args.getString("challengeId");
            // Rest of your code
            fetchChallengeDetails(challengeId, rootView);
        } else {
            // Handle the case where arguments are null
            Log.e("SubmitChallengeDetail", "Arguments are null");
        }

        return rootView;
    }

    private void fetchChallengeDetails(String challengeId, View rootView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assuming you have a "challenges" collection in Firestore
        DocumentReference challengeRef = db.collection("challenges").document(challengeId);

        challengeRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // DocumentSnapshot data extraction
                    AllChallenges challenge = document.toObject(AllChallenges.class);
                    challenge.setId(document.getId());

                    // Now you have the complete challenge details
                    // Populate the UI with the fetched details
                    populateChallengeDetails(rootView, challenge);
                } else {
                    Log.d("FetchChallengeDetails", "No such document");
                }
            } else {
                Log.e("FetchChallengeDetails", "Error getting document: ", task.getException());
                // Handle the error or show a message to the user
            }
        });
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
        Button submitButton = rootView.findViewById(R.id.submitButton);

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

        // Set click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Assuming you have a NavController instance in your Fragment/Activity
                NavController navController = Navigation.findNavController(requireView());

                // Navigate to the SubmissionActivity using the action defined in your navigation graph
                navController.navigate(R.id.action_submitChallengingFragment_to_submissionForm);
            }
        });
    }

    // Helper method to format date
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}