package com.example.ecoventur.ui.ecoeducation.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.ecoventur.R;
import com.example.ecoventur.ui.ecoeducation.adapters.BadgeAdapter;
import com.example.ecoventur.ui.ecoeducation.models.Badges;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ScoreBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ArrayList<Badges> badges = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        // Setting up a click listener for the "back" button
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(view->{
            super.onBackPressed();
        });


        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        CollectionReference badgesCollection = FirebaseFirestore.getInstance().collection("badges");
        badgesCollection.document(uid).collection("userBadges").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Handle each badge document
                                Badges badge = document.toObject(Badges.class);
                                badges.add(badge);
                            }
                            recyclerView.setAdapter(new BadgeAdapter(ScoreBoardActivity.this,badges));
                            recyclerView.setLayoutManager(new LinearLayoutManager(ScoreBoardActivity.this,LinearLayoutManager.VERTICAL,false));
                        } else {
                            // Handle the error
                            Log.e("Firestore get Badges", Objects.requireNonNull(task.getException().getLocalizedMessage()));
                        }
                    }
                });

    }
}