package com.expense.expensetracker.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.expense.expensetracker.Database.TranscationDB;
import com.expense.expensetracker.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.text.DecimalFormat;

public class DetailTransactionActivity extends AppCompatActivity {

    double amount;
    String title, date, category, paymentMethod, location, receipt, notes;
    long id;
    TextView expenseTitle, expenseDate, expenseAmount, expenseCategory, expensePaymentMethod, expenseNotes, expenseLocation;
    ImageView receiptImage;
    Button transactionDelete;

    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_transaction);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportActionBar().hide();


        transactionDelete = findViewById(R.id.transaction_delete_button);
        transactionDelete.setOnClickListener(view -> {
            deleteTransaction();
        });

        backButton = findViewById(R.id.expense_back_button);
        backButton.setOnClickListener(view -> {
            finish();
        });

        id = getIntent().getLongExtra("id", -1);
        title = getIntent().getStringExtra("title");
        date = getIntent().getStringExtra("date");
        amount = getIntent().getDoubleExtra("amount", 0);
        category = getIntent().getStringExtra("category");
        paymentMethod = getIntent().getStringExtra("paymentMethod");
        location = getIntent().getStringExtra("location");
        receipt = getIntent().getStringExtra("receipt");
        notes = getIntent().getStringExtra("notes");


        expenseTitle = findViewById(R.id.expense_title);
        expenseDate = findViewById(R.id.transcation_date);
        expenseAmount = findViewById(R.id.expense_amount);
        expenseCategory = findViewById(R.id.transcation_category);
        expensePaymentMethod = findViewById(R.id.transaction_payment_method);
        expenseNotes = findViewById(R.id.transcation_notes);
        receiptImage = findViewById(R.id.recipt_image);
        expenseLocation = findViewById(R.id.transaction_location);

        expenseTitle.setText(title);
        expenseDate.setText(date);
        expenseAmount.setText(formatAmount(amount));
        expenseCategory.setText(category);
        expensePaymentMethod.setText(paymentMethod);
        expenseNotes.setText(notes);
        expenseLocation.setText(location);

        Glide.with(this).load(receipt)
                .centerCrop()
                .into(receiptImage);
        receiptImage.setOnClickListener(view -> showFullImageDialog());

    }

    // Helper method to format double amount to string with 2 decimal places
    private String formatAmount(double amount) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00"); // Adds comma separators and ensures 2 decimal places
        return decimalFormat.format(amount);
    }

    private void deleteTransaction() {
        TranscationDB db = new TranscationDB(this);
        // Get the Id of the Data
        db.deleteTransactionById(id);
        Toast.makeText(this, "Transaction Data Deleted Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }


    private void showFullImageDialog() {
        if (receipt == null || receipt.isEmpty()) {
            Toast.makeText(this, "No image available", Toast.LENGTH_SHORT).show();
            return; // Exit if no image URL is available
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.full_recipt_image_box, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(dialogView)
                .setCancelable(true);


        Log.e("IMAGE_URL", receipt);
        ImageView fullImageView = dialogView.findViewById(R.id.recipe_full_view);

        Glide.with(this)
                .load(receipt)
                .into(fullImageView);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}