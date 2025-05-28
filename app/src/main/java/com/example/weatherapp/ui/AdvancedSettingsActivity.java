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

        // Toolbar
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

        // Áp dụng dark/light theme


        // Sự kiện toggle
        switchRealtime.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("realtime", isChecked).apply());

        switchOffline.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("offline", isChecked).apply());

        // Chuyển đổi ngôn ngữ và reload app
        tvLanguage.setOnClickListener(v -> {
            String currentLang = LocaleHelper.getSavedLanguage(this);
            String newLang = currentLang.equals("vi") ? "en" : "vi";

            LocaleHelper.setLocale(this, newLang);

            SharedPreferences.Editor editor = getSharedPreferences("settingsPrefs", MODE_PRIVATE).edit();
            editor.putString("language", newLang);
            editor.apply();

            // Gọi hàm để tự động cập nhật lại toàn bộ các Activity
            BaseActivity.recreateAllActivities();
        });


        // Toggle theme sáng/tối
        tvTheme.setOnClickListener(v -> {

        });

        // Mở Facebook Feedback
        tvSendFeedback.setOnClickListener(v -> {
            String fbUrl = "https://www.facebook.com/thuy.hoang.704548/";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl)));
        });
    }

    // Xử lý nút back Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // back lại
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
