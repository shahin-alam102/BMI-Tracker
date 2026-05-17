package com.voxo.bmitracker.viewmodel;

import android.graphics.Color;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.voxo.bmitracker.model.BmiResult;
import java.util.Locale;

public class BmiViewModel extends ViewModel {

    private final MutableLiveData<BmiResult> _bmiResult = new MutableLiveData<>();
    public LiveData<BmiResult> bmiResult = _bmiResult;

    public void calculateBMI(String hStr, String wStr, String hUnit, String wUnit) {
        if (hStr.isEmpty() || wStr.isEmpty()) {
            _bmiResult.setValue(null);
            return;
        }

        try {
            float rawH = Float.parseFloat(hStr);
            float rawW = Float.parseFloat(wStr);
            float weightKg = wUnit.toLowerCase().contains("lb") ? rawW * 0.453592f : rawW;
            float heightM = hUnit.toLowerCase().contains("ft") ? rawH * 0.3048f : rawH / 100;

            if (heightM > 0) {
                float bmi = weightKg / (heightM * heightM);
                float minW = 18.5f * (heightM * heightM);
                float maxW = 24.9f * (heightM * heightM);

                String range = String.format(Locale.US, "%.2f - %.2f kg", minW, maxW);

                String diffText;
                int diffColor;
                if (weightKg < minW) {
                    diffText = String.format(Locale.getDefault(), "Gain + %.1f kg", minW - weightKg);
                    diffColor = Color.parseColor("#3498DB");
                } else if (weightKg > maxW) {
                    diffText = String.format(Locale.getDefault(), "Lose - %.1f kg", weightKg - maxW);
                    diffColor = Color.parseColor("#E74C3C");
                } else {
                    diffText = "Your weight is perfect!";
                    diffColor = Color.parseColor("#27AE60");
                }

                _bmiResult.setValue(new BmiResult(bmi, getCategory(bmi), getCategoryColor(bmi), range, diffText, diffColor));
            }
        } catch (Exception e) {
            _bmiResult.setValue(null);
        }
    }

    private String getCategory(float bmi) {
        if (bmi <= 15.9) return "Very Severely Underweight";
        if (bmi <= 16.9) return "Severely Underweight";
        if (bmi <= 18.4) return "Underweight";
        if (bmi <= 24.9) return "Healthy Weight";
        if (bmi <= 29.9) return "Overweight";
        if (bmi <= 34.9) return "Obese Class I";
        if (bmi <= 39.9) return "Obese Class II";
        return "Obese Class III";
    }

    private int getCategoryColor(float bmi) {
        if (bmi <= 15.9) return Color.parseColor("#E74C3C");
        if (bmi <= 18.4) return Color.parseColor("#3498DB");
        if (bmi <= 24.9) return Color.parseColor("#27AE60");
        if (bmi <= 29.9) return Color.parseColor("#F1C40F");
        return Color.parseColor("#C0392B");
    }
}
