package com.example.yuwathi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yuwathi.R;
import com.example.yuwathi.models.Complaint;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying complaints in RecyclerView
 */
public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder> {

    private Context context;
    private List<Complaint> complaintList;
    private List<Complaint> complaintListFull;
    private OnComplaintActionListener listener;
    private String currentSearchQuery = "";
    private String currentStatusFilter = "All";

    public interface OnComplaintActionListener {
        void onViewDetails(Complaint complaint);
        void onUpdateStatus(Complaint complaint, String newStatus);
        void onDelete(Complaint complaint);
    }

    public ComplaintAdapter(Context context, List<Complaint> complaintList, OnComplaintActionListener listener) {
        this.context = context;
        this.complaintList = complaintList;
        this.complaintListFull = new ArrayList<>(complaintList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_complaint, parent, false);
        return new ComplaintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        Complaint complaint = complaintList.get(position);
        
        holder.tvTitle.setText(complaint.getTitle());
        holder.tvLocation.setText(complaint.getLocation());
        holder.tvDate.setText(complaint.getDate());
        holder.chipStatus.setText(complaint.getStatus());
        holder.chipPriority.setText(complaint.getPriority());

        // Set status chip color
        if ("Resolved".equals(complaint.getStatus())) {
            holder.chipStatus.setChipBackgroundColorResource(android.R.color.holo_green_light);
        } else if ("Under Review".equals(complaint.getStatus())) {
            holder.chipStatus.setChipBackgroundColorResource(android.R.color.holo_orange_light);
        } else {
            holder.chipStatus.setChipBackgroundColorResource(android.R.color.holo_red_light);
        }

        // Set priority chip color
        if ("High".equals(complaint.getPriority())) {
            holder.chipPriority.setChipBackgroundColorResource(android.R.color.holo_red_light);
        } else if ("Medium".equals(complaint.getPriority())) {
            holder.chipPriority.setChipBackgroundColorResource(android.R.color.holo_orange_light);
        } else {
            holder.chipPriority.setChipBackgroundColorResource(android.R.color.holo_blue_light);
        }

        // Button listeners
        holder.btnView.setOnClickListener(v -> listener.onViewDetails(complaint));
        holder.btnUpdateStatus.setOnClickListener(v -> {
            // Cycle through statuses
            String currentStatus = complaint.getStatus();
            String newStatus = "Resolved";
            if ("Pending".equals(currentStatus)) {
                newStatus = "Under Review";
            } else if ("Under Review".equals(currentStatus)) {
                newStatus = "Resolved";
            }
            listener.onUpdateStatus(complaint, newStatus);
        });
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(complaint));
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    public void setComplaints(List<Complaint> complaints) {
        complaintList.clear();
        complaintList.addAll(complaints);
        complaintListFull.clear();
        complaintListFull.addAll(complaints);
        applyFilters();
    }

    /**
     * Filter complaints based on search query
     */
    public void filter(String query) {
        currentSearchQuery = query == null ? "" : query;
        applyFilters();
    }

    /**
     * Filter complaints by status
     */
    public void filterByStatus(String status) {
        currentStatusFilter = status == null ? "All" : status;
        applyFilters();
    }

    private void applyFilters() {
        complaintList.clear();
        String lowerCaseQuery = currentSearchQuery.toLowerCase();
        for (Complaint complaint : complaintListFull) {
            boolean matchesStatus = "All".equals(currentStatusFilter) || currentStatusFilter.equals(complaint.getStatus());
            boolean matchesQuery = currentSearchQuery.isEmpty() ||
                    (complaint.getTitle() != null && complaint.getTitle().toLowerCase().contains(lowerCaseQuery)) ||
                    (complaint.getLocation() != null && complaint.getLocation().toLowerCase().contains(lowerCaseQuery));
            if (matchesStatus && matchesQuery) {
                complaintList.add(complaint);
            }
        }
        notifyDataSetChanged();
    }

    static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvDate;
        Chip chipStatus, chipPriority;
        MaterialButton btnView, btnUpdateStatus, btnDelete;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_complaint_title);
            tvLocation = itemView.findViewById(R.id.tv_complaint_location);
            tvDate = itemView.findViewById(R.id.tv_complaint_date);
            chipStatus = itemView.findViewById(R.id.chip_complaint_status);
            chipPriority = itemView.findViewById(R.id.chip_complaint_priority);
            btnView = itemView.findViewById(R.id.btn_view_complaint);
            btnUpdateStatus = itemView.findViewById(R.id.btn_update_status);
            btnDelete = itemView.findViewById(R.id.btn_delete_complaint);
        }
    }
}
