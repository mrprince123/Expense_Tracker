package com.expense.expensetracker.Models;

public class Transaction {

    // Expense Title
    // Date
    // Amount
    // Category
    // Payment Method
    // Location
    // Receipt
    // Notes

    // Fields
    private long id;
    private String title; // Expense Title
    private String date;    // Date of Transaction
    private double amount; // Amount spent
    private String category; // Category of the expense (e.g., Food, Travel)
    private String paymentMethod; // Payment method (e.g., Cash, Credit Card)
    private String location; // Location where the transaction took place
    private String receipt; // Path to the receipt image/file (optional)
    private String notes; // Additional notes for the transaction
    private String timestamp; // Timestamp of when the transaction was created or modified


    public Transaction(long id, String title, String date, double amount, String category, String paymentMethod, String location, String receipt, String notes, String timestamp) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.location = location;
        this.receipt = receipt;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
