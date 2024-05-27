package com.example.ecoventur.ui.greenspace;

import static com.example.ecoventur.ui.greenspace.approxDistanceBetweenLocation.HaversineFormula;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.model.LatLng;
import com.google.maps.model.Geometry;
import com.google.maps.model.OpeningHours;

public class GreenSpace {
    private String placeId = null;
    private String imageLink = null;
    private String name = "Unspecified Green Space Name";
    private double approxDistance = -1.0;
    private double rating = -1.0;
    private LatLng location = null; // for calc approxDistance
    private com.google.android.gms.maps.model.LatLng mapsLatLng = null; // for Maps view
    private String openingHours = "Unspecified Opening Hours";
    private String address = "Unspecified Address";
    private double entryFee = -1.0;
    private String mapsURL = null;
    public GreenSpace () {
        // empty constructor required for Firestore (retrieving list of GreenSpaces)
    }
    public GreenSpace (String name, double approxDistance, double rating) {
        // for hard coded data
        this.name = name;
        this.approxDistance = approxDistance;
        this.rating = rating;
    }
//    public GreenSpace (String placeId, String name, double approxDistance, float rating, LatLng location, OpeningHours openingHours, String formattedAddress) {
//        // for Google Places API
//        if (placeId != null) {
//            this.placeId = placeId;
//            this.mapsURL = "https://www.google.com/maps/place/?q=place_id:" + placeId;
//        }
//        if (name != null) {
//            this.name = name;
//        }
//        if (rating >= 1.0 && rating <= 5.0){
//            this.rating = rating;
//        }
//        if (location != null) {
//            this.location = location;
//            this.approxDistance = approxDistance;
//        }
//        if (openingHours != null) {
//            this.openingHours = openingHours.toString();
//        }
//        if (formattedAddress != null){
//            this.address = formattedAddress;
//        }
////        this.admissionFee = admissionFee;
//    }
    public GreenSpace (String placeId, LatLng currentLatLng, Callback callback) {
        // find and retrieve details of GreenSpace using placeId
        this.placeId = placeId;
        fetchDetailsFromFirestore(currentLatLng, callback);
    }
    private void fetchDetailsFromFirestore(LatLng currentLatLng, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("greenSpaces").document(placeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.contains("imageLink")) this.setImageLink(document.getString("imageLink"));
                        if (document.contains("name")) this.setName(document.getString("name"));
                        GeoPoint geoPoint = document.getGeoPoint("latLng");
                        if (geoPoint != null) {
                            this.setLocation(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                            this.setMapsLatLng(new com.google.android.gms.maps.model.LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                        }
                        this.setApproxDistance(HaversineFormula(currentLatLng,location));
                        if (document.contains("openingHours")) this.setOpeningHours(document.getString("openingHours"));
                        if (document.contains("address")) this.setAddress(document.getString("address"));
                        if (document.contains("entryFee")) this.setEntryFee(document.getDouble("entryFee"));
                        if (document.contains("link")) this.setMapsURL(document.getString("link"));
                        callback.onDataLoaded(this);
                    } else {
                        callback.onFailure(new Exception("Document does not exist."));
                    }
                });
    }
    public String getPlaceId() {
        return placeId;
    }
    public String getImageLink() {
        return imageLink;
    }
    public String getName() {
        return name;
    }
    public double getApproxDistance() {
        return approxDistance;
    }
    public double getRating() {
        return rating;
    }
    public LatLng getLocation() {
        return location;
    }
    public com.google.android.gms.maps.model.LatLng getMapsLatLng() {
        return mapsLatLng;
    }
    public String getOpeningHours() {
        return openingHours;
    }
    public String getAddress() {
        return address;
    }
    public double getEntryFee() {
        return entryFee;
    }
    public String getMapsURL() {
        return mapsURL;
    }
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setApproxDistance(double approxDistance) {
        this.approxDistance = approxDistance;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public void setLocation(LatLng location) {
        this.location = location;
    }
    public void setMapsLatLng(com.google.android.gms.maps.model.LatLng mapsLatLng) {
        this.mapsLatLng = mapsLatLng;
    }
    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setEntryFee(double entryFee) {
        this.entryFee = entryFee;
    }
    public void setMapsURL(String mapsURL) {
        this.mapsURL = mapsURL;
    }
}
