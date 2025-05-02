package com.example.budgetbuddyorg;

import android.content.Context;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;

public class CustomFingerprintManager {
    private Context context;
    private Executor executor;
    private CancellationSignal cancellationSignal;
    private FingerprintCallback callback;
    private android.hardware.fingerprint.FingerprintManager fingerprintManager;

    public interface FingerprintCallback {
        void onAuthenticationSucceeded();
        void onAuthenticationFailed();
        void onAuthenticationError(int errorCode, CharSequence errString);
    }

    public CustomFingerprintManager(Context context, FingerprintCallback callback) {
        this.context = context;
        this.callback = callback;
        this.executor = ContextCompat.getMainExecutor(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.fingerprintManager = context.getSystemService(android.hardware.fingerprint.FingerprintManager.class);
        }
    }

    public boolean isFingerprintAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return fingerprintManager != null &&
                    fingerprintManager.isHardwareDetected() &&
                    fingerprintManager.hasEnrolledFingerprints();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void authenticate() {
        if (!isFingerprintAvailable()) {
            callback.onAuthenticationError(-1, "Fingerprint authentication not available");
            return;
        }

        BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(context)
                .setTitle("Fingerprint Authentication")
                .setSubtitle("Place your finger on the sensor")
                .setNegativeButton("Cancel", executor, (dialogInterface, i) -> {
                    Toast.makeText(context, "Authentication cancelled", Toast.LENGTH_SHORT).show();
                    callback.onAuthenticationError(i, "Authentication cancelled");
                })
                .build();

        biometricPrompt.authenticate(getCancellationSignal(), executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                callback.onAuthenticationSucceeded();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                callback.onAuthenticationFailed();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                callback.onAuthenticationError(errorCode, errString);
            }
        });
    }

    private CancellationSignal getCancellationSignal() {
        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(() -> {
            Toast.makeText(context, "Authentication cancelled", Toast.LENGTH_SHORT).show();
        });
        return cancellationSignal;
    }

    public void cancelAuthentication() {
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
    }
}