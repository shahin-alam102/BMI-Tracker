package com.voxo.bmitracker.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
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
            locale = androidx.core.os.ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
        } else {
            locale = new Locale(language);
        }

        if (locale == null) {
            locale = Locale.getDefault();
        }

        Locale.setDefault(locale);
        Configuration config = new Configuration(newBase.getResources().getConfiguration());
        config.setLocale(locale);
        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }

    public static void applyLanguagePreference(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String language = prefs.getString(KEY_LANGUAGE, "system");

        Locale locale;
        if ("system".equals(language)) {
            locale = androidx.core.os.ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
        } else {
            locale = new Locale(language);
        }
        if (locale == null) return;

        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());

        config.setLocale(locale);
        context.createConfigurationContext(config);
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