package com.example.budgetbuddyorg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    ImageView ivBack, ivBell, ivNotification, ivPassword, ivDeleteac;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ivBack = findViewById(R.id.ivBack);
        ivBell = findViewById(R.id.ivBell);
        ivNotification = findViewById(R.id.ivNotification);
        ivPassword = findViewById(R.id.ivPassword);
        ivDeleteac = findViewById(R.id.ivDeleteac);


        // Back button listener
        ivBack.setOnClickListener(v -> finish());

        // Bell icon listener (optional)
        ivBell.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show();
        });


        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, NotificationSetting.class);
            startActivity(intent);
        });

        ivPassword.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, PasswordSetting.class);
            startActivity(intent);
        });

        ivDeleteac.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, DeleteAccount.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile); // current screen

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(SettingsActivity.this, SecondActivity.class));
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.nav_stat) {
                startActivity(new Intent(SettingsActivity.this, StatsActivity.class));
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.nav_transfer) {
                startActivity(new Intent(SettingsActivity.this, TransferActivity.class));
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.nav_category) {
                startActivity(new Intent(SettingsActivity.this, CategoriesActivity.class));
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.nav_profile) {
                return true;
            }

            return false;
        });
    }
    }
