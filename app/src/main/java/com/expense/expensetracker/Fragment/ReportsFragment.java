package com.expense.expensetracker.Fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.expense.expensetracker.Database.TranscationDB;
import com.expense.expensetracker.Models.Transaction;
import com.expense.expensetracker.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ReportsFragment extends Fragment {

    private PieChart pieChart, paymentMethodChart;
    private BarChart amountBarChart, locationChart;
    private TranscationDB dbHelper;
    private TextView dateRangeText;

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

        dateRangeText = view.findViewById(R.id.date_range_report);
        dateRangeText.setOnClickListener(view1 -> {
            setupDateDropdown();
        });

        // Generate the report based on the default selection
        generateReports("Last 7 Days");

        return view;
    }

    private void setupDateDropdown() {
        String[] dateRange = {"Today", "Last 7 Days", "Last 30 Days"};
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.date_range_container);
        LinearLayout dateRangeContainer = bottomSheetDialog.findViewById(R.id.dateRangeContainer);

        if (dateRangeContainer != null) {
            for (String range : dateRange) {
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.date_range, dateRangeContainer, false);
                CheckBox checkBox = itemView.findViewById(R.id.checkbox);
                TextView dateTextView = itemView.findViewById(R.id.date_range_value);
                dateTextView.setText(range);

                final String selectedRange = range;

                // Handle checkbox selection
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        Toast.makeText(getContext(), "Range " + selectedRange, Toast.LENGTH_SHORT).show();
                        generateReports(selectedRange);
                        bottomSheetDialog.dismiss();
                    }
                });
                // Add each item view to the container
                dateRangeContainer.addView(itemView);
            }
        }
        bottomSheetDialog.show();
    }

    private void generateReports(String dateRange) {
        generateReportByCategory(dateRange);
        generateReportByAmount(dateRange);
        generateReportByLocation(dateRange);
        generateReportByPaymentMethod(dateRange);
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
        colors.add(ContextCompat.getColor(getContext(), R.color.food));
        colors.add(ContextCompat.getColor(getContext(), R.color.electronics));
        colors.add(ContextCompat.getColor(getContext(), R.color.travel));
        colors.add(ContextCompat.getColor(getContext(), R.color.clothes));
        colors.add(ContextCompat.getColor(getContext(), R.color.house));
        colors.add(ContextCompat.getColor(getContext(), R.color.education));
        colors.add(ContextCompat.getColor(getContext(), R.color.rent));
        colors.add(ContextCompat.getColor(getContext(), R.color.vehicle));
        colors.add(ContextCompat.getColor(getContext(), R.color.electricity));
        colors.add(ContextCompat.getColor(getContext(), R.color.sports));
        colors.add(ContextCompat.getColor(getContext(), R.color.gas));
        colors.add(ContextCompat.getColor(getContext(), R.color.subscription));
        colors.add(ContextCompat.getColor(getContext(), R.color.pay));
        colors.add(ContextCompat.getColor(getContext(), R.color.others));

        // Ensure we match the number of entries
        ArrayList<Integer> finalColors = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            finalColors.add(colors.get(i % colors.size()));
        }

        dataSet.setColors(finalColors);
        dataSet.setSliceSpace(4f);

        pieChart.setDrawEntryLabels(false);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTypeface(Typeface.DEFAULT_BOLD);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setData(pieData);
        pieChart.setCenterText("Categories");
        pieChart.setCenterTextSize(12f);

        // Customize legend appearance
        Legend legend = pieChart.getLegend();
        legend.setYOffset(10f);
        legend.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));  // Set legend text color to white
        legend.setTextSize(12f);
        legend.setFormToTextSpace(10f);
        legend.setWordWrapEnabled(true);

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

        // Check if there are any entries to avoid empty chart
        if (dateTotals.isEmpty()) {
            // Handle the case where no transactions are available
            amountBarChart.clear(); // Clear any existing data
            amountBarChart.invalidate(); // Refresh chart
            return; // Early return to avoid proceeding with an empty dataset
        }

        for (String date : dateTotals.keySet()) {
            // Add a BarEntry with the index and the total amount for that date
            entries.add(new BarEntry(index++, dateTotals.get(date).floatValue()));
        }

        // Create a BarDataSet and assign unique colors
        BarDataSet dataSet = new BarDataSet(entries, "Amounts by Date");
        // Generate unique colors for each category
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(getContext(), R.color.food));
        colors.add(ContextCompat.getColor(getContext(), R.color.electronics));
        colors.add(ContextCompat.getColor(getContext(), R.color.travel));
        colors.add(ContextCompat.getColor(getContext(), R.color.clothes));
        colors.add(ContextCompat.getColor(getContext(), R.color.house));
        colors.add(ContextCompat.getColor(getContext(), R.color.education));
        colors.add(ContextCompat.getColor(getContext(), R.color.rent));
        colors.add(ContextCompat.getColor(getContext(), R.color.vehicle));
        colors.add(ContextCompat.getColor(getContext(), R.color.electricity));
        colors.add(ContextCompat.getColor(getContext(), R.color.sports));
        colors.add(ContextCompat.getColor(getContext(), R.color.gas));
        colors.add(ContextCompat.getColor(getContext(), R.color.subscription));
        colors.add(ContextCompat.getColor(getContext(), R.color.pay));
        colors.add(ContextCompat.getColor(getContext(), R.color.others));

        // Ensure we match the number of entries
        ArrayList<Integer> finalColors = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            finalColors.add(colors.get(i % colors.size()));
        }

        dataSet.setColors(finalColors);

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
                // Safeguard against out-of-bounds index
                int index = (int) value;
                if (index >= 0 && index < dateTotals.size()) {
                    return new ArrayList<>(dateTotals.keySet()).get(index);
                }
                return ""; // Return empty string for out-of-bounds
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

        // Customize legend appearance
        Legend legend = amountBarChart.getLegend();
        legend.setYOffset(10f);
        legend.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));  // Set legend text color to white
        legend.setTextSize(12f);
        legend.setWordWrapEnabled(true);

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

        // Generate unique colors for each category
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(getContext(), R.color.food));
        colors.add(ContextCompat.getColor(getContext(), R.color.electronics));
        colors.add(ContextCompat.getColor(getContext(), R.color.travel));
        colors.add(ContextCompat.getColor(getContext(), R.color.clothes));
        colors.add(ContextCompat.getColor(getContext(), R.color.house));
        colors.add(ContextCompat.getColor(getContext(), R.color.education));
        colors.add(ContextCompat.getColor(getContext(), R.color.rent));
        colors.add(ContextCompat.getColor(getContext(), R.color.vehicle));
        colors.add(ContextCompat.getColor(getContext(), R.color.electricity));
        colors.add(ContextCompat.getColor(getContext(), R.color.sports));
        colors.add(ContextCompat.getColor(getContext(), R.color.gas));
        colors.add(ContextCompat.getColor(getContext(), R.color.subscription));
        colors.add(ContextCompat.getColor(getContext(), R.color.pay));
        colors.add(ContextCompat.getColor(getContext(), R.color.others));

        // Ensure we match the number of entries
        ArrayList<Integer> finalColors = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            finalColors.add(colors.get(i % colors.size()));
        }

        dataSet.setColors(finalColors);
        dataSet.setSliceSpace(5f);

        PieData pieData = new PieData(dataSet);

        paymentMethodChart.setDrawEntryLabels(false);
        paymentMethodChart.setUsePercentValues(true);
        paymentMethodChart.getDescription().setEnabled(false);

        // Set the data to the chart
        pieData.setValueTextSize(10f);
        pieData.setValueTypeface(Typeface.DEFAULT_BOLD);
        pieData.setValueTextColor(Color.WHITE);
        paymentMethodChart.setData(pieData);
        paymentMethodChart.setCenterText("Payment Methods");
        paymentMethodChart.setCenterTextSize(12f);
