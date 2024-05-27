package com.example.ecoventur.ui.ecorewards.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ecoventur.R;
import com.example.ecoventur.ui.ecorewards.models.Catalog;
import com.example.ecoventur.ui.ecorewards.viewModels.ViewEcorewardsCatalogViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewEcorewardsCatalogFragment extends Fragment {
    private ViewEcorewardsCatalogViewModel viewModelE003;
    private CollectionReference catalogRef;
    private RecyclerView recyclerView;
    private TextView ecocoinsbalance;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<Catalog, CatalogViewHolder> adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();

        // Set the title for the Toolbar in the hosting activity
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("EcoRewards Catalog");
        }
        return inflater.inflate(R.layout.fragment_view_ecorewards_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModelE003 = new ViewModelProvider(requireActivity()).get(ViewEcorewardsCatalogViewModel.class);

        FirebaseApp.initializeApp(requireContext());

        db = FirebaseFirestore.getInstance();
        catalogRef = db.collection("catalogs");

        // Initialize TextView
        ecocoinsbalance = view.findViewById(R.id.ecocoinsbalance);

        // Retrieve ecocoin field from Firestore and set the value in TextView
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String UID = "";
        if (user != null) {
            UID = user.getUid();
        }
        else {
            Log.e("e003 Fragment", "Error fetching UID");
        }
        DocumentReference userDocRef = db.collection("users").document(UID);
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long ecocoin = documentSnapshot.getLong("ecocoin");
                if (ecocoin != null) {
                    ecocoinsbalance.setText(String.valueOf(ecocoin) + " ec");
                } else {
                    // Handle the case where ecocoin field is null
                    ecocoinsbalance.setText("N/A");
                }
            } else {
                // Handle the case where the document doesn't exist
                ecocoinsbalance.setText("Document not found");
            }
        }).addOnFailureListener(e -> {
            // Handle any errors while fetching the ecocoin field
            Log.e("e003 Fragment", "Error fetching ecocoin field: " + e.getMessage());
            ecocoinsbalance.setText("Error fetching ecocoin");
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        FirestoreRecyclerOptions<Catalog> options =
                new FirestoreRecyclerOptions.Builder<Catalog>()
                        .setQuery(catalogRef, Catalog.class)
                        .build();

        adapter = new FirestoreRecyclerAdapter<Catalog, CatalogViewHolder>(options) {
            @NonNull
            @Override
            public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog, parent, false);
                return new CatalogViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull CatalogViewHolder holder, int position, @NonNull Catalog model) {
                holder.bindData(model);

                holder.itemView.setOnClickListener(v -> {
                    int clickedPosition = holder.getAdapterPosition();
                    if (clickedPosition != RecyclerView.NO_POSITION) {
                        String selectedDocId = getSnapshots().getSnapshot(clickedPosition).getId();

                        Log.d("SelectedDocID", "Selected Document ID: " + selectedDocId);

                        // Set selectedDocId in ViewModel
                        viewModelE003.setSelectedDocId(selectedDocId);

                        Bundle bundle = new Bundle();
                        bundle.putString("selectedDocId", selectedDocId);

                        Navigation.findNavController(holder.itemView)
                                .navigate(R.id.action_e003_to_e001, bundle);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class CatalogViewHolder extends RecyclerView.ViewHolder {
        android.widget.ImageView villagegrocer;
        android.widget.TextView vouchertitle;
        android.widget.ImageView coinbag;
        android.widget.TextView vouchervalue;

        public CatalogViewHolder(@NonNull View itemView) {
            super(itemView);
            villagegrocer = itemView.findViewById(R.id.villagegrocer);
            vouchertitle = itemView.findViewById(R.id.vouchertitle);
            coinbag = itemView.findViewById(R.id.coinbag);
            vouchervalue = itemView.findViewById(R.id.vouchervalue);
        }

        public void bindData(Catalog model) {
            vouchertitle.setText(model.getVoucherTitle());
            Glide.with(itemView.getContext()).load(model.getImgURL1()).into(villagegrocer);
            Glide.with(itemView.getContext()).load(model.getImgURL2()).into(coinbag);
            vouchervalue.setText(String.valueOf(model.getEcoCoins() + " ec"));
        }
    }
}