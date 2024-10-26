package com.expense.expensetracker.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        addTransactionButton = view.findViewById(R.id.add_transaction);
        addTransactionButton.setOnClickListener(view1 -> {
            addTransaction();
        });

        editIncomeBalance = view.findViewById(R.id.edit_income_balance);
        editIncomeBalance.setOnClickListener(view1 -> setIncomeBalance());

        dbHelper = new TranscationDB(getContext());

        recyclerView = view.findViewById(R.id.recent_transaction);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        transcationArrayList = dbHelper.getAllTransactions();
        if (transcationArrayList == null) {
            transcationArrayList = new ArrayList<>();
        }

        transactionAdapter = new TransactionAdapter(getContext(), transcationArrayList);
        recyclerView.setAdapter(transactionAdapter);

        double totalAmount = dbHelper.getTotalAmount();

        TextView totalExpendature = view.findViewById(R.id.total_expendature);
        // Set the total amount as a formatted string in the TextView
        totalExpendature.setText(String.format("%.2f", totalAmount));

        double totalIncome = dbHelper.getIncomeBalance();
        totalIncomeBalance = view.findViewById(R.id.total_income_balance);
        totalIncomeBalance.setText(String.format("%.2f", totalIncome));



        settingButton = view.findViewById(R.id.settings_button);
        settingButton.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), SettingActivity.class));
        });

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
        String[] categoryItem = new String[]{"Food", "Electronics", "Travel", "House Hold", "Education", "Rent", "Vehicle Gas", "Electricity Bill", "Gas Bill", "Subscription", "Pay to Someone", "Others"};
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
//            Toast.makeText(getContext(), "Image selected: " + receiptUri.toString(), Toast.LENGTH_SHORT).show();
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

}