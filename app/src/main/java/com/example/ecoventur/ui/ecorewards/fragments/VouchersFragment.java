package com.example.ecoventur.ui.ecorewards.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ecoventur.R;
import com.example.ecoventur.ui.ecorewards.adapters.VoucherAdapter;
import com.example.ecoventur.ui.ecorewards.models.Voucher;
import com.example.ecoventur.ui.ecorewards.viewModels.VouchersViewModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VouchersFragment extends Fragment {

    private VouchersViewModel viewModelE00101;
    private VoucherAdapter voucherAdapter;
    private FirebaseFirestore db;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModelE00101 = new ViewModelProvider(requireActivity()).get(VouchersViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();

        // Set the title for the Toolbar in the hosting activity
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Redeem Voucher");
        }
        View view = inflater.inflate(R.layout.fragment_vouchers, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.voucherView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        voucherAdapter = new VoucherAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(voucherAdapter);

        db = FirebaseFirestore.getInstance();

        observeViewModel();
        fetchVouchersFromFirestore();

        return view;
    }

    private void fetchVouchersFromFirestore() {
        // Reference the user document under 'users'
        DocumentReference userRef = db.collection("users").document(userId);

        // Reference the 'activeVoucher' collection under the user document
        CollectionReference activeVoucherCollectionRef = userRef.collection("activeVoucher");
        CollectionReference pastVoucherCollectionRef = userRef.collection("pastVoucher");

        // Get the current timestamp
        Timestamp currentTimestamp = Timestamp.now();

        activeVoucherCollectionRef.orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Voucher> voucherList = new ArrayList<>();
                ArrayList<DocumentSnapshot> expiredVouchers = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    try {
                        String voucherTitle = document.getString("voucherTitle");

                        // Retrieve the timestamp field as a Firestore Timestamp
                        Timestamp timestamp = document.getTimestamp("timestamp");

                        if (timestamp != null) {
                            // Add 30 days to the expiry date
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(timestamp.toDate());
                            calendar.add(Calendar.DAY_OF_YEAR, 30);

                            // Get the date after adding 30 days and convert it back to a Timestamp
                            Timestamp expiryTimestamp = new Timestamp(calendar.getTime());

                            if (isVoucherExpired(expiryTimestamp.toDate())) {
                                // Add expired voucher to the list
                                expiredVouchers.add(document);
                            } else {
                                String imageUrl = document.getString("imgURL1");
                                Voucher voucher = new Voucher(voucherTitle, expiryTimestamp, imageUrl);
                                voucherList.add(voucher);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("FetchVouchers", "Error while fetching vouchers: " + e.getMessage());
                    }
                }

                // Delete expired vouchers
                deleteExpiredVouchers(expiredVouchers, userRef);

                // Update ViewModel with fetched active voucher data
                viewModelE00101.setActiveVouchers(voucherList);
            } else {
                Log.d("FetchVouchers", "Error getting vouchers: " + task.getException());
            }
        });
    }

    private boolean isVoucherExpired(Date expiryDate) {
        // Logic to check if the voucher has expired based on the current date
        // Compare 'expiryDate' with the current date
        Date currentDate = Calendar.getInstance().getTime();
        return currentDate.after(expiryDate);
    }

    private void deleteExpiredVouchers(ArrayList<DocumentSnapshot> expiredVouchers, DocumentReference userRef) {
        CollectionReference activeVoucherCollectionRef = userRef.collection("activeVoucher");

        for (DocumentSnapshot expiredVoucher : expiredVouchers) {
            // Check if the voucher is expired
            if (isVoucherExpired(expiredVoucher.getTimestamp("timestamp").toDate())) {
                // Move the expired voucher document from activeVoucher to pastVoucher
                activeVoucherCollectionRef.document(expiredVoucher.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d("DeleteExpiredVouchers", "Expired voucher deleted successfully");
                        })
                        .addOnFailureListener(e -> {
                            Log.d("DeleteExpiredVouchers", "Error deleting expired voucher: " + e.getMessage());
                        });
            }
        }
    }

    private void observeViewModel() {
        viewModelE00101.getActiveVouchers().observe(getViewLifecycleOwner(), vouchersList -> {
            if (vouchersList != null && !vouchersList.isEmpty()) {
                Log.d("e00101 Fragment", "Received vouchers list with items");
                // Update RecyclerView with fetched data from ViewModel
                voucherAdapter.updateList(vouchersList);
            } else {
                // No vouchers or error fetching data
                Log.d("e00101 Fragment", "Received empty vouchers list or null");
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve ViewModel instance
        viewModelE00101 = new ViewModelProvider(requireActivity()).get(VouchersViewModel.class);

        // Retrieve the voucher name from the bundle passed from e001 fragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            String voucherName = bundle.getString("voucherTitle");
            boolean isActiveVoucher = bundle.getBoolean("isActiveVoucher");
            String voucherImage = bundle.getString("imgURL1");
            int ecoCoins = bundle.getInt("ecoCoins");
            if (isActiveVoucher) {
                // Set the redeemed voucher details into the ViewModel
                viewModelE00101.setRedeemedVoucherDetails(voucherName, voucherImage, ecoCoins);

                // Voucher is active, store in activeVouchers collection
                storeVoucherInFirestore(db.collection("users").document(userId).collection("activeVoucher"),
                        userId, voucherName, voucherImage, ecoCoins);
            } else {
                // Voucher is past, store in pastVouchers collection
                storeVoucherInFirestore(db.collection("pastVoucher"), userId, voucherName, voucherImage, ecoCoins);
            }
        }
    }

    private void storeVoucherInFirestore(CollectionReference voucherCollectionRef, String userId, String voucherName, String voucherImageURL, int ecoCoins) {
        Timestamp timestamp = Timestamp.now();

        // Create a new document with an auto-generated ID in the specified collection
        Map<String, Object> voucherData = new HashMap<>();
        voucherData.put("voucherTitle", voucherName);
        voucherData.put("imgURL1", voucherImageURL);
        voucherData.put("timestamp", timestamp);
        voucherData.put("ecoCoins", ecoCoins);

        // Add the voucher data to the specified collection
        voucherCollectionRef.add(voucherData)
                .addOnSuccessListener(documentReference -> {
                    // Voucher added successfully
                    Toast.makeText(requireContext(), "Voucher added!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to add voucher
                    Toast.makeText(requireContext(), "Failed to add voucher to Firestore", Toast.LENGTH_SHORT).show();
                });
    }
}