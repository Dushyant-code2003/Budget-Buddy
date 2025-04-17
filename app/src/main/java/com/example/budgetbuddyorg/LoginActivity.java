package com.example.budgetbuddyorg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnSignup;
    private ImageView btnFacebook, btnGoogle;

    private TextView tvForgotPassword, tvFingerprint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvFingerprint = findViewById(R.id.tvFingerprint);

        // Login Button Click
        btnLogin.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Here you can add login logic (Firebase, Database, etc.)
                startActivity(new Intent(LoginActivity.this, SecondActivity.class));
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            }
        });

        // Signup Button Click
        btnSignup.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, Signup.class);
            startActivity(intent);
        });

        // Facebook Login Click
        btnFacebook.setOnClickListener(view ->
                Toast.makeText(LoginActivity.this, "Facebook Login Clicked", Toast.LENGTH_SHORT).show()
        );

        // Google Login Click
        btnGoogle.setOnClickListener(view ->
                Toast.makeText(LoginActivity.this, "Google Login Clicked", Toast.LENGTH_SHORT).show()
        );
    }
}
