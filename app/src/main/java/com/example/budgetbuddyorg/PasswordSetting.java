package com.example.budgetbuddyorg;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PasswordSetting extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private ImageView ivToggleCurrent, ivToggleNew, ivToggleConfirm;
    private boolean isCurrentVisible = false, isNewVisible = false, isConfirmVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_setting);

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        ivToggleCurrent = findViewById(R.id.ivToggleCurrent);
        ivToggleNew = findViewById(R.id.ivToggleNew);
        ivToggleConfirm = findViewById(R.id.ivToggleConfirm);

        ivToggleCurrent.setOnClickListener(v -> togglePasswordVisibility(etCurrentPassword, ivToggleCurrent, isCurrentVisible = !isCurrentVisible));
        ivToggleNew.setOnClickListener(v -> togglePasswordVisibility(etNewPassword, ivToggleNew, isNewVisible = !isNewVisible));
        ivToggleConfirm.setOnClickListener(v -> togglePasswordVisibility(etConfirmPassword, ivToggleConfirm, isConfirmVisible = !isConfirmVisible));

        findViewById(R.id.btnChangePassword).setOnClickListener(v -> {
            String current = etCurrentPassword.getText().toString();
            String newPass = etNewPassword.getText().toString();
            String confirm = etConfirmPassword.getText().toString();

            if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else if (!newPass.equals(confirm)) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                // Implement your secure password change logic here (e.g. Firebase, SQLite, SharedPreferences)
                Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void togglePasswordVisibility(EditText editText, ImageView icon, boolean visible) {
        if (visible) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        editText.setSelection(editText.getText().length());
    }
}
