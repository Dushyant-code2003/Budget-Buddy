package com.example.budgetbuddyorg;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView;
import android.app.DatePickerDialog;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.widget.DatePicker;

public class SearchActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private ImageButton btnNotification;
    private EditText etSearch;
    private Spinner spinnerCategory;
    private TextView tvDate;
    private RadioButton rbIncome;
    private RadioButton rbExpense;
    private RecyclerView rvSearchResults;
    private SearchTransactionAdapter adapter;
    private List<Transaction> transactions;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initializeViews();
        setupListeners();
        setupCategorySpinner();
        setupRecyclerView();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnNotification = findViewById(R.id.btnNotification);
        etSearch = findViewById(R.id.etSearch);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        tvDate = findViewById(R.id.tvDate);
        rbIncome = findViewById(R.id.rbIncome);
        rbExpense = findViewById(R.id.rbExpense);
        rvSearchResults = findViewById(R.id.rvSearchResults);

        calendar = Calendar.getInstance();
        updateDateDisplay();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnNotification.setOnClickListener(v -> {
            // Handle notification click
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTransactions();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        tvDate.setOnClickListener(v -> showDatePicker());

        rbIncome.setOnCheckedChangeListener((buttonView, isChecked) -> filterTransactions());
        rbExpense.setOnCheckedChangeListener((buttonView, isChecked) -> filterTransactions());
    }

    private void setupCategorySpinner() {
        List<String> categories = new ArrayList<>();
        categories.add("Select the category");
        categories.add("Food");
        categories.add("Transportation");
        categories.add("Shopping");
        categories.add("Bills");
        categories.add("Entertainment");
        categories.add("Others");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterTransactions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupRecyclerView() {
        transactions = new ArrayList<>();
        adapter = new SearchTransactionAdapter(transactions);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(adapter);

        // Add sample transaction with the correct constructor (title, time, date, amount, category, isIncome)
        transactions.add(new Transaction("Dinner", "18:27", "April 30", -26.00f, "Food", false));
        adapter.notifyDataSetChanged();
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateDisplay();
                    filterTransactions();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault());
        tvDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void filterTransactions() {
        String searchQuery = etSearch.getText().toString().toLowerCase();
        String selectedCategory = spinnerCategory.getSelectedItem().toString();
        boolean isIncome = rbIncome.isChecked();
        boolean isExpense = rbExpense.isChecked();

        List<Transaction> filteredList = new ArrayList<>();
        for (Transaction transaction : transactions) {
            boolean matchesSearch = transaction.getTitle().toLowerCase().contains(searchQuery);
            boolean matchesCategory = selectedCategory.equals("Select the category") ||
                    transaction.getCategory().equals(selectedCategory);
            boolean matchesType = (!isIncome && !isExpense) ||
                    (isIncome && transaction.isIncome()) ||
                    (isExpense && !transaction.isIncome());

            if (matchesSearch && matchesCategory && matchesType) {
                filteredList.add(transaction);
            }
        }

        adapter.updateTransactions(filteredList);
    }
}