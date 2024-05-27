package com.example.ecoventur.ui.ecoeducation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecoventur.R;
import com.example.ecoventur.ui.ecoeducation.database.TinyDB;
import com.example.ecoventur.ui.ecoeducation.dialogs.BadgeDialog;
import com.example.ecoventur.ui.ecoeducation.models.Badges;
import com.example.ecoventur.ui.ecoeducation.models.Quiz;
import com.example.ecoventur.ui.ecoeducation.models.QuizResult;
import com.example.ecoventur.ui.greenspace.Callback;
import com.example.ecoventur.ui.greenspace.ecoCoinsManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class QuizActivity extends AppCompatActivity {

    String category = "";
    TextView question;
    ArrayList<Quiz> quizes;
    int currentQuiz;
    int totalCorrect;
    Quiz currentQuestion;
    RadioGroup radioGroup;
    int awardedCoins;
    String uid;
    static int totalCorrectToday;
    MaterialRadioButton a, b, c, d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        question = findViewById(R.id.question);
        radioGroup = findViewById(R.id.radioGroup);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        a = findViewById(R.id.a);
        b = findViewById(R.id.b);
        c = findViewById(R.id.c);
        d = findViewById(R.id.d);

        // Setting up a click listener for the "back" button
        ImageView back = findViewById(R.id.back);
        totalCorrectToday = 0;
        quizes = new ArrayList<>();
        TinyDB db = new TinyDB(this);
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        back.setOnClickListener(view->{
            super.onBackPressed();
        });

        // Retrieving the total number of correct answers for the current week from TinyDB
        int totalFetch = db.getInt(String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)));
        if(totalFetch == 0){
            totalCorrect = 0;
        }else{
            totalCorrect = totalFetch;
        }
        // Setting up click listeners for answer buttons (a, b, c, d)
        a.setOnClickListener(view->{
            check();
        });
        b.setOnClickListener(view->{
            check();
        });
        c.setOnClickListener(view->{
            check();
        });
        d.setOnClickListener(view->{
            check();
        });
        // Initializing variables and retrieving the quiz category from the intent
        currentQuiz = 0;
        category = getIntent().getStringExtra("category");
        // Checking if the quiz has already been attempted by the user
        isDone();

        // Retrieving quiz questions from Firestore
        firestore.collection("quizWiz").document(category).collection("questions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Access each question document and its data
                                String questionId = document.getId();
                                Quiz quiz = document.toObject(Quiz.class);
                                quizes.add(quiz);
                            }
                            showNextQuestion();
                        } else {
                            Toast.makeText(QuizActivity.this,"Error getting questions: "+ task.getException(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
        // Setting up a listener for radio button changes in the radio group
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            }
        });
    }
    private static String getEcoCoins(){
        int coins = 0;
        int factor = 100;
        coins = totalCorrectToday * factor;
        return String.valueOf(coins);
    }
    private void isDone(){
        // Checking if the user has already attempted the quiz
        CollectionReference userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(uid).collection("quizWizResult");
        String pathToCheck = category+"Result";
        // Construct the full path
        DocumentReference fullPathRef = userDocumentRef.document(pathToCheck);

        // Check if the document at the specified path exists
        fullPathRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Displaying a dialog if the quiz has already been attempted
                                new MaterialAlertDialogBuilder(QuizActivity.this)
                                        .setTitle("Quiz")
                                        .setMessage("Quiz already attempted")
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            // Positive button clicked, call super.onBackPressed()
                                            onBackPressed();
                                        })
                                        .show();
                            }
                        } else {
                            // Handle the error
                            Log.e("Quiz",task.getException().getLocalizedMessage());
                        }
                    }
                });
    }
    private void setResult(){
        // Create a new QuizResult object
        QuizResult quizResultData = new QuizResult();
        // Set properties of the QuizResult object
        quizResultData.setTitle(category);
        quizResultData.setTimestamp(String.valueOf(totalCorrectToday));
        quizResultData.setEcoCoins(getEcoCoins());

        // Assuming getEcoCoins() returns a String
        String ecoCoinsString = getEcoCoins();

        // Convert the String to int
        int ecoCoinsValue;
        try {
            ecoCoinsValue = Integer.parseInt(ecoCoinsString);
        }
        catch (NumberFormatException e) {
            // Handle the case where the String cannot be parsed as an integer
            e.printStackTrace();// or log the error, show a message, etc.
            return;// Exit the method if conversion fails
        }

        // Obtain the user's UID
        String quiz = category;

        // Access Firestore to store the QuizResult data in "earning" collection using ecoCoinsManager
        ecoCoinsManager.addEcoCoins(uid, quizResultData.getTitle(), ecoCoinsValue, new Callback() {
            @Override
            public void onDataLoaded(Object data) {
                // Handle success (if needed)
                System.out.println("EcoCoins added successfully. Updated EcoCoins: " + data);

                // Access Firestore to store the QuizResult data in "quizWizResult" collection
                CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("users");
                DocumentReference quiz1ResultRef = usersCollection.document(uid)
                        .collection("quizWizResult")
                        .document(quiz + "Result");

                quiz1ResultRef.set(quizResultData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Handle success (if needed)
                                } else {
                                    Log.e("QuizWizResult", Objects.requireNonNull(task.getException().getLocalizedMessage()));
                                }
                            }
                        });
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure
                System.out.println("Failed to add EcoCoins: " + e.getMessage());
            }
        });
    }
    private void check(){
        String answer = "";
        currentQuestion = quizes.get(currentQuiz);
        if(currentQuestion.getAnswer().equals("a")){
            answer = currentQuestion.getA();
        }else if(currentQuestion.getAnswer().equals("b")){
            answer = currentQuestion.getB();
        }
        else if(currentQuestion.getAnswer().equals("c")){
            answer = currentQuestion.getC();
        }
        else if(currentQuestion.getAnswer().equals("d")){
            answer = currentQuestion.getD();
        }
        // Check the selected answer and update totalCorrectToday
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.a) {
            if(Objects.equals(quizes.get(currentQuiz).getAnswer(), "a")){
                totalCorrect++;
                totalCorrectToday++;
            }else{
                showMessage(currentQuestion.getQuestion(), "Answer: " + answer);
            }
        } else if (checkedId == R.id.b) {
            if(Objects.equals(quizes.get(currentQuiz).getAnswer(), "b")){
                totalCorrect++;
                totalCorrectToday++;
            }else{
                showMessage(currentQuestion.getQuestion(), "Answer: " + answer);
            }
        } else if (checkedId == R.id.c) {
            if(Objects.equals(quizes.get(currentQuiz).getAnswer(), "c")){
                totalCorrect++;
                totalCorrectToday++;
            }else{
                showMessage(currentQuestion.getQuestion(), "Answer: " + answer);
            }
        } else if (checkedId == R.id.d) {
            if(Objects.equals(quizes.get(currentQuiz).getAnswer(), "d")){
                totalCorrect++;
                totalCorrectToday++;
            }else{
                showMessage(currentQuestion.getQuestion(), "Answer: " + answer);
            }
        }
        currentQuiz ++;
        setProgress();
        if(currentQuiz == quizes.size()){
            TinyDB db = new TinyDB(this);
            String desc = "";
            String comp = "0";
            int weekNumber = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
            if(!db.getString("completed-"+weekNumber).equals("")){
                comp = db.getString("completed-"+weekNumber);
            }
            int totalThisWeek = Integer.parseInt(comp);
            //final question answered , disable the buttons
            a.setEnabled(false);
            b.setEnabled(false);
            c.setEnabled(false);
            d.setEnabled(false);
            //update the coins
            setResult();
            int totalCompletedThisWeek = totalThisWeek;
            if(totalCorrectToday >= quizes.size()){
                //save that this week completed
                totalCompletedThisWeek ++;
                db.putString("completed-"+weekNumber, String.valueOf(totalCompletedThisWeek));
            }
            if(totalCompletedThisWeek > 7){
                desc = "We have awarded you  a Green Guardian Badge for successfully completing more than 7 Quizzes this week" + getEcoCoins() +" Coins for answering " + totalCorrectToday +" Questions correctly";
                BadgeDialog dialog = new BadgeDialog(QuizActivity.this,desc);
                dialog.show();

                updateFirebase();
            }else{
                if(totalCorrectToday > 0){
                    desc = "You have successfully answered " + totalCorrectToday + " Correctly. Answer more next time to get your badge. You have been rewarded " + getEcoCoins() +" Coins for answering " + totalCorrectToday + " Questions correctly";
                    BadgeDialog dialog = new BadgeDialog(QuizActivity.this,desc);
                    dialog.show();
                }else{
                    desc = "You seem to not have gotten luck this time. Better luck next time!";
                    BadgeDialog dialog = new BadgeDialog(QuizActivity.this,desc);
                    dialog.show();
                }
            }
        }
        showNextQuestion();

    }
    private void showNextQuestion(){
        Toast.makeText(this,String.valueOf(currentQuiz) +":" + String.valueOf(quizes.size()),Toast.LENGTH_LONG).show();
        if(currentQuiz < quizes.size()){
            MaterialRadioButton a = findViewById(R.id.a);
            MaterialRadioButton b = findViewById(R.id.b);
            MaterialRadioButton c = findViewById(R.id.c);
            MaterialRadioButton d = findViewById(R.id.d);

            Quiz now = quizes.get(currentQuiz);
            question.setText(now.getQuestion());
            a.setText(now.getA());
            b.setText(now.getB());
            c.setText(now.getC());
            d.setText(now.getD());



        }

    }
    private void updateFirebase(){
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Badges badge = new Badges();
        badge.setDate(Calendar.getInstance().getTime().toString());
        badge.setName("Green Guardian");
        badge.setImage("https://previews.123rf.com/images/arcady31/arcady311812/arcady31181200155/114186175-green-badge-icon.jpg");
        badge.setDescription("Having completed 8+ quizes in a week");
        String badgeId = generateRandomGUID();
        badge.setId(badgeId);

        CollectionReference badgesCollection = FirebaseFirestore.getInstance().collection("badges");
        DocumentReference badgeRef = badgesCollection.document(uid).collection("userBadges").document(badgeId);

        badgeRef.set(badge)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(QuizActivity.this,"Badge Saved",Toast.LENGTH_LONG).show();
                        } else {
                            // Handle the error
                            Log.e("Firestore Badge", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                        }
                    }
                });
    }

    private void showMessage(String title, String message){
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    showNextQuestion();
                })
                .show();
    }

//    private void showMessage(String title, String message) {
//        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
//        builder.setTitle(title);
//
//        // Create a TextView with adjusted layout parameters
//        TextView textView = new TextView(this);
//        textView.setText(message);
//        textView.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//        ));
//
//        // Add padding to the TextView if needed
//        int paddingInDp = 16; // adjust as needed
//        float scale = getResources().getDisplayMetrics().density;
//        int paddingInPixels = (int) (paddingInDp * scale + 0.5f);
//        textView.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels);
//
//        builder.setView(textView);
//
//        builder.setPositiveButton("OK", (dialog, which) -> {
//            // Handle positive button click if needed
//        });
//
//        builder.show();
//    }


    private void setProgress(){
        //get the week number then display badges
        TinyDB db = new TinyDB(this);
        int weekNumber = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        db.putInt(String.valueOf(weekNumber),totalCorrect);
    }
    public static String generateRandomGUID() {
        UUID uuid = UUID.randomUUID();
        String fullUUID = uuid.toString();
        return fullUUID.substring(0, 12);
    }
}