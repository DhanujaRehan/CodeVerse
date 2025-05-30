package com.example.codeverse.Staff.Models;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.codeverse.Exam;
import com.example.codeverse.R;
import com.google.android.material.card.MaterialCardView;
import java.util.Calendar;

public class AddExamDialog {
    private Context context;
    private Dialog dialog;
    private OnExamAddedListener listener;

    public interface OnExamAddedListener {
        void onExamAdded(Exam exam);
    }

    public AddExamDialog(Context context) {
        this.context = context;
        createDialog();
    }

    public void setOnExamAddedListener(OnExamAddedListener listener) {
        this.listener = listener;
    }

    private void createDialog() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.fragment_add_exam_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setupViews();
    }

    private void setupViews() {
        MaterialCardView cvClose = dialog.findViewById(R.id.cv_close);
        EditText etSubjectName = dialog.findViewById(R.id.et_subject_name);
        EditText etCourseCode = dialog.findViewById(R.id.et_course_code);
        EditText etInstructor = dialog.findViewById(R.id.et_instructor);
        EditText etExamDate = dialog.findViewById(R.id.et_exam_date);
        EditText etStartTime = dialog.findViewById(R.id.et_start_time);
        EditText etEndTime = dialog.findViewById(R.id.et_end_time);
        EditText etRoom = dialog.findViewById(R.id.et_room);
        EditText etStudentCount = dialog.findViewById(R.id.et_student_count);
        EditText etNotes = dialog.findViewById(R.id.et_notes);
        AutoCompleteTextView dropdownExamType = dialog.findViewById(R.id.dropdown_exam_type);
        AutoCompleteTextView dropdownSemester = dialog.findViewById(R.id.dropdown_semester);
        Button btnCreateExam = dialog.findViewById(R.id.btn_create_exam);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnSaveDraft = dialog.findViewById(R.id.btn_save_draft);

        setupDropdowns(dropdownExamType, dropdownSemester);
        setupDateTimePickers(etExamDate, etStartTime, etEndTime);
        setupClickListeners(cvClose, btnCancel, btnCreateExam, btnSaveDraft, etSubjectName, etCourseCode,
                etInstructor, etExamDate, etStartTime, etEndTime, etRoom, etStudentCount,
                etNotes, dropdownExamType, dropdownSemester);
    }

    private void setupDropdowns(AutoCompleteTextView examType, AutoCompleteTextView semester) {
        String[] examTypes = {"Midterm", "Final", "Quiz", "Assignment", "Project"};
        String[] semesters = {"Fall 2024", "Spring 2025", "Summer 2025", "Fall 2025"};

        examType.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, examTypes));
        semester.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, semesters));
    }

    private void setupDateTimePickers(EditText dateField, EditText startTime, EditText endTime) {
        dateField.setOnClickListener(v -> showDatePicker(dateField));
        startTime.setOnClickListener(v -> showTimePicker(startTime));
        endTime.setOnClickListener(v -> showTimePicker(endTime));
    }

    private void setupClickListeners(MaterialCardView close, Button cancel, Button create, Button draft,
                                     EditText subjectName, EditText courseCode, EditText instructor,
                                     EditText examDate, EditText startTime, EditText endTime,
                                     EditText room, EditText studentCount, EditText notes,
                                     AutoCompleteTextView examType, AutoCompleteTextView semester) {
        close.setOnClickListener(v -> dialog.dismiss());
        cancel.setOnClickListener(v -> dialog.dismiss());

        create.setOnClickListener(v -> {
            if (validateForm(subjectName, courseCode, instructor, examDate, startTime, endTime)) {
                Exam exam = createExamFromInputs(subjectName, courseCode, instructor, examDate,
                        startTime, endTime, room, studentCount, notes,
                        examType, semester, "Scheduled");
                if (listener != null) {
                    listener.onExamAdded(exam);
                }
                dialog.dismiss();
            }
        });

        draft.setOnClickListener(v -> {
            if (validateForm(subjectName, courseCode, instructor, examDate, startTime, endTime)) {
                Exam exam = createExamFromInputs(subjectName, courseCode, instructor, examDate,
                        startTime, endTime, room, studentCount, notes,
                        examType, semester, "Draft");
                if (listener != null) {
                    listener.onExamAdded(exam);
                }
                dialog.dismiss();
            }
        });
    }

    private boolean validateForm(EditText subjectName, EditText courseCode, EditText instructor,
                                 EditText examDate, EditText startTime, EditText endTime) {
        if (subjectName.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, "Subject name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (courseCode.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, "Course code is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (instructor.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, "Instructor name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (examDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, "Exam date is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (startTime.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, "Start time is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (endTime.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, "End time is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private Exam createExamFromInputs(EditText subjectName, EditText courseCode, EditText instructor,
                                      EditText examDate, EditText startTime, EditText endTime,
                                      EditText room, EditText studentCount, EditText notes,
                                      AutoCompleteTextView examType, AutoCompleteTextView semester, String status) {
        Exam exam = new Exam();
        exam.setSubjectName(subjectName.getText().toString().trim());
        exam.setCourseCode(courseCode.getText().toString().trim());
        exam.setInstructor(instructor.getText().toString().trim());
        exam.setExamDate(examDate.getText().toString().trim());
        exam.setStartTime(startTime.getText().toString().trim());
        exam.setEndTime(endTime.getText().toString().trim());
        exam.setRoom(room.getText().toString().trim());
        exam.setExamType(examType.getText().toString());
        exam.setSemester(semester.getText().toString());
        exam.setNotes(notes.getText().toString().trim());
        exam.setStatus(status);

        String countText = studentCount.getText().toString().trim();
        exam.setStudentCount(countText.isEmpty() ? 0 : Integer.parseInt(countText));

        return exam;
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, year, month, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    editText.setText(date);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                (view, hourOfDay, minute) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minute);
                    editText.setText(time);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}