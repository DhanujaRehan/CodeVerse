package com.example.codeverse;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.widget.ImageView;
import android.widget.TextView;

public class LecturerNotesFragment extends Fragment {

    private TextInputEditText etLecturerName, etSubject, etLectureTitle, etLectureDate, etChapter, etDescription;
    private MaterialButton btnShareNotes;
    private MaterialCardView cvBack, cardUpload;
    private ImageView ivBack, ivHelp, ivNotesPreview;
    private FloatingActionButton fabRemoveNotes;
    private TextView tvUploadInstruction, tvUploadHint, tvSharingStatus;

    private LectureNotesHelper dbHelper;
    private Calendar calendar;
    private String selectedFilePath = "";
    private static final int PICK_FILE_REQUEST = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecturer_notes, container, false);

        initViews(view);
        setupDatabase();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        etLecturerName = view.findViewById(R.id.et_lecturer_name);
        etSubject = view.findViewById(R.id.et_subject);
        etLectureTitle = view.findViewById(R.id.et_lecture_title);
        etLectureDate = view.findViewById(R.id.et_lecture_date);
        etChapter = view.findViewById(R.id.et_chapter);
        etDescription = view.findViewById(R.id.et_description);

        btnShareNotes = view.findViewById(R.id.btn_share_notes);
        cvBack = view.findViewById(R.id.cv_back);
        cardUpload = view.findViewById(R.id.card_upload);

        ivBack = view.findViewById(R.id.iv_back);
        ivHelp = view.findViewById(R.id.iv_help);
        ivNotesPreview = view.findViewById(R.id.iv_notes_preview);

        fabRemoveNotes = view.findViewById(R.id.fab_remove_notes);

        tvUploadInstruction = view.findViewById(R.id.tv_upload_instruction);
        tvUploadHint = view.findViewById(R.id.tv_upload_hint);
        tvSharingStatus = view.findViewById(R.id.tv_sharing_status);

        calendar = Calendar.getInstance();
    }

    private void setupDatabase() {
        dbHelper = new LectureNotesHelper(getContext());
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        ivHelp.setOnClickListener(v -> showHelpDialog());

        etLectureDate.setOnClickListener(v -> showDatePicker());

        cardUpload.setOnClickListener(v -> openFilePicker());

        fabRemoveNotes.setOnClickListener(v -> removeSelectedFile());

        btnShareNotes.setOnClickListener(v -> shareNotes());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    etLectureDate.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {"image/*", "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "Please install a File Manager", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri fileUri = data.getData();
                selectedFilePath = fileUri.toString();
                showFilePreview();
            }
        }
    }

    private void showFilePreview() {
        tvUploadInstruction.setVisibility(View.GONE);
        tvUploadHint.setVisibility(View.GONE);
        ivNotesPreview.setVisibility(View.VISIBLE);
        fabRemoveNotes.setVisibility(View.VISIBLE);

        ivNotesPreview.setImageResource(R.drawable.ic_bill);
        updateSharingStatus("File Selected", "#4CAF50");
    }

    private void removeSelectedFile() {
        selectedFilePath = "";
        tvUploadInstruction.setVisibility(View.VISIBLE);
        tvUploadHint.setVisibility(View.VISIBLE);
        ivNotesPreview.setVisibility(View.GONE);
        fabRemoveNotes.setVisibility(View.GONE);

        updateSharingStatus("Ready to Share", "#FFA000");
    }

    private void shareNotes() {
        if (!validateInputs()) {
            return;
        }

        String lecturerName = etLecturerName.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String lectureTitle = etLectureTitle.getText().toString().trim();
        String lectureDate = etLectureDate.getText().toString().trim();
        String chapter = etChapter.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Calendar now = Calendar.getInstance();

        String currentDate = dateFormat.format(now.getTime());
        String currentTime = timeFormat.format(now.getTime());

        LecturerNotes notes = new LecturerNotes(
                lecturerName, subject, lectureTitle, lectureDate,
                chapter, selectedFilePath, description, currentDate, currentTime
        );

        long result = dbHelper.addLecturerNotes(notes);

        if (result != -1) {
            updateSharingStatus("Successfully Shared", "#4CAF50");
            Toast.makeText(getContext(), "Lecture notes shared successfully!", Toast.LENGTH_SHORT).show();
            clearForm();
        } else {
            Toast.makeText(getContext(), "Error sharing notes. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(etLecturerName.getText().toString().trim())) {
            etLecturerName.setError("Lecturer name is required");
            etLecturerName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etSubject.getText().toString().trim())) {
            etSubject.setError("Subject is required");
            etSubject.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etLectureTitle.getText().toString().trim())) {
            etLectureTitle.setError("Lecture title is required");
            etLectureTitle.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etLectureDate.getText().toString().trim())) {
            etLectureDate.setError("Lecture date is required");
            etLectureDate.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etChapter.getText().toString().trim())) {
            etChapter.setError("Chapter is required");
            etChapter.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(selectedFilePath)) {
            Toast.makeText(getContext(), "Please select a file to upload", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void clearForm() {
        etLecturerName.setText("");
        etSubject.setText("");
        etLectureTitle.setText("");
        etLectureDate.setText("");
        etChapter.setText("");
        etDescription.setText("");
        removeSelectedFile();
    }

    private void updateSharingStatus(String status, String colorCode) {
        tvSharingStatus.setText(status);
    }

    private void showHelpDialog() {
        Toast.makeText(getContext(), "Fill in all required fields and upload your lecture notes to share with students", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}