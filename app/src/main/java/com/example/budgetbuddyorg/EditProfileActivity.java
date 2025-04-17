package com.example.budgetbuddyorg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView ivProfileImage, ivCamera;
    private EditText etUsername, etPhone, etEmail;
    private SwitchCompat switchPush, switchDarkTheme;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        ivProfileImage.setImageURI(selectedImageUri);
                    } else {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        ivProfileImage.setImageBitmap(imageBitmap);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Views
        ivProfileImage = findViewById(R.id.ivProfilePic);
        ivCamera = findViewById(R.id.ivCameraIcon);
        etUsername = findViewById(R.id.etUsername);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        switchPush = findViewById(R.id.switchPush);
        switchDarkTheme = findViewById(R.id.switchDarkTheme);

        // Camera icon click to pick profile picture
        ivCamera.setOnClickListener(v -> openImagePicker());

        // Push Notification Toggle
        switchPush.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Push Notifications Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Push Notifications Disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // Dark Theme Toggle
        switchDarkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Update Profile button
        findViewById(R.id.btnUpdate).setOnClickListener(v -> saveProfile());
    }

    private void openImagePicker() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooser = Intent.createChooser(pickIntent, "Select Profile Image");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        imagePickerLauncher.launch(chooser);
    }

    private void saveProfile() {
        String username = etUsername.getText().toString();
        String phone = etPhone.getText().toString();
        String email = etEmail.getText().toString();

        // You can use SharedPreferences, SQLite, or send to server here
        Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
    }
}
