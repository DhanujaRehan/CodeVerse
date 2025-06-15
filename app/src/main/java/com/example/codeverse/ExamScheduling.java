package com.example.codeverse;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Students.Helpers.ExamSchedulingHelper;
import com.example.codeverse.Students.Models.Exam;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;

public class ExamScheduling extends Fragment {

    private View view;
    private ExamSchedulingHelper dbHelper;
    private FloatingActionButton fabAddExam;
    private MaterialButton btnCreateExam, btnSaveExam, btnCancelExam;
    private TextInputEditText etSubjectName, etCourseCode, etExamType, etSemester, etInstructor;
    private TextInputEditText etExamDate, etStartTime, etEndTime, etRoom, etStudentCount, etNotes;
    private View layoutEmptyState, bottomSheetContainer, successOverlay;
    private RecyclerView rvExams;
    private List<Exam> examList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_exam_scheduling, container, false);
        initViews();
        initDatabase();
        setupClickListeners();
        loadExams();
        return view;
    }

    private void initViews() {
        fabAddExam = view.findViewById(R.id.fab_add_exam);
        btnCreateExam = view.findViewById(R.id.btn_create_exam);
        btnSaveExam = view.findViewById(R.id.btn_save_exam);
        btnCancelExam = view.findViewById(R.id.btn_cancel_exam);

        etSubjectName = view.findViewById(R.id.et_subject_name);
        etCourseCode = view.findViewById(R.id.et_course_code);
        etExamType = view.findViewById(R.id.et_exam_type);
        etSemester = view.findViewById(R.id.et_semester);
        etInstructor = view.findViewById(R.id.et_instructor);
        etExamDate = view.findViewById(R.id.et_exam_date);
        etStartTime = view.findViewById(R.id.et_start_time);
        etEndTime = view.findViewById(R.id.et_end_time);
        etRoom = view.findViewById(R.id.et_room);
        etStudentCount = view.findViewById(R.id.et_student_count);
        etNotes = view.findViewById(R.id.et_notes);

        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        bottomSheetContainer = view.findViewById(R.id.bottom_sheet_container);
        successOverlay = view.findViewById(R.id.success_overlay);
        rvExams = view.findViewById(R.id.rv_exams);
    }

    private void initDatabase() {
        dbHelper = new ExamSchedulingHelper(getContext());
    }

    private void setupClickListeners() {
        fabAddExam.setOnClickListener(v -> showBottomSheet());
        btnCreateExam.setOnClickListener(v -> showBottomSheet());
        btnCancelExam.setOnClickListener(v -> hideBottomSheet());
        btnSaveExam.setOnClickListener(v -> saveExam());

        etExamDate.setOnClickListener(v -> showDatePicker());
        etStartTime.setOnClickListener(v -> showTimePicker(etStartTime));
        etEndTime.setOnClickListener(v -> showTimePicker(etEndTime));
    }

    private void showBottomSheet() {
        bottomSheetContainer.setVisibility(View.VISIBLE);
        clearFields();
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

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etExamDate.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                    editText.setText(time);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void saveExam() {
        String subjectName = etSubjectName.getText().toString().trim();
        String courseCode = etCourseCode.getText().toString().trim();
        String examType = etExamType.getText().toString().trim();
        String semester = etSemester.getText().toString().trim();
        String instructor = etInstructor.getText().toString().trim();
        String examDate = etExamDate.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        String room = etRoom.getText().toString().trim();
        String studentCountStr = etStudentCount.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (subjectName.isEmpty() || courseCode.isEmpty() || examDate.isEmpty()) {
            Toast.makeText(getContext(), "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int studentCount = 0;
        if (!studentCountStr.isEmpty()) {
            try {
                studentCount = Integer.parseInt(studentCountStr);
            } catch (NumberFormatException e) {
                studentCount = 0;
            }
        }

        Exam exam = new Exam();
        exam.setSubjectName(subjectName);
        exam.setCourseCode(courseCode);
        exam.setExamType(examType);
        exam.setSemester(semester);
        exam.setInstructor(instructor);
        exam.setExamDate(examDate);
        exam.setStartTime(startTime);
        exam.setEndTime(endTime);
        exam.setRoom(room);
        exam.setStudentCount(studentCount);
        exam.setNotes(notes);
        exam.setStatus("Scheduled");

        long result = dbHelper.insertExam(exam);

        if (result != -1) {
            hideBottomSheet();
            showSuccessOverlay();
            loadExams();
        } else {
            Toast.makeText(getContext(), "Failed to save exam", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSuccessOverlay() {
        successOverlay.setVisibility(View.VISIBLE);
        view.postDelayed(() -> successOverlay.setVisibility(View.GONE), 2000);
    }

    private void loadExams() {
        examList = dbHelper.getAllExams();

        if (examList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvExams.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvExams.setVisibility(View.VISIBLE);
            setupRecyclerView();
        }
    }

    private void setupRecyclerView() {
        rvExams.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}