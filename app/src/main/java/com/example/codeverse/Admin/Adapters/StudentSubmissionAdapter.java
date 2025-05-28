package com.example.codeverse.Admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Admin.Models.StudentSubmission;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class StudentSubmissionAdapter extends RecyclerView.Adapter<StudentSubmissionAdapter.SubmissionViewHolder> {

    private final List<StudentSubmission> submissions;
    private final OnSubmissionClickListener listener;

    public interface OnSubmissionClickListener {
        void onSubmissionClick(StudentSubmission submission);
    }

    public StudentSubmissionAdapter(List<StudentSubmission> submissions, OnSubmissionClickListener listener) {
        this.submissions = submissions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubmissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_submission, parent, false);
        return new SubmissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubmissionViewHolder holder, int position) {
        StudentSubmission submission = submissions.get(position);
        holder.bind(submission, listener);
    }

    @Override
    public int getItemCount() {
        return submissions.size();
    }

    static class SubmissionViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvStudentName;
        private final TextView tvStudentId;
        private final TextView tvSubmissionDate;
        private final TextView tvSubmissionStatus;
        private final MaterialButton btnGradeSubmission;

        public SubmissionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            tvSubmissionDate = itemView.findViewById(R.id.tvSubmissionDate);
            tvSubmissionStatus = itemView.findViewById(R.id.tvSubmissionStatus);
            btnGradeSubmission = itemView.findViewById(R.id.btnGradeSubmission);
        }

        public void bind(final StudentSubmission submission, final OnSubmissionClickListener listener) {
            tvStudentName.setText(submission.getStudentName());
            tvStudentId.setText(submission.getStudentId());
            tvSubmissionDate.setText(submission.getSubmissionDate());
            tvSubmissionStatus.setText(submission.getStatus());

            // Change status color based on status
            if ("Graded".equals(submission.getStatus())) {
                tvSubmissionStatus.setBackgroundResource(R.drawable.status_badge_bg_green);
            } else {
                tvSubmissionStatus.setBackgroundResource(R.drawable.status_badge_bg);
            }

            // Set the grade button text based on status
            btnGradeSubmission.setText("Graded".equals(submission.getStatus()) ? "View Grade" : "Grade");

            btnGradeSubmission.setOnClickListener(v -> listener.onSubmissionClick(submission));
        }
    }
}