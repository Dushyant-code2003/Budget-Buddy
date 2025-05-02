package com.example.budgetbuddyorg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SetBalance extends AppCompatActivity {
    private EditText etBalance;
    private Button btnSaveBalance;
    private ImageView ivNotification;
    private BottomNavigationView bottomNav;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_balance);

        // Initialize views
        etBalance = findViewById(R.id.etBalance);
        btnSaveBalance = findViewById(R.id.btnSaveBalance);
        ivNotification = findViewById(R.id.ivNotification);
        bottomNav = findViewById(R.id.bottomNav);
        sharedPreferences = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);

        // Set current balance in EditText
        float currentBalance = sharedPreferences.getFloat("total_balance", 0.0f);
        etBalance.setText(String.format("%.2f", currentBalance));

        // Notification icon click listener
        ivNotification.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show();
        });

        // Save balance button click listener
        btnSaveBalance.setOnClickListener(v -> {
            String balanceStr = etBalance.getText().toString();
            if (!balanceStr.isEmpty()) {
                try {
                    float balance = Float.parseFloat(balanceStr);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("total_balance", balance);
                    editor.apply();

                    // Notify other activities about the balance change
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("new_balance", balance);
                    setResult(RESULT_OK, resultIntent);

                    // Update UI immediately
                    updateUI();

                    finish();
                } catch (NumberFormatException e) {
                    Toast.makeText(SetBalance.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SetBalance.this, "Please enter a balance", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup Bottom Navigation
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(SetBalance.this, SecondActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_stat) {
                startActivity(new Intent(SetBalance.this, StatsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_transfer) {
                startActivity(new Intent(SetBalance.this, TransferActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_category) {
                startActivity(new Intent(SetBalance.this, CategoriesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(SetBalance.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void updateUI() {
        float balance = sharedPreferences.getFloat("total_balance", 0.0f);
        etBalance.setText(String.format("%.2f", balance));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}