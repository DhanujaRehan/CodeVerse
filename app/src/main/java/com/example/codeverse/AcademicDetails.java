package com.example.codeverse;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.codeverse.R;
import com.example.codeverse.AcademicDetails;
import com.example.codeverse.StudentDatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AcademicDetails extends Fragment {

    private static final String TAG = "AcademicDetailsFragment";

    // Views
    private AutoCompleteTextView dropdownFaculty, dropdownDepartment, dropdownSemester;
    private TextInputEditText etBatch, etEnrollmentDate;
    private TextInputLayout tilFaculty, tilDepartment, tilBatch, tilSemester, tilEnrollmentDate;
    private MaterialButton btnNextStep, btnCancel;
    private MaterialCardView cvBack;

    // Data
    private Student currentStudent;
    private Map<String, String[]> facultyDepartmentMap;

    // Database helper
    private StudentDatabaseHelper databaseHelper;

    public interface OnStepCompleteListener {
        void onStepCompleted(Student student, int nextStep);
        void onCancel();
        void onBack();
    }

    private OnStepCompleteListener stepCompleteListener;

    public static AcademicDetails newInstance() {
        return new AcademicDetails();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (currentStudent == null) {
            currentStudent = new Student();
        }
        setupFacultyDepartmentMapping();

        // Initialize database helper
        databaseHelper = StudentDatabaseHelper.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_academic_details, container, false);
        initViews(view);
        setupListeners();
        setupDropdowns();
        return view;
    }

    private void initViews(View view) {
        // Dropdowns
        dropdownFaculty = view.findViewById(R.id.dropdown_faculty);
        dropdownDepartment = view.findViewById(R.id.dropdown_department);
        dropdownSemester = view.findViewById(R.id.dropdown_semester);

        // Text input fields
        etBatch = view.findViewById(R.id.et_batch);
        etEnrollmentDate = view.findViewById(R.id.et_enrollment_date);

        // Text input layouts
        tilFaculty = view.findViewById(R.id.til_faculty);
        tilDepartment = view.findViewById(R.id.til_department);
        tilBatch = view.findViewById(R.id.til_batch);
        tilSemester = view.findViewById(R.id.til_semester);
        tilEnrollmentDate = view.findViewById(R.id.til_enrollment_date);

        // Buttons
        btnNextStep = view.findViewById(R.id.btn_next_step);
        btnCancel = view.findViewById(R.id.btn_cancel);
        cvBack = view.findViewById(R.id.cv_back);
    }

    private void setupListeners() {
        etEnrollmentDate.setOnClickListener(v -> showEnrollmentDatePicker());

        btnNextStep.setOnClickListener(v -> {
            if (validateInput()) {
                saveAcademicInfoToDatabase();
            }
        });

        btnCancel.setOnClickListener(v -> {
            if (stepCompleteListener != null) {
                stepCompleteListener.onCancel();
            }
        });

        cvBack.setOnClickListener(v -> {
            if (stepCompleteListener != null) {
                stepCompleteListener.onBack();
            }
        });

        // Faculty selection listener to update departments
        dropdownFaculty.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFaculty = (String) parent.getItemAtPosition(position);
            updateDepartmentDropdown(selectedFaculty);
            dropdownDepartment.setText("", false); // Clear department selection
        });
    }

    private void setupFacultyDepartmentMapping() {
        facultyDepartmentMap = new HashMap<>();

        facultyDepartmentMap.put("Faculty of Engineering", new String[]{
                "Computer Science & Engineering",
                "Electrical & Electronic Engineering",
                "Mechanical Engineering",
                "Civil Engineering",
                "Chemical Engineering"
        });

        facultyDepartmentMap.put("Faculty of Science", new String[]{
                "Mathematics",
                "Physics",
                "Chemistry",
                "Biology",
                "Statistics"
        });

        facultyDepartmentMap.put("Faculty of Arts", new String[]{
                "English",
                "History",
                "Philosophy",
                "Languages",
                "Fine Arts"
        });

        facultyDepartmentMap.put("Faculty of Management", new String[]{
                "Business Administration",
                "Accounting & Finance",
                "Marketing",
                "Human Resource Management",
                "Operations Management"
        });

        facultyDepartmentMap.put("Faculty of Medicine", new String[]{
                "Medicine",
                "Nursing",
                "Pharmacy",
                "Physiotherapy",
                "Medical Laboratory Sciences"
        });
    }

    private void setupDropdowns() {
        // Setup Faculty dropdown
        String[] faculties = facultyDepartmentMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                faculties
        );
        dropdownFaculty.setAdapter(facultyAdapter);

        // Setup Semester dropdown
        String[] semesters = {
                "Semester 1", "Semester 2", "Semester 3", "Semester 4",
                "Semester 5", "Semester 6", "Semester 7", "Semester 8"
        };
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                semesters
        );
        dropdownSemester.setAdapter(semesterAdapter);
    }

    private void updateDepartmentDropdown(String faculty) {
        String[] departments = facultyDepartmentMap.get(faculty);
        if (departments != null) {
            ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    departments
            );
            dropdownDepartment.setAdapter(departmentAdapter);
        }
    }

    private void showEnrollmentDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        etEnrollmentDate.setText(selectedDate);
                    }
                },
                year, month, day
        );

        // Set max date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private boolean validateInput() {
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
            tilFaculty.setError("Faculty is required");
            isValid = false;
        }

        // Validate department
        String department = dropdownDepartment.getText().toString().trim();
        if (TextUtils.isEmpty(department)) {
            tilDepartment.setError("Department is required");
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
                if (batchYear < 2000 || batchYear > currentYear + 1) {
                    tilBatch.setError("Invalid batch year");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilBatch.setError("Batch must be a valid year");
                isValid = false;
            }
        }

        // Validate semester
        String semester = dropdownSemester.getText().toString().trim();
        if (TextUtils.isEmpty(semester)) {
            tilSemester.setError("Current semester is required");
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

    private void saveAcademicInfoToDatabase() {
        // Update the student object with academic details
        currentStudent.setFaculty(dropdownFaculty.getText().toString().trim());
        currentStudent.setDepartment(dropdownDepartment.getText().toString().trim());
        currentStudent.setBatch(etBatch.getText().toString().trim());
        currentStudent.setCurrentSemester(dropdownSemester.getText().toString().trim());
        currentStudent.setEnrollmentDate(etEnrollmentDate.getText().toString().trim());

        try {
            if (currentStudent.getId() > 0) {
                // Update existing student
                int rowsUpdated = databaseHelper.updateStudent(currentStudent);
                if (rowsUpdated > 0) {
                    Toast.makeText(getContext(), "Academic information updated successfully", Toast.LENGTH_SHORT).show();
                    // Proceed to next step
                    if (stepCompleteListener != null) {
                        stepCompleteListener.onStepCompleted(currentStudent, 3);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to update academic information", Toast.LENGTH_SHORT).show();
                }
            } else {
                // This is a new student registration flow
                // Academic details will be saved when the complete registration is submitted
                Toast.makeText(getContext(), "Academic information saved", Toast.LENGTH_SHORT).show();
                if (stepCompleteListener != null) {
                    stepCompleteListener.onStepCompleted(currentStudent, 3);
                }
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error saving academic information: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method specifically for updating existing student's academic details
     */
    public void saveAcademicDetailsForExistingStudent() {
        if (validateInput()) {
            currentStudent.setFaculty(dropdownFaculty.getText().toString().trim());
            currentStudent.setDepartment(dropdownDepartment.getText().toString().trim());
            currentStudent.setBatch(etBatch.getText().toString().trim());
            currentStudent.setCurrentSemester(dropdownSemester.getText().toString().trim());
            currentStudent.setEnrollmentDate(etEnrollmentDate.getText().toString().trim());

            try {
                int rowsUpdated = databaseHelper.updateStudent(currentStudent);
                if (rowsUpdated > 0) {
                    Toast.makeText(getContext(), "Academic details updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to update academic details", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error updating academic details: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setOnStepCompleteListener(OnStepCompleteListener listener) {
        this.stepCompleteListener = listener;
    }

    public void setStudentData(Student student) {
        if (student != null) {
            this.currentStudent = student;
            populateFields();
        }
    }

    private void populateFields() {
        if (currentStudent != null) {
            if (currentStudent.getFaculty() != null) {
                dropdownFaculty.setText(currentStudent.getFaculty(), false);
                updateDepartmentDropdown(currentStudent.getFaculty());
            }
            if (currentStudent.getDepartment() != null) {
                dropdownDepartment.setText(currentStudent.getDepartment(), false);
            }
            if (currentStudent.getBatch() != null) {
                etBatch.setText(currentStudent.getBatch());
            }
            if (currentStudent.getCurrentSemester() != null) {
                dropdownSemester.setText(currentStudent.getCurrentSemester(), false);
            }
            if (currentStudent.getEnrollmentDate() != null) {
                etEnrollmentDate.setText(currentStudent.getEnrollmentDate());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        populateFields();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseHelper = null;
    }
}