package com.example.codeverse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Students.Helpers.ExamSchedulingHelper;
import com.example.codeverse.Students.Models.Exam;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ExamScheduling extends Fragment {

    private View rootView;
    private FloatingActionButton fabAddExam;
    private MaterialButton btnCreateExam, btnCancelExam, btnSaveExam;
    private EditText etSubjectName, etCourseCode, etExamType, etSemester, etInstructor;
    private EditText etExamDate, etStartTime, etEndTime, etRoom, etStudentCount, etNotes;
    private RecyclerView rvExams;
    private FrameLayout bottomSheetContainer, emptyState, successOverlay;
    private ExamSchedulingHelper dbHelper;
    private List<Exam> examList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_exam_scheduling, container, false);

        initViews();
        setupDatabase();
        setupClickListeners();
        loadExams();

        return rootView;
    }

    private void initViews() {
        fabAddExam = rootView.findViewById(R.id.fab_add_exam);
        btnCreateExam = rootView.findViewById(R.id.btn_create_exam);
        btnCancelExam = rootView.findViewById(R.id.btn_cancel_exam);
        btnSaveExam = rootView.findViewById(R.id.btn_save_exam);

        etSubjectName = rootView.findViewById(R.id.et_subject_name);
        etCourseCode = rootView.findViewById(R.id.et_course_code);
        etExamType = rootView.findViewById(R.id.et_exam_type);
        etSemester = rootView.findViewById(R.id.et_semester);
        etInstructor = rootView.findViewById(R.id.et_instructor);
        etExamDate = rootView.findViewById(R.id.et_exam_date);
        etStartTime = rootView.findViewById(R.id.et_start_time);
        etEndTime = rootView.findViewById(R.id.et_end_time);
        etRoom = rootView.findViewById(R.id.et_room);
        etStudentCount = rootView.findViewById(R.id.et_student_count);
        etNotes = rootView.findViewById(R.id.et_notes);

        rvExams = rootView.findViewById(R.id.rv_exams);
        bottomSheetContainer = rootView.findViewById(R.id.bottom_sheet_container);
        emptyState = rootView.findViewById(R.id.layout_empty_state);
        successOverlay = rootView.findViewById(R.id.success_overlay);
    }

    private void setupDatabase() {
        dbHelper = new ExamSchedulingHelper(getContext());
    }

    private void setupClickListeners() {
        fabAddExam.setOnClickListener(v -> showBottomSheet());
        btnCreateExam.setOnClickListener(v -> showBottomSheet());
        btnCancelExam.setOnClickListener(v -> hideBottomSheet());
        btnSaveExam.setOnClickListener(v -> saveExam());
    }

    private void showBottomSheet() {
        clearFields();
        bottomSheetContainer.setVisibility(View.VISIBLE);
    }

    private void hideBottomSheet() {
        bottomSheetContainer.setVisibility(View.GONE);
    }

    private void clearFields() {
        etSubjectName.setText("");
        etCourseCode.setText("");
        etExamType.setText("");
        etSemester.setText("");
        etInstructor.setText("");
        etExamDate.setText("");
        etStartTime.setText("");
        etEndTime.setText("");
        etRoom.setText("");
        etStudentCount.setText("");
        etNotes.setText("");
    }

    private void saveExam() {
        if (!validateFields()) {
            return;
        }

        Exam exam = new Exam();
        exam.setSubjectName(etSubjectName.getText().toString().trim());
        exam.setCourseCode(etCourseCode.getText().toString().trim());
        exam.setExamType(etExamType.getText().toString().trim());
        exam.setSemester(etSemester.getText().toString().trim());
        exam.setInstructor(etInstructor.getText().toString().trim());
        exam.setExamDate(etExamDate.getText().toString().trim());
        exam.setStartTime(etStartTime.getText().toString().trim());
        exam.setEndTime(etEndTime.getText().toString().trim());
        exam.setRoom(etRoom.getText().toString().trim());

        String studentCountStr = etStudentCount.getText().toString().trim();
        int studentCount = studentCountStr.isEmpty() ? 0 : Integer.parseInt(studentCountStr);
        exam.setStudentCount(studentCount);

        exam.setNotes(etNotes.getText().toString().trim());
        exam.setStatus("Scheduled");

        long result = dbHelper.insertExam(exam);

        if (result != -1) {
            hideBottomSheet();
            showSuccessMessage();
            loadExams();
        } else {
            Toast.makeText(getContext(), "Failed to save exam", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateFields() {
        if (etSubjectName.getText().toString().trim().isEmpty()) {
            etSubjectName.setError("Subject name required");
            return false;
        }
        if (etCourseCode.getText().toString().trim().isEmpty()) {
            etCourseCode.setError("Course code required");
            return false;
        }
        if (etExamType.getText().toString().trim().isEmpty()) {
            etExamType.setError("Exam type required");
            return false;
        }
        if (etExamDate.getText().toString().trim().isEmpty()) {
            etExamDate.setError("Exam date required");
            return false;
        }
        if (etStartTime.getText().toString().trim().isEmpty()) {
            etStartTime.setError("Start time required");
            return false;
        }
        if (etRoom.getText().toString().trim().isEmpty()) {
            etRoom.setError("Room required");
            return false;
        }
        return true;
    }

    private void showSuccessMessage() {
        successOverlay.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            successOverlay.setVisibility(View.GONE);
        }, 2000);
    }

    private void loadExams() {
        examList = dbHelper.getAllExams();

        if (examList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            rvExams.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvExams.setVisibility(View.VISIBLE);
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