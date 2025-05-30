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
import android.widget.TextView;
import android.widget.Toast;

import com.example.codeverse.Exam;
import com.example.codeverse.R;
import com.google.android.material.card.MaterialCardView;
import java.util.Calendar;

public class EditExamDialog {
    private Context context;
    private Dialog dialog;
    private Exam currentExam;
    private OnExamEditedListener listener;

    public interface OnExamEditedListener {
        void onExamUpdated(Exam exam);
        void onExamDeleted(int examId);
    }

    public EditExamDialog(Context context, Exam exam) {
        this.context = context;
        this.currentExam = exam;
        createDialog();
    }

    public void setOnExamEditedListener(OnExamEditedListener listener) {
        this.listener = listener;
    }

    private void createDialog() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.fragment_edit_exam_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setupViews();
    }

    private void setupViews() {
        MaterialCardView cvClose = dialog.findViewById(R.id.cv_close_edit);
        TextView tvSubjectDisplay = dialog.findViewById(R.id.tv_subject_display);
        TextView tvCourseDisplay = dialog.findViewById(R.id.tv_course_display);
        TextView tvExamTypeDisplay = dialog.findViewById(R.id.tv_exam_type_display);
        TextView tvInstructorDisplay = dialog.findViewById(R.id.tv_instructor_display);
        TextView tvCurrentStatus = dialog.findViewById(R.id.tv_current_status);
        EditText etEditExamDate = dialog.findViewById(R.id.et_edit_exam_date);
        EditText etEditStartTime = dialog.findViewById(R.id.et_edit_start_time);
        EditText etEditEndTime = dialog.findViewById(R.id.et_edit_end_time);
        EditText etEditRoom = dialog.findViewById(R.id.et_edit_room);
        EditText etEditStudentCount = dialog.findViewById(R.id.et_edit_student_count);
        EditText etEditNotes = dialog.findViewById(R.id.et_edit_notes);
        AutoCompleteTextView dropdownEditStatus = dialog.findViewById(R.id.dropdown_edit_status);
        Button btnSaveChanges = dialog.findViewById(R.id.btn_save_changes);
        Button btnCancelEdit = dialog.findViewById(R.id.btn_cancel_edit);
        Button btnDeleteExam = dialog.findViewById(R.id.btn_delete_exam);
        Button btnQuickSchedule = dialog.findViewById(R.id.btn_quick_schedule);
        Button btnQuickCancel = dialog.findViewById(R.id.btn_quick_cancel);

        populateFields(tvSubjectDisplay, tvCourseDisplay, tvExamTypeDisplay, tvInstructorDisplay,
                tvCurrentStatus, etEditExamDate, etEditStartTime, etEditEndTime, etEditRoom,
                etEditStudentCount, etEditNotes, dropdownEditStatus);

        setupDateTimePickers(etEditExamDate, etEditStartTime, etEditEndTime);
        setupClickListeners(cvClose, btnSaveChanges, btnCancelEdit, btnDeleteExam, btnQuickSchedule,
                btnQuickCancel, etEditExamDate, etEditStartTime, etEditEndTime, etEditRoom,
                etEditStudentCount, etEditNotes, dropdownEditStatus);
    }

    private void populateFields(TextView subject, TextView course, TextView examType, TextView instructor,
                                TextView status, EditText date, EditText startTime, EditText endTime,
                                EditText room, EditText studentCount, EditText notes,
                                AutoCompleteTextView statusDropdown) {
        subject.setText(currentExam.getSubjectName());
        course.setText(currentExam.getCourseCode());
        examType.setText(currentExam.getExamType());
        instructor.setText(currentExam.getInstructor());
        status.setText(currentExam.getStatus());
        date.setText(currentExam.getExamDate());
        startTime.setText(currentExam.getStartTime());
        endTime.setText(currentExam.getEndTime());
        room.setText(currentExam.getRoom());
        studentCount.setText(String.valueOf(currentExam.getStudentCount()));
        notes.setText(currentExam.getNotes());

        String[] statuses = {"Scheduled", "Pending", "Cancelled", "Completed", "Draft"};
        statusDropdown.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, statuses));
        statusDropdown.setText(currentExam.getStatus(), false);
    }

    private void setupDateTimePickers(EditText dateField, EditText startTime, EditText endTime) {
        dateField.setOnClickListener(v -> showDatePicker(dateField));
        startTime.setOnClickListener(v -> showTimePicker(startTime));
        endTime.setOnClickListener(v -> showTimePicker(endTime));
    }

    private void setupClickListeners(MaterialCardView close, Button save, Button cancel, Button delete,
                                     Button quickSchedule, Button quickCancel, EditText date, EditText startTime,
                                     EditText endTime, EditText room, EditText studentCount, EditText notes,
                                     AutoCompleteTextView status) {
        close.setOnClickListener(v -> dialog.dismiss());
        cancel.setOnClickListener(v -> dialog.dismiss());

        save.setOnClickListener(v -> {
            if (validateEditForm(date, startTime, endTime)) {
                updateExamFromInputs(date, startTime, endTime, room, studentCount, notes, status);
                if (listener != null) {
                    listener.onExamUpdated(currentExam);
                }
                dialog.dismiss();
            }
        });

        delete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExamDeleted(currentExam.getId());
            }
            dialog.dismiss();
        });

        quickSchedule.setOnClickListener(v -> {
            currentExam.setStatus("Scheduled");
            if (listener != null) {
                listener.onExamUpdated(currentExam);
            }
            dialog.dismiss();
        });

        quickCancel.setOnClickListener(v -> {
            currentExam.setStatus("Cancelled");
            if (listener != null) {
                listener.onExamUpdated(currentExam);
            }
            dialog.dismiss();
        });
    }

    private boolean validateEditForm(EditText date, EditText startTime, EditText endTime) {
        if (date.getText().toString().trim().isEmpty()) {
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

    private void updateExamFromInputs(EditText date, EditText startTime, EditText endTime,
                                      EditText room, EditText studentCount, EditText notes,
                                      AutoCompleteTextView status) {
        currentExam.setExamDate(date.getText().toString().trim());
        currentExam.setStartTime(startTime.getText().toString().trim());
        currentExam.setEndTime(endTime.getText().toString().trim());
        currentExam.setRoom(room.getText().toString().trim());
        currentExam.setNotes(notes.getText().toString().trim());
        currentExam.setStatus(status.getText().toString());

        String countText = studentCount.getText().toString().trim();
        currentExam.setStudentCount(countText.isEmpty() ? 0 : Integer.parseInt(countText));
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
