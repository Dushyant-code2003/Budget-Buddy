package com.example.budgetbuddyorg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class FingerprintActivity extends AppCompatActivity {

    private CancellationSignal cancellationSignal;
    private Executor executor;

    private static final String PREF_NAME = "AppPrefs";
    private static final String FINGERPRINT_ENABLED_KEY = "fingerprint_enabled";

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        Button btnUseFingerprint = findViewById(R.id.btnUseFingerprint);
        TextView tvInstruction = findViewById(R.id.tvInstruction);

        executor = ContextCompat.getMainExecutor(this);

        // Check if fingerprint authentication is enabled
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isFingerprintEnabled = prefs.getBoolean(FINGERPRINT_ENABLED_KEY, false);

        // If fingerprint authentication is disabled, show alternative instruction
        if (isFingerprintEnabled) {
            tvInstruction.setText("Use your fingerprint to unlock the app.");
            btnUseFingerprint.setOnClickListener(v -> authenticateUser());
        } else {
            // You can provide an option for PIN code, or direct to another screen
            tvInstruction.setText("Fingerprint authentication is not set up.");
            btnUseFingerprint.setText("Set up Fingerprint");
            btnUseFingerprint.setOnClickListener(v -> {
                // Navigate to fingerprint setup activity (if not enabled)
                startActivity(new Intent(FingerprintActivity.this, AddFingerprint.class));
                finish();
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void authenticateUser() {
        BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(this)
                .setTitle("Fingerprint Authentication")
                .setSubtitle("Use your fingerprint to access")
                .setNegativeButton("Cancel", executor, (dialogInterface, i) ->
                        Toast.makeText(FingerprintActivity.this, "Authentication Cancelled", Toast.LENGTH_SHORT).show()
                ).build();

        biometricPrompt.authenticate(getCancellationSignal(), executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(FingerprintActivity.this, "Authentication Successful", Toast.LENGTH_SHORT).show();

                // Navigate to the next activity (e.g., DashboardActivity)
                startActivity(new Intent(FingerprintActivity.this, SecondActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(FingerprintActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private CancellationSignal getCancellationSignal() {
        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(() ->
                Toast.makeText(FingerprintActivity.this, "Authentication Cancelled", Toast.LENGTH_SHORT).show()
        );
        return cancellationSignal;
    }
}
