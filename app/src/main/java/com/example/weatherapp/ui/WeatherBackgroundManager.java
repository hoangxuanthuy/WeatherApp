package com.example.weatherapp.ui;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.AnimatorSet;
import android.graphics.Color;
import com.example.weatherapp.R;
import java.util.Calendar;
import java.util.Random;

public class WeatherBackgroundManager {

    public enum WeatherCondition {
        SUNNY, CLOUDY, RAINY, STORMY, NIGHT, SNOW, CLEAR
    }

    private FrameLayout backgroundContainer;
    private View lightningOverlay;
    private FrameLayout rainContainer;
    private FrameLayout snowContainer;
    private FrameLayout cloudContainer;
    private Context context;
    private Handler animationHandler;
    private Runnable lightningRunnable;

    public WeatherBackgroundManager(Context context, FrameLayout backgroundContainer,
                                    View lightningOverlay, FrameLayout rainContainer,
                                    FrameLayout snowContainer) {
        this.context = context;
        this.backgroundContainer = backgroundContainer;
        this.lightningOverlay = lightningOverlay;
        this.rainContainer = rainContainer;
        this.snowContainer = snowContainer;
        this.animationHandler = new Handler();

        // Create cloud container if not provided
        this.cloudContainer = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        this.cloudContainer.setLayoutParams(params);
        backgroundContainer.addView(this.cloudContainer, 0);
    }

    public void setWeatherBackground(WeatherCondition condition) {
        // Stop all current animations
        stopAllAnimations();

        switch (condition) {
            case SUNNY:
            case CLEAR:
                backgroundContainer.setBackgroundResource(R.drawable.bg_sunny_gradient);
                startEnhancedSunAnimation();
                break;

            case CLOUDY:
                backgroundContainer.setBackgroundResource(R.drawable.bg_cloudy_gradient);
                startSimpleCloudyAnimation(); // 🔧 Simplified!
                break;

            case RAINY:
                backgroundContainer.setBackgroundResource(R.drawable.bg_rainy_gradient);
                startEnhancedRainAnimation();
                break;

            case STORMY:
                backgroundContainer.setBackgroundResource(R.drawable.bg_stormy_gradient);
                startEnhancedRainAnimation();
                startEnhancedLightningAnimation();
                break;

            case NIGHT:
                backgroundContainer.setBackgroundResource(R.drawable.bg_night_gradient);
                startEnhancedMoonAnimation();
                break;

            case SNOW:
                backgroundContainer.setBackgroundResource(R.drawable.bg_cloudy_gradient);
                startEnhancedSnowAnimation();
                break;

            default:
                backgroundContainer.setBackgroundResource(R.drawable.bg_sunny_gradient);
                startEnhancedSunAnimation();
                break;
        }
    }

    // ☀️ Enhanced Sun Animation
    private void startEnhancedSunAnimation() {
        if (cloudContainer == null) return;

        cloudContainer.setVisibility(View.VISIBLE);

        // Create main sun with rays
        createRotatingSun(0, 150, 50, 80, 0.9f, 15000);
        createRotatingSun(2000, 100, 200, 150, 0.6f, 20000);

        // Add floating light particles
        createFloatingParticles(8, "#FFD54F", 3000);
    }

    private void createRotatingSun(int delay, int size, int marginRight, int marginTop, float alpha, int duration) {
        animationHandler.postDelayed(() -> {
            ImageView sunRays = new ImageView(context);
            sunRays.setImageResource(R.drawable.ic_sun_rays);
            sunRays.setAlpha(alpha);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            params.rightMargin = marginRight;
            params.topMargin = marginTop;
            sunRays.setLayoutParams(params);

            cloudContainer.addView(sunRays);

            ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(sunRays, "rotation", 0f, 360f);
            rotateAnimator.setDuration(duration);
            rotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
            rotateAnimator.start();
        }, delay);
    }

    // ☁️ SIMPLIFIED Cloudy Animation
    private void startSimpleCloudyAnimation() {
        if (cloudContainer == null) return;

        cloudContainer.setVisibility(View.VISIBLE);

        // Create simple clouds
        createSimpleCloud(0, 0.8f, 18000, 120, R.drawable.ic_cloud_simple);
        createSimpleCloud(3000, 0.6f, 22000, 100, R.drawable.ic_cloud_dark_simple);
        createSimpleCloud(6000, 0.7f, 20000, 140, R.drawable.ic_cloud_simple);
        createSimpleCloud(9000, 0.5f, 25000, 110, R.drawable.ic_cloud_dark_simple);
    }

