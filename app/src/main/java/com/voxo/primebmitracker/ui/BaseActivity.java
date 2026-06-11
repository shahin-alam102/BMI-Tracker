package com.voxo.primebmitracker.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    protected InterstitialAd mInterstitialAd;
    private static final String PREFS_NAME = "settings";
    private static final String KEY_LANGUAGE = "pref_language";
    private String currentLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyLanguagePreference(this);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentLanguage = prefs.getString(KEY_LANGUAGE, "system");
        MobileAds.initialize(this, initializationStatus -> {});
    }
    protected void loadBannerAd(AdView madView) {
        if (madView != null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            madView.loadAd(adRequest);
        }
    }
    protected void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }

    protected void showInterstitialAd(Runnable onAdClosedAction) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    onAdClosedAction.run();
                    loadInterstitialAd();
                }
            });
        } else {
            onAdClosedAction.run(); // অ্যাড রেডি না থাকলে সরাসরি কাজটি হয়ে যাবে
        }
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