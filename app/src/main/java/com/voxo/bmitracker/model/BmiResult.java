package com.voxo.bmitracker.model;

public class BmiResult {
    private final float bmi;
    private final String category;
    private final int color;
    private final String normalRange;
    private final String weightDiffText;
    private final int diffColor;

    public BmiResult(float bmi, String category, int color, String normalRange, String weightDiffText, int diffColor) {
        this.bmi = bmi;
        this.category = category;
        this.color = color;
        this.normalRange = normalRange;
        this.weightDiffText = weightDiffText;
        this.diffColor = diffColor;
    }

    public float getBmi() {
        return bmi;
    }

    public String getCategory() {
        return category;
    }

    public int getColor() {
        return color;
    }

    public String getNormalRange() {
        return normalRange;
    }

    public String getWeightDiffText() {
        return weightDiffText;
    }

    public int getDiffColor() {
        return diffColor;
    }
}