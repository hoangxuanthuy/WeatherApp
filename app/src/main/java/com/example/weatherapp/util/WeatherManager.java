package com.example.weatherapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class WeatherManager {
    private static final String PREF_NAME = "weather_prefs";
    private static final String KEY_WEATHER_CONDITION = "weather_condition";
    private static final String KEY_WEATHER_MAIN = "weather_main";
    private static final String KEY_WEATHER_DESC = "weather_description";

    private static WeatherManager instance;
    private Context context;

    private WeatherManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized WeatherManager getInstance(Context context) {
        if (instance == null) {
            instance = new WeatherManager(context);
        }
        return instance;
    }

    // Save current weather condition
    public void saveWeatherCondition(String weatherMain, String weatherDescription) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_WEATHER_MAIN, weatherMain);
        editor.putString(KEY_WEATHER_DESC, weatherDescription);

        // Determine condition
        String condition = determineWeatherCondition(weatherMain, weatherDescription);
        editor.putString(KEY_WEATHER_CONDITION, condition);
        editor.apply();
    }

    // Get current weather condition
    public String getCurrentWeatherCondition() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_WEATHER_CONDITION, "SUNNY"); // Default to SUNNY
    }

    // Determine weather condition from API response
    private String determineWeatherCondition(String weatherMain, String description) {
        if (weatherMain == null) weatherMain = "";
        if (description == null) description = "";

        String main = weatherMain.toLowerCase();
        String desc = description.toLowerCase();

        if (main.contains("thunderstorm") || desc.contains("thunder") || desc.contains("storm")) {
            return "STORMY";
        } else if (main.contains("rain") || main.contains("drizzle") || desc.contains("rain")) {
            return "RAINY";
        } else if (main.contains("snow") || desc.contains("snow")) {
            return "SNOW";
        } else if (main.contains("clouds") || desc.contains("cloud")) {
            return "CLOUDY";
        } else if (main.contains("clear") || desc.contains("clear") || main.contains("sun")) {
            // Check if it's night time
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int hour = cal.get(java.util.Calendar.HOUR_OF_DAY);
            if (hour >= 18 || hour <= 6) {
                return "NIGHT";
            } else {
                return "SUNNY";
            }
        }

        return "SUNNY";
    }

    // Convert string to WeatherCondition enum
    public com.example.weatherapp.ui.WeatherBackgroundManager.WeatherCondition getWeatherConditionEnum() {
        String condition = getCurrentWeatherCondition();
        switch (condition) {
            case "STORMY":
                return com.example.weatherapp.ui.WeatherBackgroundManager.WeatherCondition.STORMY;
            case "RAINY":
                return com.example.weatherapp.ui.WeatherBackgroundManager.WeatherCondition.RAINY;
            case "SNOW":
                return com.example.weatherapp.ui.WeatherBackgroundManager.WeatherCondition.SNOW;
            case "CLOUDY":
                return com.example.weatherapp.ui.WeatherBackgroundManager.WeatherCondition.CLOUDY;
            case "NIGHT":
                return com.example.weatherapp.ui.WeatherBackgroundManager.WeatherCondition.NIGHT;
            case "SUNNY":
            default:
                return com.example.weatherapp.ui.WeatherBackgroundManager.WeatherCondition.SUNNY;
        }
    }
}
