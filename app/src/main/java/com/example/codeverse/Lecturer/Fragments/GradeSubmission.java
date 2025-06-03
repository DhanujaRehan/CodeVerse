package com.example.codeverse.Lecturer.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Students.Helpers.AssignmentUploadHelper;
import com.example.codeverse.Students.Models.AssignmentModel;
import com.example.codeverse.Lecturer.Adapters.SubmissionsListAdapter;
import com.example.codeverse.Lecturer.Adapters.SubmissionFilesAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GradeSubmission extends Fragment {

    private static final String TAG = "GradeSubmissions";

    // Selection form views
    private AutoCompleteTextView dropdownProgramme, dropdownBatch, dropdownModule, dropdownAssessment;
    private MaterialButton btnViewSubmissions;

    // Submissions list views
    private LinearLayout cardSubmissionsList, layoutGradeDetails;
    private RecyclerView rvSubmissionsList, rvSubmissionFiles;
    private TextView tvSubmissionsHeader;

    // Grade details views
    private TextView tvStudentId, tvStudentName, tvSubmissionDate;
    private TextInputEditText etMarks, etFeedback;
    private AutoCompleteTextView dropdownGrade;
    private MaterialButton btnCancel, btnSubmitGrade;

    // Loading overlay
    private FrameLayout loadingOverlay;

    // Database helper
    private AssignmentUploadHelper dbHelper;

    // Adapters
    private SubmissionsListAdapter submissionsAdapter;
    private SubmissionFilesAdapter filesAdapter;

    // Data
    private List<AssignmentModel> submissionsList;
    private List<String> submissionFiles;
    private AssignmentModel currentSubmission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grade_submissions, container, false);

        initViews(view);
        setupDatabase();
        setupAdapters();
        setupDropdowns();
        setupClickListeners(view);

        return view;
    }

    private void initViews(View view) {
        // Selection form
        dropdownProgramme = view.findViewById(R.id.dropdownProgramme);
        dropdownBatch = view.findViewById(R.id.dropdownBatch);
        dropdownModule = view.findViewById(R.id.dropdownModule);
        dropdownAssessment = view.findViewById(R.id.dropdownAssessment);
        btnViewSubmissions = view.findViewById(R.id.btnViewSubmissions);

        // Submissions list
        cardSubmissionsList = view.findViewById(R.id.card_submissions_list);
        rvSubmissionsList = view.findViewById(R.id.rvSubmissionsList);
        tvSubmissionsHeader = view.findViewById(R.id.tvSubmissionsHeader);

        // Grade details
        layoutGradeDetails = view.findViewById(R.id.layoutGradeDetails);
        tvStudentId = view.findViewById(R.id.tvStudentId);
        tvStudentName = view.findViewById(R.id.tvStudentName);
        tvSubmissionDate = view.findViewById(R.id.tvSubmissionDate);
        rvSubmissionFiles = view.findViewById(R.id.rvSubmissionFiles);
        etMarks = view.findViewById(R.id.etMarks);
        dropdownGrade = view.findViewById(R.id.dropdownGrade);
        etFeedback = view.findViewById(R.id.etFeedback);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSubmitGrade = view.findViewById(R.id.btnSubmitGrade);

        // Loading overlay
        loadingOverlay = view.findViewById(R.id.loading_overlay);
    }

    private void setupDatabase() {
        dbHelper = new AssignmentUploadHelper(getContext());
        submissionsList = new ArrayList<>();
        submissionFiles = new ArrayList<>();
    }

    private void setupAdapters() {
        // Submissions list adapter
        submissionsAdapter = new SubmissionsListAdapter(submissionsList, this::onSubmissionSelected);
        rvSubmissionsList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSubmissionsList.setAdapter(submissionsAdapter);

        // Submission files adapter
        filesAdapter = new SubmissionFilesAdapter(submissionFiles, this::onFileAction);
        rvSubmissionFiles.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSubmissionFiles.setAdapter(filesAdapter);
    }

    private void setupDropdowns() {
        // Programme dropdown
        List<String> programmes = dbHelper.getAllProgrammes();
        if (programmes.isEmpty()) {
            programmes.add("Computer Science");
            programmes.add("Software Engineering");
            programmes.add("Information Technology");
        }
        ArrayAdapter<String> programmeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, programmes);
        dropdownProgramme.setAdapter(programmeAdapter);

        List<String> batches = dbHelper.getAllBatches();
        if (batches.isEmpty()) {
            batches.add("2021");
            batches.add("2022");
            batches.add("2023");
            batches.add("2024");
        }
        ArrayAdapter<String> batchAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, batches);
        dropdownBatch.setAdapter(batchAdapter);

        List<String> modules = dbHelper.getAllModules();
        if (modules.isEmpty()) {
            modules.add("Programming Fundamentals");
            modules.add("Data Structures");
            modules.add("Database Systems");
            modules.add("Software Engineering");
            modules.add("Web Development");
        }
        ArrayAdapter<String> moduleAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, modules);
        dropdownModule.setAdapter(moduleAdapter);

        // Assessment dropdown
        List<String> assessments = dbHelper.getAllAssessmentTypes();
        if (assessments.isEmpty()) {
            assessments.add("Assignment 1");
            assessments.add("Assignment 2");
            assessments.add("Project");
            assessments.add("Lab Report");
        }
        ArrayAdapter<String> assessmentAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, assessments);
        dropdownAssessment.setAdapter(assessmentAdapter);

        // Grade dropdown
        String[] grades = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F"};
        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, grades);
        dropdownGrade.setAdapter(gradeAdapter);
    }

    private void setupClickListeners(View view) {
        // Back button
        view.findViewById(R.id.iv_back).setOnClickListener(v -> requireActivity().onBackPressed());

        // View submissions button
        btnViewSubmissions.setOnClickListener(v -> loadSubmissions());

        // Grade form buttons
        btnCancel.setOnClickListener(v -> hideGradeDetails());
        btnSubmitGrade.setOnClickListener(v -> submitGrade());

        // Hide grade details when focusing on search fields
        dropdownProgramme.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) hideGradeDetails();
        });
        dropdownBatch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) hideGradeDetails();
        });
        dropdownModule.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) hideGradeDetails();
        });
        dropdownAssessment.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) hideGradeDetails();
        });
    }

    private void loadSubmissions() {
        String programme = dropdownProgramme.getText().toString().trim();
        String batch = dropdownBatch.getText().toString().trim();
        String module = dropdownModule.getText().toString().trim();
        String assessment = dropdownAssessment.getText().toString().trim();

        if (programme.isEmpty() && batch.isEmpty() && module.isEmpty() && assessment.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one filter", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingOverlay();

        // Simulate loading delay
        new Handler().postDelayed(() -> {
            try {
                List<AssignmentModel> filteredSubmissions = dbHelper.getAssignmentsByCriteria(
                        programme.isEmpty() ? null : programme,
                        batch.isEmpty() ? null : batch,
                        module.isEmpty() ? null : module,
                        assessment.isEmpty() ? null : assessment
                );

                submissionsList.clear();
                submissionsList.addAll(filteredSubmissions);
                submissionsAdapter.notifyDataSetChanged();

                hideLoadingOverlay();

                if (submissionsList.isEmpty()) {
                    cardSubmissionsList.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "No submissions found for the selected criteria", Toast.LENGTH_LONG).show();
                } else {
                    cardSubmissionsList.setVisibility(View.VISIBLE);
                    tvSubmissionsHeader.setText("Found " + submissionsList.size() + " submission(s)");
                }

                hideGradeDetails();

            } catch (Exception e) {
                hideLoadingOverlay();
                Log.e(TAG, "Error loading submissions", e);
                Toast.makeText(getContext(), "Error loading submissions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, 1500);
    }

    private void onSubmissionSelected(AssignmentModel submission) {
        currentSubmission = submission;
        showGradeDetails(submission);
    }

    private void showGradeDetails(AssignmentModel submission) {
        // Set student information
        tvStudentId.setText(submission.getStudentId() != null ? submission.getStudentId() : "N/A");
        tvStudentName.setText(submission.getStudentName() != null ? submission.getStudentName() : "N/A");

        // Format and set submission date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date(submission.getUploadDate()));
        tvSubmissionDate.setText(formattedDate);

        // Set up submission files
        submissionFiles.clear();
        submissionFiles.add(submission.getFileName());
        filesAdapter.notifyDataSetChanged();

        // Pre-fill grade information if already graded
        if (submission.isGraded()) {
            etMarks.setText(String.valueOf(submission.getMarks()));
            dropdownGrade.setText(submission.getGrade());
            etFeedback.setText(submission.getFeedback());
            btnSubmitGrade.setText("Update Grade");
        } else {
            etMarks.setText("");
            dropdownGrade.setText("");
            etFeedback.setText("");
            btnSubmitGrade.setText("Submit Grade");
        }

        layoutGradeDetails.setVisibility(View.VISIBLE);

        // Scroll to grade details
        layoutGradeDetails.post(() -> {
            layoutGradeDetails.requestFocus();
        });
    }

    private void hideGradeDetails() {
        layoutGradeDetails.setVisibility(View.GONE);
        currentSubmission = null;
    }

    private void submitGrade() {
        if (currentSubmission == null) {
            Toast.makeText(getContext(), "No submission selected", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateGradeInput()) {
            return;
        }

        double marks = Double.parseDouble(etMarks.getText().toString().trim());
        String grade = dropdownGrade.getText().toString().trim();
        String feedback = etFeedback.getText().toString().trim();

        showLoadingOverlay();

        // Simulate processing delay
        new Handler().postDelayed(() -> {
            try {
                int result = dbHelper.gradeAssignment(currentSubmission.getId(), marks, grade, feedback);

                hideLoadingOverlay();

                if (result > 0) {
                    Toast.makeText(getContext(), "Grade submitted successfully", Toast.LENGTH_SHORT).show();

                    // Update the current submission object
                    currentSubmission.setMarks(marks);
                    currentSubmission.setGrade(grade);
                    currentSubmission.setFeedback(feedback);
                    currentSubmission.setGraded(true);
                    currentSubmission.setGradedDate(System.currentTimeMillis());

                    // Update the adapter
                    submissionsAdapter.notifyDataSetChanged();

                    hideGradeDetails();
                } else {
                    Toast.makeText(getContext(), "Failed to submit grade", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                hideLoadingOverlay();
                Log.e(TAG, "Error submitting grade", e);
                Toast.makeText(getContext(), "Error submitting grade: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    private boolean validateGradeInput() {
        String marksText = etMarks.getText().toString().trim();
        String grade = dropdownGrade.getText().toString().trim();

        if (marksText.isEmpty()) {
            etMarks.setError("Marks are required");
            etMarks.requestFocus();
            return false;
        }

        try {
            double marks = Double.parseDouble(marksText);
            if (marks < 0 || marks > 100) {
                etMarks.setError("Marks must be between 0 and 100");
                etMarks.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etMarks.setError("Invalid marks format");
            etMarks.requestFocus();
            return false;
        }

        if (grade.isEmpty()) {
            dropdownGrade.setError("Grade is required");
            dropdownGrade.requestFocus();
            return false;
        }

        return true;
    }

    private void onFileAction(String fileName, String action) {
        if (currentSubmission == null) return;

        switch (action) {
            case "download":
            case "view":
                openFile(currentSubmission.getFilePath());
                break;
        }
    }

    private void openFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), getMimeType(file.getName()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(getContext(), "Opening file", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "File not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening file", e);
            Toast.makeText(getContext(), "Error opening file", Toast.LENGTH_SHORT).show();
        }
    }

    private String getMimeType(String fileName) {
        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".doc")) {
            return "application/msword";
        } else if (fileName.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (fileName.endsWith(".txt")) {
            return "text/plain";
        }
        return "*/*";
    }

    private void showLoadingOverlay() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoadingOverlay() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}