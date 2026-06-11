package com.voxo.primebmitracker.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.voxo.primebmitracker.R;
import com.voxo.primebmitracker.databinding.ActivityHistoryBinding;
import com.voxo.primebmitracker.model.BmiHistory;
import com.voxo.primebmitracker.viewmodel.HistoryViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends BaseActivity {

    private ActivityHistoryBinding binding;
    private HistoryViewModel historyViewModel;
    private HistoryAdapter historyAdapter;
    private final List<Object> combinedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.bmi_history);
        }

        // Ensure toolbar back button handles navigation safely
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Initialize ViewModel safely using Application Context
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        historyViewModel.initialize(getApplicationContext());

        // Set up RecyclerView configuration
        setupRecyclerView();

        // Observe history live data changes
        if (historyViewModel.historyList != null) {
            historyViewModel.historyList.observe(this, this::updateHistoryDisplay);
        }

        // Handle system back press with animations
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void setupRecyclerView() {
        historyAdapter = new HistoryAdapter();
        binding.recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewHistory.setAdapter(historyAdapter);

        // Set up adapter item click listeners
        historyAdapter.setOnHistoryItemClickListener(new HistoryAdapter.OnHistoryItemClickListener() {
            @Override
            public void onHistoryItemClick(BmiHistory history) {
                if (history != null) {
                    showHistoryDetails(history);
                }
            }

            @Override
            public void onHistoryItemLongClick(int position) {
                if (combinedList.get(position) instanceof BmiHistory) {
                    showDeleteConfirmation(position);
                }
            }
        });

        attachSwipeToDelete();
    }

    private void attachSwipeToDelete() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof HistoryAdapter.AdViewHolder) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    position = viewHolder.getAbsoluteAdapterPosition();
                }
                if (position != RecyclerView.NO_POSITION && combinedList.get(position) instanceof BmiHistory) {
                    showDeleteConfirmation(position);
                }
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.recyclerViewHistory);
    }

    private void updateHistoryDisplay(List<BmiHistory> historyList) {
        if (historyList == null || historyList.isEmpty()) {
            binding.recyclerViewHistory.setVisibility(View.GONE);
            binding.emptyStateContainer.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerViewHistory.setVisibility(View.VISIBLE);
            binding.emptyStateContainer.setVisibility(View.GONE);
            AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
                    .forNativeAd(nativeAd -> {
                        // Run list building on background thread
                        new Thread(() -> {
                            List<Object> newList = new ArrayList<>();
                            int index = 0;
                            for (BmiHistory item : historyList) {
                                newList.add(item);
                                index++;
                                if (index % 4 == 0) {
                                    newList.add(nativeAd);
                                }
                            }
                            // Update UI on main thread
                            runOnUiThread(() -> {
                                combinedList.clear();
                                combinedList.addAll(newList);
                                historyAdapter.setHistoryList(combinedList);
                            });
                        }).start();
                    })
                    .withAdListener(new com.google.android.gms.ads.AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull com.google.android.gms.ads.LoadAdError adError) {
                            super.onAdFailedToLoad(adError);
                            combinedList.clear();
                            combinedList.addAll(historyList);
                            historyAdapter.setHistoryList(combinedList);
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }

    private void showClearHistoryConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Clear History")
                .setMessage("Are you sure you want to clear all BMI history? This action cannot be undone.")
                .setPositiveButton("Clear", (dialog, which) -> {
                    historyViewModel.clearHistory();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void showHistoryDetails(BmiHistory history) {
        String details = String.format(Locale.getDefault(),
                "Date & Time: %s\n\n" +
                        "BMI: %.1f\n" +
                        "Category: %s\n\n" +
                        "Height: %s %s\n" +
                        "Weight: %s %s\n" +
                        "Age: %s\n" +
                        "Gender: %s",
                history.getFormattedDate() != null ? history.getFormattedDate() : "",
                history.getBmi(),
                history.getCategory() != null ? history.getCategory() : "",
                history.getHeight() != null ? history.getHeight() : "",
                history.getHeightUnit() != null ? history.getHeightUnit() : "",
                history.getWeight() != null ? history.getWeight() : "",
                history.getWeightUnit() != null ? history.getWeightUnit() : "",
                (history.getAge() == null || history.getAge().isEmpty()) ? "Not specified" : history.getAge(),
                (history.getGender() == null || history.getGender().isEmpty()) ? "Not specified" : history.getGender()
        );

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("BMI Details")
                .setMessage(details)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void showDeleteConfirmation(int position) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete this BMI entry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int realDatabasePosition = 0;
                    for (int i = 0; i < position; i++) {
                        if (combinedList.get(i) instanceof BmiHistory) {
                            realDatabasePosition++;
                        }
                    }

                    historyViewModel.deleteItem(realDatabasePosition);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    if (historyAdapter != null) {
                        historyAdapter.notifyItemChanged(position);
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_clear_history) {
            LiveData<List<BmiHistory>> live = historyViewModel.historyList;
            List<BmiHistory> current = live != null ? live.getValue() : null;
            if (current == null || current.isEmpty()) {
                Toast.makeText(this, R.string.no_history_available, Toast.LENGTH_SHORT).show();
            } else {
                showClearHistoryConfirmation();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}