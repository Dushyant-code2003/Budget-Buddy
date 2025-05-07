package com.example.budgetbuddyorg;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransferListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MONTH_HEADER = 0;
    private static final int TYPE_TRANSFER = 1;

    private final List<Object> items = new ArrayList<>();

    public void setTransactions(Map<String, List<Transaction>> transfersByMonth) {
        items.clear();
        for (Map.Entry<String, List<Transaction>> entry : transfersByMonth.entrySet()) {
            items.add(entry.getKey()); // Month name (String)
            items.addAll(entry.getValue()); // List of Transaction objects
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
        private final TextView tvTitle, tvDateTime, tvCategory, tvAmount;

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

            float amount = transfer.getNumericAmount();
            String formattedAmount = String.format(Locale.getDefault(), "$%.2f", Math.abs(amount));

            if (transfer.isIncome()) {
                tvAmount.setTextColor(itemView.getContext().getResources().getColor(R.color.green));
                tvAmount.setText(formattedAmount);
            } else {
                tvAmount.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                tvAmount.setText("-" + formattedAmount);
            }

            // Set category icon
            int iconResId = getCategoryIcon(transfer.getCategory());
            ivCategory.setImageResource(iconResId);

            // Set background tint color
            int backgroundColor = transfer.isIncome() ?
                    itemView.getContext().getResources().getColor(R.color.green) :
                    itemView.getContext().getResources().getColor(R.color.red);
            ivCategory.setColorFilter(Color.WHITE);
            ivCategory.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
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
                case "pantry":
                    return R.drawable.ic_groceries;
                case "fuel":
                    return R.drawable.ic_transport;
                case "dinner":
                    return R.drawable.ic_food;
                default:
                    return R.drawable.ic_others;
            }
        }
    }
}
