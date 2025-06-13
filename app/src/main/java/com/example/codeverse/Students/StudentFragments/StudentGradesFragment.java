package com.example.codeverse.Students.StudentFragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Students.Adapters.StudentGradesAdapter;
import com.example.codeverse.Students.Helpers.AssignmentUploadHelper;
import com.example.codeverse.Students.Models.AssignmentModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class StudentGradesFragment extends Fragment {

    private TextView tvOverallGrade, tvOverallPercentage;
    private TextView tvTotalAssignments, tvGradedAssignments, tvPendingAssignments;
    private TextView tvEmptyState;
    private CircularProgressIndicator progressOverall;
    private RecyclerView rvGradesList;
    private MaterialButton btnRefresh;
    private FrameLayout loadingOverlay;

    private AssignmentUploadHelper dbHelper;
    private StudentGradesAdapter gradesAdapter;
    private List<AssignmentModel> gradesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_grades, container, false);

        initViews(view);
        setupDatabase();
        setupRecyclerView();
        setupClickListeners();
        loadGrades();

        return view;
    }

    private void initViews(View view) {
        view.findViewById(R.id.iv_back).setOnClickListener(v -> requireActivity().onBackPressed());

        tvOverallGrade = view.findViewById(R.id.tv_overall_grade);
        tvOverallPercentage = view.findViewById(R.id.tv_overall_percentage);
        progressOverall = view.findViewById(R.id.progress_overall);

        tvTotalAssignments = view.findViewById(R.id.tv_total_assignments);
        tvGradedAssignments = view.findViewById(R.id.tv_graded_assignments);
        tvPendingAssignments = view.findViewById(R.id.tv_pending_assignments);

        rvGradesList = view.findViewById(R.id.rv_grades_list);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        loadingOverlay = view.findViewById(R.id.loading_overlay);
    }

    private void setupDatabase() {
        dbHelper = new AssignmentUploadHelper(getContext());
        gradesList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        gradesAdapter = new StudentGradesAdapter(gradesList);
        rvGradesList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGradesList.setAdapter(gradesAdapter);
    }

    private void setupClickListeners() {
        btnRefresh.setOnClickListener(v -> loadGrades());
    }

    private void loadGrades() {
        showLoading();

        new Handler().postDelayed(() -> {
            List<AssignmentModel> allAssignments = dbHelper.getAllAssignments();
            List<AssignmentModel> gradedAssignments = new ArrayList<>();

            for (AssignmentModel assignment : allAssignments) {
                if (assignment.isGraded()) {
                    gradedAssignments.add(assignment);
                }
            }

            gradesList.clear();
            gradesList.addAll(gradedAssignments);
            gradesAdapter.notifyDataSetChanged();

            updateStatistics(allAssignments, gradedAssignments);
            hideLoading();

            if (gradedAssignments.isEmpty()) {
                showEmptyState();
            } else {
                hideEmptyState();
            }
        }, 1000);
    }

    private void updateStatistics(List<AssignmentModel> allAssignments, List<AssignmentModel> gradedAssignments) {
        int totalAssignments = allAssignments.size();
        int gradedCount = gradedAssignments.size();
        int pendingCount = totalAssignments - gradedCount;

        tvTotalAssignments.setText(String.valueOf(totalAssignments));
        tvGradedAssignments.setText(String.valueOf(gradedCount));
        tvPendingAssignments.setText(String.valueOf(pendingCount));

        if (gradedCount > 0) {
            double totalMarks = 0;
            for (AssignmentModel assignment : gradedAssignments) {
                totalMarks += assignment.getMarks();
            }

            double averageMarks = totalMarks / gradedCount;
            String overallGrade = calculateGrade(averageMarks);

            tvOverallGrade.setText(overallGrade);
            tvOverallPercentage.setText(String.format("%.1f%% Average", averageMarks));
            progressOverall.setProgress((int) averageMarks);

            setGradeColor(overallGrade);
        } else {
            tvOverallGrade.setText("N/A");
            tvOverallPercentage.setText("No grades yet");
            progressOverall.setProgress(0);
        }
    }

    private String calculateGrade(double marks) {
        if (marks >= 90) return "A+";
        else if (marks >= 85) return "A";
        else if (marks >= 80) return "A-";
        else if (marks >= 75) return "B+";
        else if (marks >= 70) return "B";
        else if (marks >= 65) return "B-";
        else if (marks >= 60) return "C+";
        else if (marks >= 55) return "C";
        else if (marks >= 50) return "C-";
        else if (marks >= 45) return "D+";
        else if (marks >= 40) return "D";
        else return "F";
    }

    private void setGradeColor(String grade) {
        int color;
        if (grade.startsWith("A")) {
            color = getResources().getColor(android.R.color.holo_green_dark);
        } else if (grade.startsWith("B")) {
            color = getResources().getColor(android.R.color.holo_blue_dark);
        } else if (grade.startsWith("C")) {
            color = getResources().getColor(android.R.color.holo_orange_dark);
        } else {
            color = getResources().getColor(android.R.color.holo_red_dark);
        }

        tvOverallGrade.setTextColor(color);
        progressOverall.setIndicatorColor(color);
    }

    private void showLoading() {
        loadingOverlay.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingOverlay.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        rvGradesList.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
    }

    private void hideEmptyState() {
        rvGradesList.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}