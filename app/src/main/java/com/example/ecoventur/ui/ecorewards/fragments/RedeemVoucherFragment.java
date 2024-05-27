package com.example.ecoventur.ui.ecorewards.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
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
import com.example.ecoventur.ui.ecorewards.viewModels.RedeemVoucherViewModel;
import com.example.ecoventur.ui.ecorewards.viewModels.ViewEcorewardsCatalogViewModel;
import com.example.ecoventur.ui.greenspace.Callback;
import com.example.ecoventur.ui.greenspace.ecoCoinsManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RedeemVoucherFragment extends Fragment {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String UID;
    private ViewEcorewardsCatalogViewModel viewModelE003;
    private RedeemVoucherViewModel viewModelE001;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (user != null) {
            UID = user.getUid();
        }
        viewModelE003 = new ViewModelProvider(requireActivity()).get(ViewEcorewardsCatalogViewModel.class);
        viewModelE001 = new ViewModelProvider(requireActivity()).get(RedeemVoucherViewModel.class);
    }

    public static RedeemVoucherFragment newInstance() {
        return new RedeemVoucherFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();

        // Set the title for the Toolbar in the hosting activity
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Redeem Voucher");
        }
        return inflater.inflate(R.layout.fragment_redeem_voucher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the selected document ID from ViewModel
        viewModelE003 = new ViewModelProvider(requireActivity()).get(ViewEcorewardsCatalogViewModel.class);
        String selectedDocId = viewModelE003.getSelectedDocId();


        if (selectedDocId != null && !selectedDocId.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference voucherRef = db.collection("redeemVouchers").document(selectedDocId);

            voucherRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {
                        populateUIWithVoucherDetails(snapshot);
                        setupRedeemButton(selectedDocId);
                    } else {
                        // Handle the case where the selected voucher ID doesn't exist
                        // For example, show an error message or navigate back to the previous fragment
                    }
                }
            });
        } else {
            // Handle the case where selectedVoucherId is null or empty
            // For example, show an error message or navigate back to the previous fragment
        }
        Button redeemButton = requireView().findViewById(R.id.redeem);
        redeemButton.setOnClickListener(v -> showRedeemConfirmationDialog(selectedDocId));
    }

    private void populateUIWithVoucherDetails(DocumentSnapshot snapshot) {
        ImageView imageView = requireView().findViewById(R.id.imageView);
        ImageView imageView2 = requireView().findViewById(R.id.imageCoin);
        TextView voucherTextView = requireView().findViewById(R.id.voucher);
        TextView reminder = requireView().findViewById(R.id.description);
        TextView valueTextView = requireView().findViewById(R.id.ecvalue);

        String imageUrl = snapshot.getString("imgURL1");
        String imageUrl2 = snapshot.getString("imgURL2");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(requireContext()).load(imageUrl).into(imageView);
        }
        if (imageUrl2 != null && !imageUrl2.isEmpty()) {
            Glide.with(requireContext()).load(imageUrl2).into(imageView2);
        }

        voucherTextView.setText(snapshot.getString("voucherTitle"));
        reminder.setText(snapshot.getString("reminder"));
        long ecoCoinsLong = snapshot.getLong("ecoCoins");
        int ecoCoins = (int) ecoCoinsLong;
        valueTextView.setText(ecoCoins + " ec");
    }

    private void setupRedeemButton(String selectedDocId) {
        Button redeemButton = requireView().findViewById(R.id.redeem);
        redeemButton.setOnClickListener(v -> showRedeemConfirmationDialog(selectedDocId));
    }

    private void showRedeemConfirmationDialog(String selectedDocId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference voucherRef = db.collection("redeemVouchers").document(selectedDocId);

        LiveData<Integer> userEcoCoinsLiveData = viewModelE001.getUserEcoCoins();
        userEcoCoinsLiveData.observe(getViewLifecycleOwner(), ecoCoins -> {
            if (ecoCoins != null) {
                voucherRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        if (snapshot != null && snapshot.exists()) {
                            Long voucherEcoCoinsLong = snapshot.getLong("ecoCoins");
                            if (voucherEcoCoinsLong != null) {
                                int voucherEcoCoins = voucherEcoCoinsLong.intValue();

                                if (ecoCoins >= voucherEcoCoins) {
                                    // Proceed with redemption confirmation dialog
                                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                    builder.setTitle("Confirm redemption?");
                                    builder.setMessage("EcoCoins will be deducted.");
                                    builder.setPositiveButton("Yes", (dialog, which) -> {
                                        redeemVoucher(selectedDocId);
                                    });
                                    builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                                    builder.show();
                                } else {
                                    // User does not have enough EcoCoins, show a message
                                    Toast.makeText(requireContext(), "Not enough EcoCoins!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Handle case where 'ecoCoins' field is null in voucher document
                                Toast.makeText(requireContext(), "EcoCoins field is null!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle the case where the selected voucher ID doesn't exist
                            Toast.makeText(requireContext(), "Voucher ID doesn't exist!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle Firestore task failure
                        Log.e("Firestore", "Error fetching voucher document: ", task.getException());
                        Toast.makeText(requireContext(), "Error fetching voucher document!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Handle case where EcoCoins LiveData returns null
                Toast.makeText(requireContext(), "Error fetching EcoCoins!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redeemVoucher(String selectedDocId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference voucherRef = db.collection("redeemVouchers").document(selectedDocId);

        voucherRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot != null && snapshot.exists()) {
                    String voucherTitle = snapshot.getString("voucherTitle");
                    String imgURL1 = snapshot.getString("imgURL1");
                    long ecoCoinsLong = snapshot.getLong("ecoCoins");
                    int ecoCoins = (int) ecoCoinsLong;

                    ecoCoinsManager.deductEcoCoins(UID, "Redeemed " + voucherTitle, ecoCoins, new Callback() {
                        @Override
                        public void onDataLoaded(Object data) {
                            Toast.makeText(requireContext(), "Voucher redeemed successfully!", Toast.LENGTH_SHORT).show();
                            // Set redeemed voucher details in ViewModel
                            viewModelE001.setRedeemedVoucherDetails(voucherTitle, imgURL1, ecoCoins);

                            // Navigate to e00101 fragment and pass voucher details via Bundle
                            Bundle bundle = new Bundle();
                            bundle.putString("voucherTitle", voucherTitle);
                            bundle.putString("imgURL1", imgURL1);
                            bundle.putInt("ecoCoins", ecoCoins);
                            bundle.putBoolean("isActiveVoucher", true);
                            bundle.putBoolean("redemptionConfirmed", true); //delete if crash

                            Navigation.findNavController(requireView()).navigate(R.id.action_e001_to_e00101, bundle);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                        }
                    });
                }
            }
        });
    }

}