package com.example.codeverse.Students.StudentFragments;

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
import com.airbnb.lottie.LottieAnimationView;
import com.example.codeverse.Students.Activities.AdmitCardDialog;
import com.example.codeverse.Exam;
import com.example.codeverse.Students.Helpers.ExamSchedulingHelper;
import com.example.codeverse.R;
import com.example.codeverse.Students.Adapters.StudentExamAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class AdmissionDownload extends Fragment implements StudentExamAdapter.OnExamActionListener {

    private RecyclerView rvAdmissionCards;
    private MaterialCardView cvBack;
    private ImageView ivHelp;
    private MaterialCardView cardSemesterSelector;
    private MaterialCardView cardSupport;
    private TextView tvSelectedSemester;
    private TextView tvSemesterLabel;
    private MaterialButton semesterSwitch;
    private LottieAnimationView animationCalendar;
    private StudentExamAdapter adapter;
    private List<Exam> examList;
    private ExamSchedulingHelper dbHelper;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_admission_download, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews();
        setupClickListeners();
        setupExamCards();
        loadExamData();
    }

    private void initializeViews() {
        dbHelper = new ExamSchedulingHelper(getContext());
        examList = new ArrayList<>();

        rvAdmissionCards = rootView.findViewById(R.id.rv_admission_cards);
        cvBack = rootView.findViewById(R.id.cv_back);
        ivHelp = rootView.findViewById(R.id.iv_help);
        cardSemesterSelector = rootView.findViewById(R.id.card_semester_selector);
        cardSupport = rootView.findViewById(R.id.card_support);
        tvSelectedSemester = rootView.findViewById(R.id.tv_selected_semester);
        tvSemesterLabel = rootView.findViewById(R.id.tv_semester_label);
        semesterSwitch = rootView.findViewById(R.id.semester_switch);
        animationCalendar = rootView.findViewById(R.id.animation_calendar);

        if (animationCalendar != null) {
            animationCalendar.playAnimation();
        }
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(view -> navigateToBack());
        ivHelp.setOnClickListener(view -> navigateToHelpFragment());
        cardSemesterSelector.setOnClickListener(view -> showSemesterSelection());
        cardSupport.setOnClickListener(view -> showSupportDialog());

        if (semesterSwitch != null) {
            semesterSwitch.setOnClickListener(view -> showSemesterSelection());
        }
    }

    private void setupExamCards() {
        adapter = new StudentExamAdapter(getContext(), examList);
        adapter.setOnExamActionListener(this);
        rvAdmissionCards.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAdmissionCards.setAdapter(adapter);
    }

    private void loadExamData() {
        examList.clear();
        List<Exam> allExams = dbHelper.getAllExams();

        for (Exam exam : allExams) {
            if ("Scheduled".equals(exam.getStatus()) || "Pending".equals(exam.getStatus())) {
                examList.add(exam);
            }
        }

        adapter.updateExams(examList);
    }

    private void navigateToBack() {
        StudentExam studentExamFragment = new StudentExam();
        if (getParentFragmentManager() != null) {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout, studentExamFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToHelpFragment() {
        try {
            DialogHelp helpFragment = new DialogHelp();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout, helpFragment)
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Help not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSemesterSelection() {
        Toast.makeText(getContext(), "Semester selection feature coming soon", Toast.LENGTH_SHORT).show();
    }

    private void showSupportDialog() {
        DialogSupport dialogSupport = new DialogSupport();
        if (getParentFragmentManager() != null) {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout, dialogSupport)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onDownloadAdmitCard(Exam exam) {
        if ("Scheduled".equals(exam.getStatus())) {
            showAdmitCardDialog(exam);
        } else {
            Toast.makeText(getContext(), "Admit card not available for this exam", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAdmitCardDialog(Exam exam) {
        AdmitCardDialog dialog = new AdmitCardDialog(getContext(), exam);
        dialog.show();
    }

    public static AdmissionDownload newInstance() {
        return new AdmissionDownload();
    }

    public static AdmissionDownload newInstance(String semester) {
        AdmissionDownload fragment = new AdmissionDownload();
        Bundle args = new Bundle();
        args.putString("selected_semester", semester);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
        examList = null;
    }
}