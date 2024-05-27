package com.example.ecoventur.ui.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ecoventur.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotPasswordPageActivity extends AppCompatActivity {
    Button BtnReset;
    EditText Email;
    String strEmail;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_page);

        ImageView back = findViewById(R.id.back);
        BtnReset = findViewById(R.id.BtnReset);
        Email = findViewById(R.id.Username2);
        progressBar = findViewById(R.id.progressBara004);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Email.setText(currentUser.getEmail());}

        BtnReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                strEmail = Email.getText().toString().trim();
                if(!TextUtils.isEmpty(strEmail)){
                    ResetPassword();
                } else{
                    Toast.makeText(ForgotPasswordPageActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void ResetPassword(){
        progressBar.setVisibility(View.VISIBLE);
        BtnReset.setVisibility(View.INVISIBLE);
        mAuth.sendPasswordResetEmail(strEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ForgotPasswordPageActivity.this, "Reset password link has been send", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotPasswordPageActivity.this, "Error", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                BtnReset.setVisibility(View.VISIBLE);
            }
        });
    }
}