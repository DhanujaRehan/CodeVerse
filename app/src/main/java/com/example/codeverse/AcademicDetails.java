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
import java.util.List;
import java.util.Locale;

public class AcademicDetails extends Fragment {

    // UI Components
    private TextInputEditText etBatch, etEnrollmentDate;
    private AutoCompleteTextView dropdownFaculty, dropdownSemester;
    private MaterialButton btnNextStep, btnCancel;
    private MaterialCardView cvBack, cvHelp;
    private FrameLayout loadingOverlay;

    // TextInputLayouts for validation
    private TextInputLayout tilFaculty, tilBatch, tilSemester, tilEnrollmentDate;

    // Step indicators
    private LinearLayout cardBasicInfoIndicator, cardAcademicIndicator,
            cardAccountIndicator, cardContactIndicator;

    // Database helper
    private StudentDatabaseHelper dbHelper;

    // Student ID passed from previous step
    private long studentId = -1;

    // Constants
    private static final String TAG = "AcademicDetailsFragment";
    private static final String ARG_STUDENT_ID = "student_id";

    public static AcademicDetails newInstance(long studentId) {
        AcademicDetails fragment = new AcademicDetails();
        Bundle args = new Bundle();
        args.putLong(ARG_STUDENT_ID, studentId);
        fragment.setArguments(args);
        Log.d("AcademicDetails", "newInstance called with studentId: " + studentId);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_academic_details, container, false);

        // Initialize database helper first
        dbHelper = new StudentDatabaseHelper(getContext());

        // Get student ID from multiple sources
        studentId = getStudentIdFromSources();

        Log.d(TAG, "Final resolved studentId: " + studentId);

        // Validate studentId
        if (studentId == -1 || !isValidStudentId(studentId)) {
            Log.e(TAG, "Invalid or non-existent studentId: " + studentId);
            handleInvalidStudentId(view);
            return view;
        }

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

