package com.example.budgetbuddyorg;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.tvTitle.setText(transaction.getTitle());
        holder.tvDate.setText(transaction.getDate());

        // Get the amount string and format it properly
        String amountStr = transaction.getAmount();
        if (amountStr != null && !amountStr.isEmpty()) {
            // If the amount already has a currency symbol, use it as is
            if (amountStr.startsWith("$")) {
                holder.tvAmount.setText(amountStr);
            } else {
                // Otherwise, format it with currency symbol
                float amount = transaction.getNumericAmount();
                String formattedAmount = String.format(Locale.getDefault(), "$%.2f", Math.abs(amount));
                if (!transaction.isIncome()) {
                    formattedAmount = "-" + formattedAmount;
                }
                holder.tvAmount.setText(formattedAmount);
            }
        } else {
            holder.tvAmount.setText("$0.00");
        }

        // Change color based on transaction type
        if (transaction.isIncome()) {
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void updateTransactions(List<Transaction> newTransactions) {
        this.transactionList = newTransactions;
        notifyDataSetChanged();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDateTime);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
