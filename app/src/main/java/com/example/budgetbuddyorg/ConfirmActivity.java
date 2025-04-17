package com.example.budgetbuddyorg;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class ConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        // Delay for 2 seconds and navigate to LoginActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(ConfirmActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close this activity
        }, 2000); // 2000ms = 2 seconds
    }
}
