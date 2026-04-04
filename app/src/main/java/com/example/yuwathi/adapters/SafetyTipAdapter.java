package com.example.yuwathi.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yuwathi.models.SafetyTip;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter that binds safety tip items to the list UI.
 */
public class SafetyTipAdapter extends RecyclerView.Adapter<SafetyTipAdapter.TipViewHolder> {

    public interface OnSafetyTipActionListener {
        void onEdit(SafetyTip tip);
        void onDelete(SafetyTip tip);
        void onToggleVisibility(SafetyTip tip);
    }

    private final Context context;
    private final OnSafetyTipActionListener actionListener;
    private final List<SafetyTip> tips = new ArrayList<>();

    public SafetyTipAdapter(Context context, List<SafetyTip> initialTips, OnSafetyTipActionListener actionListener) {
        this.context = context;
        this.actionListener = actionListener;
        if (initialTips != null) {
            tips.addAll(initialTips);
        }
    }

    public void setTips(List<SafetyTip> newTips) {
        tips.clear();
        if (newTips != null) {
            tips.addAll(newTips);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(24, 16, 24, 16);
        row.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView tvTitle = new TextView(context);
        tvTitle.setTextSize(16f);
        tvTitle.setTypeface(tvTitle.getTypeface(), Typeface.BOLD);

        TextView tvDescription = new TextView(context);
        tvDescription.setTextSize(14f);

        TextView tvMeta = new TextView(context);
        tvMeta.setTextSize(12f);
        tvMeta.setGravity(Gravity.END);

        row.addView(tvTitle);
        row.addView(tvDescription);
        row.addView(tvMeta);

        return new TipViewHolder(row, tvTitle, tvDescription, tvMeta);
    }

    @Override
    public void onBindViewHolder(@NonNull TipViewHolder holder, int position) {
        SafetyTip tip = tips.get(position);
        holder.tvTitle.setText(tip.getTitle() == null ? "Safety Tip" : tip.getTitle());
        holder.tvDescription.setText(tip.getDescription() == null ? "" : tip.getDescription());
        String category = tip.getCategory() == null ? "General" : tip.getCategory();
        holder.tvMeta.setText(category + (tip.isVisible() ? " • Visible" : " • Hidden"));

        holder.itemView.setOnLongClickListener(v -> {
            if (actionListener != null) {
                actionListener.onToggleVisibility(tip);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return tips.size();
    }

    static class TipViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final TextView tvDescription;
        final TextView tvMeta;

        TipViewHolder(@NonNull View itemView, TextView tvTitle, TextView tvDescription, TextView tvMeta) {
            super(itemView);
            this.tvTitle = tvTitle;
            this.tvDescription = tvDescription;
            this.tvMeta = tvMeta;
        }
    }
}

