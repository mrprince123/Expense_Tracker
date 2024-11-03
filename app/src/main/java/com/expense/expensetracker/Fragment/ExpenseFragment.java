package com.expense.expensetracker.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.expense.expensetracker.Activity.AboutAppActivity;
import com.expense.expensetracker.Adapter.ExpenseAdapter;
import com.expense.expensetracker.Models.Expense;
import com.expense.expensetracker.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpenseFragment extends Fragment {

    private static final int SMS_PERMISSION_CODE = 100;
    private RecyclerView smsListView;
    private TextView totalDebitAmount, totalCreditAmount;
    private ArrayList<Expense> smsList;
    private ExpenseAdapter adapter;
    private double totalCardAmount = 0.0;
    private double totalUPIAmount = 0.0;
    private TextView dateRangePicker;
    private ImageView infoButton;

    private long dateRangeStartTimestamp = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense, container, false);


        smsListView = view.findViewById(R.id.smsListView);
        totalDebitAmount = view.findViewById(R.id.debitCardTotalAmount);
        totalCreditAmount = view.findViewById(R.id.creditCardTotalAmount);
        smsList = new ArrayList<>();

        smsListView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseAdapter(requireContext(), smsList);
        smsListView.setAdapter(adapter);

        // Info Dialog Box
        infoButton = view.findViewById(R.id.info_button);
        infoButton.setOnClickListener(view1 -> {
            new MaterialAlertDialogBuilder(getContext())
                    .setView(R.layout.item_info)
                    .setPositiveButton("Learn More", (dialog, which) -> {
                        startActivity(new Intent(getContext(), AboutAppActivity.class));
                    })
                    .setNegativeButton("Ok", (dialog, which) -> {

                    })
                    .show();
        });


        dateRangePicker = view.findViewById(R.id.date_range_expense);
        dateRangePicker.setOnClickListener(view1 -> {
            setupDateDropdown();
        });

        // Check and request SMS permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestSmsPermission();
        } else {
            fetchSmsData();
        }

        return view;
    }


    private void setupDateDropdown() {
        // Define date range options
        String[] dateRange = {"Today", "Last 7 Days", "Last 30 Days", "Last 90 Days", "Last 180 Days", "Last 365 Days", "All Time"};

        // Create Bottom Sheet Dialog and set layout
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.date_range_container);

        // Get the container in which we will add date options
        LinearLayout dateRangeContainer = bottomSheetDialog.findViewById(R.id.dateRangeContainer);

        if (dateRangeContainer != null) {
            // Loop through each date range option
            for (String range : dateRange) {
                // Inflate each item view
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.date_range, dateRangeContainer, false);

                // Get the CheckBox and TextView from the layout
                CheckBox checkBox = itemView.findViewById(R.id.checkbox);
                TextView dateTextView = itemView.findViewById(R.id.date_range_value);

                // Set the date range text
                dateTextView.setText(range);

                // Handle checkbox selection
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        // Perform actions based on the selected date range
                        switch (range) {
                            case "Today":
                                dateRangeStartTimestamp = getStartOfToday();
                                break;
                            case "Last 7 Days":
                                dateRangeStartTimestamp = getStartOfLast7Days();
                                break;
                            case "Last 30 Days":
                                dateRangeStartTimestamp = getStartOfLast30Days();
                                break;
                            case "Last 90 Days":
                                dateRangeStartTimestamp = getStartOfLast90Days();
                                break;
                            case "Last 180 Days":
                                dateRangeStartTimestamp = getStartOfLast180Days();
                                break;
                            case "Last 365 Days":
                                dateRangeStartTimestamp = getStartOfLast365Days();
                                break;
                            case "All Time":
                                dateRangeStartTimestamp = 0;
                                break;
                        }
                        fetchSmsData(); // Re-fetch data with the updated range
                        bottomSheetDialog.dismiss(); // Close the bottom sheet after selection
                    }
                });
                // Add each item view to the container
                dateRangeContainer.addView(itemView);
            }
        }
        // Show the Bottom Sheet Dialog
        bottomSheetDialog.show();
    }

    private long getStartOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getStartOfLast7Days() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getStartOfLast30Days() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getStartOfLast90Days() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -90);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getStartOfLast180Days() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -180);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getStartOfLast365Days() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -365);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private void requestSmsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchSmsData();
            }
        }
    }

    private void fetchSmsData() {
        try {
            Uri uri = Uri.parse("content://sms/inbox");
            String selection = "date >= ?";
            String[] selectionArgs = new String[]{String.valueOf(dateRangeStartTimestamp)};

            Cursor cursor = requireContext().getContentResolver().query(uri, new String[]{"address", "body", "date"}, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                smsList.clear();
                totalCardAmount = 0.0;
                totalUPIAmount = 0.0;

                do {
                    String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                    String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                    long date = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
                    // Filter based on transactional keywords
                    if (isTransactionMessage(body)) {
                        double amount = extractAmountFromSms(body);

                        if (amount > 0 && amount <= 99999) {
                            Expense expense = new Expense(address, body, amount, date);
                            if (address.contains("CRD") || address.contains("CARD") || address.contains("card")) {
                                totalCardAmount += amount;
                            } else if (body.contains("UPI") || body.contains("transfer")) {
                                totalUPIAmount += amount;
                            }
                            smsList.add(expense);
                        }
                    }
                } while (cursor.moveToNext());

                cursor.close();
                adapter.notifyDataSetChanged();

                totalDebitAmount.setText("₹ " + String.format("%.2f", totalUPIAmount));
                totalCreditAmount.setText("₹ " + String.format("%.2f", totalCardAmount));
            } else {
                Toast.makeText(getContext(), "No transaction SMS found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("SMS", "Error fetching SMS: " + e.getMessage());
            Toast.makeText(getContext(), "Error fetching SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Helper function to check if an SMS is a transaction message
    private boolean isTransactionMessage(String body) {
        String[] transactionKeywords = {"debited", "credited", "withdrawn", "spent", "purchase", "transaction", "transfer"};
        for (String keyword : transactionKeywords) {
            if (body.toLowerCase().contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    // Extract Amount from the Message Body
    private double extractAmountFromSms(String smsBody) {
        String regex = "(?i)(?:(?:RS|INR|MRP)\\.?\\s?)(\\d+(:?\\,\\d+)?(\\,\\d+)?(\\.\\d{1,2})?)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(smsBody);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1).replace(",", ""));
        }
        return 0.0;
    }

    // Extract Merchant Name from the SMS
    private String extractMerchantNameFromSMS() {
        String regex = "(?i)(?:\\sat\\s|in\\*)([A-Za-z0-9]*\\s?-?\\s?[A-Za-z0-9]*\\s?-?\\.?)";

        return null;
    }

    // Find out the card name (debit/Credit Card)from the bank transaction messages
    private String extractCardNameFromSMS() {
        String regex = "(?i)(?:\\smade on|ur|made a\\s|in\\*)([A-Za-z]*\\s?-?\\s[A-Za-z]*\\s?-?\\s[A-Za-z]*\\s?-?)";

        return null;
    }
}
