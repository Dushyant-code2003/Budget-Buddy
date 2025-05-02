package com.example.budgetbuddyorg;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView userName, userId;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        profileImage = findViewById(R.id.profileImage);
        userName = findViewById(R.id.userName);
        userId = findViewById(R.id.userId);

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Set up menu item clicks
        setupMenuItems();

        // Set up bottom navigation
        setupBottomNavigation();

        // Load user data
        loadUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload user data when returning to this activity
        loadUserData();
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
                                // Get username
                                String username = snapshot.child("username").getValue(String.class);
                                if (username != null) {
                                    userName.setText(username);
                                }

                                // Get and load profile image
                                String imageData = snapshot.child("profileImage").getValue(String.class);
                                if (imageData != null && !imageData.isEmpty()) {
                                    if (imageData.startsWith("data:image")) {
                                        // Load Base64 image
                                        Glide.with(ProfileActivity.this)
                                                .load(imageData)
                                                .placeholder(R.drawable.default_profile)
                                                .into(profileImage);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
        }
    }

    private void setupMenuItems() {
        // Edit Profile
        LinearLayout editProfileButton = findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });

        // Security
        LinearLayout securityButton = findViewById(R.id.securityButton);
        securityButton.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, Security.class));
        });

        // Settings
        LinearLayout settingButton = findViewById(R.id.settingButton);
        settingButton.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
        });

        // Help
        LinearLayout helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, Terms_and_Conditions.class));
        });

        // Logout
        LinearLayout logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            // Handle logout
            // Clear user session/data
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_stat) {
                    startActivity(new Intent(ProfileActivity.this, StatsActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_transfer) {
                    startActivity(new Intent(ProfileActivity.this, TransferActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_category) {
                    startActivity(new Intent(ProfileActivity.this, CategoriesActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    return true;
                }
                return false;
            }
        });
    }
}