    private void createSimpleCloud(int delay, float alpha, int duration, int cloudSize, int cloudDrawable) {
        animationHandler.postDelayed(() -> {
            ImageView cloud = new ImageView(context);
            cloud.setImageResource(cloudDrawable);
            cloud.setAlpha(alpha);

            Random random = new Random();
            int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
            int y = random.nextInt(screenHeight / 3);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(cloudSize, cloudSize);
            params.leftMargin = -cloudSize;
            params.topMargin = y;

            cloud.setLayoutParams(params);
            cloudContainer.addView(cloud);

            // Simple drift animation
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;

            ObjectAnimator moveX = ObjectAnimator.ofFloat(cloud, "translationX",
                    -cloudSize, screenWidth + cloudSize);
            moveX.setDuration(duration);
            moveX.setRepeatCount(ValueAnimator.INFINITE);
            moveX.start();
        }, delay);
    }

    // 🌧️ Enhanced Rain Animation
    private void startEnhancedRainAnimation() {
        if (rainContainer == null) return;

        rainContainer.setVisibility(View.VISIBLE);

        for (int i = 0; i < 30; i++) {
            createEnhancedRainDrop(i * 50);
        }
    }

    private void createEnhancedRainDrop(int delay) {
        animationHandler.postDelayed(() -> {
            View rainDrop = new View(context);

            Random random = new Random();
            int width = 3 + random.nextInt(2);
            int height = 20 + random.nextInt(10);

            rainDrop.setBackgroundColor(Color.parseColor("#B0FFFFFF"));

            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int x = random.nextInt(screenWidth - 20);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
            params.leftMargin = x;
            params.topMargin = -50;

            rainDrop.setLayoutParams(params);
            rainContainer.addView(rainDrop);

            int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
            int fallDuration = 600 + random.nextInt(400);

            ObjectAnimator fallY = ObjectAnimator.ofFloat(rainDrop, "translationY",
                    -50, screenHeight + 50);
            fallY.setDuration(fallDuration);
            fallY.setRepeatCount(ValueAnimator.INFINITE);
            fallY.start();
        }, delay);
    }

    // ⚡ Enhanced Lightning Animation
    private void startEnhancedLightningAnimation() {
        if (lightningOverlay == null) return;

        lightningOverlay.setVisibility(View.VISIBLE);
        Random random = new Random();

        lightningRunnable = new Runnable() {
            @Override
            public void run() {
                ObjectAnimator flash = ObjectAnimator.ofFloat(lightningOverlay, "alpha", 0f, 0.8f, 0f);
                flash.setDuration(150);
                flash.start();

                int nextFlash = 2000 + random.nextInt(3000);
                animationHandler.postDelayed(this, nextFlash);
            }
        };

        animationHandler.postDelayed(lightningRunnable, 500);
    }

    // 🌙 Enhanced Moon Animation
    private void startEnhancedMoonAnimation() {
        if (cloudContainer == null) return;

        cloudContainer.setVisibility(View.VISIBLE);
        createGlowingMoon();
        createTwinklingStars();
    }

    private void createGlowingMoon() {
        ImageView moon = new ImageView(context);
        moon.setImageResource(R.drawable.ic_moon);
        moon.setAlpha(0.9f);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(120, 120);
        params.leftMargin = 50;
        params.topMargin = 80;
        moon.setLayoutParams(params);

        cloudContainer.addView(moon);

        ObjectAnimator glow = ObjectAnimator.ofFloat(moon, "alpha", 0.7f, 1.0f);
        glow.setDuration(4000);
        glow.setRepeatCount(ValueAnimator.INFINITE);
        glow.setRepeatMode(ValueAnimator.REVERSE);
        glow.start();
    }

    private void createTwinklingStars() {
        Random random = new Random();
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        for (int i = 0; i < 10; i++) {
            createStar(random.nextInt(screenWidth), random.nextInt(screenHeight / 2), i * 200);
        }
    }

