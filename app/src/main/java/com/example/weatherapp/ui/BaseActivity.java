package com.example.weatherapp.ui;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import com.example.weatherapp.util.LocaleHelper;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getSavedLanguage(newBase)));
    }
}
