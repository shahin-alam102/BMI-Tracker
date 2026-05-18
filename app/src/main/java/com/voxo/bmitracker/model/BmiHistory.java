package com.voxo.bmitracker.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BmiHistory implements Serializable {
    private double bmi;
    private String category;
    private int color;
    private String height;
    private String weight;
    private String heightUnit;
    private String weightUnit;
    private String age;
    private String gender;
    private long timestamp;
    private transient long databaseId = -1L;

    public BmiHistory() {
        this.timestamp = System.currentTimeMillis();
    }

    public BmiHistory(double bmi, String category, int color, String height, String weight,
                      String heightUnit, String weightUnit, String age, String gender) {
        this.bmi = bmi;
        this.category = category;
        this.color = color;
        this.height = height;
        this.weight = weight;
        this.heightUnit = heightUnit;
        this.weightUnit = weightUnit;
        this.age = age;
        this.gender = gender;
        this.timestamp = System.currentTimeMillis();
    }

    public BmiHistory(String date, String time, float height, float weight, float bmi, String category, String heightUnit, String weightUnit) {
        this.bmi = bmi;
        this.category = category;
        this.height = String.valueOf(height);
        this.weight = String.valueOf(weight);
        this.heightUnit = heightUnit;
        this.weightUnit = weightUnit;
        this.age = "";
        this.gender = "";
        this.color = 0;
        this.timestamp = System.currentTimeMillis();
    }

    public double getBmi() {
        return bmi;
    }

    public String getCategory() {
        return category;
    }

    public int getColor() {
        return color;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeightUnit() {
        return heightUnit;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(long databaseId) {
        this.databaseId = databaseId;
    }

    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setHeightUnit(String heightUnit) {
        this.heightUnit = heightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getBmiFormatted() {
        return String.format(Locale.getDefault(), "%.1f", bmi);
    }

    public String getHeightWeightDisplay() {
        return height + " " + heightUnit + " / " + weight + " " + weightUnit;
    }

    @Override
    public String toString() {
        return String.format("%s | BMI: %.1f | %s | %s", getFormattedDate(), bmi, category, getHeightWeightDisplay());
    }

    public boolean matchesSnapshot(BmiHistory o) {
        if (o == null) return false;
        return Math.abs(bmi - o.bmi) < 0.05
                && safeEq(height, o.height)
                && safeEq(weight, o.weight)
                && safeEq(heightUnit, o.heightUnit)
                && safeEq(weightUnit, o.weightUnit)
                && safeEq(age, o.age)
                && safeEq(gender, o.gender);
    }

    private static boolean safeEq(String a, String b) {
        if (a == null) return b == null || b.isEmpty();
        if (b == null) return a.isEmpty();
        return a.equals(b);
    }
}