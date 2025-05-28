package com.example.codeverse.Admin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Admin.Models.MitigationRequest;

import java.util.List;

public class MitigationRequestsAdapter extends RecyclerView.Adapter<MitigationRequestsAdapter.ViewHolder> {

    private List<MitigationRequest> requests;
    private Context context;
    private OnRequestClickListener listener;

    public interface OnRequestClickListener {
        void onRequestClick(MitigationRequest request);
    }

    public MitigationRequestsAdapter(Context context, List<MitigationRequest> requests) {
        this.context = context;
        this.requests = requests;
    }

    public void setOnRequestClickListener(OnRequestClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mitigation_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MitigationRequest request = requests.get(position);

        // Add null checks for safety
        if (holder.tvStudentName != null) holder.tvStudentName.setText(request.getStudentName());
        if (holder.tvStudentId != null) holder.tvStudentId.setText(request.getStudentId());
        if (holder.tvRequestType != null) holder.tvRequestType.setText(request.getRequestType());
        if (holder.tvModule != null) holder.tvModule.setText(request.getModule());
        if (holder.tvAssessment != null) holder.tvAssessment.setText(request.getAssessment());
        if (holder.tvSubmittedDate != null) holder.tvSubmittedDate.setText(request.getSubmittedDate());
        if (holder.tvProgrammeBatch != null) {
            holder.tvProgrammeBatch.setText(request.getProgramme() + " - " + request.getBatch());
        }

        // Set priority with color
        if (holder.tvPriority != null) {
            holder.tvPriority.setText(request.getPriority().getDisplayName());
            int priorityColor = getPriorityColor(request.getPriority());
            holder.tvPriority.setBackgroundColor(priorityColor);
        }

        // Set reason preview
        if (holder.tvReasonPreview != null) {
            String reason = request.getReason();
            if (reason.length() > 50) {
                reason = reason.substring(0, 47) + "...";
            }
            holder.tvReasonPreview.setText(reason);
        }

        // Show attachment icon if documents exist
        if (holder.ivHasDocuments != null) {
            if (request.getSupportingDocuments() != null && !request.getSupportingDocuments().isEmpty()) {
                holder.ivHasDocuments.setVisibility(View.VISIBLE);
            } else {
                holder.ivHasDocuments.setVisibility(View.GONE);
            }
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRequestClick(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    private int getPriorityColor(MitigationRequest.Priority priority) {
        switch (priority) {
            case URGENT:
                return ContextCompat.getColor(context, R.color.error_color);
            case HIGH:
                return ContextCompat.getColor(context, R.color.warning_color);
            case MEDIUM:
                return ContextCompat.getColor(context, R.color.purple_primary);
            case LOW:
            default:
                return ContextCompat.getColor(context, R.color.success_color);
        }
    }

    public void updateRequests(List<MitigationRequest> newRequests) {
        this.requests = newRequests;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvStudentId, tvPriority, tvSubmittedDate,
                tvRequestType, tvModule, tvAssessment, tvReasonPreview, tvProgrammeBatch;
        ImageView ivHasDocuments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvSubmittedDate = itemView.findViewById(R.id.tvSubmittedDate);
            tvRequestType = itemView.findViewById(R.id.tvRequestType);
            tvModule = itemView.findViewById(R.id.tvModule);
            tvAssessment = itemView.findViewById(R.id.tvAssessment);
            tvReasonPreview = itemView.findViewById(R.id.tvReasonPreview);
            tvProgrammeBatch = itemView.findViewById(R.id.tvProgrammeBatch);
            ivHasDocuments = itemView.findViewById(R.id.ivHasDocuments);
        }
    }
}