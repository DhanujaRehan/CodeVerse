package com.example.codeverse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codeverse.Assignment;
import com.example.codeverse.R;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {
    private Context context;
    private List<Assignment> assignments;
    private OnAssignmentClickListener listener;

    public interface OnAssignmentClickListener {
        void onDownloadClick(Assignment assignment);
    }

    public AssignmentAdapter(Context context, List<Assignment> assignments) {
        this.context = context;
        this.assignments = assignments != null ? assignments : new ArrayList<>();
    }

    public void setOnAssignmentClickListener(OnAssignmentClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assignment_download, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {

        if (assignments != null && position < assignments.size()) {
            Assignment assignment = assignments.get(position);

            holder.tvTitle.setText(assignment.getTitle());
            holder.tvModule.setText(assignment.getModule());
            holder.tvDescription.setText(assignment.getDescription());
            holder.tvDueDate.setText(assignment.getDueDate());
            holder.tvReleaseDate.setText(assignment.getReleaseDate());
            holder.tvWeighting.setText(assignment.getWeighting() + "%");
            holder.tvStatus.setText(assignment.getStatus());

            holder.btnDownload.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDownloadClick(assignment);
                }
            });
        }
    }

    @Override
    public int getItemCount() {

        return assignments != null ? assignments.size() : 0;
    }

    public void updateAssignments(List<Assignment> newAssignments) {

        this.assignments = newAssignments != null ? newAssignments : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvModule, tvDescription, tvDueDate, tvReleaseDate, tvWeighting, tvStatus;
        MaterialButton btnDownload;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_assignment_title);
            tvModule = itemView.findViewById(R.id.tv_assignment_module);
            tvDescription = itemView.findViewById(R.id.tv_assignment_description);
            tvDueDate = itemView.findViewById(R.id.tv_due_date);
            tvReleaseDate = itemView.findViewById(R.id.tv_release_date);
            tvWeighting = itemView.findViewById(R.id.tv_assignment_weighting);
            tvStatus = itemView.findViewById(R.id.tv_assignment_status);
            btnDownload = itemView.findViewById(R.id.btn_download);
        }
    }
}