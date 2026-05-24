package com.voxo.bmitracker.ui;

import android.os.Bundle;
import com.voxo.bmitracker.R;
import com.voxo.bmitracker.databinding.ActivityTipsDetailBinding;

public class TipsDetailActivity extends BaseActivity {


    private ActivityTipsDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTipsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarDetail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbarDetail.setNavigationOnClickListener(v -> onBackPressed());

        if (binding != null) {
            loadBannerAd(binding.adViewTips);
        }

        String category = getIntent().getStringExtra("CATEGORY_KEY");
        if (category != null) {
            switch (category) {
                case "loss":
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(R.string.title_weight_loss);
                    }
                    binding.tvDetailTitle.setText(R.string.title_weight_loss);
                    binding.tvDetailContent.setText(R.string.content_weight_loss);
                    break;

                case "gain":
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(R.string.title_weight_gain);
                    }
                    binding.tvDetailTitle.setText(R.string.title_weight_gain);
                    binding.tvDetailContent.setText(R.string.content_weight_gain);
                    break;

                case "healthy":
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(R.string.title_healthy_living);
                    }
                    binding.tvDetailTitle.setText(R.string.title_healthy_living);
                    binding.tvDetailContent.setText(R.string.content_healthy_living);
                    break;
            }
        }
    }
}