package com.example.codeverse.Staff.StaffFragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.codeverse.Assignment;
import com.example.codeverse.Staff.Helper.AssignmentHelper;
import com.example.codeverse.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StaffAddAssignment extends Fragment {

    private AutoCompleteTextView dropdownModule, dropdownTargetGroups;
    private TextInputEditText etAssignmentTitle, etDescription, etReleaseDate, etDueDate, etWeighting;
    private MaterialButton btnBrowse, btnSaveAsDraft, btnPublish;
    private MaterialCardView cvBack;
    private FrameLayout loadingOverlay;

    private AssignmentHelper assignmentHelper;
    private String selectedFilePath = "";
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    private static final int FILE_PICKER_REQUEST = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_add_assignment, container, false);

        initViews(view);
        setupDatabase();
        setupDropdowns();
        setupDatePickers();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        dropdownModule = view.findViewById(R.id.dropdownModule);
        dropdownTargetGroups = view.findViewById(R.id.dropdownTargetGroups);
        etAssignmentTitle = view.findViewById(R.id.etAssignmentTitle);
        etDescription = view.findViewById(R.id.etDescription);
        etReleaseDate = view.findViewById(R.id.etReleaseDate);
        etDueDate = view.findViewById(R.id.etDueDate);
        etWeighting = view.findViewById(R.id.etWeighting);
        btnBrowse = view.findViewById(R.id.btnBrowse);
        btnSaveAsDraft = view.findViewById(R.id.btnSaveAsDraft);
        btnPublish = view.findViewById(R.id.btnPublish);
        cvBack = view.findViewById(R.id.cv_back);
        loadingOverlay = view.findViewById(R.id.loading_overlay);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    private void setupDatabase() {
        assignmentHelper = new AssignmentHelper(getContext());
    }

    private void setupDropdowns() {
        String[] modules = {"Mathematics", "Physics", "Chemistry", "Biology", "Computer Science", "English"};
        ArrayAdapter<String> moduleAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, modules);
        dropdownModule.setAdapter(moduleAdapter);

        String[] batches = {"Batch A", "Batch B", "Batch C", "Batch D", "All Batches"};
        ArrayAdapter<String> batchAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, batches);
        dropdownTargetGroups.setAdapter(batchAdapter);
    }

    private void setupDatePickers() {
        etReleaseDate.setOnClickListener(v -> showDatePicker(true));
        etDueDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isReleaseDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String selectedDate = dateFormat.format(calendar.getTime());
                    if (isReleaseDate) {
                        etReleaseDate.setText(selectedDate);
                    } else {
                        etDueDate.setText(selectedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnBrowse.setOnClickListener(v -> openFilePicker());

        btnSaveAsDraft.setOnClickListener(v -> saveAssignment("Draft"));

        btnPublish.setOnClickListener(v -> saveAssignment("Published"));
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {"application/pdf", "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "Select File"), FILE_PICKER_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                selectedFilePath = fileUri.toString();
                Toast.makeText(getContext(), "File selected successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveAssignment(String status) {
        if (!validateInput()) {
            return;
        }

        showLoading(true);

        String module = dropdownModule.getText().toString().trim();
        String title = etAssignmentTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String releaseDate = etReleaseDate.getText().toString().trim();
        String dueDate = etDueDate.getText().toString().trim();
        String weightingStr = etWeighting.getText().toString().trim();
        String targetBatch = dropdownTargetGroups.getText().toString().trim();

        int weighting = 0;
        if (!weightingStr.isEmpty()) {
            weighting = Integer.parseInt(weightingStr);
        }

        Assignment assignment = new Assignment(
                module, title, description, releaseDate, dueDate,
                weighting, selectedFilePath, targetBatch, status
        );

        long result = assignmentHelper.addAssignment(assignment);

        showLoading(false);

        if (result != -1) {
            Toast.makeText(getContext(), "Assignment " + status.toLowerCase() + " successfully!", Toast.LENGTH_LONG).show();
            clearForm();
            if (status.equals("Published") && getActivity() != null) {
                getActivity().onBackPressed();
            }
        } else {
            Toast.makeText(getContext(), "Failed to save assignment", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput() {
        String module = dropdownModule.getText().toString().trim();
        String title = etAssignmentTitle.getText().toString().trim();
        String releaseDate = etReleaseDate.getText().toString().trim();
        String dueDate = etDueDate.getText().toString().trim();
        String targetBatch = dropdownTargetGroups.getText().toString().trim();

        if (module.isEmpty() || module.equals("Select Module")) {
            Toast.makeText(getContext(), "Please select a module", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Please enter assignment title", Toast.LENGTH_SHORT).show();
            etAssignmentTitle.requestFocus();
            return false;
        }

        if (releaseDate.isEmpty()) {
            Toast.makeText(getContext(), "Please select release date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dueDate.isEmpty()) {
            Toast.makeText(getContext(), "Please select due date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (targetBatch.isEmpty() || targetBatch.equals("Select Batch")) {
            Toast.makeText(getContext(), "Please select target batch", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void clearForm() {
        dropdownModule.setText("Select Module");
        dropdownTargetGroups.setText("Select Batch");
        etAssignmentTitle.setText("");
        etDescription.setText("");
        etReleaseDate.setText("");
        etDueDate.setText("");
        etWeighting.setText("");
        selectedFilePath = "";
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}