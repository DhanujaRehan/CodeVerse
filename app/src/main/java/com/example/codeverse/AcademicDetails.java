package com.example.codeverse;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AcademicDetails extends Fragment {

    private TextInputEditText etBatch, etEnrollmentDate;
    private AutoCompleteTextView dropdownFaculty, dropdownDepartment, dropdownSemester;
    private MaterialButton btnNextStep, btnCancel;
    private MaterialCardView cvBack, cvHelp;
    private FrameLayout loadingOverlay;

    private TextInputLayout tilFaculty, tilDepartment, tilBatch, tilSemester, tilEnrollmentDate;

    private LinearLayout cardBasicInfoIndicator, cardAcademicIndicator,
            cardAccountIndicator, cardContactIndicator;

    private StudentDatabaseHelper dbHelper;

    private long studentId = -1;

    private static final String TAG = "AcademicDetailsFragment";
    private static final String ARG_STUDENT_ID = "student_id";

    public static AcademicDetails newInstance(long studentId) {
        AcademicDetails fragment = new AcademicDetails();
        Bundle args = new Bundle();
        args.putLong(ARG_STUDENT_ID, studentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_academic_details, container, false);

        if (getArguments() != null) {
            studentId = getArguments().getLong(ARG_STUDENT_ID, -1);
        }

        dbHelper = new StudentDatabaseHelper(getContext());

        initializeViews(view);

        setupDropdowns();

        setupClickListeners();

        setupTextChangeListeners();

        populateFieldsFromData();

        return view;
    }

    private void initializeViews(View view) {
        try {
            etBatch = view.findViewById(R.id.et_batch);
            etEnrollmentDate = view.findViewById(R.id.et_enrollment_date);
            dropdownFaculty = view.findViewById(R.id.dropdown_faculty);
            dropdownDepartment = view.findViewById(R.id.dropdown_department);
            dropdownSemester = view.findViewById(R.id.dropdown_semester);

            tilFaculty = view.findViewById(R.id.til_faculty);
            tilDepartment = view.findViewById(R.id.til_department);
            tilBatch = view.findViewById(R.id.til_batch);
            tilSemester = view.findViewById(R.id.til_semester);
            tilEnrollmentDate = view.findViewById(R.id.til_enrollment_date);

            btnNextStep = view.findViewById(R.id.btn_next_step);
            btnCancel = view.findViewById(R.id.btn_cancel);

            cvBack = view.findViewById(R.id.cv_back);
            cvHelp = view.findViewById(R.id.cv_help);

            cardBasicInfoIndicator = view.findViewById(R.id.card_basic_info_indicator);
            cardAcademicIndicator = view.findViewById(R.id.card_academic_indicator);
            cardAccountIndicator = view.findViewById(R.id.card_account_indicator);
            cardContactIndicator = view.findViewById(R.id.card_contact_indicator);

            loadingOverlay = view.findViewById(R.id.loading_overlay);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            Toast.makeText(getContext(), "Failed to initialize the form", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDropdowns() {
        try {
            String[] faculties = new String[]{"Science", "Engineering", "Medicine", "Business", "Arts", "Law"};
            ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    faculties);
            dropdownFaculty.setAdapter(facultyAdapter);

            dropdownFaculty.setOnItemClickListener((parent, view, position, id) -> {
                updateDepartmentDropdown(faculties[position]);
            });

            String[] semesters = new String[]{"Semester 1", "Semester 2", "Semester 3", "Semester 4",
                    "Semester 5", "Semester 6", "Semester 7", "Semester 8"};
            ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    semesters);
            dropdownSemester.setAdapter(semesterAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up dropdowns: " + e.getMessage());
        }
    }

    private void updateDepartmentDropdown(String selectedFaculty) {
        String[] departments;

        switch (selectedFaculty) {
            case "Engineering":
                departments = new String[]{"Computer Science", "Electrical", "Mechanical", "Civil", "Chemical"};
                break;
            case "Science":
                departments = new String[]{"Physics", "Chemistry", "Biology", "Mathematics", "Statistics"};
                break;
            case "Medicine":
                departments = new String[]{"General Medicine", "Surgery", "Pediatrics", "Psychiatry", "Dermatology"};
                break;
            case "Business":
                departments = new String[]{"Accounting", "Marketing", "Finance", "Management", "Economics"};
                break;
            case "Arts":
                departments = new String[]{"Literature", "History", "Philosophy", "Languages", "Fine Arts"};
                break;
            case "Law":
                departments = new String[]{"Criminal Law", "Civil Law", "Constitutional Law", "Corporate Law", "International Law"};
                break;
            default:
                departments = new String[]{"Select a faculty first"};
                break;
        }

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                departments);
        dropdownDepartment.setAdapter(departmentAdapter);

        dropdownDepartment.setText("", false);
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        cvHelp.setOnClickListener(v -> showHelpDialog());

        etEnrollmentDate.setOnClickListener(v -> showDatePickerDialog());

        btnNextStep.setOnClickListener(v -> {
            if (validateInputs()) {
                saveAcademicDetails();
            }
        });

        btnCancel.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Cancel Student Creation")
                    .setMessage("Are you sure you want to cancel? All entered information will be lost.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (studentId != -1) {
                            dbHelper.deleteStudent(studentId);
                        }
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void setupTextChangeListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearErrors();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        etBatch.addTextChangedListener(textWatcher);
        etEnrollmentDate.addTextChangedListener(textWatcher);

        dropdownFaculty.setOnItemClickListener((parent, view, position, id) -> clearErrors());
        dropdownDepartment.setOnItemClickListener((parent, view, position, id) -> clearErrors());
        dropdownSemester.setOnItemClickListener((parent, view, position, id) -> clearErrors());
    }

    private void clearErrors() {
        // Clear all error messages
        tilFaculty.setError(null);
        tilDepartment.setError(null);
        tilBatch.setError(null);
        tilSemester.setError(null);
        tilEnrollmentDate.setError(null);
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (TextUtils.isEmpty(dropdownFaculty.getText())) {
            tilFaculty.setError("Faculty is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(dropdownDepartment.getText())) {
            tilDepartment.setError("Department is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(etBatch.getText())) {
            tilBatch.setError("Batch year is required");
            isValid = false;
        } else {
            String batchStr = etBatch.getText().toString().trim();
            try {
                int batchYear = Integer.parseInt(batchStr);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (batchYear < 1950 || batchYear > currentYear) {
                    tilBatch.setError("Please enter a valid batch year");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilBatch.setError("Please enter a valid year");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(dropdownSemester.getText())) {
            tilSemester.setError("Current semester is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(etEnrollmentDate.getText())) {
            tilEnrollmentDate.setError("Enrollment date is required");
            isValid = false;
        }

        return isValid;
    }

    private void saveAcademicDetails() {
        loadingOverlay.setVisibility(View.VISIBLE);

        try {
            if (studentId == -1) {
                loadingOverlay.setVisibility(View.GONE);
                showToast("Error: Student ID not found");
                return;
            }

            String faculty = dropdownFaculty.getText().toString().trim();
            String department = dropdownDepartment.getText().toString().trim();
            String batch = etBatch.getText().toString().trim();
            String semester = dropdownSemester.getText().toString().trim();
            String enrollmentDate = etEnrollmentDate.getText().toString().trim();

            int result = dbHelper.updateStudentAcademicDetails(studentId, faculty, department,
                    batch, semester, enrollmentDate);

            if (result > 0) {
                showToast("Academic details saved successfully!");

                loadingOverlay.postDelayed(() -> {
                    loadingOverlay.setVisibility(View.GONE);

                    showToast("Proceeding to Account Details...");


                }, 1000);

            } else {
                loadingOverlay.setVisibility(View.GONE);
                showToast("Failed to save academic details. Please try again.");
            }

        } catch (Exception e) {
            loadingOverlay.setVisibility(View.GONE);
            Log.e(TAG, "Error saving academic details: " + e.getMessage());
            showToast("An error occurred while saving data");
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                    String formattedDate = dateFormat.format(selectedDate.getTime());

                    etEnrollmentDate.setText(formattedDate);
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Academic Details")
                .setMessage("This form collects information about the student's academic background.\n\n" +
                        "• Select the faculty and department\n" +
                        "• Enter the batch year (e.g., 2023)\n" +
                        "• Select the current semester\n" +
                        "• Set the enrollment date\n\n" +
                        "All fields marked with * are required.")
                .setPositiveButton("Got it", null)
                .show();
    }

    private void populateFieldsFromData() {
        if (studentId == -1) return;

        try {
             Students student = dbHelper.getStudentById(studentId);

            if (student != null) {
                 if (student.getFaculty() != null && !student.getFaculty().isEmpty()) {
                    dropdownFaculty.setText(student.getFaculty(), false);
                     updateDepartmentDropdown(student.getFaculty());

                    if (student.getDepartment() != null && !student.getDepartment().isEmpty()) {
                        dropdownDepartment.setText(student.getDepartment(), false);
                    }
                }

                if (student.getBatch() != null && !student.getBatch().isEmpty()) {
                    etBatch.setText(student.getBatch());
                }

                if (student.getSemester() != null && !student.getSemester().isEmpty()) {
                    dropdownSemester.setText(student.getSemester(), false);
                }

                if (student.getEnrollmentDate() != null && !student.getEnrollmentDate().isEmpty()) {
                    etEnrollmentDate.setText(student.getEnrollmentDate());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error populating fields: " + e.getMessage());
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}