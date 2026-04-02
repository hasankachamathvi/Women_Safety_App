package com.example.yuwathi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yuwathi.R;
import com.example.yuwathi.models.SafetyTip;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.List;

/**
 * Adapter for displaying safety tips in RecyclerView
 */
public class SafetyTipAdapter extends RecyclerView.Adapter<SafetyTipAdapter.SafetyTipViewHolder> {

    private Context context;
    private List<SafetyTip> safetyTipList;
    private OnSafetyTipActionListener listener;

    public interface OnSafetyTipActionListener {
        void onEdit(SafetyTip tip);
        void onDelete(SafetyTip tip);
        void onToggleVisibility(SafetyTip tip);
    }

    public SafetyTipAdapter(Context context, List<SafetyTip> safetyTipList, OnSafetyTipActionListener listener) {
        this.context = context;
        this.safetyTipList = safetyTipList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SafetyTipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_safety_tip, parent, false);
        return new SafetyTipViewHolder(view);
    }

    public void setTips(List<SafetyTip> tips) {
        safetyTipList.clear();
        safetyTipList.addAll(tips);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull SafetyTipViewHolder holder, int position) {
        SafetyTip tip = safetyTipList.get(position);
        
        holder.tvTitle.setText(tip.getTitle());
        holder.tvDescription.setText(tip.getDescription());
        holder.tvCategory.setText(tip.getCategory().toUpperCase());
        holder.switchVisible.setOnCheckedChangeListener(null);
        holder.switchVisible.setChecked(tip.isVisible());

        // Button listeners
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(tip));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(tip));
        holder.switchVisible.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tip.setVisible(isChecked);
            listener.onToggleVisibility(tip);
        });
    }

    @Override
    public int getItemCount() {
        return safetyTipList.size();
    }

    static class SafetyTipViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvCategory;
        SwitchMaterial switchVisible;
        MaterialButton btnEdit, btnDelete;

        public SafetyTipViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_tip_title);
            tvDescription = itemView.findViewById(R.id.tv_tip_description);
            tvCategory = itemView.findViewById(R.id.tv_tip_category);
            switchVisible = itemView.findViewById(R.id.switch_tip_visible);
            btnEdit = itemView.findViewById(R.id.btn_edit_tip);
            btnDelete = itemView.findViewById(R.id.btn_delete_tip);
        }
    }
}
