package com.example.budgetbuddyorg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExpenseDetailsActivity extends AppCompatActivity {
    private TextView tvTotalExpense;
    private TextView tvTotalBalance;
    private RecyclerView rvExpenses;
    private ImageButton btnBack, btnNotification;
    private BottomNavigationView bottomNav;
    private TransferListAdapter adapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private SharedPreferences sharedPreferences;
    private double currentTotalExpense = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);

        sharedPreferences = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);
        initializeViews();
        setupClickListeners();
        setupRecyclerView();

        // Get values passed from TransferActivity
        String totalBalance = getIntent().getStringExtra("total_balance");
        String totalExpense = getIntent().getStringExtra("total_expense");

        // Set the values
        if (totalBalance != null) {
            tvTotalBalance.setText(totalBalance);
        }
        if (totalExpense != null) {
            tvTotalExpense.setText(totalExpense);
        }

        loadExpenses();
    }

    private void initializeViews() {
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        rvExpenses = findViewById(R.id.rvExpenses);
        btnBack = findViewById(R.id.btnBack);
        btnNotification = findViewById(R.id.btnNotification);
        bottomNav = findViewById(R.id.bottomNav);

        // Load and display saved total expense
        float savedTotalExpense = sharedPreferences.getFloat("total_expenses", 0.0f);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        tvTotalExpense.setText(formatter.format(savedTotalExpense));
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnNotification.setOnClickListener(v -> {
            // Handle notification click
        });

        bottomNav.setSelectedItemId(R.id.nav_transfer);
        bottomNav.setOnItemSelectedListener(item -> {
            // Handle navigation
            return true;
        });
    }

    private void setupRecyclerView() {
        adapter = new TransferListAdapter();
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        rvExpenses.setAdapter(adapter);
    }

    private void loadExpenses() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) return;

        db.collection("transactions")
                .whereEqualTo("userId", userId)
                .whereEqualTo("isIncome", false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    List<Transaction> expenses = new ArrayList<>();

                    if (value != null) {
                        for (var document : value) {
                            Transaction transaction = document.toObject(Transaction.class);
                            if (transaction != null) {
                                expenses.add(transaction);
                            }
                        }
                    }

                    // Group expenses by category
                    Map<String, List<Transaction>> expensesByCategory = new HashMap<>();
                    for (Transaction expense : expenses) {
                        String category = expense.getCategory() != null ? expense.getCategory() : "Uncategorized";
                        if (!expensesByCategory.containsKey(category)) {
                            expensesByCategory.put(category, new ArrayList<>());
                        }
                        expensesByCategory.get(category).add(expense);
                    }

                    adapter.setTransactions(expensesByCategory);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }

    @Override
    public void finish() {
        // Send back updated values to TransferActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_total_balance", tvTotalBalance.getText().toString());
        resultIntent.putExtra("updated_total_expense", tvTotalExpense.getText().toString());
        setResult(RESULT_OK, resultIntent);
        super.finish();
    }
}