package com.example.budgetbuddyorg;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class AddFingerprint extends AppCompatActivity {

    private LinearLayout fingerprintListLayout;
    private LinearLayout btnFingerprint;
    private int fingerprintCount = 0;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "FingerprintPrefs";
    private static final String COUNT_KEY = "FingerprintCount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint); // Replace with your actual layout name

        fingerprintListLayout = findViewById(R.id.fingerprintListLayout);
        btnFingerprint = findViewById(R.id.btnFingerprint);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        fingerprintCount = sharedPreferences.getInt(COUNT_KEY, 0);

        // Recreate saved fingerprints on app open
        for (int i = 1; i <= fingerprintCount; i++) {
            addNewFingerprintView("Fingerprint " + i, false);
        }

        btnFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerprintCount++;
                saveFingerprintCount(fingerprintCount);
                addNewFingerprintView("Fingerprint " + fingerprintCount, true);
            }
        });
    }

    private void addNewFingerprintView(String label, boolean animated) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setGravity(Gravity.CENTER_VERTICAL);
        itemLayout.setPadding(0, 24, 0, 24);

        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.fingerprint_icon); // Use your fingerprint icon
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
        arrow.setImageResource(R.drawable.ic_arrow_right); // Use your arrow icon
        LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(40, 40);
        arrow.setLayoutParams(arrowParams);

        itemLayout.addView(icon);
        itemLayout.addView(text);
        itemLayout.addView(arrow);

        // Insert before the Add A Fingerprint section
        int addButtonIndex = fingerprintListLayout.indexOfChild(btnFingerprint);
        fingerprintListLayout.addView(itemLayout, addButtonIndex);

        if (animated) {
            itemLayout.setAlpha(0f);
            itemLayout.animate().alpha(1f).setDuration(300).start();
        }
    }

    private void saveFingerprintCount(int count) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(COUNT_KEY, count);
        editor.apply();
    }
}
