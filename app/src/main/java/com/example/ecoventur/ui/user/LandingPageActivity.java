package com.example.ecoventur.ui.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ecoventur.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LandingPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null){
            goToLogin();
        } else {
            setContentView(R.layout.activity_landing_page);
            Button wellogin = findViewById(R.id.welLogin);
            wellogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToLogin();
                }
            });
        }

    }
    private void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginPageActivity.class);
        startActivity(intent);
        finish();
    }
    private boolean isLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return preferences.getBoolean("isLoggedIn", false);
    }
}