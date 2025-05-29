package com.example.codeverse;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AcademicDetails extends AppCompatActivity {

    // UI Components
    private MaterialCardView cvBack, cvHelp;
    private TextInputLayout tilFaculty, tilDepartment, tilBatch, tilSemester, tilEnrollmentDate;
    private AutoCompleteTextView dropdownFaculty, dropdownDepartment, dropdownSemester;
    private TextInputEditText etBatch, etEnrollmentDate;
    private MaterialButton btnNextStep, btnCancel;
    private View loadingOverlay;

    // Data
    private Student studentData;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private StudentDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_academic_details);

        initializeViews();
        initializeDatabase();
        getStudentData();
        setupDropdowns();
        setupDatePicker();
        setupClickListeners();
        updateProgressIndicators();
    }

    private void initializeViews() {
        cvBack = findViewById(R.id.cv_back);
        cvHelp = findViewById(R.id.cv_help);
        tilFaculty = findViewById(R.id.til_faculty);
        tilDepartment = findViewById(R.id.til_department);
        tilBatch = findViewById(R.id.til_batch);
        tilSemester = findViewById(R.id.til_semester);
        tilEnrollmentDate = findViewById(R.id.til_enrollment_date);
        dropdownFaculty = findViewById(R.id.dropdown_faculty);
        dropdownDepartment = findViewById(R.id.dropdown_department);
        dropdownSemester = findViewById(R.id.dropdown_semester);
        etBatch = findViewById(R.id.et_batch);
        etEnrollmentDate = findViewById(R.id.et_enrollment_date);
        btnNextStep = findViewById(R.id.btn_next_step);
        btnCancel = findViewById(R.id.btn_cancel);
        loadingOverlay = findViewById(R.id.loading_overlay);

        calendar = Calendar.getInstance();
    }

    private void initializeDatabase() {
        databaseHelper = new StudentDatabaseHelper(this);
    }

    private void getStudentData() {
        studentData = (Student) getIntent().getSerializableExtra("student_data");
        if (studentData == null) {
            Toast.makeText(this, "Error: No student data found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupDropdowns() {
        // Setup Faculty dropdown
        String[] faculties = {
                "Faculty of Engineering",
                "Faculty of Science",
                "Faculty of Medicine",
                "Faculty of Arts",
                "Faculty of Law",
                "Faculty of Management",
                "Faculty of Agriculture",
                "Faculty of Education"
        };
        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, faculties);
        dropdownFaculty.setAdapter(facultyAdapter);

        // Setup Department dropdown (will be populated based on faculty selection)
        dropdownFaculty.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFaculty = faculties[position];
            setupDepartmentDropdown(selectedFaculty);
        });

        // Setup Semester dropdown
        String[] semesters = {
                "Semester 1",
                "Semester 2",
                "Semester 3",
                "Semester 4",
                "Semester 5",
                "Semester 6",
                "Semester 7",
                "Semester 8"
        };
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, semesters);
        dropdownSemester.setAdapter(semesterAdapter);
    }

    private void setupDepartmentDropdown(String faculty) {
        String[] departments;

        switch (faculty) {
            case "Faculty of Engineering":
                departments = new String[]{
                        "Computer Science & Engineering",
                        "Electrical & Electronic Engineering",
                        "Mechanical Engineering",
                        "Civil Engineering",
                        "Chemical Engineering",
                        "Software Engineering"
                };
                break;
            case "Faculty of Science":
                departments = new String[]{
                        "Computer Science",
                        "Mathematics",
                        "Physics",
                        "Chemistry",
                        "Biology",
                        "Statistics"
                };
                break;
            case "Faculty of Medicine":
                departments = new String[]{
                        "Medicine",
                        "Nursing",
                        "Pharmacy",
                        "Dental Surgery",
                        "Veterinary Medicine"
                };
                break;
            case "Faculty of Arts":
                departments = new String[]{
                        "English",
                        "History",
                        "Psychology",
                        "Sociology",
                        "Philosophy",
                        "Fine Arts"
                };
                break;
            case "Faculty of Law":
                departments = new String[]{
                        "Law",
                        "Legal Studies",
                        "Criminology"
                };
                break;
            case "Faculty of Management":
                departments = new String[]{
                        "Business Administration",
                        "Accounting & Finance",
                        "Marketing",
                        "Human Resource Management",
                        "Information Systems"
                };
                break;
            case "Faculty of Agriculture":
                departments = new String[]{
                        "Agricultural Sciences",
                        "Animal Science",
                        "Food Science & Technology",
                        "Agricultural Engineering"
                };
                break;
            case "Faculty of Education":
                departments = new String[]{
                        "Educational Sciences",
                        "Physical Education",
                        "Special Education"
                };
                break;
            default:
                departments = new String[]{"Please select a faculty first"};
                break;
        }

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, departments);
        dropdownDepartment.setAdapter(departmentAdapter);
        dropdownDepartment.setText("", false); // Clear previous selection
    }

    private void setupDatePicker() {
        etEnrollmentDate.setOnClickListener(v -> showDatePicker());
        tilEnrollmentDate.setEndIconOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    updateDateDisplay();
                }, year, month, day);

        // Set maximum date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Set minimum date (e.g., 10 years ago for enrollment dates)
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -10);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        etEnrollmentDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(v -> onBackPressed());

        cvHelp.setOnClickListener(v -> showHelpDialog());

        btnNextStep.setOnClickListener(v -> {
            if (validateInputs()) {
                proceedToAccountDetails();
            }
        });

        btnCancel.setOnClickListener(v -> {
            finish();
        });
    }

    private void showHelpDialog() {
        Toast.makeText(this, "Help: Select your faculty first, then choose your department and other academic details", Toast.LENGTH_LONG).show();
    }

    private void updateProgressIndicators() {
        // Update progress indicators to show current step
        // Step 1 (Basic Info) - Completed
        // Step 2 (Academic) - Current
        // Steps 3 & 4 - Pending
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Reset errors
        tilFaculty.setError(null);
        tilDepartment.setError(null);
        tilBatch.setError(null);
        tilSemester.setError(null);
        tilEnrollmentDate.setError(null);

        // Validate faculty
        String faculty = dropdownFaculty.getText().toString().trim();
        if (TextUtils.isEmpty(faculty)) {
            tilFaculty.setError("Please select a faculty");
            isValid = false;
        }

        // Validate department
        String department = dropdownDepartment.getText().toString().trim();
        if (TextUtils.isEmpty(department)) {
            tilDepartment.setError("Please select a department");
            isValid = false;
        }

        // Validate batch
        String batch = etBatch.getText().toString().trim();
        if (TextUtils.isEmpty(batch)) {
            tilBatch.setError("Batch is required");
            isValid = false;
        } else {
            try {
                int batchYear = Integer.parseInt(batch);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (batchYear < (currentYear - 10) || batchYear > currentYear) {
                    tilBatch.setError("Please enter a valid batch year");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilBatch.setError("Please enter a valid year");
                isValid = false;
            }
        }

        // Validate semester
        String semester = dropdownSemester.getText().toString().trim();
        if (TextUtils.isEmpty(semester)) {
            tilSemester.setError("Please select current semester");
            isValid = false;
        }

        // Validate enrollment date
        String enrollmentDate = etEnrollmentDate.getText().toString().trim();
        if (TextUtils.isEmpty(enrollmentDate)) {
            tilEnrollmentDate.setError("Enrollment date is required");
            isValid = false;
        }

        return isValid;
    }

    private void proceedToAccountDetails() {
        showLoading(true);

        // Update student object with academic details
        studentData.setFaculty(dropdownFaculty.getText().toString().trim());
        studentData.setDepartment(dropdownDepartment.getText().toString().trim());
        studentData.setBatch(etBatch.getText().toString().trim());
        studentData.setSemester(dropdownSemester.getText().toString().trim());
        studentData.setEnrollmentDate(etEnrollmentDate.getText().toString().trim());

        // Pass data to Account Details activity
        Intent intent = new Intent(this, AccountDetails.class);
        intent.putExtra("student_data", studentData);
        startActivity(intent);

        showLoading(false);
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        btnNextStep.setEnabled(!show);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}