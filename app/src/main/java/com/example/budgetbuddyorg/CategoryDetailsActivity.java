package com.example.budgetbuddyorg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CategoryDetailsActivity extends AppCompatActivity {
    private ImageView btnBack, ivNotification;
    private TextView tvCategoryTitle, tvBalanceAmount, tvExpenseAmount, tvProgressDescription;
    private TextView tvMonth;
    private ImageButton btnCalendar;
    private Button btnAddExpense;
    private ProgressBar progressBar;
    private RecyclerView rvTransactions;
    private BottomNavigationView bottomNav;
    private SharedPreferences sharedPreferences;
    private List<Transaction> transactions;
    private TransactionAdapter transactionAdapter;
    private String categoryName;
    private double totalBalance = 0.0;
    private double categoryExpenses = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        // Get category name from intent
        categoryName = getIntent().getStringExtra("category_name");
        if (categoryName == null) {
            categoryName = "Category";
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);

        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        setupBottomNavigation();
        loadTransactions();
        updateUI();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        ivNotification = findViewById(R.id.ivNotification);
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        tvBalanceAmount = findViewById(R.id.tvBalanceAmount);
        tvExpenseAmount = findViewById(R.id.tvExpenseAmount);
        tvProgressDescription = findViewById(R.id.tvProgressDescription);
        progressBar = findViewById(R.id.progressBar);
        tvMonth = findViewById(R.id.tvMonth);
        btnCalendar = findViewById(R.id.btnCalendar);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        rvTransactions = findViewById(R.id.rvTransactions);
        bottomNav = findViewById(R.id.bottomNav);

        // Set category title
        tvCategoryTitle.setText(categoryName);

        // Set current month
        tvMonth.setText(new SimpleDateFormat("MMMM", Locale.getDefault()).format(Calendar.getInstance().getTime()));
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationSetting.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddExpenseActivity.class);
            intent.putExtra("category_name", categoryName);
            startActivityForResult(intent, 1);
            overridePendingTransition(0, 0);
        });

        btnCalendar.setOnClickListener(v -> {
            // TODO: Implement calendar picker
        });
    }

    private void setupRecyclerView() {
        transactions = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(transactionAdapter);
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, SecondActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_stat) {
                startActivity(new Intent(this, StatsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_transfer) {
                startActivity(new Intent(this, TransferActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_category) {
                startActivity(new Intent(this, CategoriesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void loadTransactions() {
        // Load all transactions from SharedPreferences
        String transactionsJson = sharedPreferences.getString("transactions", "[]");
        List<Transaction> allTransactions = new Gson().fromJson(transactionsJson,
                new TypeToken<List<Transaction>>(){}.getType());

        // Filter transactions for current category
        transactions.clear();
        categoryExpenses = 0.0;
        for (Transaction transaction : allTransactions) {
            if (transaction.getCategory() != null && transaction.getCategory().equals(categoryName)) {
                transactions.add(transaction);
                categoryExpenses += transaction.getNumericAmount();
            }
        }

        transactionAdapter.notifyDataSetChanged();
    }

    private void updateUI() {
        // Get total balance from SharedPreferences
        totalBalance = sharedPreferences.getFloat("total_balance", 0.0f);

        // Get category-specific expenses
        String categoryExpensesKey = "category_expenses_" + categoryName;
        categoryExpenses = sharedPreferences.getFloat(categoryExpensesKey, 0.0f);

        // Update balance display
        tvBalanceAmount.setText(String.format("$%.2f", totalBalance));

        // Update category expenses
        tvExpenseAmount.setText(String.format("-$%.2f", categoryExpenses));

        // Calculate and update progress
        int expensePercentage = 0;
        if (totalBalance > 0) {
            expensePercentage = (int) ((categoryExpenses / totalBalance) * 100);
        }

        progressBar.setProgress(expensePercentage);
        tvProgressDescription.setText(expensePercentage + "% of your expenses, " +
                (expensePercentage < 50 ? "Looks Good!" : "Consider reducing expenses"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                float newExpense = data.getFloatExtra("new_expense", 0.0f);
                String expenseCategory = data.getStringExtra("category_name");

                if (expenseCategory != null && expenseCategory.equals(categoryName)) {
                    // Reload transactions and update UI
                    loadTransactions();
                    updateUI();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions();
        updateUI();
    }
}