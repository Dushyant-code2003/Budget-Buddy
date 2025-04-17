package com.example.budgetbuddyorg;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Terms_and_Conditions extends AppCompatActivity {

    private CheckBox checkboxAgree;
    private Button btnAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        checkboxAgree = findViewById(R.id.checkboxAgree);
        btnAccept = findViewById(R.id.btnAccept);
        btnAccept.setEnabled(false);

        checkboxAgree.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnAccept.setEnabled(isChecked);
        });

        btnAccept.setOnClickListener(v -> {
            Toast.makeText(this, "You accepted the terms.", Toast.LENGTH_SHORT).show();
            finish(); // or navigate to next screen
        });

        findViewById(R.id.ivBack).setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

    }
}
