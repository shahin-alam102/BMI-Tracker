package com.voxo.bmitracker.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.voxo.bmitracker.R;

import android.widget.TextView;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.github.anastr.speedviewlib.components.Section;
import com.voxo.bmitracker.databinding.ActivityMainBinding;
import com.voxo.bmitracker.model.BmiHistory;
import com.voxo.bmitracker.viewmodel.BmiViewModel;
import com.voxo.bmitracker.viewmodel.HistoryViewModel;

import java.util.Locale;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private BmiViewModel viewModel;
    private HistoryViewModel historyViewModel;
    private String lastSavedHeight = "";
    private String lastSavedWeight = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(BmiViewModel.class);
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        historyViewModel.initialize(this);

        initUI();
        setupObservers();
        setupListeners();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        applySpeedViewThemeAndSections();
    }

    private void initUI() {
        applySpeedViewThemeAndSections();
        setupMaterialInputs();
    }

    /**
     * Safe to call from {@link #onConfigurationChanged(Configuration)} — does not re-bind listeners.
     */
    private void applySpeedViewThemeAndSections() {
        int nightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        int color = (nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) ? Color.WHITE : Color.BLACK;

        binding.speedView.setTextColor(color);
        binding.speedView.setUnitTextColor(color);
        binding.speedView.setSpeedTextColor(color);
        binding.btnReset.setColorFilter(color);
        binding.tvNormalWeightRange.setTextColor(color);

        binding.speedView.setWithTremble(false);
        binding.speedView.clearSections();
        binding.speedView.addSections(new Section(0f, .37f, Color.BLUE, binding.speedView.getSpeedometerWidth()));
        binding.speedView.addSections(new Section(.37f, .50f, Color.GREEN, binding.speedView.getSpeedometerWidth()));
        binding.speedView.addSections(new Section(.50f, .60f, Color.YELLOW, binding.speedView.getSpeedometerWidth()));
        binding.speedView.addSections(new Section(.60f, 1f, Color.RED, binding.speedView.getSpeedometerWidth()));
    }

    private void setupObservers() {
        viewModel.bmiResult.observe(this, result -> {
            if (result != null) {
                // Check current system language configuration
                boolean isBangla = Locale.getDefault().getLanguage().equals("bn");

                // Map and localize database BMI categories to localized strings
                String databaseCategory = result.getCategory();
                String localizedCategory = databaseCategory;

                if (databaseCategory.equalsIgnoreCase("Very Severely Underweight")) {
                    localizedCategory = getString(R.string.status_very_severely_underweight);
                } else if (databaseCategory.equalsIgnoreCase("Severely Underweight")) {
                    localizedCategory = getString(R.string.status_severely_underweight);
                } else if (databaseCategory.equalsIgnoreCase("Underweight")) {
                    localizedCategory = getString(R.string.status_underweight);
                } else if (databaseCategory.equalsIgnoreCase("Healthy Weight")) {
                    localizedCategory = getString(R.string.status_healthy);
                } else if (databaseCategory.equalsIgnoreCase("Overweight")) {
                    localizedCategory = getString(R.string.status_overweight);
                } else if (databaseCategory.contains("Class III") || databaseCategory.contains("III")) {
                    localizedCategory = getString(R.string.status_obese_class_iii);
                } else if (databaseCategory.contains("Class II") || databaseCategory.contains("II")) {
                    localizedCategory = getString(R.string.status_obese_class_ii);
                } else if (databaseCategory.contains("Class I") || databaseCategory.contains("I")) {
                    localizedCategory = getString(R.string.status_obese_class_i);
                }

                binding.tvCategory.setText(localizedCategory);
                binding.tvCategory.setTextColor(result.getColor());

                // Update and format ideal normal weight range
                if (result.getNormalRange() != null) {
                    String normalRange = result.getNormalRange();
                    if (isBangla) {
                        String bnRange = convertToBanglaNumber(normalRange);
                        if (bnRange.contains("kg")) {
                            bnRange = bnRange.replace("kg", "কেজি");
                        }
                        binding.tvNormalWeightRange.setText(bnRange);
                    } else {
                        binding.tvNormalWeightRange.setText(normalRange);
                    }
                }

                // Process and update weight difference feedback message
                String rawDiffText = result.getWeightDiffText();
                String localizedDiffText = rawDiffText;

                if (rawDiffText != null) {
                    if (databaseCategory.equalsIgnoreCase("Healthy Weight") || rawDiffText.toLowerCase().contains("perfect")) {
                        localizedDiffText = getString(R.string.weight_perfect);
                    } else {
                        // Extract localized numeric parts from weight difference string safely
                        StringBuilder numBuilder = new StringBuilder();
                        for (char c : rawDiffText.toCharArray()) {
                            if (Character.isDigit(c) || c == '.' || c == ',') {
                                numBuilder.append(c);
                            }
                        }
                        String numberOnly = numBuilder.toString().trim();
                        String finalNumber = isBangla ? convertToBanglaNumber(numberOnly) : numberOnly;

                        // Assign proper context to weight gain/loss messages
                        if (databaseCategory.equalsIgnoreCase("Overweight") || databaseCategory.toLowerCase().contains("obese") || rawDiffText.toLowerCase().contains("lose")) {
                            localizedDiffText = getString(R.string.lose_weight, finalNumber);
                        } else if (databaseCategory.toLowerCase().contains("underweight") || rawDiffText.toLowerCase().contains("gain")) {
                            localizedDiffText = getString(R.string.gain_weight, finalNumber);
                        }
                    }
                }

                binding.tvWeightDifference.setText(localizedDiffText);
                binding.tvWeightDifference.setTextColor(result.getDiffColor());

                // Sync and animate gauge speedometer with calculation result
                binding.speedView.speedTo(result.getBmi());
                binding.speedView.getIndicator().setColor(result.getColor());

                // Highlight corresponding row in the weight class table
                highlightRow(databaseCategory, result.getColor());

                // Persist calculation record to local storage if criteria matched
                if (isInputPlausibleForHistory()) {
                    saveToHistory(result);
                }
            } else {
                clearResults();
            }
        });
    }

    private void highlightRow(String category, int color) {
        resetTable();
        TableRow targetRow = null;

        if (category.contains("Very Severely")) targetRow = binding.tvChartVerySeverelyUnderweight;
        else if (category.contains("Severely Underweight"))
            targetRow = binding.tvChartSeverelyUnderweight;
        else if (category.equalsIgnoreCase("Underweight")) targetRow = binding.tvChartUnderweight;
        else if (category.equalsIgnoreCase("Healthy Weight")) targetRow = binding.tvChartHealthy;
        else if (category.equalsIgnoreCase("Overweight")) targetRow = binding.tvChartOverweight;

        else if (category.contains("III")) targetRow = binding.tvChartObeseClassIII;
        else if (category.contains("II")) targetRow = binding.tvChartObeseClassII;
        else if (category.contains("I")) targetRow = binding.tvChartObeseClassI;

        if (targetRow != null) {
            for (int i = 0; i < targetRow.getChildCount(); i++) {
                View v = targetRow.getChildAt(i);
                if (v instanceof TextView) {
                    ((TextView) v).setTextColor(color);
                    ((TextView) v).setTypeface(null, android.graphics.Typeface.BOLD);
                }
            }
        }
    }

    private void setupListeners() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                triggerCalc();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        binding.etHeight.addTextChangedListener(watcher);
        binding.etWeight.addTextChangedListener(watcher);

        binding.dropdownGender.setText(R.string.gender_male);
        binding.dropdownHeightUnit.setText(R.string.unit_cm);
        binding.dropdownWeightUnit.setText(R.string.unit_kg);

        binding.btnReset.setOnClickListener(v -> clearAll());
        binding.btnMenu.setOnClickListener(this::showMenu);

        // --- GENDER TOGGLE ---
        binding.dropdownGender.setOnClickListener(v -> {
            String current = binding.dropdownGender.getText().toString();
            String maleStr = getString(R.string.gender_male);

            if (current.equalsIgnoreCase(maleStr)) {
                binding.dropdownGender.setText(R.string.gender_female);
                binding.dropdownGender.setIconResource(R.drawable.outline_woman_24); // Female icon
            } else {
                binding.dropdownGender.setText(R.string.gender_male);
                binding.dropdownGender.setIconResource(R.drawable.outline_man_24); // Male icon
            }

            triggerCalc();
        });

        // --- HEIGHT UNIT TOGGLE (CM <-> Fit/Inc) ---
        binding.dropdownHeightUnit.setOnClickListener(v -> {
            String currentUnit = binding.dropdownHeightUnit.getText().toString();
            String cmStr = getString(R.string.unit_cm);

            if (currentUnit.equalsIgnoreCase(cmStr)) {
                binding.dropdownHeightUnit.setText(R.string.unit_inch);
                binding.dropdownHeightUnit.setIconResource(R.drawable.outline_height_24);
            } else {
                binding.dropdownHeightUnit.setText(R.string.unit_cm);
                binding.dropdownHeightUnit.setIconResource(R.drawable.outline_height_24);
            }
            binding.etHeight.setText("");
            triggerCalc();
        });

        // --- WEIGHT UNIT TOGGLE (KG <-> LB) ---
        binding.dropdownWeightUnit.setOnClickListener(v -> {
            String currentUnit = binding.dropdownWeightUnit.getText().toString();
            String kgStr = getString(R.string.unit_kg);

            if (currentUnit.equalsIgnoreCase(kgStr)) {
                binding.dropdownWeightUnit.setText(R.string.unit_lbs);
                binding.dropdownWeightUnit.setIconResource(R.drawable.outline_monitor_weight_24);
            } else {
                binding.dropdownWeightUnit.setText(R.string.unit_kg);
                binding.dropdownWeightUnit.setIconResource(R.drawable.outline_monitor_weight_24);
            }

            binding.etWeight.setText("");
            triggerCalc();
        });
    }

    private String getAgeText() {
        return binding.etAge.getText() != null ? binding.etAge.getText().toString().trim() : "";
    }

    /**
     * Avoid saving partial typing (e.g. "17" cm while entering "174") and nonsense values.
     * Live UI still updates from the ViewModel; history only persists plausible measurements.
     */
    private boolean isInputPlausibleForHistory() {
        String hStr = binding.etHeight.getText() != null ? binding.etHeight.getText().toString().trim() : "";
        String wStr = binding.etWeight.getText() != null ? binding.etWeight.getText().toString().trim() : "";
        if (hStr.isEmpty() || wStr.isEmpty()) {
            return false;
        }
        if (hStr.equals(lastSavedHeight) && wStr.equals(lastSavedWeight)) {
            return false;
        }
        try {
            float h = Float.parseFloat(hStr);
            float w = Float.parseFloat(wStr);
            String hUnit = getHeightUnit();
            String wUnit = getWeightUnit();
            if ("cm".equals(hUnit)) {
                if (h < 80f || h > 280f) {
                    return false;
                }
            } else if ("ft/in".equals(hUnit)) {
                if (h < 3f || h > 8.5f) {
                    return false;
                }
            } else {
                return false;
            }
            if ("kg".equals(wUnit)) {
                if (w < 12f || w > 450f) {
                    return false;
                }
            } else if ("lb".equals(wUnit)) {
                if (w < 30f || w > 1000f) {
                    return false;
                }
            } else {
                return false;
            }
            lastSavedHeight = hStr;
            lastSavedWeight = wStr;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String getHeightUnit() {
        String currentUnit = binding.dropdownHeightUnit.getText().toString().trim();
        String cmStr = getString(R.string.unit_cm);

        if (currentUnit.equalsIgnoreCase(cmStr)) {
            return "cm";
        } else {
            return "ft";
        }
    }

    private String getWeightUnit() {
        String currentUnit = binding.dropdownWeightUnit.getText().toString().trim();
        String kgStr = getString(R.string.unit_kg);

        if (currentUnit.equalsIgnoreCase(kgStr)) {
            return "kg";
        } else {
            return "lb";
        }
    }

    private String getGenderSelection() {
        CharSequence t = binding.dropdownGender.getText();
        return t != null ? t.toString() : "";
    }

    private void triggerCalc() {
        String heightText = binding.etHeight.getText() != null ? binding.etHeight.getText().toString().trim() : "";
        String weightText = binding.etWeight.getText() != null ? binding.etWeight.getText().toString().trim() : "";

        // Only calculate if both height and weight have at least 2 digits
        if (heightText.length() >= 1 && weightText.length() >= 2) {
            viewModel.calculateBMI(
                    heightText,
                    weightText,
                    getHeightUnit(),
                    getWeightUnit()
            );
        } else {
            clearResults();
        }
    }


    private void resetTable() {
        int defColor = androidx.core.content.ContextCompat.getColor(this, R.color.text_secondary_dynamic);
        TableRow[] rows = {binding.tvChartVerySeverelyUnderweight, binding.tvChartSeverelyUnderweight, binding.tvChartUnderweight,
                binding.tvChartHealthy, binding.tvChartOverweight, binding.tvChartObeseClassI,
                binding.tvChartObeseClassII, binding.tvChartObeseClassIII};
        for (TableRow row : rows) {
            if (row != null) {
                for (int i = 0; i < row.getChildCount(); i++) {
                    View v = row.getChildAt(i);
                    if (v instanceof TextView) {
                        ((TextView) v).setTextColor(defColor);
                        ((TextView) v).setTypeface(null, android.graphics.Typeface.NORMAL);
                    }
                }
            }
        }
    }

    private void clearAll() {
        binding.etAge.setText("");
        binding.etHeight.setText("");
        binding.etWeight.setText("");
        applyDropdownDefaults();
        clearResults();
        lastSavedHeight = "";
        lastSavedWeight = "";
    }

    private void clearResults() {
        binding.tvCategory.setText("");
        binding.tvNormalWeightRange.setText("00.00 - 00.00 kg");
        binding.tvWeightDifference.setText("");
        binding.speedView.speedTo(0);
        resetTable();
    }

    private void setupMaterialInputs() {

        applyDropdownDefaults();

    }

    private void showMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
        try {
            java.lang.reflect.Field field = popupMenu.getClass()
                    .getDeclaredField("mPopup");
            field.setAccessible(true);
            Object menuPopupHelper = field.get(popupMenu);
            Class<?> classPopupHelper = Class.forName(
                    menuPopupHelper.getClass().getName());
            java.lang.reflect.Method setForceIcons = classPopupHelper
                    .getMethod("setForceShowIcon", boolean.class);
            setForceIcons.invoke(menuPopupHelper, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            handleMenuItemClick(item);
            popupMenu.dismiss();  // Explicitly dismiss the menu
            return true;
        });
        popupMenu.show();
    }

    private void handleMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_history) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (itemId == R.id.menu_about) {
            showToast("BMI Tracker v1.0\nCalculate your Body Mass Index instantly!");
        } else if (itemId == R.id.menu_feedback) {
            showToast("Thank you for your feedback!");
        } else if (itemId == R.id.menu_share) {
            showToast("Thank you for sharing!");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveToHistory(com.voxo.bmitracker.model.BmiResult result) {
        BmiHistory history = new BmiHistory(
                result.getBmi(),
                result.getCategory(),
                result.getColor(),
                binding.etHeight.getText() != null ? binding.etHeight.getText().toString() : "",
                binding.etWeight.getText() != null ? binding.etWeight.getText().toString() : "",
                getHeightUnit(),
                getWeightUnit(),
                getAgeText(),
                getGenderSelection()
        );

        historyViewModel.addToHistory(history);
    }

    private void applyDropdownDefaults() {
        binding.dropdownHeightUnit.setText("CM");
        binding.dropdownWeightUnit.setText("KG");
        binding.dropdownGender.setText("Male");
        binding.dropdownGender.setIconResource(R.drawable.outline_man_24);
        binding.dropdownHeightUnit.setIconResource(R.drawable.outline_height_24);
        binding.dropdownWeightUnit.setIconResource(R.drawable.outline_monitor_weight_24);
    }


    private String convertToBanglaNumber(String englishNumber) {
        if (englishNumber == null || englishNumber.isEmpty()) return "";

        char[] banglaDigits = {'০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'};
        StringBuilder builder = new StringBuilder();

        for (char c : englishNumber.toCharArray()) {
            if (c >= '0' && c <= '9') {
                builder.append(banglaDigits[c - '0']);
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }


}


