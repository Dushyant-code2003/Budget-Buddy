package com.example.budgetbuddyorg;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText etEmail;
    private Button btnNextStep, btnSignUp;
    private TextView tvSignUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.etEmail);
        btnNextStep = findViewById(R.id.btnNextStep);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvSignUp = findViewById(R.id.tvSignUp);
    }

    private void setupClickListeners() {
        btnNextStep.setOnClickListener(v -> resetPassword());

        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPassword.this, Signup.class));
            finish();
        });

        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPassword.this, Signup.class));
            finish();
        });
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            return;
        }

        btnNextStep.setEnabled(false);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    btnNextStep.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPassword.this,
                                "Password reset email sent. Please check your inbox.",
                                Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ForgotPassword.this,
                                "Failed to send reset email: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
