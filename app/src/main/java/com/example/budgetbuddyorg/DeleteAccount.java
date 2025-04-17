package com.example.budgetbuddyorg;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DeleteAccount extends AppCompatActivity {

    private EditText etPassword;
    private ImageView ivToggle;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        etPassword = findViewById(R.id.etPassword);
        ivToggle = findViewById(R.id.ivToggle);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnCancel = findViewById(R.id.btnCancel);

        ivToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggle.setImageResource(R.drawable.ic_eye_closed); // closed
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggle.setImageResource(R.drawable.ic_eye_open); // open
            }
            isPasswordVisible = !isPasswordVisible;
            etPassword.setSelection(etPassword.getText().length());
        });

        btnDelete.setOnClickListener(v -> {
            String password = etPassword.getText().toString().trim();
            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed with account deletion (Firebase/SQLite logic here)
                Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> {
            finish(); // Close activity
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(DeleteAccount.this, SecondActivity.class));
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.nav_stat) {
                startActivity(new Intent(DeleteAccount.this, StatsActivity.class));
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.nav_transfer) {
                startActivity(new Intent(DeleteAccount.this, TransferActivity.class));
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.nav_category) {
                startActivity(new Intent(DeleteAccount.this, CategoriesActivity.class));
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.nav_profile) {
                return true;
            }

            return false;
        });
    }
}
