package com.voxo.bmitracker.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "settings";
    private static final String KEY_LANGUAGE = "pref_language";
    private String currentLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyLanguagePreference(this);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentLanguage = prefs.getString(KEY_LANGUAGE, "system");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String language = prefs.getString(KEY_LANGUAGE, "system");

        Locale locale;
        if ("system".equals(language)) {
            locale = Resources.getSystem().getConfiguration().locale;
        } else {
            locale = new Locale(language);
        }

        Locale.setDefault(locale);
        Configuration config = new Configuration(newBase.getResources().getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            Context context = newBase.createConfigurationContext(config);
            super.attachBaseContext(context);
        } else {
            config.locale = locale;
            newBase.getResources().updateConfiguration(config, newBase.getResources().getDisplayMetrics());
            super.attachBaseContext(newBase);
        }
    }

    public static void applyLanguagePreference(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String language = prefs.getString(KEY_LANGUAGE, "system");

        Locale locale;
        if ("system".equals(language)) {
            locale = Resources.getSystem().getConfiguration().locale;
        } else {
            locale = new Locale(language);
        }

        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String latestLanguage = prefs.getString(KEY_LANGUAGE, "system");

        if (!latestLanguage.equals(currentLanguage)) {
            currentLanguage = latestLanguage;
            recreate();
        }
    }
}