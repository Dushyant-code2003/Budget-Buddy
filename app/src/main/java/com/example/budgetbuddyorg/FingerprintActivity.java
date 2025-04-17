package com.example.budgetbuddyorg;

import android.content.Context;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class FingerprintActivity extends AppCompatActivity {

    private CancellationSignal cancellationSignal;
    private Executor executor;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        Button btnUseFingerprint = findViewById(R.id.btnUseFingerprint);

        executor = ContextCompat.getMainExecutor(this);

        btnUseFingerprint.setOnClickListener(v -> authenticateUser());
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
