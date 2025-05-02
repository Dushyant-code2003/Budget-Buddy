package com.example.budgetbuddyorg;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransferListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_MONTH_HEADER = 0;
    private static final int TYPE_TRANSFER = 1;

    private final List<Object> items = new ArrayList<>();

    public void setTransactions(Map<String, List<Transaction>> transfersByMonth) {
        items.clear();

        // Flatten the grouped transactions into a single list with headers
        for (Map.Entry<String, List<Transaction>> entry : transfersByMonth.entrySet()) {
            items.add(entry.getKey()); // Month header
            items.addAll(entry.getValue()); // Transfers for that month
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_MONTH_HEADER : TYPE_TRANSFER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_MONTH_HEADER) {
            View view = inflater.inflate(R.layout.item_month_header, parent, false);
            return new MonthHeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_transfer, parent, false);
            return new TransferViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MonthHeaderViewHolder) {
            ((MonthHeaderViewHolder) holder).bind((String) items.get(position));
        } else if (holder instanceof TransferViewHolder) {
            ((TransferViewHolder) holder).bind((Transaction) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MonthHeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMonth;

        MonthHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tvMonth);
        }

        void bind(String month) {
            tvMonth.setText(month);
        }
    }

    static class TransferViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCategory;
        private final TextView tvTitle;
        private final TextView tvDateTime;
        private final TextView tvCategory;
        private final TextView tvAmount;

        TransferViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategory = itemView.findViewById(R.id.ivCategory);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }

        void bind(Transaction transfer) {
            tvTitle.setText(transfer.getTitle());
            tvDateTime.setText(transfer.getDate());
            tvCategory.setText(transfer.getCategory());

            // Set text color based on transaction type
            int textColor = transfer.isIncome() ? 0xFF00D09E : 0xFFFF2E2E;
            tvAmount.setTextColor(textColor);
            tvAmount.setText(transfer.getAmount());

            // Set category icon based on category name
            int iconResId = getCategoryIcon(transfer.getCategory());
            ivCategory.setImageResource(iconResId);
        }

        private int getCategoryIcon(String category) {
            if (category == null) return R.drawable.ic_others;

            switch (category.toLowerCase()) {
                case "salary":
                    return R.drawable.ic_savings;
                case "groceries":
                    return R.drawable.ic_groceries;
                case "rent":
                    return R.drawable.ic_rent;
                case "transport":
                    return R.drawable.ic_transport;
                case "food":
                    return R.drawable.ic_food;
                default:
                    return R.drawable.ic_others;
            }
        }
    }
}