package com.example.budgetbuddyorg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class Security extends AppCompatActivity {

    private ImageView ivBack;
    private ImageView ivArrowChangePin, ivArrowFingerprint, ivArrowTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        // Back button
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        // Arrow buttons
        ivArrowChangePin = findViewById(R.id.ivArrowChangePin);
        ivArrowFingerprint = findViewById(R.id.ivArrowFingerprint);
        ivArrowTerms = findViewById(R.id.ivArrowTerms);

        ivArrowChangePin.setOnClickListener(v -> {
            Intent intent = new Intent(Security.this, ChangePinActivity.class);
            startActivity(intent);
        });

        ivArrowFingerprint.setOnClickListener(v -> {
            Intent intent = new Intent(Security.this, FingerprintActivity.class);
            startActivity(intent);
        });

        ivArrowTerms.setOnClickListener(v -> {
            Intent intent = new Intent(Security.this, Terms_and_Conditions.class);
            startActivity(intent);
        });
    }
}
