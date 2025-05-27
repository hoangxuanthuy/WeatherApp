package com.example.weatherapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.weatherapp.util.LocaleHelper;
import com.example.weatherapp.util.WeatherManager;
import com.example.weatherapp.R;

public class BaseActivity extends AppCompatActivity {

    protected WeatherBackgroundManager backgroundManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getSavedLanguage(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 🌦️ Update background when activity resumes
        updateWeatherBackground();
    }

    // 🌦️ Method to initialize weather background for any activity
    protected void initializeWeatherBackground() {
        FrameLayout backgroundContainer = findViewById(R.id.backgroundContainer);
        View lightningOverlay = findViewById(R.id.lightningOverlay);
        FrameLayout rainContainer = findViewById(R.id.rainContainer);
        FrameLayout snowContainer = findViewById(R.id.snowContainer);

        if (backgroundContainer != null) {
            backgroundManager = new WeatherBackgroundManager(
                    this, backgroundContainer, lightningOverlay, rainContainer, snowContainer
            );

            // Set background based on current weather
            updateWeatherBackground();
        }
    }

    // 🌦️ Update background based on current weather condition
    protected void updateWeatherBackground() {
        if (backgroundManager != null) {
            WeatherManager weatherManager = WeatherManager.getInstance(this);
            WeatherBackgroundManager.WeatherCondition condition = weatherManager.getWeatherConditionEnum();
            backgroundManager.setWeatherBackground(condition);
        }
    }
}
