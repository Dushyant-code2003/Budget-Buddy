package com.example.budgetbuddyorg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction {
    private String title;
    private String date;
    private String amount;
    private boolean isIncome; // true for income, false for expense
    private String category;
    private String message;
    private String type; // For backward compatibility
    private String description; // For backward compatibility
    private String userId;
    private long timestamp;

    public Transaction() {
        // Required empty constructor for Firebase
        this.timestamp = System.currentTimeMillis();
    }

    public Transaction(String title, String date, String amount, boolean isIncome) {
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.isIncome = isIncome;
        this.type = isIncome ? "income" : "expense";
        this.description = title;
        this.timestamp = System.currentTimeMillis();
    }

    // New constructor that accepts 6 parameters
    public Transaction(String title, String date, String amount, float numericAmount, String category, boolean isIncome) {
        this(title, date, String.format("$%.2f", numericAmount), isIncome);
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.description = title; // Keep description in sync
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public float getNumericAmount() {
        try {
            return Float.parseFloat(amount.replaceAll("[^\\d.-]", ""));
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public void setIncome(boolean income) {
        isIncome = income;
        this.type = income ? "income" : "expense"; // Keep type in sync
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Backward compatibility methods
    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    // Helper method to get time from date string
    public long getTime() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            Date dateObj = sdf.parse(date);
            return dateObj != null ? dateObj.getTime() : 0;
        } catch (ParseException e) {
            return 0;
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
