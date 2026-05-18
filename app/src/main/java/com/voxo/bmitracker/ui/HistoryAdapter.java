package com.voxo.bmitracker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.voxo.bmitracker.R;
import com.voxo.bmitracker.model.BmiHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<BmiHistory> historyList = new ArrayList<>();
    private OnHistoryItemClickListener listener;

    public interface OnHistoryItemClickListener {
        void onHistoryItemClick(BmiHistory history);

        void onHistoryItemLongClick(int position);
    }

    public void setOnHistoryItemClickListener(OnHistoryItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        BmiHistory history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void setHistoryList(List<BmiHistory> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < historyList.size()) {
            historyList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, historyList.size());
        }
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvBmiValue;
        private final TextView tvCategory;
        private final TextView tvDateTime;
        private final TextView tvHeightWeight;
        private final TextView tvAgeGender;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBmiValue = itemView.findViewById(R.id.tvBmiValue);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvHeightWeight = itemView.findViewById(R.id.tvHeightWeight);
            tvAgeGender = itemView.findViewById(R.id.tvAgeGender);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onHistoryItemClick(historyList.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onHistoryItemLongClick(position);
                    return true;
                }
                return false;
            });
        }

        public void bind(BmiHistory history) {
            if (history == null) return;

            Context context = itemView.getContext();
            boolean isBangla = Locale.getDefault().getLanguage().equals("bn");

            String rawBmi = history.getBmiFormatted();
            tvBmiValue.setText(isBangla ? convertToBanglaNumber(rawBmi) : rawBmi);

            String databaseStatus = history.getCategory();
            String localizedStatus = databaseStatus;

            if (databaseStatus != null) {
                String lowerStatus = databaseStatus.toLowerCase();
                if (lowerStatus.contains("very severely")) {
                    localizedStatus = context.getString(R.string.status_very_severely_underweight);
                } else if (lowerStatus.contains("severely underweight")) {
                    localizedStatus = context.getString(R.string.status_severely_underweight);
                } else if (lowerStatus.contains("underweight")) {
                    localizedStatus = context.getString(R.string.status_underweight);
                } else if (lowerStatus.contains("healthy")) {
                    localizedStatus = context.getString(R.string.status_healthy);
                } else if (lowerStatus.contains("overweight")) {
                    localizedStatus = context.getString(R.string.status_overweight);
                } else if (databaseStatus.contains("III") || databaseStatus.contains("3")) {
                    localizedStatus = context.getString(R.string.status_obese_class_iii);
                } else if (databaseStatus.contains("II") || databaseStatus.contains("2")) {
                    localizedStatus = context.getString(R.string.status_obese_class_ii);
                } else if (databaseStatus.contains("I") || databaseStatus.contains("1")) {
                    localizedStatus = context.getString(R.string.status_obese_class_i);
                }
            }
            tvCategory.setText(localizedStatus);
            tvCategory.setTextColor(history.getColor());

            String rawDate = history.getFormattedDate();
            tvDateTime.setText(isBangla ? convertToBanglaNumber(rawDate) : rawDate);

            String rawHeight = String.valueOf(history.getHeight());
            String rawWeight = String.valueOf(history.getWeight());

            String finalHeight = isBangla ? convertToBanglaNumber(rawHeight) : rawHeight;
            String finalWeight = isBangla ? convertToBanglaNumber(rawWeight) : rawWeight;

            tvHeightWeight.setText(finalHeight + " " + context.getString(R.string.unit_cm) + " / " + finalWeight + " " + context.getString(R.string.unit_kg));

            StringBuilder ageGenderBuilder = new StringBuilder();

            if (history.getAge() != null && !history.getAge().isEmpty()) {
                String rawAge = history.getAge();
                String finalAge = isBangla ? convertToBanglaNumber(rawAge) : rawAge;
                ageGenderBuilder.append(context.getString(R.string.history_age, finalAge));
            }

            if (history.getGender() != null && !history.getGender().isEmpty()) {
                if (ageGenderBuilder.length() > 0) {
                    ageGenderBuilder.append(" | ");
                }

                String databaseGender = history.getGender();
                String localizedGender = context.getString(databaseGender.equalsIgnoreCase("Male") ? R.string.gender_male : R.string.gender_female);
                ageGenderBuilder.append(context.getString(R.string.history_gender, localizedGender));
            }

            String ageGenderText = ageGenderBuilder.toString();
            tvAgeGender.setText(ageGenderText);
            tvAgeGender.setVisibility(ageGenderText.isEmpty() ? View.GONE : View.VISIBLE);
        }

        private String convertToBanglaNumber(String text) {
            if (text == null) return "";
            return text
                    .replace("0", "০")
                    .replace("1", "১")
                    .replace("2", "২")
                    .replace("3", "৩")
                    .replace("4", "৪")
                    .replace("5", "৫")
                    .replace("6", "৬")
                    .replace("7", "৭")
                    .replace("8", "৮")
                    .replace("9", "৯");
        }
    }
}