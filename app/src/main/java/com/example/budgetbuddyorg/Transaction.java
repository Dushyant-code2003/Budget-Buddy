package com.example.budgetbuddyorg;

public class Transaction {
    private String title;
    private String date;
    private String amount;
    private boolean isIncome; // true for income, false for expense

    public Transaction(String title, String date, String amount, boolean isIncome) {
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.isIncome = isIncome;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public boolean isIncome() {
        return isIncome;
    }
}
