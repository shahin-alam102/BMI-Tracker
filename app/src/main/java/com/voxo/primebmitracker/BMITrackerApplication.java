package com.voxo.primebmitracker;

import com.voxo.primebmitracker.ui.BaseActivity;

import android.app.Application;

public class BMITrackerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BaseActivity.applyLanguagePreference(this);
    }


}

