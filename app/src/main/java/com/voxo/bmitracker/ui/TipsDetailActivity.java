package com.voxo.bmitracker.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.voxo.bmitracker.R;

public class TipsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_detail);

        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvContent = findViewById(R.id.tvDetailContent);

        // কোন ক্যাটাগরি ক্লিক হয়েছে তা রিসিভ করা
        String category = getIntent().getStringExtra("CATEGORY_KEY");

        if (category != null) {
            switch (category) {
                case "loss":
                    if (getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.title_weight_loss); // টাইটেল সেট
                    tvTitle.setText(getString(R.string.title_weight_loss));
                    tvContent.setText(getString(R.string.content_weight_loss));
                    break;

                case "gain":
                    if (getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.title_weight_gain); // টাইটেল সেট
                    tvTitle.setText(getString(R.string.title_weight_gain));
                    tvContent.setText(getString(R.string.content_weight_gain));
                    break;

                case "healthy":
                    if (getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.title_healthy_living); // টাইটেল সেট
                    tvTitle.setText(getString(R.string.title_healthy_living));
                    tvContent.setText(getString(R.string.content_healthy_living));
                    break;
            }
        }
    }
}