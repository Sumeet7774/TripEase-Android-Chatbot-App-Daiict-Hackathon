package com.example.tripease;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    SessionManagement sessionManagement;
    private static final int SPLASH_SCREEN_TIMEOUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        sessionManagement = new SessionManagement(this);

        new Handler().postDelayed(() -> {
            // Check if user_id is present in the session
            if (sessionManagement.getUserId().isEmpty()) {
                // If no session, navigate to IndexPage (login/signup)
                Intent intent = new Intent(SplashScreen.this, IndexPage.class);
                startActivity(intent);
            } else {
                // If session exists, navigate to HomeScreen
                Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
                startActivity(intent);
            }

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }, SPLASH_SCREEN_TIMEOUT);
    }
}
