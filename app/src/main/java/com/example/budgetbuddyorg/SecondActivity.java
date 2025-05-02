package com.example.budgetbuddyorg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {
    private TextView tvBalanceAmount;
    private TextView tvExpenseAmount;
    private RecyclerView rvTransactions;
    private BottomNavigationView bottomNav;
    private TabLayout tabLayout;
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
        updateBalance();
    }

    private void initializeViews() {
        tvBalanceAmount = findViewById(R.id.tvBalanceAmount);
        tvExpenseAmount = findViewById(R.id.tvExpenseAmount);
        rvTransactions = findViewById(R.id.rvTransactions);
        tabLayout = findViewById(R.id.tabLayout);
        bottomNav = findViewById(R.id.bottomNav);
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
        transactionAdapter.updateTransactions(transactions);
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
    }
}