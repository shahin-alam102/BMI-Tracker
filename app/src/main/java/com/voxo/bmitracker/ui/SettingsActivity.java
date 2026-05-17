package com.voxo.bmitracker.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.voxo.bmitracker.R;
import java.util.Locale;

public class SettingsActivity extends BaseActivity {

    private static final String PREFS_NAME = "settings";
    private static final String KEY_LANGUAGE = "pref_language";

    private TextView tvCurrentLanguage;
    private LinearLayout layoutLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            androidx.activity.EdgeToEdge.enable(this);
        } catch (Throwable ignored) {}

        setContentView(R.layout.activity_settings);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tvCurrentLanguage = findViewById(R.id.tvCurrentLanguage);
        layoutLanguage = findViewById(R.id.layoutLanguage);

        String current = getSavedLanguage();
        updateLanguageLabel(current);

        layoutLanguage.setOnClickListener(v -> showLanguageDialog());
    }

    private String getSavedLanguage() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_LANGUAGE, "system");
    }

    private void saveLanguage(String lang) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, lang).apply();
    }

    private void updateLanguageLabel(String lang) {
        if ("system".equals(lang)) {
            String systemLang;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                systemLang = Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
            } else {
                systemLang = Locale.getDefault().getLanguage();
            }

            if ("bn".equals(systemLang)) {
                tvCurrentLanguage.setText("System Default (বাংলা)");
            } else {
                tvCurrentLanguage.setText("System Default (English)");
            }
        } else if ("bn".equals(lang)) {
            tvCurrentLanguage.setText("বাংলা");
        } else {
            tvCurrentLanguage.setText("English");
        }
    }

    private void showLanguageDialog() {
        final String[] options = {
                "English",
                "বাংলা",
                getString(R.string.system_default)
        };

        String current = getSavedLanguage();

        int checked = 2;
        if ("en".equals(current)) {
            checked = 0;
        } else if ("bn".equals(current)) {
            checked = 1;
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.choose_language)
                .setSingleChoiceItems(options, checked, (dialog, which) -> {
                    String sel;
                    if (which == 0) {
                        sel = "en";
                    } else if (which == 1) {
                        sel = "bn";
                    } else {
                        sel = "system";
                    }

                    saveLanguage(sel);
                    setLocale(sel);
                    updateLanguageLabel(sel);

                    Toast.makeText(this, R.string.language_changed, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                    startActivity(getIntent());
                })
                .setNegativeButton(android.R.string.cancel, (d, w) -> d.dismiss())
                .show();
    }

    private void setLocale(@NonNull String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        recreate();
    }
}