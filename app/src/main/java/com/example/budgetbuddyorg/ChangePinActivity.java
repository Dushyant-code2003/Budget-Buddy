package com.example.budgetbuddyorg;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChangePinActivity extends AppCompatActivity {

    EditText etCurrentPin, etNewPin, etConfirmPin;
    ImageView ivToggleCurrent, ivToggleNew, ivToggleConfirm;
    Button btnChangePin;
    boolean isCurrentVisible = false;
    boolean isNewVisible = false;
    boolean isConfirmVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);

        // Find views
        etCurrentPin = findViewById(R.id.etCurrentPin);
        etNewPin = findViewById(R.id.etNewPin);
        etConfirmPin = findViewById(R.id.etConfirmPin);

        ivToggleCurrent = findViewById(R.id.ivToggle1);
        ivToggleNew = findViewById(R.id.ivToggle2);
        ivToggleConfirm = findViewById(R.id.ivToggle3);


        btnChangePin = findViewById(R.id.btnChangePin);

        // Toggle current pin visibility
        ivToggleCurrent.setOnClickListener(v -> {
            isCurrentVisible = !isCurrentVisible;
            togglePasswordVisibility(etCurrentPin, ivToggleCurrent, isCurrentVisible);
        });

        // Toggle new pin visibility
        ivToggleNew.setOnClickListener(v -> {
            isNewVisible = !isNewVisible;
            togglePasswordVisibility(etNewPin, ivToggleNew, isNewVisible);
        });

        // Toggle confirm pin visibility
        ivToggleConfirm.setOnClickListener(v -> {
            isConfirmVisible = !isConfirmVisible;
            togglePasswordVisibility(etConfirmPin, ivToggleConfirm, isConfirmVisible);
        });

        // Change PIN button
        btnChangePin.setOnClickListener(v -> {
            String current = etCurrentPin.getText().toString();
            String newPin = etNewPin.getText().toString();
            String confirm = etConfirmPin.getText().toString();

            if (current.isEmpty() || newPin.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPin.equals(confirm)) {
                Toast.makeText(this, "New PIN and Confirm PIN do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update logic placeholder
            Toast.makeText(this, "PIN changed successfully", Toast.LENGTH_SHORT).show();
        });

        // Back button
        findViewById(R.id.ivBack).setOnClickListener(v -> onBackPressed());

        // Setup Bottom Nav
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_profile); // set current tab

//        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.nav_home:
//                        startActivity(new Intent(ChangePinActivity.this, LoginActivity.class));
//                        overridePendingTransition(0, 0);
//                        return true;
//                    case R.id.nav_stats:
//                        startActivity(new Intent(ChangePinActivity.this, StatsActivity.class));
//                        overridePendingTransition(0, 0);
//                        return true;
//                    case R.id.nav_profile:
//                        startActivity(new Intent(ChangePinActivity.this, ProfileActivity.class));
//                        overridePendingTransition(0, 0);
//                        return true;
//                }
//                return false;
//            }
//        });
    }

    private void togglePasswordVisibility(EditText editText, ImageView toggleIcon, boolean visible) {
        if (visible) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            toggleIcon.setImageResource(R.drawable.ic_eye_open); // replace with your open-eye icon
        } else {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_closed); // replace with your closed-eye icon
        }
        editText.setSelection(editText.getText().length());
    }
}
