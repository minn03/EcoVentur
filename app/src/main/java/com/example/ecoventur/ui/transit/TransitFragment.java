package com.example.ecoventur.ui.transit;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoventur.R;
import com.example.ecoventur.databinding.FragmentTransitBinding;
import com.example.ecoventur.ui.transit.adapters.AllChallengesAdapter;
import com.example.ecoventur.ui.transit.adapters.ChallengingAdapter;
import com.example.ecoventur.ui.transit.adapters.CompletedAdapter;
import com.example.ecoventur.ui.transit.adapters.OnChallengeItemClickListener;
import com.example.ecoventur.ui.transit.model.AllChallenges;
import com.example.ecoventur.ui.transit.model.Challenging;
import com.example.ecoventur.ui.transit.model.Completed;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TransitFragment extends Fragment {

    FirebaseFirestore db;
    String userID;

    ProgressDialog progressDialog;

    //All Challenges
    static List<AllChallenges> allChallengesList;
    private RecyclerView allChallengesRecyclerView;
    private AllChallengesAdapter allChallengesAdapter;

    //All Challenging
    List <Challenging> allChallengingList;
    private RecyclerView allChallengingRecyclerView;
    private ChallengingAdapter allChallengingAdapter;

    //Completed
    static List<Completed> allCompletedList;
    RecyclerView allCompletedRecyclerView;
    CompletedAdapter allCompletedAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_transit, container, false);

        // Find the app bar (assuming it's present in your activity)
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();


        if (actionBar != null) {
            // Set custom layout for the action bar
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.toolbar_custom);

            // Find the ImageView in your custom layout
            ImageView passportIcon = actionBar.getCustomView().findViewById(R.id.passport_icon);

            // Set click listener or perform any other actions as needed
            passportIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle icon click
                    showPassportDialog(userID);
                }
            });
        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        db = FirebaseFirestore.getInstance();


        //Initialize RecyclerView and Set Layout Manager for AllChallenges
        //Initialize adpater and set it to RecyclerView
        allChallengesRecyclerView = root.findViewById(R.id.all_challenges);
        allChallengesList = new ArrayList<>();
        allChallengesAdapter = new AllChallengesAdapter(getContext(),allChallengesList);

        // Set the click listener for the ChallengeAdapter
        allChallengesAdapter.setOnChallengeItemClickListener(new OnChallengeItemClickListener() {
            @Override
            public void onChallengeItemClick(int position) {
                // Handle item click, e.g., navigate to ChallengeDetailScreen
                // You can access the clicked challenge using position and challengeAdapter.getItem(position)
            }
        });
        allChallengesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allChallengesRecyclerView.setAdapter(allChallengesAdapter);

        //Initialize RecyclerView and Set Layout Manager for All Challenging
        //Initialize adpater and set it to RecyclerView
        allChallengingRecyclerView = root.findViewById(R.id.all_challenging);
        allChallengingList = new ArrayList<>();
        allChallengingAdapter = new ChallengingAdapter(getContext(), allChallengingList);
        // Set the click listener for the ChallengingAdapter
        allChallengingAdapter.setOnChallengeItemClickListener(new OnChallengeItemClickListener() {
            @Override
            public void onChallengeItemClick(int position) {
                // Handle item click, e.g., navigate to ChallengeDetailScreen
                // You can access the clicked challenge using position and challengeAdapter.getItem(position)
            }
        });
        allChallengingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allChallengingRecyclerView.setAdapter(allChallengingAdapter);
        allChallengingAdapter.setHorizontalLayoutManager(allChallengingRecyclerView);

        allCompletedRecyclerView = root.findViewById(R.id.all_completed_challenges);
        allCompletedList = new ArrayList<>();
        allCompletedAdapter = new CompletedAdapter(getContext(),allCompletedList);
        allCompletedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allCompletedRecyclerView.setAdapter(allCompletedAdapter);

        //Fetch data from firestore
        fetchAllChallenging(userID);
        fetchAllCompleted(userID);


        // Inside your onCreateView method in TransitFragment.java
        TextView viewAllChallengesTextView = root.findViewById(R.id.view_all_challenges);

        viewAllChallengesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Transit Fragment", "View All Challenges is clicked");
                // Handle the "View all challenges" click event
                // For example, replace the current fragment with a new fragment
                // Use the NavController to navigate to the fragment
                Navigation.findNavController(v).navigate(R.id.action_TransitFragment_to_ViewAllChallenge);
            }
        });

        // Inside your onCreateView method in TransitFragment.java
        TextView viewAllCompletedTextView = root.findViewById(R.id.view_all_completed);

        viewAllCompletedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Transit Fragment", "View All Challenges is clicked");
                // Handle the "View all challenges" click event
                // You can perform a fragment transaction here to show all challenges
                // For example, replace the current fragment with a new fragment
                // Use the NavController to navigate to the fragment
                Navigation.findNavController(v).navigate(R.id.action_TransitFragment_to_ViewAllCompleted);
            }
        });

        return root;
    }

    private void fetchAllChallenges() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("FirebaseFetch (All Challenges)", "Fetching all challenges from Firestore...");

        db.collection("challenges")
                .orderBy("startDate")
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        int totalRecord = task.getResult().size();;  // Get the number of records
                        Log.d("FirebaseFetch (All Challenges)", "Number of records in 'challenges' collection: " + totalRecord);

                        int recordCount = 0;

                        try {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                // Check if the challenge has not expired
                                Date currentDate = new Date();
                                Date startDate = document.getDate("startDate");

                                if (startDate != null && !startDate.before(currentDate)){
                                    if (!isChallengeInChallengingList(document.getId())){

                                        Log.d("FirebaseFetch (All Challenges)", "Document ID (All Challenges): " + document.getId());

                                        AllChallenges allChallenges = document.toObject(AllChallenges.class);
                                        allChallenges.setId(document.getId());
                                        allChallengesList.add(allChallenges);
                                        recordCount++;
                                    }

                                }
                            }

                            allChallengesAdapter.notifyDataSetChanged();

                            // Log the message with the record count
                            String message = recordCount + " records in challenges collection fetched from Firebase";
                            Log.d("FirebaseFetch (All Challenges)", message);


                        } catch (Exception e) {
                            Log.e("FirebaseFetch (All Challenges)", "Exception while processing documents: " + e.getMessage());
                        }

                    } else {
                        Log.e("FirebaseFetch (All Challenges)", "Error getting documents: ", task.getException());
                        Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchAllChallenging(String userID) {

        //Show the Progress Dialog when start fetching the challenges from Firebase
        progressDialog.show();

        Log.d("FirebaseFetch (Challenging)", "Fetching challenging items from Firestore for user: " + userID);

        db.collection("users").document(userID).collection("challenging")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        try {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            int recordCount = documents.size(); // Get the number of records
                            Log.d("FirebaseFetch (Challenging)", "Number of records in 'challenging' collection: " + recordCount);

                            for (DocumentSnapshot document : documents) {
                                DocumentReference challengingIDRef = document.getDocumentReference("challengingID");

                                if (challengingIDRef != null) {
                                    fetchSpecificChallengingChallenge(challengingIDRef);
                                }
                                else {
                                    Log.e("FirebaseFetch (Challenging)", "challengingIDRef is null for document ID: " + document.getId());
                                }
                            }

                            // Log the message with the record count
                            String message = recordCount + " records fetched from Firebase";
                            Log.d("FirebaseFetch (Challenging)", message);

                            fetchAllChallenges();
                        } catch (Exception e) {
                            Log.e("FirebaseFetch (Challenging)", "Exception while processing documents: " + e.getMessage());
                        }
                    } else {
                        Log.e("FirebaseFetch (Challenging)", "Error getting documents: ", task.getException());
                        Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void fetchSpecificChallengingChallenge(DocumentReference challengingIDRef) {
        Log.d("FirebaseFetch (Challenging)", "Fetching specific challenging item from Firestore with ID: " + challengingIDRef);

        challengingIDRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // DocumentSnapshot data extraction
                    String imageUrl = document.getString("imageUrl");
                    String title = document.getString("title");
                    Date endDate = document.getDate("endDate");
                    String challengingID = document.getId();

                    // Check if the challenge is still valid based on endDate
                    Date currentDate = new Date();
                    if (endDate != null && currentDate.before(endDate)) {
                        // Create a Challenging object with the retrieved data
                        Challenging challenging = new Challenging(imageUrl, title, endDate, challengingID);

                        // Add the Challenging object to your list or adapter
                        allChallengingList.add(challenging);
                        allChallengingAdapter.notifyDataSetChanged();

                        Log.d("FirebaseFetch (Challenging)", "Challenging data fetched: " + document.getData());

                        // Log the contents of allChallengingList
                        Log.d("FirebaseFetch (Challenging)", "allChallengingList: " + allChallengingList.toString());

                        // Sort the list based on end date
                        Collections.sort(allChallengingList, (c1, c2) -> {
                            Date endDate1 = c1.getEndDate();
                            Date endDate2 = c2.getEndDate();

                            Log.d("FirebaseFetch (Challenging)", "EndDate1: " + endDate1 + ", EndDate2: " + endDate2);

                            // Adjust the comparison logic as needed
                            return endDate1.compareTo(endDate2);
                        });

                        Log.d("FirebaseFetch (Challenging)", "Challenging list sorted");

                        // Log the contents of allChallengingList
                        Log.d("FirebaseFetch (Challenging)", "allChallengingList (After Sorting): " + allChallengingList.toString());

                        // Notify adapter after sorting
                        allChallengingAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d("FirebaseFetch (Challenging)", "No such challenging document");
                }
            } else {
                Log.e("FirebaseFetch (Challenging)", "Error getting challenging document: ", task.getException());
                Toast.makeText(getActivity(), "Error fetching challenging data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAllCompleted(String userID) {
        Log.d("FirebaseFetch (Completed)", "Fetching completed items from Firestore for user: " + userID);

        db.collection("users").document(userID).collection("completed")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss(); // Dismiss progress dialog
                        try {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            int recordCount = documents.size(); // Get the number of records
                            Log.d("FirebaseFetch (Completed)", "Number of records in 'completed' collection: " + recordCount);

                            for (DocumentSnapshot document : documents) {
                                DocumentReference challengingIDRef = document.getDocumentReference("completedID");
                                fetchSpecificCompletedChallenge(challengingIDRef);
                            }

                            // Log the message with the record count
                            String message = recordCount + " records fetched from Firebase";
                            Log.d("FirebaseFetch (Completed)", message);
                        } catch (Exception e) {
                            Log.e("FirebaseFetch (Completed)", "Exception while processing documents: " + e.getMessage());
                        }
                    } else {
                        Log.e("FirebaseFetch (Completed)", "Error getting documents: ", task.getException());
                        Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchSpecificCompletedChallenge(DocumentReference challengingIDRef) {
        Log.d("FirebaseFetch (Completed)", "Fetching specific completed item from Firestore with ID: " + challengingIDRef);

        challengingIDRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // DocumentSnapshot data extraction
                    String imageUrl = document.getString("imageUrl");
                    String title = document.getString("title");
                    Date endDate = document.getDate("endDate");
                    Date startDate = document.getDate("startDate");
                    List<String> tags = (List<String>) document.get("tags");

                    // Create a Completed object with the retrieved data
                    Completed completed = new Completed(imageUrl, tags, title, startDate, endDate);

                    // Add the Completed object to your list or adapter
                    allCompletedList.add(completed);
                    allCompletedAdapter.notifyDataSetChanged();

                    Log.d("FirebaseFetch (Completed)", "Completed data fetched: " + document.getData());

                    // Log the contents of allChallengingList
                    Log.d("FirebaseFetch (Challenging)", "allChallengingList: " + allCompletedList.toString());

                    // Sort the list based on end date
                    Collections.sort(allCompletedList, (c1, c2) -> {
                        Date startDate1 = c1.getStartDate();
                        Date startDate2 = c2.getStartDate();

                        Log.d("FirebaseFetch (Challenging)", "EndDate1: " + startDate1 + ", EndDate2: " + startDate2);

                        // Adjust the comparison logic as needed
                        return startDate1.compareTo(startDate2);
                    });

                    Log.d("FirebaseFetch (Challenging)", "Challenging list sorted");

                    // Log the contents of allChallengingList
                    Log.d("FirebaseFetch (Challenging)", "allChallengingList (After Sorting): " + allCompletedList.toString());

                    // Notify adapter after sorting
                    allCompletedAdapter.notifyDataSetChanged();

                } else {
                    Log.d("FirebaseFetch (Completed)", "No such completed document");
                }
            } else {
                Log.e("FirebaseFetch (Completed)", "Error getting completed document: ", task.getException());
                Toast.makeText(getActivity(), "Error fetching completed data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //method to check whether the challenges is currently challenging by the user or not
    private boolean isChallengeInChallengingList(String challengeId) {
        for (Challenging challenging : allChallengingList) {
            String challengingId = challenging.getChallengingID();
            Log.d("ChallengingListComparison", "Challenging ID: " + challengingId + ", Challenge ID: " + challengeId);
            if (challenging.getChallengingID().equals(challengeId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Reset the custom view when the fragment is destroyed
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setCustomView(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Update the bottom navigation bar
        updateBottomNavigationBar();
    }

    private void updateBottomNavigationBar() {
        // Assuming you have a reference to the BottomNavigationView
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);

        // Perform any update logic for the BottomNavigationView
        // For example, you can select the correct menu item based on your requirements
        bottomNavigationView.setSelectedItemId(R.id.navigation_transit);
    }

    protected void showPassportDialog(String userID) {
        DigitalPassportFragment passportFragment = new DigitalPassportFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userID", userID);
        passportFragment.setArguments(bundle);

        passportFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);

        passportFragment.show(getParentFragmentManager(), "passport_dialog");
    }
}