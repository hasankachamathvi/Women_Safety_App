package com.example.yuwathi.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RecyclerView adapter that binds emergency contact items.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    public interface OnContactEditListener {
        void onEdit(Map<String, Object> contact);
    }

    public interface OnContactDeleteListener {
        void onDelete(Map<String, Object> contact);
    }

    private final Context context;
    private final OnContactEditListener editListener;
    private final OnContactDeleteListener deleteListener;
    private final List<Map<String, Object>> contacts = new ArrayList<>();

    public ContactAdapter(Context context, List<Map<String, Object>> initialContacts, OnContactEditListener editListener, OnContactDeleteListener deleteListener) {
        this.context = context;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
        if (initialContacts != null) {
            contacts.addAll(initialContacts);
        }
    }

    public void setContacts(List<Map<String, Object>> newContacts) {
        int oldSize = contacts.size();
        contacts.clear();
        if (oldSize > 0) {
            notifyItemRangeRemoved(0, oldSize);
        }
        if (newContacts != null && !newContacts.isEmpty()) {
            contacts.addAll(newContacts);
            notifyItemRangeInserted(0, contacts.size());
        }
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

        LinearLayout actionsCol = new LinearLayout(context);
        actionsCol.setOrientation(LinearLayout.VERTICAL);
        actionsCol.setGravity(Gravity.END);

        MaterialButton btnEdit = new MaterialButton(context);
        btnEdit.setText(R.string.edit_contact);
        btnEdit.setAllCaps(false);

        MaterialButton btnDelete = new MaterialButton(context);
        btnDelete.setText(R.string.delete_contact);
        btnDelete.setAllCaps(false);

        actionsCol.addView(btnEdit);
        actionsCol.addView(btnDelete);

        row.addView(infoCol);
        row.addView(actionsCol);

        return new ContactViewHolder(row, tvName, tvPhone, tvRelation, btnEdit, btnDelete);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Map<String, Object> contact = contacts.get(position);
        holder.tvName.setText(String.valueOf(contact.getOrDefault("name", "Unknown")));
        holder.tvPhone.setText(String.valueOf(contact.getOrDefault("phone", "")));
        holder.tvRelation.setText(String.valueOf(contact.getOrDefault("relationship", "")));
        holder.btnEdit.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEdit(contact);
            }
        });
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

    public static final class ContactViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvPhone;
        final TextView tvRelation;
        final MaterialButton btnEdit;
        final MaterialButton btnDelete;

        public ContactViewHolder(@NonNull View itemView, TextView tvName, TextView tvPhone, TextView tvRelation, MaterialButton btnEdit, MaterialButton btnDelete) {
            super(itemView);
            this.tvName = tvName;
            this.tvPhone = tvPhone;
            this.tvRelation = tvRelation;
            this.btnEdit = btnEdit;
            this.btnDelete = btnDelete;
        }
    }
}
