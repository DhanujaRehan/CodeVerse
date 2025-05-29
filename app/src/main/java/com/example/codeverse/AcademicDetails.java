package com.example.codeverse;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
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

    // Interface for communication with parent activity
    public interface OnAcademicInfoListener {
        void onAcademicInfoCompleted(StudentDetails academicInfo);
        void onAcademicInfoCancelled();
        void onNavigateToStep(int step);
    }

    // UI Components
    private TextInputEditText etBatch, etEnrollmentDate;
    private AutoCompleteTextView dropdownFaculty, dropdownDepartment, dropdownSemester;
    private MaterialButton btnNextStep, btnCancel;
    private MaterialCardView cvBack, cvHelp;
    private FrameLayout loadingOverlay;

    // TextInputLayouts for validation
    private TextInputLayout tilFaculty, tilDepartment, tilBatch, tilSemester, tilEnrollmentDate;

    // Step indicators
    private LinearLayout cardBasicInfoIndicator, cardAcademicIndicator,
            cardAccountIndicator, cardContactIndicator;

    // Data
    private StudentDetails studentDetails;
    private OnAcademicInfoListener listener;
    private StudentDatabaseHelper databaseHelper;

    // Constants
    private static final String TAG = "AcademicDetailsFragment";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAcademicInfoListener) {
            listener = (OnAcademicInfoListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnAcademicInfoListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize database helper
        databaseHelper = StudentDatabaseHelper.getInstance(requireContext());

        // Get student details from arguments
        if (getArguments() != null) {
            studentDetails = getArguments().getParcelable("student_details");
        }

        if (studentDetails == null) {
            studentDetails = new StudentDetails();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_academic_details, container, false);

        // Initialize UI components
        initializeViews(view);

        // Set up dropdowns
        setupDropdowns();

        // Set up click listeners
        setupClickListeners();

        // Set up text change listeners for validation
        setupTextChangeListeners();

        // Populate fields with existing data (if any)
        populateFieldsFromData();

        return view;
    }

    private void initializeViews(View view) {
        try {
            // Input fields
            etBatch = view.findViewById(R.id.et_batch);
            etEnrollmentDate = view.findViewById(R.id.et_enrollment_date);
            dropdownFaculty = view.findViewById(R.id.dropdown_faculty);
            dropdownDepartment = view.findViewById(R.id.dropdown_department);
            dropdownSemester = view.findViewById(R.id.dropdown_semester);

            // TextInputLayouts for validation
            tilFaculty = view.findViewById(R.id.til_faculty);
            tilDepartment = view.findViewById(R.id.til_department);
            tilBatch = view.findViewById(R.id.til_batch);
            tilSemester = view.findViewById(R.id.til_semester);
            tilEnrollmentDate = view.findViewById(R.id.til_enrollment_date);

            // Buttons
            btnNextStep = view.findViewById(R.id.btn_next_step);
            btnCancel = view.findViewById(R.id.btn_cancel);

            // Navigation components
            cvBack = view.findViewById(R.id.cv_back);
            cvHelp = view.findViewById(R.id.cv_help);

            // Step indicators
            cardBasicInfoIndicator = view.findViewById(R.id.card_basic_info_indicator);
            cardAcademicIndicator = view.findViewById(R.id.card_academic_indicator);
            cardAccountIndicator = view.findViewById(R.id.card_account_indicator);
            cardContactIndicator = view.findViewById(R.id.card_contact_indicator);

            // Loading overlay
            loadingOverlay = view.findViewById(R.id.loading_overlay);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            Toast.makeText(getContext(), "Failed to initialize the form", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDropdowns() {
        try {
            // Faculty dropdown
            String[] faculties = new String[]{"Science", "Engineering", "Medicine", "Business", "Arts", "Law"};
            ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    faculties);
            dropdownFaculty.setAdapter(facultyAdapter);

            // Department dropdown (will be updated based on faculty selection)
            dropdownFaculty.setOnItemClickListener((parent, view, position, id) -> {
                updateDepartmentDropdown(faculties[position]);
            });

            // Semester dropdown
            String[] semesters = new String[]{"Semester 1", "Semester 2", "Semester 3", "Semester 4",
                    "Semester 5", "Semester 6", "Semester 7", "Semester 8"};
            ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    semesters);
            dropdownSemester.setAdapter(semesterAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up dropdowns: " + e.getMessage());
        }
    }

    private void updateDepartmentDropdown(String selectedFaculty) {
        String[] departments;

        // Set departments based on selected faculty
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

        // Update the department dropdown
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                departments);
        dropdownDepartment.setAdapter(departmentAdapter);

        // Clear current selection
        dropdownDepartment.setText("", false);
    }

    private void setupClickListeners() {
        // Back button click listener
        cvBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Help button click listener
        cvHelp.setOnClickListener(v -> showHelpDialog());

        // Enrollment date field click listener
        etEnrollmentDate.setOnClickListener(v -> showDatePickerDialog());

        // Step indicators click listeners for navigation
        cardBasicInfoIndicator.setOnClickListener(v -> listener.onNavigateToStep(1));
        cardAccountIndicator.setOnClickListener(v -> {
            if (validateInputs()) {
                saveCurrentStepData();
                listener.onNavigateToStep(3);
            }
        });
        cardContactIndicator.setOnClickListener(v -> {
            if (validateInputs()) {
                saveCurrentStepData();
                listener.onNavigateToStep(4);
            }
        });

        // Next step button click listener
        btnNextStep.setOnClickListener(v -> {
            if (validateInputs()) {
                saveCurrentStepData();
                listener.onAcademicInfoCompleted(studentDetails);
            }
        });

        // Cancel button click listener
        btnCancel.setOnClickListener(v -> {
            // Show confirmation dialog before canceling
            new AlertDialog.Builder(requireContext())
                    .setTitle("Cancel Student Creation")
                    .setMessage("Are you sure you want to cancel? All entered information will be lost.")
                    .setPositiveButton("Yes", (dialog, which) -> listener.onAcademicInfoCancelled())
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void setupTextChangeListeners() {
        // Create a generic TextWatcher for clearing errors when text changes
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear errors when text changes
                clearErrors();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        };

        // Apply the TextWatcher to all input fields
        etBatch.addTextChangedListener(textWatcher);
        etEnrollmentDate.addTextChangedListener(textWatcher);

        // For dropdowns, use the item click listener
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

        // Validate faculty
        if (TextUtils.isEmpty(dropdownFaculty.getText())) {
            tilFaculty.setError("Faculty is required");
            isValid = false;
        }

        // Validate department
        if (TextUtils.isEmpty(dropdownDepartment.getText())) {
            tilDepartment.setError("Department is required");
            isValid = false;
        }

        // Validate batch
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

        // Validate semester
        if (TextUtils.isEmpty(dropdownSemester.getText())) {
            tilSemester.setError("Current semester is required");
            isValid = false;
        }

        // Validate enrollment date
        if (TextUtils.isEmpty(etEnrollmentDate.getText())) {
            tilEnrollmentDate.setError("Enrollment date is required");
            isValid = false;
        }

        return isValid;
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    // Format the date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                    String formattedDate = dateFormat.format(selectedDate.getTime());

                    // Set the formatted date to the input field
                    etEnrollmentDate.setText(formattedDate);
                },
                year, month, day
        );

        // Limit the date picker to be no later than today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(requireContext())
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
        if (studentDetails != null) {
            if (studentDetails.getFaculty() != null) {
                dropdownFaculty.setText(studentDetails.getFaculty(), false);
                // Also update departments dropdown
                updateDepartmentDropdown(studentDetails.getFaculty());

                if (studentDetails.getDepartment() != null) {
                    dropdownDepartment.setText(studentDetails.getDepartment(), false);
                }
            }

            if (studentDetails.getBatch() != null) {
                etBatch.setText(studentDetails.getBatch());
            }

            if (studentDetails.getSemester() != null) {
                dropdownSemester.setText(studentDetails.getSemester(), false);
            }

            if (studentDetails.getEnrollmentDate() != null) {
                etEnrollmentDate.setText(studentDetails.getEnrollmentDate());
            }
        }
    }

    private void saveCurrentStepData() {
        // Save academic details
        studentDetails.setFaculty(dropdownFaculty.getText().toString());
        studentDetails.setDepartment(dropdownDepartment.getText().toString());
        studentDetails.setBatch(etBatch.getText().toString());
        studentDetails.setSemester(dropdownSemester.getText().toString());
        studentDetails.setEnrollmentDate(etEnrollmentDate.getText().toString());

        // Save to database
        databaseHelper.insertOrUpdateStudent(studentDetails);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}