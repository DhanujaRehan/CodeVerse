package com.example.codeverse.Staff.StaffFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.Staff.Models.AddExamDialog;
import com.example.codeverse.Students.Models.Exam;
import com.example.codeverse.R;
import com.example.codeverse.Staff.Adapters.ExamAdapter;
import com.example.codeverse.Staff.Models.EditExamDialog;
import com.example.codeverse.Students.Helpers.ExamSchedulingHelper;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class StaffExam extends Fragment implements ExamAdapter.OnItemClickListener,
        AddExamDialog.OnExamAddedListener, EditExamDialog.OnExamEditedListener {

    private RecyclerView rvExams;
    private ExamAdapter adapter;
    private AutoCompleteTextView dropdownExamType, dropdownSemester;
    private List<Exam> examList;
    private MaterialCardView cvBack;
    private ExamSchedulingHelper dbHelper;
    private FloatingActionButton fabAddExam;
    private TextView tvTotalExams, tvScheduledExams, tvPendingExams;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_exam, container, false);

        cvBack.setOnClickListener(v -> navigateBack());
        initViews(view);
        setupRecyclerView();
        setupDropdowns();
        loadData();

        return view;
    }

    private void navigateBack() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void initViews(View view) {
        dbHelper = new ExamSchedulingHelper(getContext());
        examList = new ArrayList<>();
        dropdownExamType = view.findViewById(R.id.dropdown_exam_type);
        dropdownSemester = view.findViewById(R.id.dropdown_semester);
        rvExams = view.findViewById(R.id.rv_exams);
        fabAddExam = view.findViewById(R.id.fab_add_exam);
        tvTotalExams = view.findViewById(R.id.tv_total_exams);
        tvScheduledExams = view.findViewById(R.id.tv_scheduled_exams);
        tvPendingExams = view.findViewById(R.id.tv_pending_exams);
        cvBack = view.findViewById(R.id.cv_back);
        fabAddExam.setOnClickListener(v -> showAddDialog());
    }

    private void setupRecyclerView() {
        adapter = new ExamAdapter(getContext(), examList);
        adapter.setOnItemClickListener(this);
        rvExams.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExams.setAdapter(adapter);
    }

    private void setupDropdowns() {
        String[] modules = {"Written", "Online"};
        ArrayAdapter<String> moduleAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, modules);
        dropdownExamType.setAdapter(moduleAdapter);

        String[] batches = {"1", "2", "3", "4"};
        ArrayAdapter<String> batchAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, batches);
        dropdownSemester.setAdapter(batchAdapter);
    }

    private void loadData() {
        examList.clear();
        examList.addAll(dbHelper.getAllExams());
        adapter.updateList(examList);
        updateStats();
    }

    private void updateStats() {
        int total = examList.size();
        int scheduled = 0;
        int pending = 0;

        for (Exam exam : examList) {
            if ("Scheduled".equals(exam.getStatus())) {
                scheduled++;
            } else if ("Pending".equals(exam.getStatus())) {
                pending++;
            }
        }

        tvTotalExams.setText(String.valueOf(total));
        tvScheduledExams.setText(String.valueOf(scheduled));
        tvPendingExams.setText(String.valueOf(pending));
    }


    private void showAddDialog() {
        AddExamDialog dialog = new AddExamDialog(getContext());
        dialog.setOnExamAddedListener(this);
        dialog.show();
    }

    @Override
    public void onEditClick(Exam exam) {
        EditExamDialog dialog = new EditExamDialog(getContext(), exam);
        dialog.setOnExamEditedListener(this);
        dialog.show();
    }

    @Override
    public void onExamAdded(Exam exam) {
        long result = dbHelper.insertExam(exam);
        if (result > 0) {
            Toast.makeText(getContext(), "Exam added successfully", Toast.LENGTH_SHORT).show();
            loadData();
        }
    }

    @Override
    public void onExamUpdated(Exam exam) {
        int result = dbHelper.updateExam(exam);
        if (result > 0) {
            Toast.makeText(getContext(), "Exam updated successfully", Toast.LENGTH_SHORT).show();
            loadData();
        }
    }

    @Override
    public void onExamDeleted(int examId) {
        dbHelper.deleteExam(examId);
        Toast.makeText(getContext(), "Exam deleted successfully", Toast.LENGTH_SHORT).show();
        loadData();
    }
}