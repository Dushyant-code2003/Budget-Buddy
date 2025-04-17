package com.example.budgetbuddyorg;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class otpverification extends AppCompatActivity {

    private EditText[] otpFields = new EditText[6];
    private Button btnAccept, btnSendAgain;
    private ImageView btnFacebook, btnGoogle;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        otpFields[0] = findViewById(R.id.otp1);
        otpFields[1] = findViewById(R.id.otp2);
        otpFields[2] = findViewById(R.id.otp3);
        otpFields[3] = findViewById(R.id.otp4);
        otpFields[4] = findViewById(R.id.otp5);
        otpFields[5] = findViewById(R.id.otp6);

        btnAccept = findViewById(R.id.btnAccept);
        btnSendAgain = findViewById(R.id.btnSendAgain);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnGoogle = findViewById(R.id.btnGoogle);

        // Get email from intent
        email = getIntent().getStringExtra("email");

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder otpCode = new StringBuilder();
                for (EditText otpField : otpFields) {
                    String digit = otpField.getText().toString().trim();
                    if (TextUtils.isEmpty(digit)) {
                        Toast.makeText(otpverification.this, "Enter complete OTP", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    otpCode.append(digit);
                }

                // Validate OTP and navigate to next step
                if (otpCode.toString().equals("273916")) { // Example OTP for testing
                    Toast.makeText(otpverification.this, "OTP Verified", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(otpverification.this, ResetPassword.class));
                } else {
                    Toast.makeText(otpverification.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSendAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(otpverification.this, "OTP Resent to " + email, Toast.LENGTH_SHORT).show();
            }
        });

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(otpverification.this, "Facebook login coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(otpverification.this, "Google login coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
