package com.voxo.bmitracker.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.voxo.bmitracker.R;

public class HealthTipsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tips);

        Toolbar toolbar = findViewById(R.id.toolbarHealthTips);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        CardView cardLoss = findViewById(R.id.cardWeightLoss);
        CardView cardGain = findViewById(R.id.cardWeightGain);
        CardView cardHealthy = findViewById(R.id.cardHealthyLiving);
        cardLoss.setOnClickListener(v -> openDetailPage("loss"));
        cardGain.setOnClickListener(v -> openDetailPage("gain"));
        cardHealthy.setOnClickListener(v -> openDetailPage("healthy"));
    }

    private void openDetailPage(String category) {
        Intent intent = new Intent(this, TipsDetailActivity.class);
        intent.putExtra("CATEGORY_KEY", category);
        startActivity(intent);
    }
}