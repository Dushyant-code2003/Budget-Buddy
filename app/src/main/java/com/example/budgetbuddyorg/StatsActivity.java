package com.example.budgetbuddyorg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import java.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import org.json.JSONArray;
import org.json.JSONObject;

public class StatsActivity extends AppCompatActivity {
    private BarChart barChart;
    private TabLayout tabLayout;
    private TextView tvBalanceAmount, tvExpenseAmount, tvProgressDescription;
    private TextView tvIncomeAmount, tvExpenseSummary;
    private ProgressBar progressBar;
    private ImageView ivNotification;
    private ImageButton btnBack, btnSearch, btnCalendar;
    private BottomNavigationView bottomNav;
    private SharedPreferences sharedPreferences;
    private List<Transaction> transactionList;
    private double totalBalance = 0.0;
    private double totalExpenses = 0.0;
    private double periodExpenses = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);

        // Initialize views and setup listeners
        initializeViews();

        // Only proceed with setup if views are properly initialized
        if (areViewsInitialized()) {
            setupClickListeners();
            setupTabLayout();
            setupBottomNavigation();
            loadTransactions();
            updateUI();
            setupDailyChart();
        }
    }

    private void initializeViews() {
        try {
            // Header views
            btnBack = findViewById(R.id.btnBack);
            ivNotification = findViewById(R.id.ivNotification);

            // Balance and expense views
            tvBalanceAmount = findViewById(R.id.tvBalanceAmount);
            tvExpenseAmount = findViewById(R.id.tvExpenseAmount);
            tvProgressDescription = findViewById(R.id.tvProgressDescription);
            progressBar = findViewById(R.id.progressBar);

            // Chart related views
            barChart = findViewById(R.id.barChart);
            tabLayout = findViewById(R.id.tabLayoutStats);
            btnSearch = findViewById(R.id.btnSearch);
            btnCalendar = findViewById(R.id.btnCalendar);

            // Summary views
            tvIncomeAmount = findViewById(R.id.tvIncomeAmount);
            tvExpenseSummary = findViewById(R.id.tvExpenseSummary);

            // Navigation
            bottomNav = findViewById(R.id.bottomNav);

            // Initialize transaction list
            transactionList = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean areViewsInitialized() {
        return btnBack != null &&
                ivNotification != null &&
                tvBalanceAmount != null &&
                tvExpenseAmount != null &&
                tvProgressDescription != null &&
                progressBar != null &&
                barChart != null &&
                tabLayout != null &&
                btnSearch != null &&
                btnCalendar != null &&
                tvIncomeAmount != null &&
                tvExpenseSummary != null &&
                bottomNav != null;
    }

    private void loadTransactions() {
        // Load transactions from SharedPreferences
        String transactionsJson = sharedPreferences.getString("transactions", "[]");
        try {
            JSONArray jsonArray = new JSONArray(transactionsJson);
            transactionList.clear(); // Clear existing transactions
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String type = jsonObject.getString("type");
                float amount = (float) jsonObject.getDouble("amount");
                String description = jsonObject.getString("description");
                // Create transaction with current date and proper boolean flag for income/expense
                String currentDate = new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(new Date());
                boolean isIncome = type.equals("income");
                transactionList.add(new Transaction(description, currentDate, String.format("$%.2f", amount), isIncome));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Calculate total expenses and income
        totalExpenses = 0.0;
        double totalIncome = 0.0;
        for (Transaction transaction : transactionList) {
            float amount = Float.parseFloat(transaction.getAmount().replace("$", ""));
            if (!transaction.isIncome()) {
                totalExpenses += amount;
            } else {
                totalIncome += amount;
            }
        }

        // Update chart data
        updateChartData("Monthly"); // Default to monthly view
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(StatsActivity.this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        btnCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(StatsActivity.this, CalendarActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(StatsActivity.this, NotificationSetting.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Daily"));
        tabLayout.addTab(tabLayout.newTab().setText("Weekly"));
        tabLayout.addTab(tabLayout.newTab().setText("Monthly"));
        tabLayout.addTab(tabLayout.newTab().setText("Year"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        setupDailyChart();
                        break;
                    case 1:
                        setupWeeklyChart();
                        break;
                    case 2:
                        setupMonthlyChart();
                        break;
                    case 3:
                        setupYearlyChart();
                        break;
                }
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
                startActivity(new Intent(StatsActivity.this, SecondActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_stat) {
                return true; // Already here
            } else if (itemId == R.id.nav_transfer) {
                startActivity(new Intent(StatsActivity.this, TransferActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_category) {
                startActivity(new Intent(StatsActivity.this, CategoriesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(StatsActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });

        bottomNav.setSelectedItemId(R.id.nav_stat);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            // Always update UI when resuming
            loadTransactions();
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI() {
        try {
            // Get balance from SharedPreferences
            totalBalance = sharedPreferences.getFloat("total_balance", 0.0f);
            totalExpenses = sharedPreferences.getFloat("total_expenses", 0.0f);

            // Update balance display (as income)
            if (tvBalanceAmount != null) {
                tvBalanceAmount.setText("$" + String.format("%.2f", totalBalance));
            }

            // Update expense display
            if (tvExpenseAmount != null) {
                tvExpenseAmount.setText("- $" + String.format("%.2f", totalExpenses));
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

            // Update income and expense summary
            tvIncomeAmount.setText(String.format("$%.2f", totalBalance));
            tvExpenseSummary.setText(String.format("$%.2f", totalExpenses));

            // Update chart data to include the balance as income
            updateChartData(tabLayout.getSelectedTabPosition() >= 0 ?
                    tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString() : "Monthly");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateChartData(String type) {
        ArrayList<BarEntry> incomeEntries = new ArrayList<>();
        ArrayList<BarEntry> expenseEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        // Get transactions for the selected period
        List<Transaction> periodTransactions = getTransactionsForPeriod(type);

        // Calculate income and expenses for the period
        float periodIncome = 0;
        float periodExpenses = 0;
        for (Transaction transaction : periodTransactions) {
            float amount = Float.parseFloat(transaction.getAmount().replace("$", ""));
            if (transaction.isIncome()) {
                periodIncome += amount;
            } else {
                periodExpenses += amount;
            }
        }

        // Add the total balance as income
        periodIncome += totalBalance;

        // Add data points
        incomeEntries.add(new BarEntry(0, periodIncome));
        expenseEntries.add(new BarEntry(0, periodExpenses));
        labels.add(getPeriodLabel(type));

        // Update the chart
        BarDataSet incomeSet = new BarDataSet(incomeEntries, "Income");
        incomeSet.setColor(getResources().getColor(R.color.green));
        incomeSet.setValueTextSize(10f);

        BarDataSet expenseSet = new BarDataSet(expenseEntries, "Expense");
        expenseSet.setColor(getResources().getColor(R.color.blue));
        expenseSet.setValueTextSize(10f);

        BarData data = new BarData(incomeSet, expenseSet);
        float groupSpace = 0.2f;
        float barSpace = 0.05f;
        float barWidth = 0.35f;
        data.setBarWidth(barWidth);

        barChart.setData(data);
        barChart.groupBars(0f, groupSpace, barSpace);
        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getXAxis().setCenterAxisLabels(true);
        barChart.getXAxis().setAxisMinimum(0f);
        barChart.getXAxis().setAxisMaximum(labels.size());
        barChart.getXAxis().setDrawGridLines(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private List<Transaction> getTransactionsForPeriod(String type) {
        List<Transaction> periodTransactions = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();

        for (Transaction transaction : transactionList) {
            // For simplicity, we'll use all transactions for now
            // In a real app, you would filter based on the transaction date
            periodTransactions.add(transaction);
        }

        return periodTransactions;
    }

    private String getPeriodLabel(String type) {
        Calendar calendar = Calendar.getInstance();
        switch (type) {
            case "Daily":
                return "Today";
            case "Weekly":
                return "This Week";
            case "Monthly":
                return "This Month";
            case "Year":
                return "This Year";
            default:
                return "Period";
        }
    }

    private void setupDailyChart() {
        ArrayList<BarEntry> incomeEntries = new ArrayList<>();
        ArrayList<BarEntry> expenseEntries = new ArrayList<>();

        // Sample data for daily view (Monday to Sunday)
        float[] incomeData = {6000f, 8000f, 7000f, 4000f, 15000f, 1000f, 5000f};
        float[] expenseData = {2000f, 1000f, 3000f, 4000f, 8000f, 1000f, 5000f};

        for (int i = 0; i < 7; i++) {
            incomeEntries.add(new BarEntry(i, incomeData[i]));
            expenseEntries.add(new BarEntry(i, expenseData[i]));
        }

        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(getResources().getColor(R.color.green));
        incomeDataSet.setDrawValues(false);

        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expense");
        expenseDataSet.setColor(getResources().getColor(R.color.blue));
        expenseDataSet.setDrawValues(false);

        BarData barData = new BarData(incomeDataSet, expenseDataSet);
        barData.setBarWidth(0.3f);

        // X-axis setup
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.BLACK);
        String[] days = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setCenterAxisLabels(true);

        // Y-axis setup
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setGranularity(5000f);
        leftAxis.setLabelCount(4, true);

        // Custom value formatter for Y-axis
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) return "0";
                if (value >= 1000) {
                    return (int) (value / 1000) + "k";
                }
                return String.valueOf((int) value);
            }
        });

        barChart.getAxisRight().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setExtraBottomOffset(10f);

        float groupSpace = 0.4f;
        float barSpace = 0f;
        barData.setBarWidth(0.3f);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0 + barData.getGroupWidth(groupSpace, barSpace) * 7);
        barChart.groupBars(0, groupSpace, barSpace);

        barChart.setData(barData);
        barChart.invalidate();
    }

    private void setupWeeklyChart() {
        ArrayList<BarEntry> incomeEntries = new ArrayList<>();
        ArrayList<BarEntry> expenseEntries = new ArrayList<>();
        String[] days = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        float[] incomeData = new float[7];
        float[] expenseData = new float[7];
        Calendar now = Calendar.getInstance();
        for (Transaction t : transactionList) {
            Calendar transCal = Calendar.getInstance();
            // Parse date from transaction (format: "HH:mm - MMM dd")
            try {
                String[] parts = t.getDate().split(" - ");
                if (parts.length == 2) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault());
                    java.util.Date dateObj = sdf.parse(parts[1]);
                    if (dateObj != null) {
                        transCal.setTime(dateObj);
                        transCal.set(Calendar.YEAR, now.get(Calendar.YEAR));
                    }
                }
            } catch (Exception ignored) {}
            if (now.get(Calendar.YEAR) == transCal.get(Calendar.YEAR) &&
                    now.get(Calendar.WEEK_OF_YEAR) == transCal.get(Calendar.WEEK_OF_YEAR)) {
                int dayOfWeek = transCal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
                if (dayOfWeek < 0) dayOfWeek += 7;
                if (t.isIncome()) {
                    incomeData[dayOfWeek] += t.getNumericAmount();
                } else {
                    expenseData[dayOfWeek] += t.getNumericAmount();
                }
            }
        }
        for (int i = 0; i < 7; i++) {
            incomeEntries.add(new BarEntry(i, incomeData[i]));
            expenseEntries.add(new BarEntry(i, expenseData[i]));
        }
        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(getResources().getColor(R.color.green));
        incomeDataSet.setDrawValues(false);
        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expense");
        expenseDataSet.setColor(getResources().getColor(R.color.blue));
        expenseDataSet.setDrawValues(false);
        BarData barData = new BarData(incomeDataSet, expenseDataSet);
        barData.setBarWidth(0.3f);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setCenterAxisLabels(true);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setGranularity(5000f);
        leftAxis.setLabelCount(4, true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) return "0";
                if (value >= 1000) {
                    return (int) (value / 1000) + "k";
                }
                return String.valueOf((int) value);
            }
        });
        barChart.getAxisRight().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setExtraBottomOffset(10f);
        float groupSpace = 0.4f;
        float barSpace = 0f;
        barData.setBarWidth(0.3f);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0 + barData.getGroupWidth(groupSpace, barSpace) * 7);
        barChart.groupBars(0, groupSpace, barSpace);
        barChart.setData(barData);
        barChart.invalidate();
    }

    private void setupMonthlyChart() {
        ArrayList<BarEntry> incomeEntries = new ArrayList<>();
        ArrayList<BarEntry> expenseEntries = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        int daysInMonth = now.getActualMaximum(Calendar.DAY_OF_MONTH);
        float[] incomeData = new float[daysInMonth];
        float[] expenseData = new float[daysInMonth];
        for (Transaction t : transactionList) {
            Calendar transCal = Calendar.getInstance();
            try {
                String[] parts = t.getDate().split(" - ");
                if (parts.length == 2) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault());
                    java.util.Date dateObj = sdf.parse(parts[1]);
                    if (dateObj != null) {
                        transCal.setTime(dateObj);
                        transCal.set(Calendar.YEAR, now.get(Calendar.YEAR));
                    }
                }
            } catch (Exception ignored) {}
            if (now.get(Calendar.YEAR) == transCal.get(Calendar.YEAR) &&
                    now.get(Calendar.MONTH) == transCal.get(Calendar.MONTH)) {
                int dayOfMonth = transCal.get(Calendar.DAY_OF_MONTH) - 1;
                if (t.isIncome()) {
                    incomeData[dayOfMonth] += t.getNumericAmount();
                } else {
                    expenseData[dayOfMonth] += t.getNumericAmount();
                }
            }
        }
        for (int i = 0; i < daysInMonth; i++) {
            incomeEntries.add(new BarEntry(i, incomeData[i]));
            expenseEntries.add(new BarEntry(i, expenseData[i]));
        }
        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(getResources().getColor(R.color.green));
        incomeDataSet.setDrawValues(false);
        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expense");
        expenseDataSet.setColor(getResources().getColor(R.color.blue));
        expenseDataSet.setDrawValues(false);
        BarData barData = new BarData(incomeDataSet, expenseDataSet);
        barData.setBarWidth(0.3f);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.BLACK);
        String[] days = new String[daysInMonth];
        for (int i = 0; i < daysInMonth; i++) days[i] = String.valueOf(i + 1);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setCenterAxisLabels(true);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setGranularity(5000f);
        leftAxis.setLabelCount(4, true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) return "0";
                if (value >= 1000) {
                    return (int) (value / 1000) + "k";
                }
                return String.valueOf((int) value);
            }
        });
        barChart.getAxisRight().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setExtraBottomOffset(10f);
        float groupSpace = 0.4f;
        float barSpace = 0f;
        barData.setBarWidth(0.3f);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0 + barData.getGroupWidth(groupSpace, barSpace) * daysInMonth);
        barChart.groupBars(0, groupSpace, barSpace);
        barChart.setData(barData);
        barChart.invalidate();
    }

    private void setupYearlyChart() {
        ArrayList<BarEntry> incomeEntries = new ArrayList<>();
        ArrayList<BarEntry> expenseEntries = new ArrayList<>();
        String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        float[] incomeData = new float[12];
        float[] expenseData = new float[12];
        Calendar now = Calendar.getInstance();
        for (Transaction t : transactionList) {
            Calendar transCal = Calendar.getInstance();
            try {
                String[] parts = t.getDate().split(" - ");
                if (parts.length == 2) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault());
                    java.util.Date dateObj = sdf.parse(parts[1]);
                    if (dateObj != null) {
                        transCal.setTime(dateObj);
                        transCal.set(Calendar.YEAR, now.get(Calendar.YEAR));
                    }
                }
            } catch (Exception ignored) {}
            if (now.get(Calendar.YEAR) == transCal.get(Calendar.YEAR)) {
                int month = transCal.get(Calendar.MONTH);
                if (t.isIncome()) {
                    incomeData[month] += t.getNumericAmount();
                } else {
                    expenseData[month] += t.getNumericAmount();
                }
            }
        }
        for (int i = 0; i < 12; i++) {
            incomeEntries.add(new BarEntry(i, incomeData[i]));
            expenseEntries.add(new BarEntry(i, expenseData[i]));
        }
        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(getResources().getColor(R.color.green));
        incomeDataSet.setDrawValues(false);
        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expense");
        expenseDataSet.setColor(getResources().getColor(R.color.blue));
        expenseDataSet.setDrawValues(false);
        BarData barData = new BarData(incomeDataSet, expenseDataSet);
        barData.setBarWidth(0.3f);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setCenterAxisLabels(true);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setGranularity(5000f);
        leftAxis.setLabelCount(4, true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) return "0";
                if (value >= 1000) {
                    return (int) (value / 1000) + "k";
                }
                return String.valueOf((int) value);
            }
        });
        barChart.getAxisRight().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setExtraBottomOffset(10f);
        float groupSpace = 0.4f;
        float barSpace = 0f;
        barData.setBarWidth(0.3f);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0 + barData.getGroupWidth(groupSpace, barSpace) * 12);
        barChart.groupBars(0, groupSpace, barSpace);
        barChart.setData(barData);
        barChart.invalidate();
    }

    private void calculatePeriodExpenses() {
        periodExpenses = 0.0f;
        for (Transaction transaction : transactionList) {
            if (!transaction.isIncome()) {
                periodExpenses += transaction.getNumericAmount();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            float newBalance = data.getFloatExtra("new_balance", 0.0f);
            // Save the new balance to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("total_balance", newBalance);
            editor.apply();
            // Update the UI
            updateUI();
        }
    }
}
