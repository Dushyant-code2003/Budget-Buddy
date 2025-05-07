package com.example.budgetbuddyorg;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteAccount extends AppCompatActivity {

    private EditText etPassword;
    private ImageView ivToggle;
    private boolean isPasswordVisible = false;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etPassword = findViewById(R.id.etPassword);
        ivToggle = findViewById(R.id.ivToggle);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnCancel = findViewById(R.id.btnCancel);

        ivToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggle.setImageResource(R.drawable.ic_eye_closed);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggle.setImageResource(R.drawable.ic_eye_open);
            }
            isPasswordVisible = !isPasswordVisible;
            etPassword.setSelection(etPassword.getText().length());
        });

        btnDelete.setOnClickListener(v -> {
            String password = etPassword.getText().toString().trim();
            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Account Deletion")
                    .setMessage("Are you absolutely sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Yes, Delete", (dialog, which) -> {
                        deleteAccount(password);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnCancel.setOnClickListener(v -> finish());

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

    private void deleteAccount(String password) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reauthenticate user before deletion
        user.reauthenticate(com.google.firebase.auth.EmailAuthProvider
                        .getCredential(user.getEmail(), password))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Delete user data from Firestore
                        String userId = user.getUid();
                        db.collection("users").document(userId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Delete user authentication
                                    user.delete()
                                            .addOnCompleteListener(deleteTask -> {
                                                if (deleteTask.isSuccessful()) {
                                                    // Clear local data and preferences
                                                    getSharedPreferences("BudgetBuddy", MODE_PRIVATE)
                                                            .edit()
                                                            .clear()
                                                            .apply();

                                                    // Sign out and redirect to login
                                                    mAuth.signOut();
                                                    Intent intent = new Intent(DeleteAccount.this, MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                    Toast.makeText(DeleteAccount.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(DeleteAccount.this, "Failed to delete account: " + deleteTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(DeleteAccount.this, "Failed to delete user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(DeleteAccount.this, "Invalid password", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
