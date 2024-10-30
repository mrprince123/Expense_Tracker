package com.expense.expensetracker.Fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.expense.expensetracker.Database.TranscationDB;
import com.expense.expensetracker.Models.Transaction;
import com.expense.expensetracker.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ReportsFragment extends Fragment {

    private PieChart pieChart, paymentMethodChart;
    private BarChart amountBarChart, locationChart;
    private TranscationDB dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        // Initialize database helper and chart
        dbHelper = new TranscationDB(getContext());
        pieChart = view.findViewById(R.id.chart_category);
        locationChart = view.findViewById(R.id.chart_location);
        paymentMethodChart = view.findViewById(R.id.chart_payment_method);
        amountBarChart = view.findViewById(R.id.bar_chart_amount);


        String[] dateRange = new String[]{"Today", "Last 7 Days", "Last 30 Days"};
        AutoCompleteTextView dateAutoComplete = view.findViewById(R.id.date_picker_range);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, dateRange);
        dateAutoComplete.setAdapter(adapter);
        dateAutoComplete.setThreshold(1);

        // Generate the report based on the default selection
        generateReportByCategory("Last 7 Days");
        generateReportByAmount("Last 7 Days");
        generateReportByLocation("Last 7 Days");
        generateReportByPaymentMethod("Last 7 Days");

        // Set item click listener to capture selected date range
        dateAutoComplete.setOnItemClickListener((parent, view1, position, id) -> {
            String DateValue = dateAutoComplete.getText().toString();

            generateReportByCategory(DateValue);
            generateReportByAmount(DateValue);
            generateReportByLocation(DateValue);
            generateReportByPaymentMethod(DateValue);
        });

        return view;
    }

    private void generateReportByCategory(String dateRange) {
        ArrayList<Transaction> transactions = filterTransactionsByDateRange(dbHelper.getAllTransactions(), dateRange);
        HashMap<String, Integer> categoryCount = new HashMap<>();
        int totalTransactions = transactions.size();

        // Count the number of occurrences of each category
        for (Transaction transaction : transactions) {
            String category = transaction.getCategory();
            categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
        }

        // Prepare data for the pie chart, converting counts to percentages
        List<PieEntry> entries = new ArrayList<>();
        for (String category : categoryCount.keySet()) {
            double percentage = ((double) categoryCount.get(category) / totalTransactions) * 100;
            entries.add(new PieEntry((float) percentage, category));
        }

        // Create a PieDataSet and assign unique colors
        PieDataSet dataSet = new PieDataSet(entries, "Categories");

        // Generate unique colors for each category
        ArrayList<Integer> colors = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            colors.add(ColorTemplate.COLORFUL_COLORS[i % ColorTemplate.COLORFUL_COLORS.length]);
        }
        dataSet.setColors(colors);
        dataSet.setSliceSpace(4f);

        pieChart.setDrawEntryLabels(false);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(12f);
        pieData.setValueTypeface(Typeface.DEFAULT_BOLD);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setData(pieData);
        pieChart.setCenterText("Categories");
        pieChart.setCenterTextSize(15f);
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        pieChart.invalidate(); // Refresh the chart
    }

    private void generateReportByAmount(String dateRange) {

        ArrayList<Transaction> transactions = filterTransactionsByDateRange(dbHelper.getAllTransactions(), dateRange);
        HashMap<String, Double> dateTotals = new HashMap<>();

        // Calculate total amounts by date
        for (Transaction transaction : transactions) {
            String date = transaction.getDate();
            double amount = transaction.getAmount();
            dateTotals.put(date, dateTotals.getOrDefault(date, 0.0) + amount);
        }

        // Prepare data for the bar chart
        List<BarEntry> entries = new ArrayList<>();
        int index = 0;
        for (String date : dateTotals.keySet()) {
            // Add a BarEntry with the index and the total amount for that date
            entries.add(new BarEntry(index++, dateTotals.get(date).floatValue()));
        }

        // Create a BarDataSet and assign unique colors
        BarDataSet dataSet = new BarDataSet(entries, "Amounts by Date");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS); // Set colors

        // Create BarData with the dataset
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        // Set up the bar chart
        amountBarChart.setData(barData);
        amountBarChart.setFitBars(true);

        // Customize x-axis to display dates
        XAxis xAxis = amountBarChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return new ArrayList<>(dateTotals.keySet()).get((int) value);
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Display one label per entry
        xAxis.setDrawGridLines(false);

        // Enable Horizontal Scrolling
        amountBarChart.setDragEnabled(true);
        amountBarChart.setScaleEnabled(true);
        amountBarChart.setVisibleXRangeMaximum(5);

        // Additional styling
        amountBarChart.getAxisLeft().setDrawGridLines(false);
        amountBarChart.getAxisRight().setEnabled(false); // Hide right axis
        amountBarChart.getDescription().setEnabled(false); // Disable description text
        amountBarChart.getLegend().setEnabled(true); // Show legend
        amountBarChart.getLegend().setTextSize(12f); // Customize legend text size

        amountBarChart.invalidate();
    }

    private void generateReportByPaymentMethod(String dateRange) {
        ArrayList<Transaction> transactions = filterTransactionsByDateRange(dbHelper.getAllTransactions(), dateRange);
        HashMap<String, Integer> paymentCount = new HashMap<>();
        int totalTransactions = transactions.size();

        // Count the number of occurrences of each payment method
        for (Transaction transaction : transactions) {
            String payment = transaction.getPaymentMethod();

            // Count how many times each payment method occurs
            paymentCount.put(payment, paymentCount.getOrDefault(payment, 0) + 1);
        }

        // Prepare data for the pie chart, converting counts to percentages
        List<PieEntry> entries = new ArrayList<>();
        for (String payment : paymentCount.keySet()) {
            // Calculate percentage based on the occurrence count
            double percentage = ((double) paymentCount.get(payment) / totalTransactions) * 100;
            entries.add(new PieEntry((float) percentage, payment)); // Add percentage to PieEntry
        }

        // Create a PieDataSet and assign unique colors
        PieDataSet dataSet = new PieDataSet(entries, "Payment Methods");

        // Generate unique colors for each payment method
        ArrayList<Integer> colors = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            colors.add(ColorTemplate.COLORFUL_COLORS[i % ColorTemplate.COLORFUL_COLORS.length]);
        }
        dataSet.setColors(colors);
        dataSet.setSliceSpace(5f);

        PieData pieData = new PieData(dataSet);

        paymentMethodChart.setDrawEntryLabels(false);
        paymentMethodChart.setUsePercentValues(true);
        paymentMethodChart.getDescription().setEnabled(false);

        // Set the data to the chart
        pieData.setValueTextSize(12f);
        pieData.setValueTypeface(Typeface.DEFAULT_BOLD);
        pieData.setValueTextColor(Color.WHITE);
        paymentMethodChart.setData(pieData);
        paymentMethodChart.setCenterText("Payment Methods");
        paymentMethodChart.setCenterTextSize(15f);
        paymentMethodChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        paymentMethodChart.invalidate();
    }

    private void generateReportByLocation(String dateRange) {
        ArrayList<Transaction> transactions = filterTransactionsByDateRange(dbHelper.getAllTransactions(), dateRange);
        HashMap<String, Integer> locationCount = new HashMap<>();

        // Count the number of transactions by location
        for (Transaction transaction : transactions) {
            String location = transaction.getLocation();

            // Increment the count for each location
            locationCount.put(location, locationCount.getOrDefault(location, 0) + 1);
        }

        // Prepare data for the bar chart
        List<BarEntry> entries = new ArrayList<>();
        List<String> locationList = new ArrayList<>(locationCount.keySet()); // Store locations in a list
        int index = 1;

        for (String location : locationList) {
            entries.add(new BarEntry(index++, locationCount.get(location).floatValue()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Transactions by Location");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // Create BarData with the dataset
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        // Set data to the chart
        locationChart.setData(barData);
        locationChart.setFitBars(true);

        // Set up the x-axis to display locations
        XAxis xAxis = locationChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                if (value >= 1 && value < locationList.size()) {
                    return locationList.get((int) value-1);
                }
                return "";
            }
        });

        // Customize the X-axis appearance
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);


        // Customize the Y-axis
        locationChart.getAxisLeft().setDrawGridLines(false);
        locationChart.getAxisRight().setEnabled(false);
        locationChart.getDescription().setEnabled(false);
        locationChart.getLegend().setEnabled(true);
        locationChart.getLegend().setTextSize(12f);

        // Refresh the chart
        locationChart.invalidate();
    }

    private ArrayList<Transaction> filterTransactionsByDateRange(ArrayList<Transaction> transactions, String dateRange) {
        ArrayList<Transaction> filteredTransactions = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // For "Today" scenario, we can use just the current date
        Date today = new Date(calendar.getTimeInMillis());

        for (Transaction transaction : transactions) {
            // Get the string timestamp from the transaction
            String stringTimestamp = transaction.getTimestamp(); // Assuming getTimestamp() returns a String
            long timestampLong = Long.parseLong(stringTimestamp);
            Date transactionDate = new Date(timestampLong);

            switch (dateRange) {
                case "Today":
                    if (isSameDay(transactionDate, today)) {
                        filteredTransactions.add(transaction);
                    }
                    break;

                case "Last 7 Days":
                    calendar.setTime(today); // Reset calendar to today
                    calendar.add(Calendar.DAY_OF_YEAR, -7);
                    if (transactionDate.after(calendar.getTime()) || isSameDay(transactionDate, (Date) calendar.getTime())) {
                        filteredTransactions.add(transaction);
                    }
                    break;

                case "Last 30 Days":
                    calendar.setTime(today); // Reset calendar to today
                    calendar.add(Calendar.DAY_OF_YEAR, -30);
                    if (transactionDate.after(calendar.getTime()) || isSameDay(transactionDate, (Date) calendar.getTime())) {
                        filteredTransactions.add(transaction);
                    }
                    break;
            }
        }

        return filteredTransactions;
    }

    // Helper method to compare if two dates are the same day
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
