package com.example.ecoventur.ui.greenspace;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.ecoventur.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class GreenEventDetailsActivity extends AppCompatActivity {
    private String eventId;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String UID;
    private GreenEvent event = new GreenEvent();
    private ImageView IVEventImage;
    private TextView TVEventName, TVEventEcoCoins, TVEventDate, TVEventDuration, TVEventFee, TVEventVenue, TVEventDistance, TVEventParticipants, TVEventTnC, TVEventDetails;
    private CardView CVSaveEventToWishlist, CVEventSavedToWishlist, CVShare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_green_event_details);

        UID = user.getUid();
        Intent intent = getIntent();
        if (intent != null) {
            eventId = intent.getStringExtra("eventId");
        }
        if (eventId != null) {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                LatLng currentLatLng = null;
                if (location != null) {
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                }
                initializeWidgets();
                this.event = new GreenEvent(eventId, UID, currentLatLng, new Callback() {
                    @Override
                    public void onDataLoaded(Object object) {
                        assignUIWidgets();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        System.out.println("Error retrieving event details: " + e);
                    }
                });
            });
        }
    }
    private void initializeWidgets () {
        IVEventImage = findViewById(R.id.IVEventImage);
        TVEventName = findViewById(R.id.TVEventName);
        TVEventEcoCoins = findViewById(R.id.TVEventEcoCoins);
        TVEventDate = findViewById(R.id.TVEventDate);
        TVEventDuration = findViewById(R.id.TVEventDuration);
        TVEventFee = findViewById(R.id.TVEventFee);
        TVEventVenue = findViewById(R.id.TVEventVenue);
        TVEventDistance = findViewById(R.id.TVEventDistance);
        TVEventParticipants = findViewById(R.id.TVEventParticipants);
        TVEventTnC = findViewById(R.id.TVEventTnC);
        TVEventDetails = findViewById(R.id.TVEventDetails);
        CVSaveEventToWishlist = findViewById(R.id.CVSaveEventToWishlist);
        CVEventSavedToWishlist = findViewById(R.id.CVEventSavedToWishlist);
        CVShare = findViewById(R.id.CVShare);
    }
    private void assignUIWidgets() {
        if (event.getImageLink() != null) {
            Glide.with(this)
                    .load(event.getImageLink())
                    .into(IVEventImage);
        }
        else {
            Glide.with(this)
                    .load(R.drawable.event_card)
                    .into(IVEventImage);
        }

        TVEventName.setText(event.getName());

        if (event.getEcoCoins() == -1) TVEventEcoCoins.setText("EcoCoins Unspecified");
        else TVEventEcoCoins.setText(String.valueOf(event.getEcoCoins()));

        TVEventDate.setText(event.getDate());
        TVEventDuration.setText(event.getDuration());

        if (event.getRegistrationFee() == 0.0) TVEventFee.setText("FREE");
        else if (event.getRegistrationFee() == -1.0) TVEventFee.setText("Registration Fee Unspecified");
        else TVEventFee.setText(String.format("RM %.2f", event.getRegistrationFee()));

        if (event.getVenueLink() != null){
            SpannableString spannableVenue = customTabLauncher.makeTextSpannable(event.getVenue() + "\n" + event.getVenueAddress(),event.getVenueLink());
            TVEventVenue.setText(spannableVenue);
            TVEventVenue.setMovementMethod(LinkMovementMethod.getInstance());
            TVEventVenue.setHighlightColor(Color.TRANSPARENT);
        }
        else {
            TVEventVenue.setText(event.getVenue() + "\n" + event.getVenueAddress());
        }

        if (event.getApproxDistance() != -1.0){
            TVEventDistance.setText(String.format("Approximately %.1fkm from current location", event.getApproxDistance()));
        }
        else {
            TVEventDistance.setText("Distance from current location not available");
        }

        int going = event.getGoing();
        int interested = event.getInterested();
        if (going == -1 && interested == -1){
            TVEventParticipants.setText("Participants not available");
        }
        else if (going == -1 || interested == -1){
            if (going == -1) going = 0;
            if (interested == -1) interested = 0;
            TVEventParticipants.setText(String.format("%d going, %d interested", going, interested));
        }
        else {
            TVEventParticipants.setText(String.format("%d going, %d interested", going, interested));
        }

        if (event.getTncLink() != null){
            SpannableString spannableTnC = customTabLauncher.makeTextSpannable("View terms & conditions", event.getTncLink());
            TVEventTnC.setText(spannableTnC);
            TVEventTnC.setMovementMethod(LinkMovementMethod.getInstance());
            TVEventTnC.setHighlightColor(Color.TRANSPARENT);
        }
        else {
            TVEventTnC.setText("Terms & Conditions\nnot available");
        }

        if (event.getDetailsLink() != null){
            SpannableString spannableDetails = customTabLauncher.makeTextSpannable("View more details", event.getDetailsLink());
            TVEventDetails.setText(spannableDetails);
            TVEventDetails.setMovementMethod(LinkMovementMethod.getInstance());
            TVEventDetails.setHighlightColor(Color.TRANSPARENT);
        }
        else {
            TVEventDetails.setText("Details not available");
        }

        if (!event.isSavedToWishlist()){
            CVSaveEventToWishlist.setVisibility(View.VISIBLE);
            CVEventSavedToWishlist.setVisibility(View.GONE);
        }
        else {
            CVSaveEventToWishlist.setVisibility(View.GONE);
            CVEventSavedToWishlist.setVisibility(View.VISIBLE);
        }
        CVSaveEventToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToWishlist(new Callback() {
                    @Override
                    public void onDataLoaded(Object object) {
                        CVSaveEventToWishlist.setVisibility(View.GONE);
                        CVEventSavedToWishlist.setVisibility(View.VISIBLE);
                        ecoCoinsManager.addEcoCoins(UID, String.format("Saved %s to wishlist", event.getName()), 10, new Callback() {
                            @Override
                            public void onDataLoaded(Object object) {
                                Toast.makeText(getApplicationContext(), "10 EcoCoins added! Current balance: " + (int) object, Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onFailure(Exception e) {
                                System.out.println("Error adding 10 EcoCoins to user: " + e);
                            }
                        });
                        if (calculateScheduledTime(event.getDate(), 7) != -1) {
                            notificationScheduler.scheduleNotification(
                                    getApplicationContext(),
                                    String.format("Soft Reminder: Upcoming Green Event"),
                                    String.format("\uD83C\uDF1F Don't miss out: %s in 7 days! %d people are interested!", event.getName(), event.getInterested()),
                                    event.getEventId().hashCode() * 10 + 7,
                                    calculateScheduledTime(event.getDate(), 7)
                            );
                        }
                        if (calculateScheduledTime(event.getDate(), 3) != -1) {
                            notificationScheduler.scheduleNotification(
                                    getApplicationContext(),
                                    String.format("Soft Reminder: Upcoming Green Event"),
                                    String.format("\uD83D\uDE80 Just 3 days until %s! %d people are going!", event.getName(), event.getGoing()),
                                    event.getEventId().hashCode() * 10 + 3,
                                    calculateScheduledTime(event.getDate(), 3)
                            );
                        }
                        if (calculateScheduledTime(event.getDate(), 0) != -1) {
                            notificationScheduler.scheduleNotification(
                                    getApplicationContext(),
                                    String.format("Green Event Today"),
                                    String.format("\uD83C\uDF89 %s is happening today! Event will happen on %s, at %s. Can't wait to see you there!", event.getName(), event.getDuration(), event.getVenue()),
                                    event.getEventId().hashCode() * 10,
                                    calculateScheduledTime(event.getDate(), 0)
                            );
                        }
                    }
                    @Override
                    public void onFailure(Exception e) {
                        System.out.println("Error adding document to wishlist: " + e);
                    }
                });
            }
        });
        CVEventSavedToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFromWishlist(new Callback() {
                    @Override
                    public void onDataLoaded(Object object) {
                        CVSaveEventToWishlist.setVisibility(View.VISIBLE);
                        CVEventSavedToWishlist.setVisibility(View.GONE);
                        ecoCoinsManager.deductEcoCoins(UID, String.format("Removed %s from wishlist", event.getName()), 10, new Callback() {
                            @Override
                            public void onDataLoaded(Object object) {
                                Toast.makeText(getApplicationContext(), "10 EcoCoins deducted! Current balance: " + (int) object, Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onFailure(Exception e) {
                                System.out.println("Error deducting 10 EcoCoins from user: " + e);
                            }
                        });
                        notificationScheduler.cancelScheduledNotification(getApplicationContext(), event.getEventId().hashCode() * 10 + 7);
                        notificationScheduler.cancelScheduledNotification(getApplicationContext(), event.getEventId().hashCode() * 10 + 3);
                        notificationScheduler.cancelScheduledNotification(getApplicationContext(), event.getEventId().hashCode() * 10);
                    }
                    @Override
                    public void onFailure(Exception e) {
                        System.out.println("Error deleting document from wishlist: " + e);
                    }
                });
            }
        });

        CVShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                String shareBody =
                        String.format("\uD83C\uDF3F Join me at the Green Event '%s' on %s! Earn EcoCoins, explore sustainability practices, and connect like-minded individuals.\n" +
                                "Venue: %s\n%s\n" +
                                "Download EcoVentur now to find out more!\n%s",
                                event.getName(), event.getDate(), event.getVenue(), (event.getVenueLink() == null? "" : event.getVenueLink()), "https://play.google.com/store/apps/details?id=com.example.ecoventur");
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "EcoVentur");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });
    }
    private void saveToWishlist(Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.document("greenEvents/" + eventId);

        Map<String, Object> wishlistEvent = new HashMap<>();
        wishlistEvent.put("eventId", eventRef);

        db.collection("users").document(UID)
                .collection("eventsWishlist")
                .add(wishlistEvent)
                .addOnSuccessListener(documentReference -> {
                    event.setSavedToWishlist(true);
                    callback.onDataLoaded(event);
                })
                .addOnFailureListener(callback::onFailure);
    }
    private void removeFromWishlist(Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(UID)
                .collection("eventsWishlist")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document: queryDocumentSnapshots) {
                        DocumentReference eventRef = document.getDocumentReference("eventId");
                        if (eventRef != null){
                            String eventRefPath = eventRef.getPath();
                            if (eventRefPath.equals("greenEvents/" + eventId)) {
                                document.getReference().delete()
                                        .addOnSuccessListener(aVoid -> {
                                            event.setSavedToWishlist(false);
                                            callback.onDataLoaded(event);
                                        })
                                        .addOnFailureListener(callback::onFailure);
                                break; // found the document to delete, so break out of the loop
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error getting documents: " + e);
                });
    }
    private long calculateScheduledTime(String date, int daysBefore) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        try {
            calendar.setTime(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
        calendar.add(Calendar.DAY_OF_YEAR, -daysBefore);

        return calendar.getTimeInMillis();
    }
}
