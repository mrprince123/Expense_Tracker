package com.expense.expensetracker.Models;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Expense {

    private String messageTitle;
    private String messageBody;
    private double amount;
    private long date; // Add a date field

    public Expense(String messageTitle, String messageBody, double amount, long date) {
        this.messageTitle = messageTitle;
        this.messageBody = messageBody;
        this.amount = amount;
        this.date = date;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public double getAmount() {
        return amount;
    }


    public long getDate() {
        return date;
    }

    // Optionally, add a method to format the date as a string
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        return sdf.format(new Date(date));
    }
}
