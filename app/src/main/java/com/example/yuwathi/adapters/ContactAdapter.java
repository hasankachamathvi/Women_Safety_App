package com.example.yuwathi.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    public interface OnContactDeleteListener {
        void onDelete(Map<String, Object> contact);
    }

    private final Context context;
    private final OnContactDeleteListener deleteListener;
    private final List<Map<String, Object>> contacts = new ArrayList<>();

    public ContactAdapter(Context context, List<Map<String, Object>> initialContacts, OnContactDeleteListener deleteListener) {
        this.context = context;
        this.deleteListener = deleteListener;
        if (initialContacts != null) {
            contacts.addAll(initialContacts);
        }
    }

    public void setContacts(List<Map<String, Object>> newContacts) {
        contacts.clear();
        if (newContacts != null) {
            contacts.addAll(newContacts);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(24, 16, 24, 16);
        row.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        row.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout infoCol = new LinearLayout(context);
        infoCol.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        infoCol.setLayoutParams(infoParams);

        TextView tvName = new TextView(context);
        tvName.setTextSize(16f);
        tvName.setTypeface(tvName.getTypeface(), android.graphics.Typeface.BOLD);

        TextView tvPhone = new TextView(context);
        tvPhone.setTextSize(14f);

        TextView tvRelation = new TextView(context);
        tvRelation.setTextSize(13f);

        infoCol.addView(tvName);
        infoCol.addView(tvPhone);
        infoCol.addView(tvRelation);

        MaterialButton btnDelete = new MaterialButton(context);
        btnDelete.setText("Delete");
        btnDelete.setAllCaps(false);

        row.addView(infoCol);
        row.addView(btnDelete);

        return new ContactViewHolder(row, tvName, tvPhone, tvRelation, btnDelete);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Map<String, Object> contact = contacts.get(position);
        holder.tvName.setText(String.valueOf(contact.getOrDefault("name", "Unknown")));
        holder.tvPhone.setText(String.valueOf(contact.getOrDefault("phone", "")));
        holder.tvRelation.setText(String.valueOf(contact.getOrDefault("relationship", "")));
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvPhone;
        final TextView tvRelation;
        final MaterialButton btnDelete;

        ContactViewHolder(@NonNull View itemView, TextView tvName, TextView tvPhone, TextView tvRelation, MaterialButton btnDelete) {
            super(itemView);
            this.tvName = tvName;
            this.tvPhone = tvPhone;
            this.tvRelation = tvRelation;
            this.btnDelete = btnDelete;
        }
    }
}

