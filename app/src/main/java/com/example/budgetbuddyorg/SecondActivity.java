package com.example.budgetbuddyorg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SecondActivity extends AppCompatActivity {
    private TextView tvBalanceAmount;
    private TextView tvExpenseAmount ,tvProgressDescription;
    private RecyclerView rvTransactions;
    private BottomNavigationView bottomNav;
    private TabLayout tabLayout;

    private ProgressBar progressBar;
    private TransactionAdapter transactionAdapter;
    private SharedPreferences sharedPreferences;
    private List<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        sharedPreferences = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);
        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        setupTabLayout();
        setupBottomNavigation();
        updateui();
        updateBalance();
    }

    private void initializeViews() {
        tvBalanceAmount = findViewById(R.id.tvBalanceAmount);
        tvExpenseAmount = findViewById(R.id.tvExpenseAmount);
        rvTransactions = findViewById(R.id.rvTransactions);
        tabLayout = findViewById(R.id.tabLayout);
        bottomNav = findViewById(R.id.bottomNav);
        progressBar = findViewById(R.id.progressBar);
        tvProgressDescription = findViewById(R.id.tvProgressDescription);
    }

    private void setupClickListeners() {
        tvBalanceAmount.setOnClickListener(v -> {
            Intent intent = new Intent(SecondActivity.this, SetBalance.class);
            startActivityForResult(intent, 1);
        });
    }

    private void setupRecyclerView() {
        transactions = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(transactionAdapter);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterTransactions(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_stat) {
                startActivity(new Intent(SecondActivity.this, StatsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_transfer) {
                startActivity(new Intent(SecondActivity.this, TransferActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_category) {
                startActivity(new Intent(SecondActivity.this, CategoriesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(SecondActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });

        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void filterTransactions(int position) {
        List<Transaction> filtered = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        for (Transaction t : transactions) {
            Calendar transCal = Calendar.getInstance();
            // Parse date from transaction (format: "HH:mm - MMM dd")
            try {
                String[] parts = t.getDate().split(" - ");
                if (parts.length == 2) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault());
                    java.util.Date dateObj = sdf.parse(parts[1]);
                    if (dateObj != null) {
                        transCal.setTime(dateObj);
                        transCal.set(Calendar.YEAR, now.get(Calendar.YEAR)); // Assume current year
                    }
                }
            } catch (Exception ignored) {}
            boolean add = false;
            if (position == 0) { // Daily
                add = now.get(Calendar.YEAR) == transCal.get(Calendar.YEAR) &&
                        now.get(Calendar.DAY_OF_YEAR) == transCal.get(Calendar.DAY_OF_YEAR);
            } else if (position == 1) { // Weekly
                add = now.get(Calendar.YEAR) == transCal.get(Calendar.YEAR) &&
                        now.get(Calendar.WEEK_OF_YEAR) == transCal.get(Calendar.WEEK_OF_YEAR);
            } else if (position == 2) { // Monthly
                add = now.get(Calendar.YEAR) == transCal.get(Calendar.YEAR) &&
                        now.get(Calendar.MONTH) == transCal.get(Calendar.MONTH);
            }
            if (add) filtered.add(t);
        }
        transactionAdapter.updateTransactions(filtered);
    }


    private void updateui() {
        // Get balance from SharedPreferences
        float balance = sharedPreferences.getFloat("total_balance", 0.0f);
        tvBalanceAmount.setText(String.format("$%.2f", balance));

        // Get total expenses from SharedPreferences
        float totalExpenses = sharedPreferences.getFloat("total_expenses", 0.0f);
        tvExpenseAmount.setText(String.format("-$%.2f", totalExpenses));

        // Save expense to SharedPreferences for other activities
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("total_expenses", totalExpenses);
        editor.apply();

// Calculate and update progress
        int expensePercentage = 0;
        if (balance > 0) {
            expensePercentage = (int) ((totalExpenses / balance) * 100);
        }

        if (progressBar != null) {
            progressBar.setProgress(expensePercentage);
        }

        if (tvProgressDescription != null) {
            tvProgressDescription.setText(expensePercentage + "% of your expenses, " +
                    (expensePercentage < 50 ? "Looks Good!" : "Consider reducing expenses"));
        }

    }
    private void updateBalance() {
        // Get balance from SharedPreferences
        float balance = sharedPreferences.getFloat("total_balance", 0.0f);
        tvBalanceAmount.setText(String.format("$%.2f", balance));

        // Get total expenses from SharedPreferences
        float totalExpenses = sharedPreferences.getFloat("total_expenses", 0.0f);
        tvExpenseAmount.setText(String.format("-$%.2f", totalExpenses));

        // Save expense to SharedPreferences for other activities
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("total_expenses", totalExpenses);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            float newBalance = data.getFloatExtra("new_balance", 0.0f);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("total_balance", newBalance);
            editor.apply();
            updateBalance();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBalance();
        loadTransactions();
        // Default to current tab or first tab
        int selectedTab = tabLayout.getSelectedTabPosition();
        if (selectedTab == -1) selectedTab = 0;
        filterTransactions(selectedTab);
    }

    private void loadTransactions() {
        String transactionsJson = sharedPreferences.getString("transactions", "[]");
        transactions = new Gson().fromJson(transactionsJson,
                new TypeToken<List<Transaction>>(){}.getType());
        if (transactions == null) transactions = new ArrayList<>();
    }
}