    /**
     * Get student ID from multiple sources with fallback options
     */
    private long getStudentIdFromSources() {
        long resolvedStudentId = -1;

        // Source 1: Fragment arguments
        if (getArguments() != null) {
            resolvedStudentId = getArguments().getLong(ARG_STUDENT_ID, -1);
            Log.d(TAG, "StudentId from arguments: " + resolvedStudentId);
            if (resolvedStudentId != -1 && isValidStudentId(resolvedStudentId)) {
                return resolvedStudentId;
            }
        }

        // Source 2: Activity intent extras (if fragment is hosted in an activity with intent data)
        if (getActivity() != null && getActivity().getIntent() != null) {
            resolvedStudentId = getActivity().getIntent().getLongExtra(ARG_STUDENT_ID, -1);
            Log.d(TAG, "StudentId from activity intent: " + resolvedStudentId);
            if (resolvedStudentId != -1 && isValidStudentId(resolvedStudentId)) {
                return resolvedStudentId;
            }
        }

        // Source 3: Try to get the most recently created student (as a fallback)
        try {
            Student mostRecentStudent = getMostRecentlyCreatedStudent();
            if (mostRecentStudent != null) {
                resolvedStudentId = mostRecentStudent.getId();
                Log.d(TAG, "StudentId from most recent student: " + resolvedStudentId);
                if (isValidStudentId(resolvedStudentId)) {
                    return resolvedStudentId;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting most recent student: " + e.getMessage());
        }

        // Source 4: Check if there's a student with incomplete academic details
        try {
            Student incompleteStudent = getStudentWithIncompleteAcademicDetails();
            if (incompleteStudent != null) {
                resolvedStudentId = incompleteStudent.getId();
                Log.d(TAG, "StudentId from incomplete academic details: " + resolvedStudentId);
                if (isValidStudentId(resolvedStudentId)) {
                    return resolvedStudentId;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting student with incomplete details: " + e.getMessage());
        }

        Log.w(TAG, "Could not resolve studentId from any source");
        return -1;
    }

    /**
     * Check if the student ID is valid by verifying it exists in the database
     */
    private boolean isValidStudentId(long studentId) {
        if (studentId <= 0) {
            return false;
        }

        try {
            Student student = dbHelper.getStudentById(studentId);
            boolean isValid = student != null;
            Log.d(TAG, "StudentId " + studentId + " validation result: " + isValid);
            return isValid;
        } catch (Exception e) {
            Log.e(TAG, "Error validating studentId " + studentId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the most recently created student (fallback method)
     */
    private Student getMostRecentlyCreatedStudent() {
        try {
            List<Student> allStudents = dbHelper.getAllStudent();
            if (allStudents != null && !allStudents.isEmpty()) {
                // Return the last student (assuming they're ordered by creation time or ID)
                return allStudents.get(allStudents.size() - 1);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting most recent student: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get a student with incomplete academic details (another fallback method)
     */
    private Student getStudentWithIncompleteAcademicDetails() {
        try {
            List<Student> allStudents = dbHelper.getAllStudent();
            if (allStudents != null) {
                for (Student student : allStudents) {
                    // Check if academic details are missing or incomplete
                    if (student.getFaculty() == null || student.getFaculty().isEmpty() ||
                            student.getBatch() == null || student.getBatch().isEmpty()) {
                        return student;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting student with incomplete details: " + e.getMessage());
        }
        return null;
    }

    /**
     * Handle invalid student ID scenario
     */
    private void handleInvalidStudentId(View view) {
        // Show error dialog with options
        new AlertDialog.Builder(getContext())
                .setTitle("Student ID Error")
                .setMessage("Unable to find student record. Would you like to:")
                .setPositiveButton("Start New Registration", (dialog, which) -> {
                    // Navigate to the first step of registration
                    navigateToFirstStep();
                })
                .setNegativeButton("Go Back", (dialog, which) -> {
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                })
                .setNeutralButton("Show All Students", (dialog, which) -> {
                    // Show a list of existing students to choose from
                    showStudentSelectionDialog();
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Navigate to the first step of registration
     */
    private void navigateToFirstStep() {
        if (getActivity() != null) {
            // Replace with your first registration fragment
            // BasicDetails basicDetailsFragment = new BasicDetails();
            // getActivity().getSupportFragmentManager()
            //         .beginTransaction()
            //         .replace(R.id.framelayout, basicDetailsFragment)
            //         .commit();

            // Or finish activity and start registration activity
            getActivity().finish();
        }
    }

    /**
     * Show dialog to select from existing students
     */
    private void showStudentSelectionDialog() {
        try {
            List<Student> allStudents = dbHelper.getAllStudent();
            if (allStudents == null || allStudents.isEmpty()) {
                showToast("No students found in database");
                navigateToFirstStep();
                return;
            }

            String[] studentNames = new String[allStudents.size()];
            for (int i = 0; i < allStudents.size(); i++) {
                Student student = allStudents.get(i);
                studentNames[i] = student.getFullName() + " (ID: " + student.getId() + ")";
            }

            new AlertDialog.Builder(getContext())
                    .setTitle("Select Student")
                    .setSingleChoiceItems(studentNames, -1, (dialog, which) -> {
                        Student selectedStudent = allStudents.get(which);
                        studentId = selectedStudent.getId();
                        Log.d(TAG, "Student selected from dialog: " + studentId);

                        // Update arguments
                        Bundle args = getArguments();
                        if (args == null) {
                            args = new Bundle();
                        }
                        args.putLong(ARG_STUDENT_ID, studentId);
                        setArguments(args);

                        dialog.dismiss();

                        // Reinitialize the fragment with correct student ID
                        if (getActivity() != null) {
                            AcademicDetails newFragment = AcademicDetails.newInstance(studentId);
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.framelayout, newFragment)
                                    .commit();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    })
                    .show();

        } catch (Exception e) {
            Log.e(TAG, "Error showing student selection dialog: " + e.getMessage());
            showToast("Error loading students");
            navigateToFirstStep();
        }
    }

    private void initializeViews(View view) {
        try {
            // Input fields
            etBatch = view.findViewById(R.id.et_batch);
            etEnrollmentDate = view.findViewById(R.id.et_enrollment_date);
            dropdownFaculty = view.findViewById(R.id.dropdown_faculty);
            dropdownSemester = view.findViewById(R.id.dropdown_semester);

            // TextInputLayouts for validation
            tilFaculty = view.findViewById(R.id.til_faculty);
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
                    getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    faculties);
            dropdownFaculty.setAdapter(facultyAdapter);

            // Semester dropdown
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

    private void setupClickListeners() {
        // Back button click listener
        cvBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Help button click listener
        cvHelp.setOnClickListener(v -> showHelpDialog());

        // Enrollment date field click listener
        etEnrollmentDate.setOnClickListener(v -> showDatePickerDialog());

        // Next step button click listener
        btnNextStep.setOnClickListener(v -> {
            if (validateInputs()) {
                saveAcademicDetails();
            }
        });

        // Cancel button click listener
        btnCancel.setOnClickListener(v -> {
            // Show confirmation dialog before canceling
            new AlertDialog.Builder(getContext())
                    .setTitle("Cancel Student Creation")
                    .setMessage("Are you sure you want to cancel? All entered information will be lost.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Delete the student record if it exists
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
        dropdownSemester.setOnItemClickListener((parent, view, position, id) -> clearErrors());
    }

    private void clearErrors() {
        // Clear all error messages
        tilFaculty.setError(null);
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

    private void saveAcademicDetails() {
        // Show loading
        loadingOverlay.setVisibility(View.VISIBLE);

        // Add comprehensive debugging
        Log.d(TAG, "saveAcademicDetails called with studentId: " + studentId);

        try {
            // Double check studentId
            if (studentId == -1 || studentId <= 0) {
                Log.e(TAG, "Invalid student ID: " + studentId);
                loadingOverlay.setVisibility(View.GONE);
                showToast("Error: Invalid student ID. Please restart the registration process.");
                handleInvalidStudentId(null);
                return;
            }

            // Verify student exists in database before updating
            Student existingStudent = dbHelper.getStudentById(studentId);
            if (existingStudent == null) {
                Log.e(TAG, "Student not found in database with ID: " + studentId);
                loadingOverlay.setVisibility(View.GONE);
                showToast("Error: Student record not found. Please restart the registration process.");
                handleInvalidStudentId(null);
                return;
            }

            Log.d(TAG, "Student found in database: " + existingStudent.getFullName());

            // Get input values
            String faculty = dropdownFaculty.getText().toString().trim();
            String batch = etBatch.getText().toString().trim();
            String semester = dropdownSemester.getText().toString().trim();
            String enrollmentDate = etEnrollmentDate.getText().toString().trim();

            Log.d(TAG, "Updating academic details - Faculty: " + faculty + ", Batch: " + batch +
                    ", Semester: " + semester + ", Enrollment Date: " + enrollmentDate);

            // Update academic details in database
            // FIXED: Remove the (int) cast - pass studentId as long
            int result = dbHelper.updateStudentAcademicDetails(studentId, faculty,
                    batch, semester, enrollmentDate);

            Log.d(TAG, "Database update result: " + result);

            if (result > 0) {
                showToast("Academic details saved successfully!");

                // Navigate to next step after a short delay
                loadingOverlay.postDelayed(() -> {
                    loadingOverlay.setVisibility(View.GONE);

                    // Navigate to Account Details Fragment
                    if (getActivity() != null) {
                        AccountDetails accountFragment = AccountDetails.newInstance(studentId);

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.framelayout, accountFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }, 1000);

            } else {
                loadingOverlay.setVisibility(View.GONE);
                showToast("Failed to save academic details. Please try again.");
                Log.e(TAG, "Database update returned 0 rows affected");
            }

        } catch (Exception e) {
            loadingOverlay.setVisibility(View.GONE);
            Log.e(TAG, "Error saving academic details: " + e.getMessage(), e);
            showToast("An error occurred while saving data: " + e.getMessage());
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
        new AlertDialog.Builder(getContext())
                .setTitle("Academic Details")
                .setMessage("This form collects information about the student's academic background.\n\n" +
                        "• Select the faculty\n" +
                        "• Enter the batch year (e.g., 2023)\n" +
                        "• Select the current semester\n" +
                        "• Set the enrollment date\n\n" +
                        "All fields marked with * are required.")
                .setPositiveButton("Got it", null)
                .show();
    }

    private void populateFieldsFromData() {
        if (studentId == -1) {
            Log.e(TAG, "Cannot populate fields - invalid studentId: " + studentId);
            return;
        }

        try {
            Log.d(TAG, "Attempting to populate fields for studentId: " + studentId);

            // Get existing student data from database
            Student student = dbHelper.getStudentById(studentId);

            if (student != null) {
                Log.d(TAG, "Student data found, populating fields");

                // Populate fields with existing academic data if available
                if (student.getFaculty() != null && !student.getFaculty().isEmpty()) {
                    dropdownFaculty.setText(student.getFaculty(), false);
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
            } else {
                Log.w(TAG, "No student data found for ID: " + studentId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error populating fields: " + e.getMessage(), e);
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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