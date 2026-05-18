package com.voxo.bmitracker;

import com.voxo.bmitracker.ui.BaseActivity;

import android.app.Application;

public class BMITrackerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BaseActivity.applyLanguagePreference(this);
    }


}