//        paymentMethodChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);


        // Customize legend appearance
        Legend legend = paymentMethodChart.getLegend();
        legend.setYOffset(10f);
        legend.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor)); // Set legend text color to white
        legend.setTextSize(12f);
        legend.setFormToTextSpace(10f);
        legend.setWordWrapEnabled(true);

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


        // Generate unique colors for each category
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(getContext(), R.color.food));
        colors.add(ContextCompat.getColor(getContext(), R.color.electronics));
        colors.add(ContextCompat.getColor(getContext(), R.color.travel));
        colors.add(ContextCompat.getColor(getContext(), R.color.clothes));
        colors.add(ContextCompat.getColor(getContext(), R.color.house));
        colors.add(ContextCompat.getColor(getContext(), R.color.education));
        colors.add(ContextCompat.getColor(getContext(), R.color.rent));
        colors.add(ContextCompat.getColor(getContext(), R.color.vehicle));
        colors.add(ContextCompat.getColor(getContext(), R.color.electricity));
        colors.add(ContextCompat.getColor(getContext(), R.color.sports));
        colors.add(ContextCompat.getColor(getContext(), R.color.gas));
        colors.add(ContextCompat.getColor(getContext(), R.color.subscription));
        colors.add(ContextCompat.getColor(getContext(), R.color.pay));
        colors.add(ContextCompat.getColor(getContext(), R.color.others));

        // Ensure we match the number of entries
        ArrayList<Integer> finalColors = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            finalColors.add(colors.get(i % colors.size()));
        }

        dataSet.setColors(finalColors);

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
                    return locationList.get((int) value - 1);
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

        // Customize legend appearance
        Legend legend = locationChart.getLegend();
        legend.setYOffset(10f);
        legend.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));  // Set legend text color to white
        legend.setTextSize(12f);
        legend.setFormToTextSpace(10f);
        legend.setWordWrapEnabled(true);

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