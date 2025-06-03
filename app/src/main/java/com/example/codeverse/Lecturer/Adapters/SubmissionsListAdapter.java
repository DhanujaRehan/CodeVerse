package com.example.codeverse.Lecturer.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Students.Models.AssignmentModel;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SubmissionsListAdapter extends RecyclerView.Adapter<SubmissionsListAdapter.SubmissionViewHolder> {

    private List<AssignmentModel> submissions;
    private OnSubmissionClickListener listener;

    public interface OnSubmissionClickListener {
        void onSubmissionSelected(AssignmentModel submission);
    }

    public SubmissionsListAdapter(List<AssignmentModel> submissions, OnSubmissionClickListener listener) {
        this.submissions = submissions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubmissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_submission, parent, false);
        return new SubmissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubmissionViewHolder holder, int position) {
        AssignmentModel submission = submissions.get(position);
        holder.bind(submission);
    }

    @Override
    public int getItemCount() {
        return submissions.size();
    }

    class SubmissionViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardSubmission;
        private TextView tvStudentName, tvStudentId, tvAssignmentTitle, tvSubmissionDate, tvGradeStatus;

        public SubmissionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardSubmission = itemView.findViewById(R.id.card_submission);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStudentId = itemView.findViewById(R.id.tv_student_id);
            tvAssignmentTitle = itemView.findViewById(R.id.tv_assignment_title);
            tvSubmissionDate = itemView.findViewById(R.id.tv_submission_date);
            tvGradeStatus = itemView.findViewById(R.id.tv_grade_status);
        }

        public void bind(AssignmentModel submission) {
            tvStudentName.setText(submission.getStudentName() != null ? submission.getStudentName() : "Unknown Student");
            tvStudentId.setText(submission.getStudentId() != null ? submission.getStudentId() : "N/A");
            tvAssignmentTitle.setText(submission.getTitle());

            // Format submission date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(new Date(submission.getUploadDate()));
            tvSubmissionDate.setText(formattedDate);

            // Set grade status
            if (submission.isGraded()) {
                tvGradeStatus.setText("Graded: " + submission.getGrade() + " (" + submission.getMarks() + "/100)");
                tvGradeStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            } else {
                tvGradeStatus.setText("Not Graded");
                tvGradeStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
            }

            // Set click listener
            cardSubmission.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSubmissionSelected(submission);
                }
            });

            // Add visual feedback for graded submissions
            if (submission.isGraded()) {
                cardSubmission.setStrokeColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_light));
                cardSubmission.setStrokeWidth(2);
            } else {
                cardSubmission.setStrokeColor(itemView.getContext().getResources().getColor(android.R.color.holo_orange_light));
                cardSubmission.setStrokeWidth(2);
            }
        }
    }
}