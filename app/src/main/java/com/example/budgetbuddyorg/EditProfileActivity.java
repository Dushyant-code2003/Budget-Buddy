package com.example.budgetbuddyorg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImage;
    private TextView userName, userId;
    private EditText usernameEdit, phoneEdit, emailEdit;
    private SwitchCompat notificationSwitch, darkThemeSwitch;
    private MaterialButton updateProfileButton;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeFirebase();
        initializeViews();
        loadUserData();
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void initializeViews() {
        // Initialize profile section views
        profileImage = findViewById(R.id.profileImage);
        userName = findViewById(R.id.userName);
        userId = findViewById(R.id.userId);

        // Initialize edit text fields
        usernameEdit = findViewById(R.id.usernameEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        emailEdit = findViewById(R.id.emailEdit);

        // Initialize switches
        notificationSwitch = findViewById(R.id.notificationSwitch);
        darkThemeSwitch = findViewById(R.id.darkThemeSwitch);

        // Initialize buttons
        updateProfileButton = findViewById(R.id.updateProfileButton);
        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton cameraButton = findViewById(R.id.cameraButton);

        // Set click listeners
        backButton.setOnClickListener(v -> finish());
        cameraButton.setOnClickListener(v -> openImagePicker());
        updateProfileButton.setOnClickListener(v -> updateProfile());

        // Set switch listeners
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateNotificationSetting(isChecked);
        });

        darkThemeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateDarkThemeSetting(isChecked);
        });
    }

    private void updateNotificationSetting(boolean enabled) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mDatabase.child("users").child(user.getUid())
                    .child("notifications_enabled").setValue(enabled);
        }
    }

    private void updateDarkThemeSetting(boolean enabled) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mDatabase.child("users").child(user.getUid())
                    .child("dark_theme_enabled").setValue(enabled);
        }
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            userId.setText("ID: " + uid);

            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String username = snapshot.child("username").getValue(String.class);
                                String phone = snapshot.child("phone").getValue(String.class);
                                String email = snapshot.child("email").getValue(String.class);
                                String imageUrl = snapshot.child("profileImage").getValue(String.class);
                                Boolean notifications = snapshot.child("notifications_enabled").getValue(Boolean.class);
                                Boolean darkTheme = snapshot.child("dark_theme_enabled").getValue(Boolean.class);

                                // Set the data to views
                                if (username != null) {
                                    userName.setText(username);
                                    usernameEdit.setText(username);
                                }
                                if (phone != null) phoneEdit.setText(phone);
                                if (email != null) emailEdit.setText(email);
                                if (notifications != null) notificationSwitch.setChecked(notifications);
                                if (darkTheme != null) darkThemeSwitch.setChecked(darkTheme);

                                // Load profile image
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Glide.with(EditProfileActivity.this)
                                            .load(imageUrl)
                                            .placeholder(R.drawable.default_profile)
                                            .into(profileImage);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(EditProfileActivity.this,
                                    "Failed to load user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.default_profile)
                    .into(profileImage);
        }
    }

    private void updateProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        String username = usernameEdit.getText().toString().trim();

        // Validate username
        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading state
        updateProfileButton.setEnabled(false);
        updateProfileButton.setText("Updating...");

        // Update profile data
        DatabaseReference userRef = mDatabase.child("users").child(uid);

        if (imageUri != null) {
            try {
                // Convert image to Base64
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                // Compress and resize image to reduce size
                Bitmap resizedImage = Bitmap.createScaledBitmap(selectedImage, 300, 300, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resizedImage.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageData = baos.toByteArray();

                // Convert to Base64 string
                String base64Image = Base64.encodeToString(imageData, Base64.DEFAULT);

                // Update database with image and username
                userRef.child("username").setValue(username);
                userRef.child("profileImage").setValue("data:image/jpeg;base64," + base64Image)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditProfileActivity.this,
                                    "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            updateProfileButton.setEnabled(true);
                            updateProfileButton.setText("Update Profile");
                            Toast.makeText(EditProfileActivity.this,
                                    "Failed to update profile", Toast.LENGTH_SHORT).show();
                        });

            } catch (FileNotFoundException e) {
                updateProfileButton.setEnabled(true);
                updateProfileButton.setText("Update Profile");
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update only username if no new image
            userRef.child("username").setValue(username)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditProfileActivity.this,
                                "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        updateProfileButton.setEnabled(true);
                        updateProfileButton.setText("Update Profile");
                        Toast.makeText(EditProfileActivity.this,
                                "Failed to update profile", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
