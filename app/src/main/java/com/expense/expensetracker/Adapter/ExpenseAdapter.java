package com.expense.expensetracker.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.expense.expensetracker.Models.Expense;
import com.expense.expensetracker.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Random;

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new ExpenseViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.messageTextView.setText(expense.getMessageTitle());
        holder.amountTextView.setText("₹" + String.valueOf(expense.getAmount()));
        holder.dateTextView.setText(expense.getFormattedDate());

        int[] colors = {
                R.color.food,
                R.color.electronics,
                R.color.travel,
                R.color.clothes,
                R.color.house,
                R.color.education,
                R.color.rent,
                R.color.vehicle,
                R.color.electricity,
                R.color.sports,
                R.color.gas,
                R.color.subscription,
                R.color.pay,
                R.color.others
        };

        int randomIndex = new Random().nextInt(colors.length);
        int color = context.getResources().getColor(colors[randomIndex]);
        holder.imageView.setBackgroundTintList(ColorStateList.valueOf(color));


        // I want to set the Random Color to this imageview Backgroundtint
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
                    .setPositiveButton("Ok", (dialog, which) -> {
                    })

                    .setNegativeButton("Cancel", (dialog, which) -> {
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
        ImageView imageView;
        LinearLayout expenseDetailButton;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.transaction_name);
            amountTextView = itemView.findViewById(R.id.transaction_desc);
            dateTextView = itemView.findViewById(R.id.transaction_time);
            imageView = itemView.findViewById(R.id.transaction_image);
            expenseDetailButton = itemView.findViewById(R.id.full_expense_message);
        }
    }
}
