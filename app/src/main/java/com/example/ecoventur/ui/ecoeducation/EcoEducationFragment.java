package com.example.ecoventur.ui.ecoeducation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoventur.R;
import com.example.ecoventur.databinding.FragmentEcoeducationBinding;
import com.example.ecoventur.ui.ecoeducation.activities.MoreActivity;
import com.example.ecoventur.ui.ecoeducation.activities.QuizCategoryActivity;
import com.example.ecoventur.ui.ecoeducation.activities.ScoreBoardActivity;
import com.example.ecoventur.ui.ecoeducation.adapters.ArticlesAdapter;
import com.example.ecoventur.ui.ecoeducation.adapters.TutorialsAdapter;
import com.example.ecoventur.ui.ecoeducation.database.TinyDB;
import com.example.ecoventur.ui.ecoeducation.dialogs.TipsDialog;
import com.example.ecoventur.ui.ecoeducation.models.Articles;
import com.example.ecoventur.ui.ecoeducation.models.Tips;
import com.example.ecoventur.ui.ecoeducation.models.Tutorials;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

public class EcoEducationFragment extends Fragment {
    private RecyclerView articleRecyclerView;
    private RecyclerView tutorialRecyclerView;
    public static EcoEducationFragment newInstance() {
        return new EcoEducationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ecoeducation, container, false);
    }
    private static <T> T getRandomObject(ArrayList<T> list) {
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton quiz = view.findViewById(R.id.QuizText);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView moreArticles = view.findViewById(R.id.moreArticles);
        TextView moreTutorials = view.findViewById(R.id.moreTutorials);
        RecyclerView articleRecyclerView = view.findViewById(R.id.articleRecyclerView);
        RecyclerView tutorialRecyclerView = view.findViewById(R.id.tutorialRecyclerView);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        ArrayList<Tips> tips = new ArrayList<>();
        TinyDB tinyDB = new TinyDB(requireContext());
        String date = tinyDB.getString("date");
        //show the tip
        LocalDate currentDate = LocalDate.now();
        Context mContext = requireContext();
        String formattedDate = currentDate.format(formatter);
        if (!date.equals(formattedDate)) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            // Reference to the "tutorials" collection
            CollectionReference tutorialsRef = firestore.collection("dailyTips");
            tutorialsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Map each document to the Tutorial class
                            Tips tip = document.toObject(Tips.class);
                            tips.add(tip);
                        }

                        if (tips.isEmpty()) {
                            Toast.makeText(mContext,"Tips List is empty or null",Toast.LENGTH_LONG).show();
                        }else{
                            Tips tip = getRandomObject(tips);
                            TipsDialog tipsDialog = new TipsDialog(mContext, tip);
                            tipsDialog.show();
                        }
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                }
            });
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        ArrayList<Articles> allArticles = new ArrayList<>();
        ArrayList<Tutorials> allTutorials = new ArrayList<>();

        // Reference to the "tutorials" collection
        CollectionReference tutorialsRef = firestore.collection("tutorials");

        // Get all documents in the collection
        tutorialsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Map each document to the Tutorial class
                        Tutorials tutorial = document.toObject(Tutorials.class);
                        allTutorials.add(tutorial);
                    }
                    tutorialRecyclerView.setAdapter(new TutorialsAdapter(requireContext(),allTutorials));
                    tutorialRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));

                } else {
                    Log.e("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });
        // Reference to the "tutorials" collection
        CollectionReference articlesRef = firestore.collection("articles");

        // Get all documents in the collection
        articlesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Map each document to the Tutorial class
                        Articles article = document.toObject(Articles.class);
                        allArticles.add(article);
                    }
                    articleRecyclerView.setAdapter(new ArticlesAdapter(requireContext(),allArticles));
                    articleRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
                } else {
                    Log.e("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });

        quiz.setOnClickListener(quizView->{
            Intent intent = new Intent(requireContext(), QuizCategoryActivity.class);
            startActivity(intent);
        });
        imageView.setOnClickListener(viewScoreBoard->{
            Intent intent = new Intent(requireContext(), ScoreBoardActivity.class);
            startActivity(intent);
        });
        moreArticles.setOnClickListener(viewMoreArticles->{
            Intent intent = new Intent(requireContext(), MoreActivity.class);
            intent.putExtra("op","a");
            startActivity(intent);

        });
        moreTutorials.setOnClickListener(viewMoreTutorials->{
            Intent intent = new Intent(requireContext(), MoreActivity.class);
            intent.putExtra("op","t");
            startActivity(intent);
        });
    }
}