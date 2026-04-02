package com.example.yuwathi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Adapter for displaying emergency contacts in RecyclerView
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private final Context context;
    private final List<Map<String, Object>> contactList;
    private final List<Map<String, Object>> contactListFull;
    private final OnContactActionListener listener;

    public interface OnContactActionListener {
        void onDelete(Map<String, Object> contact);
    }

    public ContactAdapter(Context context, List<Map<String, Object>> contactList, OnContactActionListener listener) {
        this.context = context;
        this.contactList = contactList;
        this.contactListFull = new ArrayList<>(contactList);
        this.listener = listener;
    }

    public void setContacts(List<Map<String, Object>> contacts) {
        contactList.clear();
        contactList.addAll(contacts);
        contactListFull.clear();
        contactListFull.addAll(contacts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Map<String, Object> contact = contactList.get(position);
        holder.tvName.setText(String.valueOf(contact.get("name")));
        holder.tvPhone.setText(String.valueOf(contact.get("phone")));
        holder.tvRelationship.setText(String.valueOf(contact.get("relationship")));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(contact));
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvRelationship;
        MaterialButton btnDelete;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_contact_name);
            tvPhone = itemView.findViewById(R.id.tv_contact_phone);
            tvRelationship = itemView.findViewById(R.id.tv_contact_relationship);
            btnDelete = itemView.findViewById(R.id.btn_delete_contact);
        }
    }
}

