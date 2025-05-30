package com.expense.expensetracker.Fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.expense.expensetracker.Adapter.TransactionAdapter;
import com.expense.expensetracker.Database.TranscationDB;
import com.expense.expensetracker.Models.Transaction;
import com.expense.expensetracker.R;

import java.util.ArrayList;
import java.util.Locale;


public class TransactionFragment extends Fragment {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    RecyclerView recyclerTransactions;
    ArrayList<Transaction> transactionArrayList;
    TransactionAdapter transactionAdapter;
    ArrayList<Transaction> filterList;
    EditText searchTransaction;
    String searchValue;
    String voiceSearchValue = "";
    ImageView searchMic;
    private TranscationDB dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        filterList = new ArrayList<>();
        dbHelper = new TranscationDB(getContext());
        recyclerTransactions = view.findViewById(R.id.all_transactions);
        recyclerTransactions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        transactionArrayList = dbHelper.getAllTransactions();
        if (transactionArrayList == null) {
            transactionArrayList = new ArrayList<>();
        }

        transactionAdapter = new TransactionAdapter(getContext(), transactionArrayList);
        recyclerTransactions.setAdapter(transactionAdapter);

        searchTransaction = view.findViewById(R.id.search_transaction);
        searchTransaction.setOnClickListener(view1 -> {
            searchValue = searchTransaction.getText().toString().trim();
            filterSearch(searchValue);
        });

        searchMic = view.findViewById(R.id.search_mic);
        searchMic.setOnClickListener(view1 -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Search Transaction");

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            } catch (Exception e) {
                Toast
                        .makeText(getContext(), " " + e.getMessage(),
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        return view;
    }

    void filterSearch(String searchValue) {
        filterList.clear();
        if (searchValue.isEmpty()) {
            filterList.addAll(transactionArrayList);
        } else {
            for (Transaction transaction : transactionArrayList) {
                if (transaction.getTitle().toLowerCase().contains(searchValue.toLowerCase())) {
                    filterList.add(transaction);
                }
            }
        }

        if (filterList.isEmpty()) {
            Toast.makeText(getContext(), "No Transaction Data Found with Provided Input", Toast.LENGTH_SHORT).show();
        }

        transactionAdapter.updateTransactionList(filterList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && result.size() > 0) {
                    voiceSearchValue = result.get(0);
                    searchTransaction.setText(voiceSearchValue);
                    filterSearch(voiceSearchValue);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}