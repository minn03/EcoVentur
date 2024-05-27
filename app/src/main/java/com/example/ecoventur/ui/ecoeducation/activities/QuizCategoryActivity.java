package com.example.ecoventur.ui.ecoeducation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ecoventur.R;
import com.example.ecoventur.ui.ecoeducation.adapters.CategoriesAdapter;
import com.example.ecoventur.ui.ecoeducation.models.QuizCategories;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class QuizCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_category);
        RecyclerView categories = findViewById(R.id.categories);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(view->{
            super.onBackPressed();
        });

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        ArrayList<QuizCategories> allCategories = new ArrayList<>();

        //firestore
        firestore.collection("quizWiz").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();

                            if (querySnapshot != null) {
                                int numberOfQuizzes = querySnapshot.size();
                                Log.d("Firestore", "Number of quizzes: " + numberOfQuizzes);

                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    // You can also access individual quiz data here if needed
                                    String quizId = document.getId();
                                    QuizCategories quiz = new QuizCategories();
                                    quiz.setName(quizId);
                                    allCategories.add(quiz);
                                }
                            } else {
                                Log.d("Firestore", "No quizzes found");
                            }
                            categories.setAdapter(new CategoriesAdapter(QuizCategoryActivity.this,allCategories));
                            categories.setLayoutManager(new GridLayoutManager(QuizCategoryActivity.this,3));
                        } else {
                            Toast.makeText(QuizCategoryActivity.this,"Error getting quizzes " + task.getException(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}