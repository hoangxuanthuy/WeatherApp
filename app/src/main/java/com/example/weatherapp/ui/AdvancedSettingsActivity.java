package com.example.weatherapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.example.weatherapp.R;
import com.example.weatherapp.util.LocaleHelper;

public class AdvancedSettingsActivity extends BaseActivity {

    private SwitchCompat switchRealtime, switchOffline;
    private TextView tvLanguage, tvTheme, tvSendFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_settings);

        // Toolbar back
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Views
        switchRealtime = findViewById(R.id.switchRealtime);
        switchOffline = findViewById(R.id.switchOffline);
        tvLanguage = findViewById(R.id.tvLanguage);
        tvTheme = findViewById(R.id.tvTheme);
        tvSendFeedback = findViewById(R.id.tvSendFeedback);

        SharedPreferences prefs = getSharedPreferences("settingsPrefs", MODE_PRIVATE);
        switchRealtime.setChecked(prefs.getBoolean("realtime", false));
        switchOffline.setChecked(prefs.getBoolean("offline", false));
        boolean isDark = prefs.getBoolean("dark_theme", false);

        // Apply theme
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        // Sự kiện
        switchRealtime.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("realtime", isChecked).apply());

        switchOffline.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("offline", isChecked).apply());

        tvLanguage.setOnClickListener(v -> {
            String currentLang = LocaleHelper.getSavedLanguage(this);
            String newLang = currentLang.equals("vi") ? "en" : "vi";
            LocaleHelper.setLocale(this, newLang);
            startActivity(new Intent(this, AdvancedSettingsActivity.class));
            finish();
        });

        tvTheme.setOnClickListener(v -> {
            boolean newDark = !prefs.getBoolean("dark_theme", false);
            prefs.edit().putBoolean("dark_theme", newDark).apply();
            AppCompatDelegate.setDefaultNightMode(
                    newDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        tvSendFeedback.setOnClickListener(v -> {
            String fbUrl = "https://www.facebook.com/thuy.hoang.704548/";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl)));
        });
    }

    // Xử lý nút back
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Quay lại
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
