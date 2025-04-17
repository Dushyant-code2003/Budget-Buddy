package com.example.budgetbuddyorg;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationSetting extends AppCompatActivity {

    Switch switchGeneral, switchSound, switchSoundCall, switchVibrate,
            switchTransaction, switchReminder, switchBudget, switchLowBalance;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);

        sharedPreferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);

        switchGeneral = findViewById(R.id.switchGeneral);
        switchSound = findViewById(R.id.switchSound);
        switchSoundCall = findViewById(R.id.switchSoundCall);
        switchVibrate = findViewById(R.id.switchVibrate);
        switchTransaction = findViewById(R.id.switchTransaction);
        switchReminder = findViewById(R.id.switchReminder);
        switchBudget = findViewById(R.id.switchBudget);
        switchLowBalance = findViewById(R.id.switchLowBalance);

        loadSwitchStates();

        setToggle(switchGeneral, "general");
        setToggle(switchSound, "sound");
        setToggle(switchSoundCall, "soundCall");
        setToggle(switchVibrate, "vibrate");
        setToggle(switchTransaction, "transaction");
        setToggle(switchReminder, "reminder");
        setToggle(switchBudget, "budget");
        setToggle(switchLowBalance, "lowBalance");

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
    }

    private void loadSwitchStates() {
        switchGeneral.setChecked(sharedPreferences.getBoolean("general", true));
        switchSound.setChecked(sharedPreferences.getBoolean("sound", true));
        switchSoundCall.setChecked(sharedPreferences.getBoolean("soundCall", true));
        switchVibrate.setChecked(sharedPreferences.getBoolean("vibrate", true));
        switchTransaction.setChecked(sharedPreferences.getBoolean("transaction", false));
        switchReminder.setChecked(sharedPreferences.getBoolean("reminder", false));
        switchBudget.setChecked(sharedPreferences.getBoolean("budget", false));
        switchLowBalance.setChecked(sharedPreferences.getBoolean("lowBalance", false));
    }

    private void setToggle(Switch toggle, String key) {
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(key, isChecked).apply();
        });
    }
}
