package com.voxo.primebmitracker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.voxo.primebmitracker.R;
import com.voxo.primebmitracker.model.BmiHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_AD = 1;

    private List<Object> historyList = new ArrayList<>();
    private OnHistoryItemClickListener listener;

    public interface OnHistoryItemClickListener {
        void onHistoryItemClick(BmiHistory history);
        void onHistoryItemLongClick(int position);
    }

    public void setOnHistoryItemClickListener(OnHistoryItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (historyList.get(position) instanceof NativeAd) {
            return TYPE_AD;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_AD) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_native_ad, parent, false);
            return new AdViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history, parent, false);
            return new HistoryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_AD) {
            NativeAd nativeAd = (NativeAd) historyList.get(position);
            ((AdViewHolder) holder).bind(nativeAd);
        } else {
            BmiHistory history = (BmiHistory) historyList.get(position);
            ((HistoryViewHolder) holder).bind(history);
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void setHistoryList(List<Object> newHistoryList) {
        if (newHistoryList == null) newHistoryList = new ArrayList<>();
        BmiHistoryDiffCallback diffCallback = new BmiHistoryDiffCallback(this.historyList, newHistoryList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.historyList = newHistoryList;
        diffResult.dispatchUpdatesTo(this);
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
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
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    Object item = historyList.get(position);
                    if (item instanceof BmiHistory) {
                        listener.onHistoryItemClick((BmiHistory) item);
                    }
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
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
                    localizedStatus = context.getString(R.string.healthy_weight);
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

            tvHeightWeight.setText(String.format("%s %s / %s %s", finalHeight, context.getString(R.string.unit_cm), finalWeight, context.getString(R.string.unit_kg)));

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

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        private final NativeAdView adView;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            // itemView IS the NativeAdView, don't search inside it
            adView = (NativeAdView) itemView; // ← CHANGE THIS
        }
        public void bind(NativeAd nativeAd) {
            if (nativeAd == null) return;

            // ভিউ আইডি ম্যাপিং
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            adView.setMediaView(adView.findViewById(R.id.ad_media));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

            ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
            if (nativeAd.getBody() == null) {
                Objects.requireNonNull(adView.getBodyView()).setVisibility(View.GONE);
            } else {
                Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }

            if (nativeAd.getCallToAction() == null) {
                Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
            } else {
                Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
            if (nativeAd.getIcon() == null) {
                Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
            } else {
                ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
            if (nativeAd.getAdvertiser() == null) {
                Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(View.GONE);
            } else {
                ((TextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }
            if (adView.getMediaView() != null && nativeAd.getMediaContent() != null) {
                adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
            }
            adView.setNativeAd(nativeAd);
        }
    }

    private static class BmiHistoryDiffCallback extends DiffUtil.Callback {
        private final List<Object> oldList;
        private final List<Object> newList;

        public BmiHistoryDiffCallback(List<Object> oldList, List<Object> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldPos, int newPos) {
            Object oldItem = oldList.get(oldPos);
            Object newItem = newList.get(newPos);

            if (oldItem instanceof BmiHistory && newItem instanceof BmiHistory) {
                BmiHistory oldHistory = (BmiHistory) oldItem;
                BmiHistory newHistory = (BmiHistory) newItem;
                if (oldHistory.getDatabaseId() != -1 && newHistory.getDatabaseId() != -1) {
                    return oldHistory.getDatabaseId() == newHistory.getDatabaseId();
                }
                return oldHistory.getTimestamp() == newHistory.getTimestamp();
            } else if (oldItem instanceof NativeAd && newItem instanceof NativeAd) {
                return oldItem.hashCode() == newItem.hashCode();
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {
            Object oldItem = oldList.get(oldPos);
            Object newItem = newList.get(newPos);

            if (oldItem instanceof BmiHistory && newItem instanceof BmiHistory) {
                BmiHistory oldHistory = (BmiHistory) oldItem;
                BmiHistory newHistory = (BmiHistory) newItem;
                return oldHistory.getBmi() == newHistory.getBmi()
                        && Objects.equals(oldHistory.getCategory(), newHistory.getCategory())
                        && Objects.equals(oldHistory.getHeight(), newHistory.getHeight())
                        && Objects.equals(oldHistory.getWeight(), newHistory.getWeight())
                        && Objects.equals(oldHistory.getAge(), newHistory.getAge())
                        && Objects.equals(oldHistory.getGender(), newHistory.getGender());
            }
            return oldItem == newItem;
        }
    }
}