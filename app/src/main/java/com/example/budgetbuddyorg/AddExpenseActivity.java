package com.example.budgetbuddyorg;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {
    private ImageView btnBack, ivNotification;
    private RelativeLayout datePickerLayout;
    private TextView tvSelectedDate;
    private Spinner spinnerCategory;
    private EditText etAmount, etExpenseTitle, etMessage;
    private Button btnSave;
    private BottomNavigationView bottomNav;
    private SharedPreferences sharedPreferences;
    private Calendar selectedDate;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Get category name from intent
        categoryName = getIntent().getStringExtra("category_name");

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        ivNotification = findViewById(R.id.ivNotification);
        datePickerLayout = findViewById(R.id.datePickerLayout);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        etAmount = findViewById(R.id.etAmount);
        etExpenseTitle = findViewById(R.id.etExpenseTitle);
        etMessage = findViewById(R.id.etMessage);
        btnSave = findViewById(R.id.btnSave);
        bottomNav = findViewById(R.id.bottomNav);

        // Initialize date
        selectedDate = Calendar.getInstance();
        updateDateDisplay();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationSetting.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        datePickerLayout.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveExpense());
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

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateDisplay();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        tvSelectedDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void saveExpense() {
        String amount = etAmount.getText().toString().trim();
        String title = etExpenseTitle.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (TextUtils.isEmpty(amount)) {
            etAmount.setError("Please enter amount");
            return;
        }

        if (TextUtils.isEmpty(title)) {
            etExpenseTitle.setError("Please enter title");
            return;
        }

        try {
            float expenseAmount = Float.parseFloat(amount);

            // Create new transaction
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
            String time = timeFormat.format(selectedDate.getTime());
            String date = dateFormat.format(selectedDate.getTime());

            Transaction transaction = new Transaction(
                    title,
                    time + " - " + date,
                    String.format("$%.2f", expenseAmount),
                    false
            );
            transaction.setCategory(categoryName);
            transaction.setMessage(message);

            // Load existing transactions
            String transactionsJson = sharedPreferences.getString("transactions", "[]");
            List<Transaction> transactions = new Gson().fromJson(transactionsJson,
                    new TypeToken<List<Transaction>>(){}.getType());

            if (transactions == null) {
                transactions = new ArrayList<>();
            }

            // Add new transaction
            transactions.add(transaction);

            // Save updated transactions
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("transactions", new Gson().toJson(transactions));

            // Update total expenses
            float currentExpenses = sharedPreferences.getFloat("total_expenses", 0.0f);
            float newTotalExpenses = currentExpenses + expenseAmount;
            editor.putFloat("total_expenses", newTotalExpenses);

            // Save category-specific expenses
            String categoryExpensesKey = "category_expenses_" + categoryName;
            float currentCategoryExpenses = sharedPreferences.getFloat(categoryExpensesKey, 0.0f);
            editor.putFloat(categoryExpensesKey, currentCategoryExpenses + expenseAmount);

            editor.apply();

            // Return success with the new expense amount and category
            Intent resultIntent = new Intent();
            resultIntent.putExtra("new_expense", expenseAmount);
            resultIntent.putExtra("category_name", categoryName);
            setResult(RESULT_OK, resultIntent);
            finish();
            overridePendingTransition(0, 0);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }
}