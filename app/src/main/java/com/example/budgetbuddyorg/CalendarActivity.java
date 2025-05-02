package com.example.budgetbuddyorg;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private ImageView ivNotification;
    private TextView tvMonthYear;
    private GridLayout calendarGrid;
    private Button btnSpends, btnCategories;
    private BottomNavigationView bottomNav;
    private Calendar currentCalendar;
    private List<Transaction> transactions;
    private TransactionAdapter transactionAdapter;
    private RecyclerView rvTransactions;
    private CalendarTransactionAdapter adapter;
    private LinearLayout categoriesContainer;
    private PieChart pieChart;
    private TextView tvGroceriesPercentage;
    private TextView tvOthersPercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        initializeViews();
        setupListeners();
        setupCalendar();
        setupBottomNavigation();
        setupRecyclerView();
        setupPieChart();
        loadTransactions();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        ivNotification = findViewById(R.id.ivNotification);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        calendarGrid = findViewById(R.id.calendarGrid);
        btnSpends = findViewById(R.id.btnSpends);
        btnCategories = findViewById(R.id.btnCategories);
        bottomNav = findViewById(R.id.bottomNav);
        rvTransactions = findViewById(R.id.rvTransactions);
        categoriesContainer = findViewById(R.id.categoriesContainer);
        pieChart = findViewById(R.id.pieChart);
        tvGroceriesPercentage = findViewById(R.id.tvGroceriesPercentage);
        tvOthersPercentage = findViewById(R.id.tvOthersPercentage);

        currentCalendar = Calendar.getInstance();
        updateMonthYearDisplay();
    }

    private void updateMonthYearDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthYear.setText(dateFormat.format(currentCalendar.getTime()));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        ivNotification.setOnClickListener(v -> {
            // Handle notification click
        });

        btnSpends.setOnClickListener(v -> {
            btnSpends.setBackgroundResource(R.drawable.button_selected);
            btnSpends.setTextColor(getResources().getColor(android.R.color.white));
            btnCategories.setBackgroundResource(R.drawable.button_unselected);
            btnCategories.setTextColor(getResources().getColor(android.R.color.black));
            showTransactions();
        });

        btnCategories.setOnClickListener(v -> {
            btnCategories.setBackgroundResource(R.drawable.button_selected);
            btnCategories.setTextColor(getResources().getColor(android.R.color.white));
            btnSpends.setBackgroundResource(R.drawable.button_unselected);
            btnSpends.setTextColor(getResources().getColor(android.R.color.black));
            showCategories();
        });
    }

    private void setupCalendar() {
        updateMonthYearDisplay();
        populateCalendarGrid();
    }

    private void populateCalendarGrid() {
        calendarGrid.removeAllViews();
        // Add day headers (already in XML)

        Calendar calendar = (Calendar) currentCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Adjust for Monday as first day of week
        int offset = firstDayOfWeek - Calendar.MONDAY;
        if (offset < 0) offset += 7;

        // Add empty cells for days before the first day of month
        for (int i = 0; i < offset; i++) {
            addCalendarDay("");
        }

        // Add days of the month
        for (int day = 1; day <= maxDays; day++) {
            addCalendarDay(String.valueOf(day));
        }
    }

    private void addCalendarDay(String text) {
        TextView dayView = new TextView(this);
        dayView.setLayoutParams(new GridLayout.LayoutParams());
        dayView.setText(text);
        dayView.setTextColor(getResources().getColor(android.R.color.black));
        dayView.setGravity(android.view.Gravity.CENTER);
        dayView.setPadding(8, 8, 8, 8);

        if (!text.isEmpty()) {
            dayView.setOnClickListener(v -> onDaySelected(Integer.parseInt(text)));
        }

        calendarGrid.addView(dayView);
    }

    private void onDaySelected(int day) {
        currentCalendar.set(Calendar.DAY_OF_MONTH, day);
        loadTransactions(); // Reload transactions for selected day
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_stat);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                finish();
                return true;
            } else if (itemId == R.id.nav_stat) {
                return true;
            } else if (itemId == R.id.nav_transfer) {
                finish();
                return true;
            } else if (itemId == R.id.nav_category) {
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                finish();
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        transactions = new ArrayList<>();
        adapter = new CalendarTransactionAdapter(transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(70f);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setEnabled(false);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(10f, "Groceries"));
        entries.add(new PieEntry(79f, "Others"));

        PieDataSet dataSet = new PieDataSet(entries, "Categories");
        dataSet.setColors(Color.parseColor("#2196F3"), Color.parseColor("#4CAF50"));
        dataSet.setDrawValues(false);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void loadTransactions() {
        // Add sample transactions
        Transaction groceries = new Transaction("Groceries", "17:00 - April 24", "$100.00", false);
        groceries.setCategory("Pantry");
        transactions.add(groceries);

        Transaction others = new Transaction("Others", "17:00 - April 24", "$120.00", true);
        others.setCategory("Payments");
        transactions.add(others);

        adapter.notifyDataSetChanged();
    }

    private void showTransactions() {
        rvTransactions.setVisibility(View.VISIBLE);
        categoriesContainer.setVisibility(View.GONE);
    }

    private void showCategories() {
        rvTransactions.setVisibility(View.GONE);
        categoriesContainer.setVisibility(View.VISIBLE);
    }
}