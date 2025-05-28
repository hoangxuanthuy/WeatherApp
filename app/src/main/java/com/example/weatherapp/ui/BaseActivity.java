package com.example.weatherapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.R;
import com.example.weatherapp.util.LocaleHelper;
import com.example.weatherapp.util.WeatherManager;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    protected WeatherBackgroundManager backgroundManager;

    // ⭐ Quản lý danh sách Activity đang mở
    private static final List<BaseActivity> activeActivities = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getSavedLanguage(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activeActivities.add(this);
    }

    @Override
    protected void onDestroy() {
        activeActivities.remove(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWeatherBackground();
    }

    protected void initializeWeatherBackground() {
        FrameLayout backgroundContainer = findViewById(R.id.backgroundContainer);
        View lightningOverlay = findViewById(R.id.lightningOverlay);
        FrameLayout rainContainer = findViewById(R.id.rainContainer);
        FrameLayout snowContainer = findViewById(R.id.snowContainer);

        if (backgroundContainer != null) {
            backgroundManager = new WeatherBackgroundManager(
                    this, backgroundContainer, lightningOverlay, rainContainer, snowContainer
            );
            updateWeatherBackground();
        }
    }

    protected void updateWeatherBackground() {
        if (backgroundManager != null) {
            WeatherManager weatherManager = WeatherManager.getInstance(this);
            WeatherBackgroundManager.WeatherCondition condition = weatherManager.getWeatherConditionEnum();
            backgroundManager.setWeatherBackground(condition);
        }
    }

    // ⭐ Gọi để làm mới tất cả các Activity (recreate sau khi đổi ngôn ngữ)
    public static void recreateAllActivities() {
        for (BaseActivity activity : new ArrayList<>(activeActivities)) {
            activity.recreate();
        }
    }
}
