package com.expense.expensetracker.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.expense.expensetracker.Adapter.ExpenseAdapter;
import com.expense.expensetracker.Models.Expense;
import com.expense.expensetracker.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Calendar;

public class ExpenseFragment extends Fragment {

    private static final int SMS_PERMISSION_CODE = 100;
    private RecyclerView smsListView;
    private TextView totalDebitAmount, totalCreditAmount;
    private ArrayList<Expense> smsList;
    private ExpenseAdapter adapter;
    private double totalCardAmount = 0.0;
    private double totalUPIAmount = 0.0;
    private AutoCompleteTextView dateAutoComplete;

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


        dateAutoComplete = view.findViewById(R.id.date_range_expense);
        setupDateDropdown();

        // Check and request SMS permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestSmsPermission();
        } else {
            fetchSmsData();
        }

        return view;
    }

    private void setupDateDropdown() {
        String[] dateRange = new String[]{"Today", "Last 7 Days", "Last 30 Days", "Last 90 Days", "Last 180 Days", "Last 365 Days", "All Time"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, dateRange);
        dateAutoComplete.setAdapter(adapter);

        dateAutoComplete.setOnItemClickListener((parent, view1, position, id) -> {
            switch (position) {
                case 0: dateRangeStartTimestamp = getStartOfToday(); break;
                case 1: dateRangeStartTimestamp = getStartOfLast7Days(); break;
                case 2: dateRangeStartTimestamp = getStartOfLast30Days(); break;
                case 3: dateRangeStartTimestamp = getStartOfLast90Days(); break;
                case 4: dateRangeStartTimestamp = getStartOfLast180Days(); break;
                case 5: dateRangeStartTimestamp = getStartOfLast365Days(); break;
                case 6: dateRangeStartTimestamp = 0; break;
            }
            fetchSmsData(); // Re-fetch data with the updated range
            dateAutoComplete.clearFocus(); // Reset focus to ensure dropdown works again
        });
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
                    if (isTransactionMessage(address)) {
                        double amount = extractAmountFromSms(body);

                        if (amount > 0 && amount <= 99999) {
                            Expense expense = new Expense(address, body, amount, date);
                            if (address.contains("SBICRD")) {
                                totalCardAmount += amount;

                            } else if (address.contains("SBIUPI")) {
                                totalUPIAmount += amount;
                            }
                            smsList.add(expense);
                        }
                    }
                } while (cursor.moveToNext());

                cursor.close();
                adapter.notifyDataSetChanged();

                totalDebitAmount.setText("Total UPI: ₹" + String.format("%.2f", totalUPIAmount));
                totalCreditAmount.setText("Total Card: ₹" + String.format("%.2f", totalCardAmount));
            } else {
                Toast.makeText(getContext(), "No transaction SMS found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("SMS", "Error fetching SMS: " + e.getMessage());
            Toast.makeText(getContext(), "Error fetching SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isTransactionMessage(String smsAddress) {
        String lowerCaseAddress = smsAddress.toLowerCase();
        return lowerCaseAddress.contains("sbiupi") || lowerCaseAddress.contains("sbicrd") || lowerCaseAddress.contains("juspay");
    }

    private double extractAmountFromSms(String smsBody) {
        String regex = "₹?(\\d+(?:,\\d{3})*(?:\\.\\d{1,2})?)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(smsBody);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1).replace(",", ""));
        }
        return 0.0;
    }
}
