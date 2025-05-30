package com.example.codeverse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display examination screen with results
 */
public class StudentExam extends Fragment {

    private RecyclerView rvExamResults;
    private ExamResultsAdapter examResultsAdapter;
    private List<ExamResult> examResultsList;

    // UI Components
    private MaterialCardView cvBack;
    private ImageView ivBack, ivNotification, ivFilter;
    private TextView tvSelectedSemester, tvViewAllResults;
    private MaterialCardView cardAdmission, cardSubmissions;

    // Fragment's root view
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_student_exam, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        initViews();

        // Set up click listeners
        setupClickListeners();

        // Set up the RecyclerView with mock data
        setupRecyclerView();
    }

    private void initViews() {
        // Initialize main UI components
        cvBack = rootView.findViewById(R.id.cv_back);
        ivBack = rootView.findViewById(R.id.iv_back);
        ivNotification = rootView.findViewById(R.id.iv_notification);
        ivFilter = rootView.findViewById(R.id.iv_filter);
        tvSelectedSemester = rootView.findViewById(R.id.tv_selected_semester);
        tvViewAllResults = rootView.findViewById(R.id.tv_view_all_results);

        // Initialize cards
        cardAdmission = rootView.findViewById(R.id.card_admission);
        cardSubmissions = rootView.findViewById(R.id.card_submissions);

        // Initialize RecyclerView
        rvExamResults = rootView.findViewById(R.id.rv_exam_results);
    }

    private void setupClickListeners() {
        // Back button click listener
        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back using fragment manager
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                } else if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        // Notification icon click listener
        ivNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Notifications", Toast.LENGTH_SHORT).show();
            }
        });

        // Filter icon click listener
        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Filter options", Toast.LENGTH_SHORT).show();
            }
        });

        // Semester selector click listener
        tvSelectedSemester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Select semester", Toast.LENGTH_SHORT).show();
            }
        });

        // View all results click listener
        tvViewAllResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "View all results", Toast.LENGTH_SHORT).show();
            }
        });

        // Exam Admission card click listener
        cardAdmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Exam Admissions", Toast.LENGTH_SHORT).show();
            }
        });

        // Exam Submissions card click listener
        cardSubmissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Exam Submissions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        // Initialize the exam results list with mock data
        examResultsList = createMockExamResults();

        // Create and set the adapter
        examResultsAdapter = new ExamResultsAdapter(examResultsList);

        // Set up the RecyclerView
        rvExamResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExamResults.setAdapter(examResultsAdapter);
        rvExamResults.setNestedScrollingEnabled(false);
    }

    /**
     * Create mock data for exam results
     */
    private List<ExamResult> createMockExamResults() {
        List<ExamResult> results = new ArrayList<>();

        // Add mock exam results
        results.add(new ExamResult(
                "Advanced Algorithms",
                "Final Exam • May 2, 2025",
                "A (92%)",
                R.raw.successfull,
                R.drawable.chip_success_background,
                "#2E7D32"
        ));

        results.add(new ExamResult(
                "Database Systems",
                "Midterm • April 15, 2025",
                "B+ (87%)",
                R.raw.successfull,
                R.drawable.chip_success_background,
                "#2E7D32"
        ));

        results.add(new ExamResult(
                "Software Engineering",
                "Project Defense • April 28, 2025",
                "A- (90%)",
                R.raw.successfull,
                R.drawable.chip_success_background,
                "#2E7D32"
        ));

        return results;
    }

    /**
     * Model class for exam result
     */


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up references to avoid memory leaks
        rootView = null;
        examResultsAdapter = null;
        examResultsList = null;
    }
}