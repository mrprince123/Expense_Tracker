package com.expense.expensetracker.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.expense.expensetracker.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class SettingActivity extends AppCompatActivity {

    ImageView backButton;
    LinearLayout aboutthisApp;
    TextView themeChangeText;
    ImageView emailContact;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportActionBar().hide();

        themeChangeText = findViewById(R.id.theme_change_text);

        sharedPreferences = getSharedPreferences("theme", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);


        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            themeChangeText.setText("Dark");
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            themeChangeText.setText("Light");
        }

        // Back Button
        backButton = findViewById(R.id.back_button_setting);
        backButton.setOnClickListener(view -> {
            finish();
        });

        // About App
        aboutthisApp = findViewById(R.id.about_this_app);
        aboutthisApp.setOnClickListener(view -> {
            startActivity(new Intent(this, AboutAppActivity.class));
        });

        // Rate this App
        LinearLayout linearLayout = findViewById(R.id.rate_this_app);
        linearLayout.setOnClickListener(view -> {
            String link = "https://play.google.com/store/apps/details?id=com.expense.expensetracker";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(intent);
        });


        // for Contact Email
        emailContact = findViewById(R.id.email_contact_button);
        emailContact.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:princekrdss2018@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
            intent.putExtra(Intent.EXTRA_TEXT, "Body of the Email");
            startActivity(intent);

        });

        // Show the Theme Change
        themeChangeText.setOnClickListener(view -> {
            themeChange();
        });

    }

    private void themeChange() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.item_theme);

        CheckBox lightCheckBox = bottomSheetDialog.findViewById(R.id.light_check);
        CheckBox darkCheckBox = bottomSheetDialog.findViewById(R.id.dark_check);

        // Set initial state based on saved preference
        boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);
        lightCheckBox.setChecked(!isDarkModeOn);
        darkCheckBox.setChecked(isDarkModeOn);

        // Light theme selection
        lightCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                darkCheckBox.setChecked(false); // Uncheck dark theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("isDarkModeOn", false);
                editor.apply();
            }
        });

        // Dark theme selection
        darkCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                lightCheckBox.setChecked(false); // Uncheck light theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("isDarkModeOn", true);
                editor.apply();
            }
        });

        bottomSheetDialog.show();
    }
}