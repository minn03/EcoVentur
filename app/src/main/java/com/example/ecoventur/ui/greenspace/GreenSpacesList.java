package com.example.ecoventur.ui.greenspace;

import static com.example.ecoventur.ui.greenspace.approxDistanceBetweenLocation.HaversineFormula;
import static com.google.maps.model.PlaceType.PARK;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.maps.GeoApiContext;
import com.google.maps.model.LatLng;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PendingResult;
import com.google.maps.PlacesApi;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GreenSpacesList {
    private Location currentLocation;
    private int placesCount = 5;
    private PlacesClient placesClient;
    private final ArrayList<GreenSpace> greenSpaces = new ArrayList<>();
    private Context context;
    public GreenSpacesList() {
        //hard coded data
        greenSpaces.add(new GreenSpace("Taman Desa Playground", 0.5, 4.3));
        greenSpaces.add(new GreenSpace("Lake Garden @ Bangsar South", 1.3, 4.4));
        GreenSpace space = new GreenSpace("KLCC Park", 2.7, 4.6);
        space.setOpeningHours("10 am - 10 pm (Wednesday)");
        space.setAddress("KLCC, Lot No. 241, Level 2, Suria, Kuala Lumpur City Centre, 50088 Kuala Lumpur");
        space.setEntryFee(0.00);
        space.setMapsURL("https://maps.app.goo.gl/e9KBcMLR2dGc3PJV7");
        greenSpaces.add(space);
    }
//    public GreenSpacesList(Context context, int placesCount) {
//        //fetch api
//        this.context = context;
//        this.placesCount = placesCount;
//        Places.initialize(context, "@string/API_key");
//        placesClient = Places.createClient(context);
//        fetchNearbyGreenSpaces();
//    }
    public GreenSpacesList(FirebaseFirestore db, LatLng currentLatLng, Callback callback) {
        //fetch firestore hardcoded data
        retrieveFirestoreData(db, currentLatLng, callback);
    }
//    private void fetchNearbyGreenSpaces() {
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//        }
//        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
//        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
//            if (location != null) {
//                this.currentLocation = location;
//                LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//
//                GeoApiContext geoApiContext = new GeoApiContext.Builder()
//                        .apiKey("@string/API_key")
//                        .build();
//
//                NearbySearchRequest request = PlacesApi.nearbySearchQuery(geoApiContext, currentLatLng);
//                request.radius(5000);
//                request.type(PARK);
//
//                request.setCallback(new PendingResult.Callback<PlacesSearchResponse>() {
//                    @Override
//                    public void onResult(PlacesSearchResponse result) {
//                        for (PlacesSearchResult place: result.results) {
//                            LatLng locationLatLng = place.geometry.location;
//                            com.google.android.gms.maps.model.LatLng location = new com.google.android.gms.maps.model.LatLng(place.geometry.location.lat, place.geometry.location.lng);
//                            GreenSpace space = new GreenSpace(place.placeId, place.name, HaversineFormula(currentLatLng,locationLatLng), place.rating, location, place.openingHours, place.formattedAddress);
//                            greenSpaces.add(space);
//                            if (greenSpaces.size() >= placesCount) {
//                                break;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Throwable e) {
//
//                    }
//                });
//            }
//        });
//    }
    private void retrieveFirestoreData(FirebaseFirestore db, LatLng currentLatLng, Callback callback) {
        db.collection("greenSpaces")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AtomicInteger count = new AtomicInteger(task.getResult().size());
                        for (QueryDocumentSnapshot document: task.getResult()) {
                            new ReviewsList(document.getId(), new Callback() {
                                @Override
                                public void onDataLoaded(Object object) {
                                    GreenSpace space = new GreenSpace();
                                    space.setPlaceId(document.getId());
                                    if (document.contains("imageLink")) space.setImageLink(document.getString("imageLink"));
                                    if (document.contains("name")) space.setName(document.getString("name"));
                                    GeoPoint geoPoint = document.getGeoPoint("latLng");
                                    if (geoPoint != null){
                                        space.setLocation(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                                        space.setMapsLatLng(new com.google.android.gms.maps.model.LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                                    }
                                    space.setApproxDistance(HaversineFormula(currentLatLng, space.getLocation()));
                                    if (document.contains("openingHours")) space.setOpeningHours(document.getString("openingHours"));
                                    space.setRating(GreenSpaceReviewsActivity.calcAverageRating((ArrayList<Review>) object));
                                    if (document.contains("address")) space.setAddress(document.getString("address"));
                                    if (document.contains("entryFee")) space.setEntryFee(document.getDouble("entryFee"));
                                    if (document.contains("link")) space.setMapsURL(document.getString("link"));
                                    greenSpaces.add(space);
                                    if (count.decrementAndGet() == 0) {
                                        sortPlacesByDistance();
                                        callback.onDataLoaded(greenSpaces);
                                    }
                                }
                                @Override
                                public void onFailure(Exception e) {
                                    System.out.println("Error retrieving green space rating: " + e.getMessage());
                                    if (count.decrementAndGet() == 0) {
                                        sortPlacesByDistance();
                                        callback.onDataLoaded(greenSpaces);
                                    }
                                }
                            });
                        }
                    }
                    else {
                        callback.onFailure(task.getException());
                    }
                });
    }
    private void sortPlacesByDistance() {
        Collections.sort(greenSpaces, new Comparator<GreenSpace>() {
            @Override
            public int compare(GreenSpace space1, GreenSpace space2) {
                return Double.compare(space1.getApproxDistance(), space2.getApproxDistance());
            }
        });
    }
    public ArrayList<GreenSpace> getGreenSpaces() {
        return greenSpaces;
    }
}
