package com.example.budgetbuddyorg;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.view.View;
import androidx.cardview.widget.CardView;

public class TransferActivity extends AppCompatActivity {
    private TextView tvTotalBalance, tvIncomeAmount, tvExpenseAmount;
    private RecyclerView rvTransfers;
    private ImageButton btnBack, btnNotification;
    private CardView incomeCard;

    private BottomNavigationView bottomNav;
    private TransferListAdapter adapter;
    private final List<Transaction> transfers = new ArrayList<>();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private Map<String, List<Transaction>> transactionsByMonth = new HashMap<>();
    private static final int EXPENSE_DETAILS_REQUEST_CODE = 1001;
    private SharedPreferences sharedPreferences;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm - MMM dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        sharedPreferences = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);
        initializeViews();
        setupBottomNavigation();
        setupClickListeners();
        setupRecyclerView();
        loadTransfers();
    }

    private void initializeViews() {
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvIncomeAmount = findViewById(R.id.tvIncomeAmount);
        tvExpenseAmount = findViewById(R.id.tvExpenseAmount);
        rvTransfers = findViewById(R.id.rvTransfers);
        btnBack = findViewById(R.id.btnBack);
        btnNotification = findViewById(R.id.btnNotification);
        bottomNav = findViewById(R.id.bottomNav);
        incomeCard = findViewById(R.id.incomeCard);
    }


    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(TransferActivity.this, SecondActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_stat) {
                startActivity(new Intent(TransferActivity.this, StatsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_transfer) {
                return true;
            } else if (itemId == R.id.nav_category) {
                startActivity(new Intent(TransferActivity.this, CategoriesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(TransferActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }


    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnNotification.setOnClickListener(v -> {
            // Handle notification click
        });

        // Add click listener for expense section
        tvExpenseAmount.setOnClickListener(v -> {
            openExpenseDetails(tvTotalBalance.getText().toString(), tvExpenseAmount.getText().toString());
        });

        bottomNav.setSelectedItemId(R.id.nav_transfer);
        bottomNav.setOnItemSelectedListener(item -> {
            // Handle navigation
            return true;
        });

        // Setup income section click
        incomeCard.setOnClickListener(v -> showAddIncomeDialog());
    }

    private void setupRecyclerView() {
        adapter = new TransferListAdapter();
        rvTransfers.setLayoutManager(new LinearLayoutManager(this));
        rvTransfers.setAdapter(adapter);
    }

    private void loadTransfers() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) return;

        // Get values from SharedPreferences
        float totalBalance = sharedPreferences.getFloat("total_balance", 0.0f);
        float totalExpenses = sharedPreferences.getFloat("total_expenses", 0.0f);

        // Update UI with values from SharedPreferences
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        tvTotalBalance.setText(formatter.format(totalBalance));
        tvExpenseAmount.setText(formatter.format(totalExpenses));

        // Retrieve actual income stored separately
        float totalIncome = sharedPreferences.getFloat("total_income", 0.0f);
        tvIncomeAmount.setText(formatter.format(totalIncome));


        firestore.collection("transactions")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        return;
                    }

                    transfers.clear();
                    transactionsByMonth.clear();

                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (var doc : snapshot.getDocuments()) {
                            Transaction transaction = doc.toObject(Transaction.class);
                            if (transaction != null) {
                                transfers.add(transaction);

                                // Group by month
                                String month = transaction.getDate().split(" - ")[1];
                                List<Transaction> monthTransactions = transactionsByMonth.get(month);
                                if (monthTransactions == null) {
                                    monthTransactions = new ArrayList<>();
                                    transactionsByMonth.put(month, monthTransactions);
                                }
                                monthTransactions.add(transaction);
                            }
                        }
                    }

                    adapter.setTransactions(transactionsByMonth);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransfers();
    }

    private void openExpenseDetails(String totalBalance, String totalExpense) {
        Intent intent = new Intent(TransferActivity.this, ExpenseDetailsActivity.class);
        intent.putExtra("total_balance", totalBalance);
        intent.putExtra("total_expense", totalExpense);
        startActivity(intent);
    }

    private void showAddIncomeDialog() {
        AddIncomeDialog dialog = new AddIncomeDialog();
        dialog.setOnIncomeAddedListener((title, category, amount, date) -> {
            // Create new transaction
            Transaction transaction = new Transaction(title, dateFormatter.format(date),
                    String.format(Locale.US, "$%.2f", amount), true);
            transaction.setCategory(category);

            // Save to SharedPreferences
            saveTransaction(transaction);
        });
        dialog.show(getSupportFragmentManager(), "AddIncome");
    }

    private void saveTransaction(Transaction transaction) {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) return;

        // Get current balance and expenses
        float currentBalance = sharedPreferences.getFloat("total_balance", 0.0f);
        float currentExpenses = sharedPreferences.getFloat("total_expenses", 0.0f);
        float transactionAmount = transaction.getNumericAmount();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (transaction.isIncome()) {
            float newBalance = currentBalance + transactionAmount;
            float currentIncome = sharedPreferences.getFloat("total_income", 0.0f);
            float newIncome = currentIncome + transactionAmount;
            editor.putFloat("total_balance", newBalance);
            editor.putFloat("total_income", newIncome);


        } else {
            // For expenses, update both balance and total expenses
            float newBalance = currentBalance - transactionAmount;
            float newExpenses = currentExpenses + transactionAmount;
            editor.putFloat("total_balance", newBalance);
            editor.putFloat("total_expenses", newExpenses);
        }
        editor.apply();

        // Add userId to transaction
        transaction.setUserId(userId);

        // Save transaction to Firestore
        firestore.collection("transactions")
                .add(transaction)
                .addOnSuccessListener(documentReference -> {
                    // Transaction saved successfully
                    loadTransfers();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save transaction", Toast.LENGTH_SHORT).show();
                });
    }
}