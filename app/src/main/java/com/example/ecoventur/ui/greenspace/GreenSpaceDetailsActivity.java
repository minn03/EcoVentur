package com.example.ecoventur.ui.greenspace;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.example.ecoventur.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GreenSpaceDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String placeId;
    private String UID;
    private GreenSpace space;
    private Bundle savedInstanceState;
    private MapView MVSpaceLocation;
    private GoogleMap googleMap;
    private TextView TVSpaceName, TVSpaceOpeningHours, TVSpaceAddress, TVSpaceDistance, TVSpaceFee, TVSpaceLink;
    private LinearLayout LLSpaceReviews;
    private CardView CVWriteReview, CVShare;
    ActivityResultLauncher<Intent> ARL = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (writeReviewDialog != null) writeReviewDialog.handleReviewImage(data);
            }
        }
    });
    WriteReviewDialog writeReviewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greenspace_details);
        this.savedInstanceState = savedInstanceState;

        Intent intent = getIntent();
        if (intent != null) {
            this.placeId = intent.getStringExtra("placeId");
            this.UID = intent.getStringExtra("UID");
        }
        if (placeId != null) {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                com.google.maps.model.LatLng currentLatLng = null;
                if (location != null) {
                    currentLatLng = new com.google.maps.model.LatLng(location.getLatitude(), location.getLongitude());
                }
                initializeWidgets();
                this.space = new GreenSpace(placeId, currentLatLng, new Callback() {
                    @Override
                    public void onDataLoaded (Object object) {
                        assignUIWidgets();
                    }
                    @Override
                    public void onFailure (Exception e) {
                        System.out.println("Error retrieving green space details: " + e);
                    }
                });
            });
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // move camera to location of green space and add marker
        LatLng location = space.getMapsLatLng();
        MarkerOptions greenSpaceMarker = new MarkerOptions().position(location).title(space.getName());
        googleMap.addMarker(greenSpaceMarker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

    }
    private void initializeWidgets() {
        MVSpaceLocation = findViewById(R.id.MVSpaceLocation);
        TVSpaceName = findViewById(R.id.TVSpaceName);
        TVSpaceOpeningHours = findViewById(R.id.TVSpaceOpeningHours);
        TVSpaceAddress = findViewById(R.id.TVSpaceAddress);
        TVSpaceDistance = findViewById(R.id.TVSpaceDistance);
        TVSpaceFee = findViewById(R.id.TVSpaceFee);
        TVSpaceLink = findViewById(R.id.TVSpaceLink);
        LLSpaceReviews = findViewById(R.id.LLSpaceReviews);
        CVWriteReview = findViewById(R.id.CVWriteReview);
        CVShare = findViewById(R.id.CVShare);
    }
    private void assignUIWidgets() {
        MVSpaceLocation.onCreate(savedInstanceState);
        MVSpaceLocation.getMapAsync(this);
        TVSpaceName.setText(space.getName());
        TVSpaceOpeningHours.setText(space.getOpeningHours());
        TVSpaceAddress.setText(space.getAddress());

        if (space.getApproxDistance() != -1.0){
            TVSpaceDistance.setText(String.format("Approximately %.1fkm from current location", space.getApproxDistance()));
        }
        else {
            TVSpaceDistance.setText("Distance from current location not available");
        }

        if (space.getEntryFee() == 0.0) TVSpaceFee.setText("FREE");
        else if (space.getEntryFee() == -1.0) TVSpaceFee.setText("Entry Fee Unspecified");
        else TVSpaceFee.setText(String.format("RM %.2f", space.getEntryFee()));

        if (space.getMapsURL() != null) {
            SpannableString spannableLink = customTabLauncher.makeTextSpannable("View in Google Maps", space.getMapsURL());
            TVSpaceLink.setText(spannableLink);
            TVSpaceLink.setMovementMethod(LinkMovementMethod.getInstance());
            TVSpaceLink.setHighlightColor(Color.TRANSPARENT);
        }

        LLSpaceReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GreenSpaceDetailsActivity.this, GreenSpaceReviewsActivity.class);
                intent.putExtra("placeId", placeId);
                intent.putExtra("UID", UID);
                startActivity(intent);
            }
        });
        CVWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeReviewDialog = new WriteReviewDialog(GreenSpaceDetailsActivity.this, ARL,
                        new WriteReviewDialog.WriteReviewDialogListener() {
                            @Override
                            public void onCancelClicked() {
                                Toast.makeText(GreenSpaceDetailsActivity.this, "Review cancelled.", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onSubmitClicked() {
                            }
                        },
                        placeId, space.getName(), UID);
            }
        });
        CVShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                String shareBody =
                        String.format("\uD83C\uDF3F Look! This is what I found from EcoVentur, a safe, inclusive and accessible, green and public place - '%s'.\n" +
                                        "Location: %s\n%s\n" +
                                        "Download EcoVentur now to find out more!\n%s",
                                space.getName(), space.getAddress(), (space.getMapsURL() == null? "" : space.getMapsURL()), "https://play.google.com/store/apps/details?id=com.example.ecoventur");
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "EcoVentur");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });
    }
}
