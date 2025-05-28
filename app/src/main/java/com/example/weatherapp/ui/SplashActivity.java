package com.example.weatherapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherapp.R;

public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initializeWeatherBackground();
        startAnimations();

        boolean returnToSettings = getIntent().getBooleanExtra("returnToSettings", false);

        new Handler().postDelayed(() -> {
            SharedPreferences pref = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            boolean isRemembered = pref.getBoolean("remember", false);
            boolean isLoggedIn = pref.getBoolean("isLoggedIn", false);

            if (returnToSettings) {
                startActivity(new Intent(SplashActivity.this, AdvancedSettingsActivity.class));
            } else if (isRemembered && isLoggedIn) {
                startActivity(new Intent(SplashActivity.this, SettingsActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }

            finish();
        }, 3000);
    }


    private void startAnimations() {
        ImageView logo = findViewById(R.id.imgLogo);
        TextView appName = findViewById(R.id.txtAppName);
        TextView welcome = findViewById(R.id.txtWelcome);

        if (logo != null && appName != null && welcome != null) {
            try {
                Animation logoAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
                logo.startAnimation(logoAnim);
            } catch (Exception ignored) {}

            new Handler().postDelayed(() -> {
                try {
                    Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                    appName.startAnimation(fadeIn);
                } catch (Exception ignored) {}
            }, 500);

            new Handler().postDelayed(() -> {
                try {
                    Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
                    welcome.startAnimation(slideUp);
                } catch (Exception ignored) {}
            }, 1000);
        }
    }
}

