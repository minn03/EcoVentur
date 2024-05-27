package com.example.ecoventur.ui.greenspace;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoventur.R;
import com.example.ecoventur.databinding.FragmentGreenspaceBinding;
import com.example.ecoventur.ui.greenspace.adapter.GreenEventsAdapter;
import com.example.ecoventur.ui.greenspace.adapter.GreenSpacesAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GreenSpaceFragment extends Fragment implements OnMapReadyCallback {

    private String UID;
    private FragmentGreenspaceBinding binding;
    private SearchView searchView;
    private MapView mapView;
    private GoogleMap googleMap;
    private ArrayList<GreenSpace> nearbyGreenSpaces = new ArrayList<GreenSpace>();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGreenspaceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UID = user.getUid();
        } else {
            Log.e(TAG, "User is null");
        }

        searchView = root.findViewById(R.id.SVGreenSpace);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String search = searchView.getQuery().toString();
                List<Address> searchResults = null;
                Geocoder geocoder = new Geocoder(requireContext());
                try {
                    searchResults = geocoder.getFromLocationName(search, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (searchResults == null || searchResults.size() == 0) {
                    Toast.makeText(getContext(), "No results found for \"" + search + "\"", Toast.LENGTH_SHORT).show();
                }
                else {
                    Address address = searchResults.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(latLng).title(search));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    StringBuilder addressText = new StringBuilder();
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        addressText.append(address.getAddressLine(i)).append(", ");
                    }
                    String formattedAddress = addressText.toString().replaceAll(", $", "");
                    Toast.makeText(getContext(), formattedAddress, Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (googleMap != null){
                    googleMap.clear();
                }
                return false;
            }
        });

        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //Nearby Green Spaces
        RecyclerView recyclerViewNearbyGreenSpaces = binding.recyclerViewNearbyGreenSpaces;
//        nearbyGreenSpaces = new GreenSpacesList().getGreenSpaces();//hardcoded
//        nearbyGreenSpaces = new GreenSpacesList(requireActivity(),5).getGreenSpaces());//retrieved using google places api based on user's current location
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            com.google.maps.model.LatLng currentLatLng;
            if (location != null) {
                currentLatLng = new com.google.maps.model.LatLng(location.getLatitude(), location.getLongitude());
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                new GreenSpacesList(firestore, currentLatLng, new Callback() {
                    @Override
                    public void onDataLoaded(Object object) {
                        GreenSpacesAdapter greenSpacesAdapter = new GreenSpacesAdapter((ArrayList<GreenSpace>) object, UID);
                        recyclerViewNearbyGreenSpaces.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                        recyclerViewNearbyGreenSpaces.setAdapter(greenSpacesAdapter);

                        //add markers to map
                        for (GreenSpace space : (ArrayList<GreenSpace>) object) {
                            LatLng location = space.getMapsLatLng();
                            if (location != null){
                                MarkerOptions greenSpaceMarker = new MarkerOptions()
                                        .position(location)
                                        .title(space.getName())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                googleMap.addMarker(greenSpaceMarker);
                            }
                        }
                    }
                    @Override
                    public void onFailure(Exception e) {
                        System.out.println("Error retrieving nearby green spaces: " + e);
                    }
                });
            }
        });


        //Discover Green Events
        CardView cardViewGreenEventsHeader = root.findViewById(R.id.CVGreenEventHeader);
        RecyclerView recyclerViewDiscoverGreenEvents = binding.recyclerViewDiscoverGreenEvents;
//        GreenEventsAdapter adapter = new GreenEventsAdapter(new GreenEventsList().getGreenEvents());//hardcoded
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        new GreenEventsList(firestore, new Callback() {
            @Override
            public void onDataLoaded(Object object) {
                GreenEventsAdapter eventsAdapter = new GreenEventsAdapter((ArrayList<GreenEvent>) object);
                recyclerViewDiscoverGreenEvents.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerViewDiscoverGreenEvents.setAdapter(eventsAdapter);
            }
            @Override
            public void onFailure(Exception e) {
                System.out.println("Error retrieving green events: " + e);
            }
        });//firestore

        //My Events Wishlist
        CardView cardViewEventsWishlistHeader = root.findViewById(R.id.CVEventsWishlistHeader);
        RecyclerView recyclerViewMyEventsWishlist = binding.recyclerViewMyEventsWishlist;
        firestore = FirebaseFirestore.getInstance();
        new GreenEventsList(firestore, UID, new Callback() {
            @Override
            public void onDataLoaded(Object object) {
                GreenEventsAdapter wishlistAdapter = new GreenEventsAdapter((ArrayList<GreenEvent>) object);
                recyclerViewMyEventsWishlist.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerViewMyEventsWishlist.setAdapter(wishlistAdapter);
            }
            @Override
            public void onFailure(Exception e) {
                System.out.println("Error retrieving user's wishlist: " + e);
            }
        });

        Button buttonNearbyGreenSpaces = root.findViewById(R.id.ToggleNearbyGreenSpaces);
        buttonNearbyGreenSpaces.setOnClickListener(view -> toggleVisibility(recyclerViewNearbyGreenSpaces));
        Button buttonDiscoverGreenEvents = root.findViewById(R.id.ToggleDiscoverGreenEvents);
        buttonDiscoverGreenEvents.setOnClickListener(view -> {
            toggleVisibility(cardViewGreenEventsHeader);
            toggleVisibility(recyclerViewDiscoverGreenEvents);
        });
        Button buttonMyEventsWishlist = root.findViewById(R.id.ToggleMyEventsWishlist);
        buttonMyEventsWishlist.setOnClickListener(view -> {
            toggleVisibility(cardViewEventsWishlistHeader);
            toggleVisibility(recyclerViewMyEventsWishlist);
        });

        return root;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // hardcoded default location: Malaysia
        LatLng malaysia = new LatLng(4.2105, 101.9758);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(malaysia, 5));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void toggleVisibility(View view) {
        if (view.getVisibility() == View.GONE) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
        searchView.clearFocus();
    }
    @Override
    public void onPause(){
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}