package com.expense.expensetracker.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.expense.expensetracker.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;

public class SettingActivity extends AppCompatActivity {

    ImageView backButton;
    LinearLayout aboutThisApp, changeTheme;
    TextView themeChangeText;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    LinearLayout clearCache, clearData;

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

        themeChangeText = findViewById(R.id.theme_text);
        changeTheme = findViewById(R.id.change_theme);

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
        backButton = findViewById(R.id.setting_back_button);
        backButton.setOnClickListener(view -> {
            finish();
        });

        // About App
        aboutThisApp = findViewById(R.id.about_us_button);
        aboutThisApp.setOnClickListener(view -> {
            startActivity(new Intent(this, AboutAppActivity.class));
        });

        // Rate this App
        LinearLayout linearLayout = findViewById(R.id.rate_us_button);
        linearLayout.setOnClickListener(view -> {
            String link = "https://play.google.com/store/apps/details?id=com.expense.expensetracker";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(intent);
        });

        // for Contact Email
        LinearLayout emailContact = findViewById(R.id.contact_us_button);
        emailContact.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:princekrdss2018@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
            intent.putExtra(Intent.EXTRA_TEXT, "Body of the Email");
            startActivity(intent);

        });

        // Clear App Cache
        clearCache = findViewById(R.id.clear_cache);
        clearCache.setOnClickListener(view -> {
            clearCache(getApplicationContext());
            Toast.makeText(getApplicationContext(), "App Cache cleared", Toast.LENGTH_SHORT).show();
        });


        // Clear App Data
        clearData = findViewById(R.id.clear_data);
        clearData.setOnClickListener(view -> {
            clearAppData();
            Toast.makeText(getApplicationContext(), "App Data cleared", Toast.LENGTH_SHORT).show();
        });

        // Show the Theme Change
        changeTheme.setOnClickListener(view -> {
            themeChange();
        });

    }

    public void clearCache(Context context) {
        try {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                deleteDir(cacheDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public void clearAppData() {
        try {
            // This clears all the app's data and restarts it.
            Runtime.getRuntime().exec("pm clear " + getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
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