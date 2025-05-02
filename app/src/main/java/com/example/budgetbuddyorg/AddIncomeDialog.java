package com.example.budgetbuddyorg;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddIncomeDialog extends DialogFragment {
    private TextInputEditText etTitle, etAmount, etDate;
    private OnIncomeAddedListener listener;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm - MMM dd", Locale.getDefault());

    public interface OnIncomeAddedListener {
        void onIncomeAdded(String title, String category, double amount, Date date);
    }

    public void setOnIncomeAddedListener(OnIncomeAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_add_income_dialog, null);

        etTitle = view.findViewById(R.id.etTitle);
        etAmount = view.findViewById(R.id.etAmount);
        etDate = view.findViewById(R.id.etDate);

        // Set current date as default
        etDate.setText(dateFormatter.format(calendar.getTime()));

        // Setup date picker
        etDate.setOnClickListener(v -> showDatePicker());

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(view);

        // Setup buttons
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnSave).setOnClickListener(v -> saveIncome());

        return dialog;
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    etDate.setText(dateFormatter.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveIncome() {
        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Please enter a title");
            return;
        }

        if (amountStr.isEmpty()) {
            etAmount.setError("Please enter an amount");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                etAmount.setError("Amount must be greater than 0");
                return;
            }

            if (listener != null) {
                listener.onIncomeAdded(title, "Income", amount, calendar.getTime());
            }
            dismiss();

        } catch (NumberFormatException e) {
            etAmount.setError("Invalid amount");
        }
    }
}