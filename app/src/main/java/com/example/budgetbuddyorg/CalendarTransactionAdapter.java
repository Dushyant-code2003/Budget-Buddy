package com.example.budgetbuddyorg;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class CalendarTransactionAdapter extends RecyclerView.Adapter<CalendarTransactionAdapter.ViewHolder> {
    private List<Transaction> transactions;

    public CalendarTransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.tvTitle.setText(transaction.getTitle());
        holder.tvTime.setText(transaction.getDate());
        holder.tvCategory.setText(transaction.getCategory());

        // Parse the amount string and remove currency symbol if present
        String amountStr = transaction.getAmount().replaceAll("[^\\d.-]", "");
        float amount = Float.parseFloat(amountStr);

        // Format amount with currency symbol and proper decimals
        String amountText = String.format(Locale.getDefault(), "$%.2f", Math.abs(amount));
        if (!transaction.isIncome()) {
            amountText = "-" + amountText;
        }
        holder.tvAmount.setText(amountText);

        holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(
                transaction.isIncome() ? android.R.color.holo_green_dark : android.R.color.holo_red_dark
        ));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvCategory, tvAmount;

        ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvTime = view.findViewById(R.id.tvTime);
            tvCategory = view.findViewById(R.id.tvCategory);
            tvAmount = view.findViewById(R.id.tvAmount);
        }
    }
}