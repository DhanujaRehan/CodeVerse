package com.example.codeverse;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

/**
 * Adapter for displaying exam results in a RecyclerView
 */
public class ExamResultsAdapter extends RecyclerView.Adapter<ExamResultsAdapter.ExamViewHolder> {

    private List<ExamResult> examResultsList;


    public ExamResultsAdapter(List<ExamResult> examResultsList) {
        this.examResultsList = examResultsList;

    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exam_card, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        ExamResult result = examResultsList.get(position);

        // Set exam subject and details
        holder.tvExamSubject.setText(result.getSubject());
        holder.tvExamDetails.setText(result.getDetails());

        // Set exam grade
        holder.tvExamGrade.setText(result.getGrade());
        holder.tvExamGrade.setBackgroundResource(result.getBackgroundRes());
        holder.tvExamGrade.setTextColor(Color.parseColor(result.getTextColor()));

        // Set animation resource
        holder.animationExamStatus.setAnimation(result.getAnimationRes());
        holder.animationExamStatus.playAnimation();

        // Set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle item click
                // You can implement this based on your requirements
            }
        });
    }

    @Override
    public int getItemCount() {
        return examResultsList.size();
    }

    /**
     * ViewHolder for exam card
     */
    static class ExamViewHolder extends RecyclerView.ViewHolder {
        TextView tvExamSubject, tvExamDetails, tvExamGrade;
        LottieAnimationView animationExamStatus;
        MaterialCardView cvExamIcon;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            tvExamSubject = itemView.findViewById(R.id.tv_exam_subject);
            tvExamDetails = itemView.findViewById(R.id.tv_exam_details);
            tvExamGrade = itemView.findViewById(R.id.tv_exam_grade);
            animationExamStatus = itemView.findViewById(R.id.animation_exam_status);
            cvExamIcon = itemView.findViewById(R.id.cv_exam_icon);
        }
    }
}