package com.voxo.primebmitracker.viewmodel;

import static com.google.common.truth.Truth.assertThat;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.voxo.primebmitracker.model.BmiResult;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BmiViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private BmiViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = new BmiViewModel();
    }

    @Test
    public void calculateBMI_emptyInputs_returnsNull() {
        viewModel.calculateBMI("", "65", "cm", "kg");
        assertThat(viewModel.bmiResult.getValue()).isNull();
    }

    @Test
    public void calculateBMI_invalidInputs_returnsNull() {
        viewModel.calculateBMI("abc", "65", "cm", "kg");
        assertThat(viewModel.bmiResult.getValue()).isNull();
    }

    @Test
    public void calculateBMI_healthyWeight_metricUnits_returnsCorrectResult() {
        viewModel.calculateBMI("175", "65", "cm", "kg");
        BmiResult result = viewModel.bmiResult.getValue();

        assertThat(result).isNotNull();
        assertThat(result.getBmi()).isWithin(0.1f).of(21.22f);
        assertThat(result.getCategory()).isEqualTo("Healthy Weight");
        assertThat(result.getWeightDiffText()).isEqualTo("Your weight is perfect!");
    }

    @Test
    public void calculateBMI_underweight_imperialUnits_returnsGainMessage() {
        viewModel.calculateBMI("5.8", "110", "ft", "lb");
        BmiResult result = viewModel.bmiResult.getValue();

        assertThat(result).isNotNull();
        assertThat(result.getCategory()).contains("Underweight");
        assertThat(result.getWeightDiffText()).contains("Gain");
    }

    @Test
    public void calculateBMI_negativeHeight_returnsNull() {

        viewModel.calculateBMI("-175", "65", "cm", "kg");
        assertThat(viewModel.bmiResult.getValue()).isNull();
    }

    @Test
    public void calculateBMI_extremeHighWeight_handlesCorrectly() {
        viewModel.calculateBMI("175", "99999", "cm", "kg");
        BmiResult result = viewModel.bmiResult.getValue();
        assertThat(result).isNotNull();
        assertThat(result.getCategory()).isEqualTo("Obese Class III");
        assertThat(result.getWeightDiffText()).contains("Lose");
    }
}