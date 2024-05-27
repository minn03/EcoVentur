package com.example.ecoventur.ui.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecoventur.MainActivity;
import com.example.ecoventur.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterPageActivity extends AppCompatActivity {
    EditText Email, Password, ConfirmPassword, Username, PhoneNumber;
    TextView Login;
    Button Register;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressBar progressBar;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        mAuth = FirebaseAuth.getInstance();
        Username = findViewById(R.id.registerUsername);
        PhoneNumber = findViewById(R.id.phone);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.password);
        ConfirmPassword = findViewById(R.id.ConfirmPassword);
        Login = findViewById(R.id.ToLogin);
        Register = findViewById(R.id.BtnRegister);
        progressBar = findViewById(R.id.progressBar);
        db = FirebaseFirestore.getInstance();

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    progressBar.setVisibility(View.VISIBLE);
                    String username = Username.getText().toString();
                    String phoneNumber = PhoneNumber.getText().toString();
                    String email = Email.getText().toString().trim();
                    String pass = Password.getText().toString().trim();
                    String confirmPass = ConfirmPassword.getText().toString();

                    if(TextUtils.isEmpty(username)){
                        Toast.makeText(RegisterPageActivity.this,"Enter Username", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }

                    if(TextUtils.isEmpty(email)){
                        Toast.makeText(RegisterPageActivity.this,"Enter Email", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    } else if(!email.contains("@")){
                        Toast.makeText(RegisterPageActivity.this,"Invalid Email", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }

                    if(TextUtils.isEmpty(phoneNumber)){
                        Toast.makeText(RegisterPageActivity.this,"Enter Phone Number", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }else if(!phoneNumber.startsWith("01")|| phoneNumber.length()<10 || phoneNumber.length()>11){
                        Toast.makeText(RegisterPageActivity.this,"Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    }

                    if(TextUtils.isEmpty(pass)){
                        Toast.makeText(RegisterPageActivity.this,"Enter Password", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }else if (pass.length() < 6) {
                        Toast.makeText(RegisterPageActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    } else if(!containsAlphabet(pass) || !containsSymbol(pass)||!containsNumber(pass)) {
                        Toast.makeText(RegisterPageActivity.this, "Password must contain at least one alphabet and one symbol", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    if(TextUtils.isEmpty(confirmPass)){
                        Toast.makeText(RegisterPageActivity.this,"Enter Reconfirm Password", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }

                    if(!pass.equals(confirmPass)){
                        Toast.makeText(RegisterPageActivity.this,"Enter Same Reconfirm Password", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }

                    mAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {

                                        userID = task.getResult().getUser().getUid();
                                        DocumentReference docRef = db.collection("users").document(userID);
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("username",username);
                                        user.put("phone", phoneNumber);
                                        user.put("email", email);
                                        user.put("ecocoin", 500);
                                        Toast.makeText(RegisterPageActivity.this, "Account created.",
                                                Toast.LENGTH_SHORT).show();
                                        docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                initializeUserFirestoreStructure(docRef);
                                                Toast.makeText(RegisterPageActivity.this, "Profile created", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(RegisterPageActivity.this, "Failed to create profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        // If register fails, display a message to the user.
                                        Toast.makeText(RegisterPageActivity.this, "Failed to create profile.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterPageActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void initializeUserFirestoreStructure(DocumentReference userRef) {
        List<String> collectionsToCreate = Arrays.asList(
                "activeVoucher", "challenging", "completed", "earning",
                "eventsWishlist", "quizWizResult", "spending", "transitAwards"
        );

        for (String collection: collectionsToCreate) {
            CollectionReference collectionRef = userRef.collection(collection);
            collectionRef.add(new HashMap<>())
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterPageActivity.this, "Failed to initialize Firestore structure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private boolean containsAlphabet(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsNumber(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSymbol(String password) {
        for (char c : password.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return true;
            }
        }
        return false;
    }
}