package com.example.budgetbuddyorg;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class ConfirmAnimationActivity extends AppCompatActivity {

    private static final int DELAY_MILLIS = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_animation);

        // Auto-redirect after animation
        new Handler().postDelayed(() -> {
            // Example: go back to home or profile
            Intent intent = new Intent(ConfirmAnimationActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish(); // so the user doesn't return here on back press
        }, DELAY_MILLIS);
    }
}
