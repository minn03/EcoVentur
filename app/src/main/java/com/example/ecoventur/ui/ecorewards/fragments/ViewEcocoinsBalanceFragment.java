package com.example.ecoventur.ui.ecorewards.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ecoventur.R;
import com.example.ecoventur.ui.ecorewards.adapters.TransactionAdapter;
import com.example.ecoventur.ui.ecorewards.models.Transaction;
import com.example.ecoventur.ui.ecorewards.viewModels.ViewEcocoinsBalanceViewModel;
import com.example.ecoventur.ui.greenspace.Callback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ViewEcocoinsBalanceFragment extends Fragment {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String UID;
    private ViewEcocoinsBalanceViewModel viewEcocoinsBalanceViewModel;
    private TransactionAdapter spendingAdapter;
    private TransactionAdapter earningAdapter;
    private int ecocoin = -1;
    private TextView ecocoinsbalance;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (user != null) {
            UID = user.getUid();
        }
        else {
            Log.e("ViewEcocoinsBalanceFragment", "User is not logged in.");
        }
        viewEcocoinsBalanceViewModel = new ViewEcocoinsBalanceViewModel(UID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();

        // Set the title for the Toolbar in the hosting activity
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("EcoCoins Balance");
        }
        View view = inflater.inflate(R.layout.fragment_view_ecocoins_balance, container, false);

        // Find the TextView for ecocoinsbalance
        ecocoinsbalance = view.findViewById(R.id.ecocoinsbalance);
        RecyclerView earningRecyclerView = view.findViewById(R.id.earningView);
        RecyclerView spendingRecyclerView = view.findViewById(R.id.spendingView);

        viewEcocoinsBalanceViewModel.retrieveEcocoinsBalance(new Callback() {
            @Override
            public void onDataLoaded(Object data) {
                ecocoin = viewEcocoinsBalanceViewModel.getEcocoinsBalance();
                ecocoinsbalance.setText((int) data + " ec");
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("ViewEcocoinsBalanceFragment", "Error retrieving ecocoins balance: " + exception.getMessage());
            }
        });

        viewEcocoinsBalanceViewModel.retrieveEarningList(new Callback() {
            @Override
            public void onDataLoaded(Object data) {
                earningRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                earningAdapter = new TransactionAdapter(requireContext(), (ArrayList<Transaction>) data, true);
                earningRecyclerView.setAdapter(earningAdapter);
            }
            @Override
            public void onFailure(Exception exception) {
                Log.e("ViewEcocoinsBalanceFragment", "Error retrieving earning list: " + exception.getMessage());
            }
        });

        viewEcocoinsBalanceViewModel.retrieveSpendingList(new Callback() {
            @Override
            public void onDataLoaded(Object data) {
                spendingRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                spendingAdapter = new TransactionAdapter(requireContext(), (ArrayList<Transaction>) data, false);
                spendingRecyclerView.setAdapter(spendingAdapter);
            }
            @Override
            public void onFailure(Exception exception) {
                Log.e("ViewEcocoinsBalanceFragment", "Error retrieving spending list: " + exception.getMessage());
            }
        });

        return view;
    }
}