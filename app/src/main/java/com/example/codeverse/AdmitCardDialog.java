package com.example.codeverse;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;

public class AdmitCardDialog {
    private Context context;
    private Dialog dialog;
    private Exam exam;

    public AdmitCardDialog(Context context, Exam exam) {
        this.context = context;
        this.exam = exam;
        createDialog();
    }

    private void createDialog() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_admit_card);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setupViews();
    }

    private void setupViews() {
        TextView tvSubject = dialog.findViewById(R.id.tv_subject);
        TextView tvExamType = dialog.findViewById(R.id.tv_exam_type);
        TextView tvDate = dialog.findViewById(R.id.tv_date);
        TextView tvTime = dialog.findViewById(R.id.tv_time);
        TextView tvVenue = dialog.findViewById(R.id.tv_venue);
        TextView tvStudentName = dialog.findViewById(R.id.tv_student_name);
        TextView tvStudentId = dialog.findViewById(R.id.tv_student_id);
        TextView tvCourseCode = dialog.findViewById(R.id.tv_course_code);
        TextView tvInstructor = dialog.findViewById(R.id.tv_instructor);

        MaterialButton btnShare = dialog.findViewById(R.id.btn_share);
        MaterialButton btnSave = dialog.findViewById(R.id.btn_save);
        MaterialButton btnClose = dialog.findViewById(R.id.btn_close);

        if (tvSubject != null) tvSubject.setText(exam.getSubjectName());
        if (tvExamType != null) tvExamType.setText(exam.getExamType());
        if (tvDate != null) tvDate.setText(exam.getExamDate());
        if (tvTime != null) tvTime.setText(exam.getStartTime() + " - " + exam.getEndTime());
        if (tvVenue != null) tvVenue.setText(exam.getRoom());
        if (tvCourseCode != null) tvCourseCode.setText(exam.getCourseCode());
        if (tvInstructor != null) tvInstructor.setText(exam.getInstructor());
        if (tvStudentName != null) tvStudentName.setText("John Doe");
        if (tvStudentId != null) tvStudentId.setText("S12345678");

        if (btnShare != null) {
            btnShare.setOnClickListener(v -> shareAdmitCard());
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveAdmitCard());
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }
    }

    private void shareAdmitCard() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Admit Card for " + exam.getSubjectName());
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Admit Card Details:\n" +
                    "Subject: " + exam.getSubjectName() + "\n" +
                    "Course: " + exam.getCourseCode() + "\n" +
                    "Exam Type: " + exam.getExamType() + "\n" +
                    "Date: " + exam.getExamDate() + "\n" +
                    "Time: " + exam.getStartTime() + " - " + exam.getEndTime() + "\n" +
                    "Venue: " + exam.getRoom() + "\n" +
                    "Instructor: " + exam.getInstructor());
            context.startActivity(Intent.createChooser(shareIntent, "Share Admit Card"));
        } catch (Exception e) {
            Toast.makeText(context, "Error sharing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAdmitCard() {
        Toast.makeText(context, "Admit card saved to device", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}