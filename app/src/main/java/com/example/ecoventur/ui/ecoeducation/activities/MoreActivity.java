package com.example.ecoventur.ui.ecoeducation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecoventur.R;
import com.example.ecoventur.ui.ecoeducation.adapters.MoreAdapter;
import com.example.ecoventur.ui.ecoeducation.adapters.MoreTutorialsAdapter;
import com.example.ecoventur.ui.ecoeducation.models.Articles;
import com.example.ecoventur.ui.ecoeducation.models.Tutorials;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        RecyclerView universalRecyclerView = findViewById(R.id.universalRecyclerView);
        String op = getIntent().getStringExtra("op");
        TextView tv = findViewById(R.id.text);
        ImageView back = findViewById(R.id.back);

        back.setOnClickListener(view->{
            super.onBackPressed();
        });

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        ArrayList<Articles> allArticles = new ArrayList<>();
        ArrayList<Tutorials> allTutorials = new ArrayList<>();


        if(op.equals("t")){
            tv.setText("Tutorials");
            //load the tutorials
            // Reference to the "tutorials" collection
            CollectionReference tutorialsRef = firestore.collection("tutorials");

            // Get all documents in the collection from firestore
            tutorialsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Map each document to the Tutorial class
                            Tutorials tutorial = document.toObject(Tutorials.class);
                            allTutorials.add(tutorial);
                        }
                        universalRecyclerView.setAdapter(new MoreTutorialsAdapter(MoreActivity.this,allTutorials));
                        universalRecyclerView.setLayoutManager(new LinearLayoutManager(MoreActivity.this,LinearLayoutManager.VERTICAL,false));
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                }
            });
        }else{
            //load the articles
            // Reference to the "tutorials" collection
            CollectionReference articlesRef = firestore.collection("articles");

            // Get all documents in the collection from firestore
            articlesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Map each document to the Tutorial class
                            Articles article = document.toObject(Articles.class);
                            allArticles.add(article);
                        }
                        universalRecyclerView.setAdapter(new MoreAdapter(MoreActivity.this,allArticles));
                        universalRecyclerView.setLayoutManager(new LinearLayoutManager(MoreActivity.this,LinearLayoutManager.VERTICAL,false));
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }
}