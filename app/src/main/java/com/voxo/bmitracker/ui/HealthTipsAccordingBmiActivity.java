package com.voxo.bmitracker.ui;

import android.os.Bundle;
import com.voxo.bmitracker.R;
import com.voxo.bmitracker.databinding.ActivityHealthTipsAccordingBmiBinding;

public class HealthTipsAccordingBmiActivity extends BaseActivity {

    private ActivityHealthTipsAccordingBmiBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHealthTipsAccordingBmiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        double bmi = getIntent().getDoubleExtra("bmi_value", 0.0);

        if (binding != null) {
            loadBannerAd(binding.adViewTipsAsBmi);
        }
        displayBmiInfo(bmi);
        showPersonalizedHealthTips(bmi);
        binding.btnClose.setOnClickListener(v -> finish());

    }

    private void displayBmiInfo(double bmi) {
        binding.tvBmiValue.setText(String.format("%.1f", bmi));
        binding.tvBmiCategory.setText(getBmiCategory(bmi));

        int color = getCategoryColor(bmi);
        binding.tvBmiValue.setTextColor(color);
        binding.tvBmiCategory.setTextColor(color);
    }

    private String getBmiCategory(double bmi) {
        if (bmi <= 15.9) return getString(R.string.severely_underweight);
        else if (bmi <= 16.9) return getString(R.string.moderately_underweight);
        else if (bmi <= 18.4) return getString(R.string.underweight);
        else if (bmi <= 24.9) return getString(R.string.healthy_weight);
        else if (bmi <= 29.9) return getString(R.string.overweight);
        else if (bmi <= 34.9) return getString(R.string.obese_class_i);
        else if (bmi <= 39.9) return getString(R.string.obese_class_ii);
        else return getString(R.string.obese_class_iii);
    }

    private int getCategoryColor(double bmi) {
        if (bmi <= 18.4) return getColor(android.R.color.holo_orange_dark);
        else if (bmi <= 24.9) return getColor(android.R.color.holo_green_dark);
        else if (bmi <= 29.9) return getColor(android.R.color.holo_orange_light);
        else return getColor(android.R.color.holo_red_dark);
    }

    private void showPersonalizedHealthTips(double bmi) {
        String tips;

        if (bmi < 16.0) {
            tips = String.format(getString(R.string.tips_bmi_below_16_title), bmi) + "\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    getString(R.string.section_emergency) + ":\n" + getString(R.string.tips_bmi_below_16_emergency) + "\n\n" +
                    getString(R.string.section_target) + ":\n" + getString(R.string.tips_bmi_below_16_target) + "\n\n" +
                    getString(R.string.section_diet) + ":\n" + getString(R.string.tips_bmi_below_16_diet) + "\n\n" +
                    getString(R.string.section_exercise) + ":\n" + getString(R.string.tips_bmi_below_16_exercise) + "\n\n" +
                    getString(R.string.section_other) + ":\n" + getString(R.string.tips_bmi_below_16_other);
        } else if (bmi < 17.0) {
            tips = String.format(getString(R.string.tips_bmi_16_to_169_title), bmi) + "\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    getString(R.string.section_target) + ":\n" + getString(R.string.tips_bmi_16_to_169_target) + "\n\n" +
                    getString(R.string.section_diet) + ":\n" + getString(R.string.tips_bmi_16_to_169_diet) + "\n\n" +
                    getString(R.string.section_exercise) + ":\n" + getString(R.string.tips_bmi_16_to_169_exercise) + "\n\n" +
                    getString(R.string.section_other) + ":\n" + getString(R.string.tips_bmi_16_to_169_other);
        } else if (bmi < 18.5) {
            tips = String.format(getString(R.string.tips_bmi_17_to_184_title), bmi) + "\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    getString(R.string.section_target) + ":\n" + getString(R.string.tips_bmi_17_to_184_target) + "\n\n" +
                    getString(R.string.section_diet) + ":\n" + getString(R.string.tips_bmi_17_to_184_diet) + "\n\n" +
                    getString(R.string.section_exercise) + ":\n" + getString(R.string.tips_bmi_17_to_184_exercise) + "\n\n" +
                    getString(R.string.section_other) + ":\n" + getString(R.string.tips_bmi_17_to_184_other);
        } else if (bmi < 20.0) {
            tips = String.format(getString(R.string.tips_bmi_185_to_199_title), bmi) + "\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    getString(R.string.section_target) + ":\n" + getString(R.string.tips_bmi_185_to_199_target) + "\n\n" +
                    getString(R.string.section_diet) + ":\n" + getString(R.string.tips_bmi_185_to_199_diet) + "\n\n" +
                    getString(R.string.section_exercise) + ":\n" + getString(R.string.tips_bmi_185_to_199_exercise) + "\n\n" +
                    getString(R.string.section_other) + ":\n" + getString(R.string.tips_bmi_185_to_199_other);
        } else if (bmi < 23.0) {
            tips = String.format(getString(R.string.tips_bmi_20_to_229_title), bmi) + "\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    getString(R.string.section_target) + ":\n" + getString(R.string.tips_bmi_20_to_229_target) + "\n\n" +
                    getString(R.string.section_diet) + ":\n" + getString(R.string.tips_bmi_20_to_229_diet) + "\n\n" +
                    getString(R.string.section_exercise) + ":\n" + getString(R.string.tips_bmi_20_to_229_exercise) + "\n\n" +
                    getString(R.string.section_other) + ":\n" + getString(R.string.tips_bmi_20_to_229_other);
        } else if (bmi <= 24.9) {
            tips = String.format(getString(R.string.tips_bmi_23_to_249_title), bmi) + "\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    getString(R.string.section_target) + ":\n" + getString(R.string.tips_bmi_23_to_249_target) + "\n\n" +
                    getString(R.string.section_diet) + ":\n" + getString(R.string.tips_bmi_23_to_249_diet) + "\n\n" +
                    getString(R.string.section_exercise) + ":\n" + getString(R.string.tips_bmi_23_to_249_exercise) + "\n\n" +
                    getString(R.string.section_other) + ":\n" + getString(R.string.tips_bmi_23_to_249_other);
        } else if (bmi < 27.0) {
            tips = String.format(getString(R.string.tips_bmi_25_to_269_title), bmi) + "\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    getString(R.string.section_target) + ":\n" + getString(R.string.tips_bmi_25_to_269_target) + "\n\n" +
                    getString(R.string.section_diet) + ":\n" + getString(R.string.tips_bmi_25_to_269_diet) + "\n\n" +
                    getString(R.string.section_exercise) + ":\n" + getString(R.string.tips_bmi_25_to_269_exercise) + "\n\n" +
                    getString(R.string.section_other) + ":\n" + getString(R.string.tips_bmi_25_to_269_other);
        } else if (bmi < 29.0) {
            tips = String.format(getString(R.string.tips_bmi_27_to_289_title), bmi) + "\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    getString(R.string.section_target) + ":\n" + getString(R.string.tips_bmi_27_to_289_target) + "\n\n" +
                    getString(R.string.section_diet) + ":\n" + getString(R.string.tips_bmi_27_to_289_diet) + "\n\n" +
                    getString(R.string.section_exercise) + ":\n" + getString(R.string.tips_bmi_27_to_289_exercise) + "\n\n" +
                    getString(R.string.section_other) + ":\n" + getString(R.string.tips_bmi_27_to_289_other);
        } else if (bmi < 30.0) {
            tips = String.format(getString(R.string.tips_bmi_29_to_299_title), bmi) + "\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    getString(R.string.section_target) + ":\n" + getString(R.string.tips_bmi_29_to_299_target) + "\n\n" +
                    getString(R.string.section_diet) + ":\n" + getString(R.string.tips_bmi_29_to_299_diet) + "\n\n" +
                    getString(R.string.section_exercise) + ":\n" + getString(R.string.tips_bmi_29_to_299_exercise) + "\n\n" +
                    getString(R.string.section_other) + ":\n" + getString(R.string.tips_bmi_29_to_299_other);
        } else if (bmi < 32.0) {
            tips = String.format(getString(R.string.tips_bmi_30_to_319_title), bmi) + "\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    getString(R.string.section_target) + ":\n" + getString(R.string.tips_bmi_30_to_319_target) + "\n\n" +
                    getString(R.string.section_diet) + ":\n" + getString(R.string.tips_bmi_30_to_319_diet) + "\n\n" +
                    getString(R.string.section_exercise) + ":\n" + getString(R.string.tips_bmi_30_to_319_exercise) + "\n\n" +
                    getString(R.string.section_other) + ":\n" + getString(R.string.tips_bmi_30_to_319_other);
        } else if (bmi < 35.0) {
            tips = String.format(getString(R.string.tips_bmi_32_to_349_title), bmi) + "\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    getString(R.string.section_warning) + ":\n" + getString(R.string.tips_bmi_32_to_349_emergency) + "\n\n" +
                    getString(R.string.section_target) + ":\n" + getString(R.string.tips_bmi_32_to_349_target) + "\n\n" +
                    getString(R.string.section_diet) + ":\n" + getString(R.string.tips_bmi_32_to_349_diet) + "\n\n" +
                    getString(R.string.section_exercise) + ":\n" + getString(R.string.tips_bmi_32_to_349_exercise) + "\n\n" +
                    getString(R.string.section_other) + ":\n" + getString(R.string.tips_bmi_32_to_349_other);
        } else if (bmi < 40.0) {
            tips = String.format(getString(R.string.tips_bmi_35_to_399_title), bmi) + "\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    getString(R.string.section_warning) + ":\n" + getString(R.string.tips_bmi_35_to_399_emergency) + "\n\n" +
                    getString(R.string.section_medical) + ":\n" + getString(R.string.tips_bmi_35_to_399_medical) + "\n\n" +
                    getString(R.string.section_diet) + ":\n" + getString(R.string.tips_bmi_35_to_399_diet) + "\n\n" +
                    getString(R.string.section_exercise) + ":\n" + getString(R.string.tips_bmi_35_to_399_exercise) + "\n\n" +
                    getString(R.string.section_other) + ":\n" + getString(R.string.tips_bmi_35_to_399_other);
        } else {
            tips = String.format(getString(R.string.tips_bmi_40_plus_title), bmi) + "\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    getString(R.string.section_emergency) + ":\n" + getString(R.string.tips_bmi_40_plus_emergency) + "\n\n" +
                    getString(R.string.section_medical) + ":\n" + getString(R.string.tips_bmi_40_plus_medical) + "\n\n" +
                    getString(R.string.section_other) + " (পরীক্ষা):\n" + getString(R.string.tips_bmi_40_plus_tests) + "\n\n" +
                    getString(R.string.section_diet) + ":\n" + getString(R.string.tips_bmi_40_plus_diet) + "\n\n" +
                    getString(R.string.section_exercise) + ":\n" + getString(R.string.tips_bmi_40_plus_treatment) + "\n\n" +
                    "🧠 মানসিক স্বাস্থ্য:\n" + getString(R.string.tips_bmi_40_plus_mental);
        }

        binding.tvHealthTips.setText(tips);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // memory leak এড়ানোর জন্য
    }
}