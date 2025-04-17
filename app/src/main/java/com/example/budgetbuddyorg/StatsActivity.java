package com.example.budgetbuddyorg;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;

public class StatsActivity extends AppCompatActivity {

    private BarChart barChart;
    private TabLayout tabLayout;
    private TextView textIncome, textExpense;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        barChart = findViewById(R.id.barChart);
        tabLayout = findViewById(R.id.tabLayoutStats);
//        textIncome = findViewById(R.id.textIncome);
//        textExpense = findViewById(R.id.textExpense);
//        recyclerView = findViewById(R.id.recyclerView);

        tabLayout.addTab(tabLayout.newTab().setText("Daily"));
        tabLayout.addTab(tabLayout.newTab().setText("Weekly"));
        tabLayout.addTab(tabLayout.newTab().setText("Monthly"));

        updateChartData("Daily");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                updateChartData(tab.getText().toString());
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void updateChartData(String type) {
        ArrayList<BarEntry> incomeEntries = new ArrayList<>();
        ArrayList<BarEntry> expenseEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        float totalIncome = 0, totalExpense = 0;

        switch (type) {
            case "Daily":
                incomeEntries.add(new BarEntry(0, 5000)); expenseEntries.add(new BarEntry(0, 1000));
                incomeEntries.add(new BarEntry(1, 7000)); expenseEntries.add(new BarEntry(1, 1500));
                incomeEntries.add(new BarEntry(2, 6000)); expenseEntries.add(new BarEntry(2, 1200));
                incomeEntries.add(new BarEntry(3, 8000)); expenseEntries.add(new BarEntry(3, 1800));
                incomeEntries.add(new BarEntry(4, 9000)); expenseEntries.add(new BarEntry(4, 1400));
                incomeEntries.add(new BarEntry(5, 7500)); expenseEntries.add(new BarEntry(5, 1300));
                incomeEntries.add(new BarEntry(6, 6800)); expenseEntries.add(new BarEntry(6, 1100));
                labels.addAll(Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
                break;

            case "Weekly":
                incomeEntries.add(new BarEntry(0, 20000)); expenseEntries.add(new BarEntry(0, 4000));
                incomeEntries.add(new BarEntry(1, 22000)); expenseEntries.add(new BarEntry(1, 5000));
                incomeEntries.add(new BarEntry(2, 21000)); expenseEntries.add(new BarEntry(2, 4800));
                incomeEntries.add(new BarEntry(3, 25000)); expenseEntries.add(new BarEntry(3, 5200));
                labels.addAll(Arrays.asList("Week 1", "Week 2", "Week 3", "Week 4"));
                break;

            case "Monthly":
                incomeEntries.add(new BarEntry(0, 80000)); expenseEntries.add(new BarEntry(0, 18000));
                incomeEntries.add(new BarEntry(1, 82000)); expenseEntries.add(new BarEntry(1, 20000));
                incomeEntries.add(new BarEntry(2, 78000)); expenseEntries.add(new BarEntry(2, 19000));
                labels.addAll(Arrays.asList("Jan", "Feb", "Mar"));
                break;
        }

        for (BarEntry entry : incomeEntries) totalIncome += entry.getY();
        for (BarEntry entry : expenseEntries) totalExpense += entry.getY();

        textIncome.setText("Income: ₹" + (int) totalIncome);
        textExpense.setText("Expense: ₹" + (int) totalExpense);

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
}
