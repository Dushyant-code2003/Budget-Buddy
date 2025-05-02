package com.example.budgetbuddyorg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class AddFingerprint extends AppCompatActivity implements CustomFingerprintManager.FingerprintCallback {
    private LinearLayout fingerprintListLayout;
    private LinearLayout btnFingerprint;
    private int fingerprintCount = 0;
    private SharedPreferences sharedPreferences;
    private CustomFingerprintManager fingerprintManager;
    private Button btnSetupFingerprint;
    private TextView txtStatus;

    private static final String PREF_NAME = "FingerprintPrefs";
    private static final String COUNT_KEY = "FingerprintCount";
    private static final String FINGERPRINT_ENABLED = "FingerprintEnabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fingerprint);

        // Initialize all views
        initializeViews();

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        fingerprintCount = sharedPreferences.getInt(COUNT_KEY, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            fingerprintManager = new CustomFingerprintManager(this, this);
        }

        updateUI();
        setupClickListeners();
    }

    private void initializeViews() {
        fingerprintListLayout = findViewById(R.id.fingerprintListLayout);
        btnFingerprint = findViewById(R.id.btnFingerprint);
        btnSetupFingerprint = findViewById(R.id.btnSetupFingerprint);
        txtStatus = findViewById(R.id.txtStatus);

        // Verify all views are initialized
        if (fingerprintListLayout == null) {
            Toast.makeText(this, "Error: fingerprintListLayout not found", Toast.LENGTH_SHORT).show();
        }
        if (btnFingerprint == null) {
            Toast.makeText(this, "Error: btnFingerprint not found", Toast.LENGTH_SHORT).show();
        }
        if (btnSetupFingerprint == null) {
            Toast.makeText(this, "Error: btnSetupFingerprint not found", Toast.LENGTH_SHORT).show();
        }
        if (txtStatus == null) {
            Toast.makeText(this, "Error: txtStatus not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        if (fingerprintManager != null && fingerprintManager.isFingerprintAvailable()) {
            btnSetupFingerprint.setVisibility(View.GONE);
            txtStatus.setText("Fingerprint sensor is available");
            txtStatus.setTextColor(Color.GREEN);
        } else {
            btnSetupFingerprint.setVisibility(View.VISIBLE);
            txtStatus.setText("Please set up fingerprint in system settings");
            txtStatus.setTextColor(Color.RED);
        }

        // Clear existing views except the "Add Fingerprint" button
        fingerprintListLayout.removeAllViews();

        // Add saved fingerprints
        for (int i = 1; i <= fingerprintCount; i++) {
            addNewFingerprintView("Fingerprint " + i, false);
        }
    }

    private void setupClickListeners() {
        btnFingerprint.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (fingerprintManager.isFingerprintAvailable()) {
                    txtStatus.setText("Place your finger on the sensor");
                    txtStatus.setTextColor(Color.BLUE);
                    fingerprintManager.authenticate();
                } else {
                    showFingerprintSetupDialog();
                }
            } else {
                Toast.makeText(this, "Fingerprint authentication not supported on this device", Toast.LENGTH_SHORT).show();
            }
        });

        btnSetupFingerprint.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            startActivity(intent);
        });
    }

    private void showFingerprintSetupDialog() {
        Toast.makeText(this, "Please set up fingerprint in system settings first", Toast.LENGTH_LONG).show();
        btnSetupFingerprint.setVisibility(View.VISIBLE);
    }

    private void addNewFingerprintView(String label, boolean animated) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setGravity(Gravity.CENTER_VERTICAL);
        itemLayout.setPadding(0, 24, 0, 24);

        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.fingerprint_icon);
        icon.setColorFilter(ContextCompat.getColor(this, R.color.blue));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(60, 60);
        iconParams.setMargins(0, 0, 24, 0);
        icon.setLayoutParams(iconParams);

        TextView text = new TextView(this);
        text.setText(label);
        text.setTextSize(16);
        text.setTextColor(Color.BLACK);
        text.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        ImageView arrow = new ImageView(this);
        arrow.setImageResource(R.drawable.ic_arrow_right);
        LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(40, 40);
        arrow.setLayoutParams(arrowParams);

        itemLayout.addView(icon);
        itemLayout.addView(text);
        itemLayout.addView(arrow);

        fingerprintListLayout.addView(itemLayout);

        if (animated) {
            itemLayout.setAlpha(0f);
            itemLayout.animate().alpha(1f).setDuration(300).start();
        }
    }

    private void saveFingerprintCount(int count) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(COUNT_KEY, count);
        editor.putBoolean(FINGERPRINT_ENABLED, true);
        editor.apply();
    }

    @Override
    public void onAuthenticationSucceeded() {
        fingerprintCount++;
        saveFingerprintCount(fingerprintCount);
        addNewFingerprintView("Fingerprint " + fingerprintCount, true);
        txtStatus.setText("Fingerprint added successfully!");
        txtStatus.setTextColor(Color.GREEN);
        Toast.makeText(this, "Fingerprint added successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationFailed() {
        txtStatus.setText("Authentication failed. Please try again.");
        txtStatus.setTextColor(Color.RED);
        Toast.makeText(this, "Fingerprint authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        txtStatus.setText("Authentication Error: " + errString);
        txtStatus.setTextColor(Color.RED);
        Toast.makeText(this, "Authentication Error: " + errString, Toast.LENGTH_SHORT).show();
        if (errorCode == -1) {
            showFingerprintSetupDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fingerprintManager != null) {
            fingerprintManager.cancelAuthentication();
        }
    }
}