package com.example.weatherapp.ui;

import android.content.Intent;
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

        // 🌦️ Initialize weather-based background
        initializeWeatherBackground();
        startAnimations();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
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
            } catch (Exception e) {
                // Fallback
            }

            new Handler().postDelayed(() -> {
                try {
                    Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                    appName.startAnimation(fadeIn);
                } catch (Exception e) {
                    // Fallback
                }
            }, 500);

            new Handler().postDelayed(() -> {
                try {
                    Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
                    welcome.startAnimation(slideUp);
                } catch (Exception e) {
                    // Fallback
                }
            }, 1000);
        }
    }
}
