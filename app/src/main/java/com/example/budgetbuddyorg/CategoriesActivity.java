package com.example.budgetbuddyorg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CategoriesActivity extends AppCompatActivity implements View.OnClickListener {
    private BottomNavigationView bottomNav;
    private TextView tvBalanceAmount, tvExpenseAmount, tvProgressDescription;
    private ProgressBar progressBar;
    private ImageView ivNotification, ivBack;
    private SharedPreferences sharedPreferences;
    private double totalBalance = 0.0;
    private double totalExpenses = 0.0;
    private LinearLayout foodLayout, transportLayout, medicineLayout, groceriesLayout;
    private LinearLayout rentLayout, giftsLayout, savingsLayout, entertainmentLayout, moreLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categories);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);

        // Initialize views
        initializeViews();
        setupClickListeners();

        // Initialize bottom navigation
        setupBottomNavigation();

        // Update UI with current balance and expenses
        updateUI();
    }

    private void initializeViews() {
        // Header views
        ivBack = findViewById(R.id.ivBack);
        ivNotification = findViewById(R.id.ivNotification);

        // Balance and expense views
        tvBalanceAmount = findViewById(R.id.tvBalanceAmount);
        tvExpenseAmount = findViewById(R.id.tvExpenseAmount);
        tvProgressDescription = findViewById(R.id.tvProgressDescription);
        progressBar = findViewById(R.id.progressBar);

        // Category layouts
        foodLayout = findViewById(R.id.foodLayout);
        transportLayout = findViewById(R.id.transportLayout);
        medicineLayout = findViewById(R.id.medicineLayout);
        groceriesLayout = findViewById(R.id.Groceries);
        rentLayout = findViewById(R.id.Rent);
        giftsLayout = findViewById(R.id.Gifts);
        savingsLayout = findViewById(R.id.Savings);
        entertainmentLayout = findViewById(R.id.Entertainment);
        moreLayout = findViewById(R.id.More);

        // Navigation
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_category);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(CategoriesActivity.this, NotificationSetting.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // Set click listeners for category layouts
        foodLayout.setOnClickListener(this);
        transportLayout.setOnClickListener(this);
        medicineLayout.setOnClickListener(this);
        groceriesLayout.setOnClickListener(this);
        rentLayout.setOnClickListener(this);
        giftsLayout.setOnClickListener(this);
        savingsLayout.setOnClickListener(this);
        entertainmentLayout.setOnClickListener(this);
        moreLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String categoryName = "";
        int id = v.getId();

        if (id == R.id.foodLayout) {
            categoryName = "Food";
        } else if (id == R.id.transportLayout) {
            categoryName = "Transport";
        } else if (id == R.id.medicineLayout) {
            categoryName = "Medicine";
        } else if (id == R.id.Groceries) {
            categoryName = "Groceries";
        } else if (id == R.id.Rent) {
            categoryName = "Rent";
        } else if (id == R.id.Gifts) {
            categoryName = "Gifts";
        } else if (id == R.id.Savings) {
            categoryName = "Savings";
        } else if (id == R.id.Entertainment) {
            categoryName = "Entertainment";
        } else if (id == R.id.More) {
            // TODO: Handle more options
            return;
        }

        if (!categoryName.isEmpty()) {
            Intent intent = new Intent(this, CategoryDetailsActivity.class);
            intent.putExtra("category_name", categoryName);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(CategoriesActivity.this, SecondActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_stat) {
                startActivity(new Intent(CategoriesActivity.this, StatsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_transfer) {
                startActivity(new Intent(CategoriesActivity.this, TransferActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_category) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(CategoriesActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                float newExpense = data.getFloatExtra("new_expense", 0.0f);
                updateUI();
            }
        }
    }

    private void updateUI() {
        try {
            // Get balance from SharedPreferences
            totalBalance = sharedPreferences.getFloat("total_balance", 0.0f);
            totalExpenses = sharedPreferences.getFloat("total_expenses", 0.0f);

            // Update balance display
            if (tvBalanceAmount != null) {
                tvBalanceAmount.setText(String.format("$%.2f", totalBalance));
            }

            // Update expense display
            if (tvExpenseAmount != null) {
                tvExpenseAmount.setText(String.format("-$%.2f", totalExpenses));
            }

            // Calculate and update progress
            int expensePercentage = 0;
            if (totalBalance > 0) {
                expensePercentage = (int) ((totalExpenses / totalBalance) * 100);
            }

            if (progressBar != null) {
                progressBar.setProgress(expensePercentage);
            }

            if (tvProgressDescription != null) {
                tvProgressDescription.setText(expensePercentage + "% of your expenses, " +
                        (expensePercentage < 50 ? "Looks Good!" : "Consider reducing expenses"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}