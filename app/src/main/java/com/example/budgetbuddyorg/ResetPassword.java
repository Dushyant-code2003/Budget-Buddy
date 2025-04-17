package com.example.budgetbuddyorg;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ResetPassword extends AppCompatActivity {

    private EditText etNewPassword, etConfirmPassword;
    private ImageView ivToggleNewPassword, ivToggleConfirmPassword;
    private Button btnChangePassword;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Initializing views
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
//        ivToggleNewPassword = findViewById(R.id.ivToggleNewPassword);
//        ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        // Toggle password visibility for New Password
        ivToggleNewPassword.setOnClickListener(view -> {
            isNewPasswordVisible = !isNewPasswordVisible;
            togglePasswordVisibility(etNewPassword, ivToggleNewPassword, isNewPasswordVisible);
        });

        // Toggle password visibility for Confirm Password
        ivToggleConfirmPassword.setOnClickListener(view -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            togglePasswordVisibility(etConfirmPassword, ivToggleConfirmPassword, isConfirmPasswordVisible);
        });

        // Handle password change
        btnChangePassword.setOnClickListener(view -> {
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(ResetPassword.this, "Please enter both fields", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(ResetPassword.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed with password change logic (e.g., API call)
                Toast.makeText(ResetPassword.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                // Finish the activity or navigate to login screen
                finish();
            }
        });
    }

    private void togglePasswordVisibility(EditText editText, ImageView toggleIcon, boolean isVisible) {
        if (isVisible) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_open);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_closed);
        }
        editText.setSelection(editText.getText().length()); // Keep cursor at the end
    }
}
