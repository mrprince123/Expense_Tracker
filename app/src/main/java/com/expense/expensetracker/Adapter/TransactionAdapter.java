package com.expense.expensetracker.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.expense.expensetracker.Activity.DetailTransactionActivity;
import com.expense.expensetracker.Models.Transaction;
import com.expense.expensetracker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final Context context;
    private final ArrayList<Transaction> transactions;

    public TransactionAdapter(Context context, ArrayList<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.TransactionViewHolder holder, int position) {

        Transaction transcation = transactions.get(position);

        holder.itemView.setOnClickListener(View -> {
            Intent intent = new Intent(context, DetailTransactionActivity.class);

            intent.putExtra("id", transcation.getId());
            intent.putExtra("title", transcation.getTitle());
            intent.putExtra("date", transcation.getDate());
            intent.putExtra("amount", transcation.getAmount());
            intent.putExtra("category", transcation.getCategory());
            intent.putExtra("paymentMethod", transcation.getPaymentMethod());
            intent.putExtra("location", transcation.getLocation());
            intent.putExtra("receipt", transcation.getReceipt());
            intent.putExtra("notes", transcation.getNotes());

            context.startActivity(intent);

        });

        switch (transcation.getCategory()) {
            case "Food":
                Glide.with(context).load(R.drawable.menu_24).into(holder.transactionImage);
                break;
            case "Electronics":
                Glide.with(context)
                        .load(R.drawable.electronics_24)
                        .into(holder.transactionImage);
                break;
            case "Travel":
                Glide.with(context).load(R.drawable.travel_24)
                        .into(holder.transactionImage);
                break;
            case "House Hold":
                Glide.with(context).load(R.drawable.house_24).into(holder.transactionImage);
                break;
            case "Education":
                Glide.with(context).load(R.drawable.education_24).into(holder.transactionImage);
                break;
            case "Rent":
                Glide.with(context).load(R.drawable.rents_24).into(holder.transactionImage);
                break;
            case "Vehicle Gas":
                Glide.with(context).load(R.drawable.gas_24).into(holder.transactionImage);
                break;
            case "Electricity Bill":
                Glide.with(context).load(R.drawable.electric_bill_24).into(holder.transactionImage);
                break;
            case "Gas Bill":
                Glide.with(context).load(R.drawable.gas_meter_24).into(holder.transactionImage);
                break;
            case "Subscription":
                Glide.with(context).load(R.drawable.subscriptions_24).into(holder.transactionImage);
                break;
            case "Pay to Someone":
                Glide.with(context).load(R.drawable.payment_24).into(holder.transactionImage);
                break;
            case "Others":
                Glide.with(context).load(R.drawable.others_24).into(holder.transactionImage);
                break;
        }

        holder.title.setText(transcation.getTitle());
        // Getting the Time Stamp
        String timestamp = transcation.getTimestamp();
        long timestampLong = Long.parseLong(timestamp);
        Date date = new Date(timestampLong);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());

        String formattedDate = dateFormat.format(date);
        holder.time.setText(formattedDate);
        if (transcation.getNotes().length() > 15) {
            holder.description.setText(transcation.getNotes().substring(0, 15) + "...");
        } else {
            holder.description.setText(transcation.getNotes());
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView title, time, description;
        ImageView transactionImage;


        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.transaction_name);
            description = itemView.findViewById(R.id.transaction_desc);
            time = itemView.findViewById(R.id.transaction_time);
            transactionImage = itemView.findViewById(R.id.transaction_image);
        }
    }


    public void updateData(ArrayList<Transaction> newTransactions) {
        this.transactions.clear();   // Assuming transactionList is the list used in the adapter
        this.transactions.addAll(newTransactions);
        notifyDataSetChanged();  // Notify the adapter of data changes
    }
}
