package com.example.weatherapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LocaleHelper {

    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";
    private static final String DEFAULT_LANGUAGE = "vi"; // Default to Vietnamese

    // Sets the locale and persists it
    public static Context setLocale(Context context, String language) {
        if (language == null || language.isEmpty()) {
            language = DEFAULT_LANGUAGE; // Fallback to default language if input is invalid
        }
        persist(context, language);
        return updateResources(context, language);
    }

    // Gets the saved language preference, default to Vietnamese if not set
    public static String getSavedLanguage(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, DEFAULT_LANGUAGE);
    }

    // Persists the selected language to SharedPreferences
    private static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(SELECTED_LANGUAGE, language).apply();
    }

    // Updates the resources based on the selected language
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            return context;
        }
    }

    // Resets to the default system language
    public static void resetToSystemLocale(Context context) {
        String systemLanguage = Locale.getDefault().getLanguage();
        persist(context, systemLanguage);
        updateResources(context, systemLanguage);
    }
}
