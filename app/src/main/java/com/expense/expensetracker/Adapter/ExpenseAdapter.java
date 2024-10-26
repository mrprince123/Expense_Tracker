package com.expense.expensetracker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.expense.expensetracker.Models.Expense;
import com.expense.expensetracker.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final List<Expense> expenses;
    private final Context context;

    public ExpenseAdapter(@NonNull Context context, List<Expense> expenses) {
        this.context = context;
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.messageTextView.setText(expense.getMessageTitle());
        holder.amountTextView.setText("$" + String.valueOf(expense.getAmount()));
        holder.dateTextView.setText("Date : " + expense.getFormattedDate());

        holder.expenseDetailButton.setOnClickListener(view -> {

            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.item_message, null);

            TextView messageBody = dialogView.findViewById(R.id.message_body);
            TextView messageTitle = dialogView.findViewById(R.id.message_title);
            messageBody.setText(expense.getMessageBody());
            messageTitle.setText(expense.getMessageTitle());

            new MaterialAlertDialogBuilder(context)
                    .setTitle("SMS Message")
                    .setView(dialogView)
                    .setPositiveButton("Ok", (dialog, which)->{
                        // Do nothing
                    })

                    .setNegativeButton("Cancel", (dialog, which)->{
                        dialog.dismiss();
            }).show();
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView amountTextView;
        TextView dateTextView;
        Button expenseDetailButton;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.expense_title_expense);
            amountTextView = itemView.findViewById(R.id.expense_amount_expense);
            dateTextView = itemView.findViewById(R.id.expense_date_expense);
            expenseDetailButton = itemView.findViewById(R.id.expense_details_button);
        }
    }
}