    private void createStar(int x, int y, int delay) {
        animationHandler.postDelayed(() -> {
            View star = new View(context);
            star.setBackgroundColor(Color.parseColor("#FFFFFF"));

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(4, 4);
            params.leftMargin = x;
            params.topMargin = y;
            star.setLayoutParams(params);

            cloudContainer.addView(star);

            ObjectAnimator twinkle = ObjectAnimator.ofFloat(star, "alpha", 0.2f, 1.0f);
            twinkle.setDuration(1500);
            twinkle.setRepeatCount(ValueAnimator.INFINITE);
            twinkle.setRepeatMode(ValueAnimator.REVERSE);
            twinkle.start();
        }, delay);
    }

    // ❄️ Enhanced Snow Animation
    private void startEnhancedSnowAnimation() {
        if (snowContainer == null) return;

        snowContainer.setVisibility(View.VISIBLE);

        for (int i = 0; i < 25; i++) {
            createSnowflake(i * 100);
        }
    }

    private void createSnowflake(int delay) {
        animationHandler.postDelayed(() -> {
            View snowflake = new View(context);
            snowflake.setBackgroundColor(Color.parseColor("#FFFFFF"));

            Random random = new Random();
            int size = 4 + random.nextInt(4);
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int x = random.nextInt(screenWidth - 20);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            params.leftMargin = x;
            params.topMargin = -20;

            snowflake.setLayoutParams(params);
            snowContainer.addView(snowflake);

            int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
            int fallDuration = 3000 + random.nextInt(2000);

            ObjectAnimator fallY = ObjectAnimator.ofFloat(snowflake, "translationY",
                    -20, screenHeight + 20);
            fallY.setDuration(fallDuration);
            fallY.setRepeatCount(ValueAnimator.INFINITE);
            fallY.start();
        }, delay);
    }

    // Helper method to create floating particles
    private void createFloatingParticles(int count, String color, int duration) {
        Random random = new Random();
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        for (int i = 0; i < count; i++) {
            createFloatingParticle(
                    random.nextInt(screenWidth),
                    random.nextInt(screenHeight),
                    color,
                    duration + random.nextInt(1000),
                    i * 300
            );
        }
    }

    private void createFloatingParticle(int x, int y, String color, int duration, int delay) {
        animationHandler.postDelayed(() -> {
            View particle = new View(context);
            particle.setBackgroundColor(Color.parseColor(color));
            particle.setAlpha(0.3f);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(6, 6);
            params.leftMargin = x;
            params.topMargin = y;
            particle.setLayoutParams(params);

            cloudContainer.addView(particle);

            ObjectAnimator floatY = ObjectAnimator.ofFloat(particle, "translationY",
                    0, -30, 0, 20, 0);
            floatY.setDuration(duration);
            floatY.setRepeatCount(ValueAnimator.INFINITE);
            floatY.start();
        }, delay);
    }

    private void stopAllAnimations() {
        if (animationHandler != null) {
            animationHandler.removeCallbacksAndMessages(null);
        }

        if (lightningOverlay != null) {
            lightningOverlay.setVisibility(View.GONE);
            lightningOverlay.clearAnimation();
        }

        if (rainContainer != null) {
            rainContainer.setVisibility(View.GONE);
            rainContainer.removeAllViews();
        }

        if (snowContainer != null) {
            snowContainer.setVisibility(View.GONE);
            snowContainer.removeAllViews();
        }

        if (cloudContainer != null) {
            cloudContainer.setVisibility(View.GONE);
            cloudContainer.removeAllViews();
        }
    }

    public WeatherCondition getWeatherConditionFromDescription(String weatherMain, String description) {
        if (weatherMain == null) weatherMain = "";
        if (description == null) description = "";

        String main = weatherMain.toLowerCase();
        String desc = description.toLowerCase();

        if (main.contains("thunderstorm") || desc.contains("thunder") || desc.contains("storm")) {
            return WeatherCondition.STORMY;
        } else if (main.contains("rain") || main.contains("drizzle") || desc.contains("rain")) {
            return WeatherCondition.RAINY;
        } else if (main.contains("snow") || desc.contains("snow")) {
            return WeatherCondition.SNOW;
        } else if (main.contains("clouds") || desc.contains("cloud")) {
            return WeatherCondition.CLOUDY;
        } else if (main.contains("clear") || desc.contains("clear") || main.contains("sun")) {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if (hour >= 18 || hour <= 6) {
                return WeatherCondition.NIGHT;
            } else {
                return WeatherCondition.SUNNY;
            }
        }

        return WeatherCondition.SUNNY;
    }
}
