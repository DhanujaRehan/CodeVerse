package com.example.codeverse.Students.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Students.Models.AssignmentModel;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentGradesAdapter extends RecyclerView.Adapter<StudentGradesAdapter.GradeViewHolder> {

    private List<AssignmentModel> gradesList;

    public StudentGradesAdapter(List<AssignmentModel> gradesList) {
        this.gradesList = gradesList;
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade, parent, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        AssignmentModel assignment = gradesList.get(position);

        holder.tvAssignmentTitle.setText(assignment.getTitle());

        String subjectModule = assignment.getSubject();
        if (assignment.getModule() != null && !assignment.getModule().isEmpty()) {
            subjectModule = assignment.getModule();
        }
        holder.tvSubjectModule.setText(subjectModule);

        holder.tvGrade.setText(assignment.getGrade());
        holder.tvMarks.setText(String.format("%.0f/100", assignment.getMarks()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        String submissionDate = "Submitted: " + dateFormat.format(new Date(assignment.getUploadDate()));
        holder.tvSubmissionDate.setText(submissionDate);

        if (assignment.getGradedDate() > 0) {
            String gradedDate = "Graded: " + dateFormat.format(new Date(assignment.getGradedDate()));
            holder.tvGradedDate.setText(gradedDate);
            holder.tvGradedDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvGradedDate.setVisibility(View.GONE);
        }

        if (assignment.getFeedback() != null && !assignment.getFeedback().trim().isEmpty()) {
            holder.tvFeedback.setText(assignment.getFeedback());
            holder.btnViewFeedback.setVisibility(View.VISIBLE);

            holder.btnViewFeedback.setOnClickListener(v -> {
                if (holder.tvFeedback.getVisibility() == View.VISIBLE) {
                    holder.tvFeedback.setVisibility(View.GONE);
                    holder.btnViewFeedback.setText("View Feedback");
                } else {
                    holder.tvFeedback.setVisibility(View.VISIBLE);
                    holder.btnViewFeedback.setText("Hide Feedback");
                }
            });
        } else {
            holder.btnViewFeedback.setVisibility(View.GONE);
            holder.tvFeedback.setVisibility(View.GONE);
        }

        setGradeColor(holder.tvGrade, assignment.getGrade());
    }

    @Override
    public int getItemCount() {
        return gradesList.size();
    }

    private void setGradeColor(TextView tvGrade, String grade) {
        int color;
        if (grade.startsWith("A")) {
            color = tvGrade.getContext().getResources().getColor(android.R.color.holo_green_dark);
        } else if (grade.startsWith("B")) {
            color = tvGrade.getContext().getResources().getColor(android.R.color.holo_blue_dark);
        } else if (grade.startsWith("C")) {
            color = tvGrade.getContext().getResources().getColor(android.R.color.holo_orange_dark);
        } else {
            color = tvGrade.getContext().getResources().getColor(android.R.color.holo_red_dark);
        }
        tvGrade.setTextColor(color);
    }

    public static class GradeViewHolder extends RecyclerView.ViewHolder {
        TextView tvAssignmentTitle, tvSubjectModule, tvGrade, tvMarks;
        TextView tvSubmissionDate, tvGradedDate, tvFeedback;
        MaterialButton btnViewFeedback;

        public GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAssignmentTitle = itemView.findViewById(R.id.tv_assignment_title);
            tvSubjectModule = itemView.findViewById(R.id.tv_subject_module);
            tvGrade = itemView.findViewById(R.id.tv_grade);
            tvMarks = itemView.findViewById(R.id.tv_marks);
            tvSubmissionDate = itemView.findViewById(R.id.tv_submission_date);
            tvGradedDate = itemView.findViewById(R.id.tv_graded_date);
            tvFeedback = itemView.findViewById(R.id.tv_feedback);
            btnViewFeedback = itemView.findViewById(R.id.btn_view_feedback);
        }
    }
}