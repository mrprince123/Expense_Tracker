package com.expense.expensetracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.expense.expensetracker.Models.Transaction;

import java.util.ArrayList;

public class TranscationDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseTracker.db";
    private static final int DATABASE_VERSION = 2; // Incremented version to create/update tables

    // Table Names
    private static final String TABLE_TRANSACTION = "transactions";
    private static final String TABLE_INCOME = "income_table";

    // Column Names for transactions
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_PAYMENT_METHOD = "payment_method";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_RECEIPT = "receipt";
    private static final String COLUMN_NOTES = "notes";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    // Column Names for income
    private static final String COLUMN_INCOME_BALANCE = "income_balance";

    public TranscationDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public TranscationDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create SQL table for transactions
        String CREATE_TABLE_TRANSACTION = "CREATE TABLE " + TABLE_TRANSACTION + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_AMOUNT + " REAL,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_PAYMENT_METHOD + " TEXT,"
                + COLUMN_LOCATION + " TEXT,"
                + COLUMN_RECEIPT + " TEXT,"
                + COLUMN_NOTES + " TEXT,"
                + COLUMN_TIMESTAMP + " TEXT DEFAULT CURRENT_TIMESTAMP" + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE_TRANSACTION);

        // Create income table
        String CREATE_TABLE_INCOME = "CREATE TABLE " + TABLE_INCOME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_INCOME_BALANCE + " REAL DEFAULT 0)";
        sqLiteDatabase.execSQL(CREATE_TABLE_INCOME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop older tables if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME);
        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public long insertTransaction(String title, String date, double amount, String category, String paymentMethod, String location, String receipt, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_PAYMENT_METHOD, paymentMethod);
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_RECEIPT, receipt);
        values.put(COLUMN_NOTES, notes);
        values.put(COLUMN_TIMESTAMP, String.valueOf(System.currentTimeMillis())); // Set current timestamp

        // Insert row
        long id = db.insert(TABLE_TRANSACTION, null, values);
        db.close();
        return id;
    }

    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSACTION, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                double amount = cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT));
                String category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
                String paymentMethod = cursor.getString(cursor.getColumnIndex(COLUMN_PAYMENT_METHOD));
                String location = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION));
                String receipt = cursor.getString(cursor.getColumnIndex(COLUMN_RECEIPT));
                String notes = cursor.getString(cursor.getColumnIndex(COLUMN_NOTES));
                String timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP));

                // Create a Transaction object and add it to the list
                transactions.add(new Transaction(id, title, date, amount, category, paymentMethod, location, receipt, notes, timestamp));

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close(); // Close the database connection
        return transactions;
    }

    public void deleteTransactionById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTION, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close(); // Close the Database connection
    }

    public double getTotalAmount() {
        double totalAmount = 0.0;
        SQLiteDatabase db = this.getReadableDatabase();

        // Execute SQL query to get the sum of the amount column
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_AMOUNT + ") AS total FROM " + TABLE_TRANSACTION, null);

        if (cursor != null && cursor.moveToFirst()) {
            totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
            cursor.close();
        }

        db.close(); // Close the database connection
        return totalAmount; // Return the total amount
    }

    public void setIncomeBalance(double income) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INCOME_BALANCE, income);

        // Check if there's an existing entry; if so, update it; otherwise, insert a new one
        int rowsAffected = db.update(TABLE_INCOME, values, null, null);
        if (rowsAffected == 0) { // No rows were updated, so insert a new entry
            db.insert(TABLE_INCOME, null, values);
        }
        db.close();
    }

    public double getIncomeBalance() {
        double incomeBalance = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_INCOME_BALANCE + " FROM " + TABLE_INCOME, null);

        if (cursor != null && cursor.moveToFirst()) {
            incomeBalance = cursor.getDouble(0);
            cursor.close();
        } else if (cursor != null) {
            cursor.close(); // Ensure cursor is closed
        }
        db.close(); // Close the database connection
        return incomeBalance;
    }
}
