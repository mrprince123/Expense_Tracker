package com.expense.expensetracker.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.expense.expensetracker.Activity.SettingActivity;
import com.expense.expensetracker.Adapter.TransactionAdapter;
import com.expense.expensetracker.Database.TranscationDB;
import com.expense.expensetracker.Models.Transaction;
import com.expense.expensetracker.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    Button addTransactionButton;
    private TranscationDB dbHelper;
    private RecyclerView recyclerView;
    private ArrayList<Transaction> transcationArrayList;
    private TransactionAdapter transactionAdapter;
    private Uri receiptUri;
    String receipt;
    Button inputReceiptButton;
    ImageView receiptImage, editIncomeBalance;
    TextView totalIncomeBalance;
    ImageView settingButton;

    PieChart pieChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Get all the Recent Transactions
        dbHelper = new TranscationDB(getContext());

        // Get the Pie Chart View and set the Value by calling the function
        pieChart  = view.findViewById(R.id.monthly_chart);
        getMonthlyReport();


        // Add the New Transaction Button
        addTransactionButton = view.findViewById(R.id.add_transaction);
        addTransactionButton.setOnClickListener(view1 -> {
            addTransaction();
        });

        // Edit the Income Balance
        editIncomeBalance = view.findViewById(R.id.edit_income_balance);
        editIncomeBalance.setOnClickListener(view1 -> setIncomeBalance());

        // Total Amount After Addition
        double totalAmount = dbHelper.getTotalAmount();
        TextView totalExpendature = view.findViewById(R.id.total_expendature);
        totalExpendature.setText(String.format("%.2f", totalAmount));

        // Set Total Income Amount
        double totalIncome = dbHelper.getIncomeBalance();
        totalIncomeBalance = view.findViewById(R.id.total_income_balance);
        totalIncomeBalance.setText(String.format("%.2f", totalIncome));

        // Navigate to the Setting Page
        settingButton = view.findViewById(R.id.settings_button);
        settingButton.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), SettingActivity.class));
        });


        recyclerView = view.findViewById(R.id.recent_transaction);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

            transcationArrayList = dbHelper.getLatestTransactions();
            if (transcationArrayList == null) {
                transcationArrayList = new ArrayList<>();
            }

        transactionAdapter = new TransactionAdapter(getContext(), transcationArrayList);
        recyclerView.setAdapter(transactionAdapter);

        return view;
    }

    void addTransaction() {

        dbHelper = new TranscationDB(getContext());
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_transaction_box, null);

        inputReceiptButton = dialogView.findViewById(R.id.input_receipt);
        receiptImage = dialogView.findViewById(R.id.receipt_image_view);

        inputReceiptButton.setOnClickListener(view -> openImagePicker());

        // Prepare the adapter for the AutoCompleteTextView
        String[] categoryItem = new String[]{"Food", "Electronics", "Travel", "Clothes", "House Hold", "Education", "Rent", "Vehicle Gas", "Electricity Bill", "Sports", "Gas Bill", "Subscription", "Pay to Someone", "Others"};
        AutoCompleteTextView transactionCategory = dialogView.findViewById(R.id.transaction_category);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, categoryItem);
        transactionCategory.setAdapter(adapter);
        transactionCategory.setThreshold(1);

        String[] paymentMethods = new String[]{"Credit Card", "Debit Card", "UPI", "CASH", "COUPAN"};
        AutoCompleteTextView transactionPaymentMethod = dialogView.findViewById(R.id.input_payment_method);
        ArrayAdapter<String> adapterPayment = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, paymentMethods);
        transactionPaymentMethod.setAdapter(adapterPayment);
        transactionPaymentMethod.setThreshold(1);


        // Open the material alert box
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Add Transaction")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {

                    EditText titleInput = dialogView.findViewById(R.id.input_title);
                    EditText dateInput = dialogView.findViewById(R.id.input_date);
                    EditText amountInput = dialogView.findViewById(R.id.input_amount);
                    EditText paymentMethodInput = dialogView.findViewById(R.id.input_payment_method);
                    EditText notesInput = dialogView.findViewById(R.id.input_notes);
                    EditText locationInput = dialogView.findViewById(R.id.input_location);
                    String title = titleInput.getText().toString();
                    double amount = Double.parseDouble(amountInput.getText().toString());
                    String dateString = dateInput.getText().toString();
                    String category = transactionCategory.getText().toString();
                    String paymentMethod = transactionPaymentMethod.getText().toString();
                    String location = locationInput.getText().toString();

                    if (receiptUri != null) {
                        receipt = receiptUri.toString();  // Save URI as string
                        receiptImage.setImageURI(receiptUri);
                    }

                    String notes = notesInput.getText().toString();

                    long transactionId = dbHelper.insertTransaction(
                            title,
                            dateString,
                            amount,
                            category,
                            paymentMethod,
                            location,
                            receipt,
                            notes
                    );

                    if (transactionId != -1) {
                        // Success
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                        updateTransactionList();
                    } else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle the Cancel button action (optional)
                    dialog.dismiss();
                })
                .show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            receiptUri = data.getData();
            if (receiptImage != null) {
                try {
                    // Load the bitmap at full resolution
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), receiptUri);
                    receiptImage.setImageBitmap(bitmap);  // Set the bitmap to the ImageView
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void updateTransactionList() {
        transcationArrayList = dbHelper.getAllTransactions();
        transactionAdapter.updateData(transcationArrayList);  // Call the adapter’s updateData method
    }

    private void setIncomeBalance() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.income_balance_input, null);

        TextInputEditText inputEditText = dialogView.findViewById(R.id.input_income_balance);


        // Create the dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Enter Income Balance");
        builder.setView(dialogView); // Set the EditText as the dialog view

        // Set positive button action
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Retrieve user input
            String inputValue = inputEditText.getText().toString();
            if (!inputValue.isEmpty()) {
                double incomeBalance = Double.parseDouble(inputValue);
                dbHelper.setIncomeBalance(incomeBalance);
                Toast.makeText(getContext(), "Income Balance Saved: " + incomeBalance, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });

        // Set negative button action
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.show();
    }

    private void getMonthlyReport() {
        ArrayList<Transaction> transactions = dbHelper.getAllTransactions();
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
            // Calculate percentage based on the occurrence count
            double percentage = ((double) categoryCount.get(category) / totalTransactions) * 100;
            entries.add(new PieEntry((float) percentage, category)); // Add percentage to PieEntry
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

        // Customize legend appearance
        Legend legend = pieChart.getLegend();
        legend.setTextColor(Color.WHITE); // Set legend text color to white
        legend.setTextSize(12f);
        legend.setWordWrapEnabled(true);

        pieChart.invalidate(); // Refresh the chart
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.close(); // Ensure the database is closed when the fragment is destroyed
    }
}