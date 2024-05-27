package com.example.ecoventur.ui.greenspace;

import com.google.maps.model.LatLng;

public class approxDistanceBetweenLocation {
    public static double HaversineFormula(LatLng currentLatLng, LatLng placeLatLng) {
        // https://www.movable-type.co.uk/scripts/latlong.html
        // Haversine formula
        if (currentLatLng == null || placeLatLng == null) {
            return -1.0;
        }
        double lat1 = currentLatLng.lat;
        double lon1 = currentLatLng.lng;
        double lat2 = placeLatLng.lat;
        double lon2 = placeLatLng.lng;
        double R = 6371e3; // metres
        double φ1 = Math.toRadians(lat1);
        double φ2 = Math.toRadians(lat2);
        double Δφ = Math.toRadians(lat2-lat1);
        double Δλ = Math.toRadians(lon2-lon1);
        double a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
                Math.cos(φ1) * Math.cos(φ2) *
                        Math.sin(Δλ/2) * Math.sin(Δλ/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c / 1000;
    }
}
