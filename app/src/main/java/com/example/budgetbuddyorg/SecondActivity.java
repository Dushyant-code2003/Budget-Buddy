package com.example.budgetbuddyorg;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {
    private TextView tvBalanceAmount, tvExpenseAmount, tvProgressDescription;
    private ProgressBar progressBar;
    private RecyclerView rvTransactions;
    private BottomNavigationView bottomNav;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;

    private double totalBalance = 7783.00;  // Initial balance
    private double totalExpenses = 0.00;    // Auto-calculated expenses

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Initialize UI Components
        tvBalanceAmount = findViewById(R.id.tvBalanceAmount);
        tvExpenseAmount = findViewById(R.id.tvExpenseAmount);
        tvProgressDescription = findViewById(R.id.tvProgressDescription);
        progressBar = findViewById(R.id.progressBar);
        rvTransactions = findViewById(R.id.rvTransactions);
        bottomNav = findViewById(R.id.bottomNav);

        // Load Transactions & Update UI
        transactionList = new ArrayList<>();
        loadTransactions();
        updateUI();

        // Setup RecyclerView
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(transactionList);
        rvTransactions.setAdapter(transactionAdapter);

        // Handle Bottom Navigation Clicks
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) return true;
            else if (itemId == R.id.nav_stat) {
                startActivity(new Intent(SecondActivity.this, StatsActivity.class));
                return true;
            } else if (itemId == R.id.nav_transfer) {
                startActivity(new Intent(SecondActivity.this, TransferActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(SecondActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    // Load transactions dynamically
    private void loadTransactions() {
        transactionList.add(new Transaction("Salary", "April 30", "$4,000.00", true));
        transactionList.add(new Transaction("Groceries", "April 24", "-$100.00", false));
        transactionList.add(new Transaction("Rent", "April 15", "-$674.40", false));

        // Calculate expenses dynamically
        for (Transaction transaction : transactionList) {
            if (!transaction.isIncome()) {
                totalExpenses += Math.abs(Double.parseDouble(transaction.getAmount().replace("$", "").replace("-", "")));
            }
        }
    }

    // Update UI elements dynamically
    private void updateUI() {
        tvBalanceAmount.setText("$" + String.format("%.2f", totalBalance));
        tvExpenseAmount.setText("- $" + String.format("%.2f", totalExpenses));

        int expensePercentage = (int) ((totalExpenses / totalBalance) * 100);
        progressBar.setProgress(expensePercentage);
        tvProgressDescription.setText(expensePercentage + "% of your expenses, Looks Good!");
    }
}
