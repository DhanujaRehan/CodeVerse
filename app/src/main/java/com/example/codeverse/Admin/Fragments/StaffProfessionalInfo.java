package com.example.codeverse.Admin.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.codeverse.R;
import com.example.codeverse.Admin.Models.Staff;
import com.example.codeverse.Admin.Helpers.StaffDatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class StaffProfessionalInfo extends Fragment {

    private static final String TAG = "AddStaffProfessionalFragment";
    private static final String ARG_STAFF_ID = "staff_id";

    private StaffDatabaseHelper dbHelper;
    private Staff currentStaff;
    private long staffId = -1;

    private MaterialCardView cvBack, cvHelp;
    private TextInputLayout tilPosition, tilDepartment, tilProgramCoordinating, tilTeachingSubjectSoftware, tilTeachingSubjectDatascience, tilPassword;
    private TextInputLayout tilHighestQualification, tilFieldOfStudy, tilUniversity, tilGraduationYear, tilExperienceYears;
    private AutoCompleteTextView dropdownPosition, dropdownDepartment, dropdownQualification, dropdownProgram, dropdownTeachingSubjectSoftware, dropdownTeachingSubjectDatascience;
    private TextInputEditText etFieldOfStudy, etUniversity, etGraduationYear, etExperienceYears, etPassword;
    private MaterialButton btnComplete, btnBack, btn_go_to_dashboard;
    private FrameLayout loadingOverlay, successOverlay;

    public static StaffProfessionalInfo newInstance(long staffId) {
        StaffProfessionalInfo staffProfessionalInfo = new StaffProfessionalInfo();
        Bundle args = new Bundle();
        args.putLong(ARG_STAFF_ID, staffId);
        staffProfessionalInfo.setArguments(args);
        return staffProfessionalInfo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new StaffDatabaseHelper(getContext());

        if (getArguments() != null) {
            staffId = getArguments().getLong(ARG_STAFF_ID, -1);
        }

        Bundle args = getArguments();
        if (args != null && args.containsKey("staff")) {
            currentStaff = (Staff) args.getSerializable("staff");
            Log.d(TAG, "Editing existing staff: " + currentStaff.getFullName());
        } else if (staffId != -1) {
            currentStaff = dbHelper.getStaffById(staffId);
            if (currentStaff != null) {
                Log.d(TAG, "Loaded existing staff: " + currentStaff.getFullName());
            }
        } else {
            currentStaff = new Staff();
            Log.d(TAG, "Creating new staff");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_professional_info, container, false);
        initializeViews(view);
        setupClickListeners();
        setupTextChangeListeners();
        setupDropdowns();
        setupPositionDependentFields();
        populateFieldsFromStaff();
        return view;
    }

    private void initializeViews(View view) {
        try {
            cvBack = view.findViewById(R.id.cv_back);
            cvHelp = view.findViewById(R.id.cv_help);

            tilPosition = view.findViewById(R.id.til_position);
            tilDepartment = view.findViewById(R.id.til_department);
            tilTeachingSubjectSoftware = view.findViewById(R.id.til_teaching_subject_software);
            tilTeachingSubjectDatascience = view.findViewById(R.id.til_teaching_subject_datascience);
            tilProgramCoordinating = view.findViewById(R.id.til_program_coordinating);
            tilPassword = view.findViewById(R.id.til_password);
            tilHighestQualification = view.findViewById(R.id.til_highest_qualification);
            tilFieldOfStudy = view.findViewById(R.id.til_field_of_study);
            tilUniversity = view.findViewById(R.id.til_university);
            tilGraduationYear = view.findViewById(R.id.til_graduation_year);
            tilExperienceYears = view.findViewById(R.id.til_experience_years);

            dropdownPosition = view.findViewById(R.id.dropdown_position);
            dropdownDepartment = view.findViewById(R.id.dropdown_department);
            dropdownQualification = view.findViewById(R.id.dropdown_qualification);
            dropdownProgram = view.findViewById(R.id.dropdown_program);
            dropdownTeachingSubjectSoftware = view.findViewById(R.id.dropdown_teaching_subject_software);
            dropdownTeachingSubjectDatascience = view.findViewById(R.id.dropdown_teaching_subject_datascience);

            etFieldOfStudy = view.findViewById(R.id.et_field_of_study);
            etUniversity = view.findViewById(R.id.et_university);
            etGraduationYear = view.findViewById(R.id.et_graduation_year);
            etExperienceYears = view.findViewById(R.id.et_experience_years);
            etPassword = view.findViewById(R.id.et_password);

            btnComplete = view.findViewById(R.id.btn_complete);
            btnBack = view.findViewById(R.id.btn_back);
            btn_go_to_dashboard = view.findViewById(R.id.btn_go_to_dashboard);
            loadingOverlay = view.findViewById(R.id.loading_overlay);
            successOverlay = view.findViewById(R.id.success_overlay);

        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            showToast("Failed to initialize the form");
        }
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        cvHelp.setOnClickListener(v -> showHelpDialog());

        btnComplete.setOnClickListener(v -> {
            if (validateProfessionalInformation()) {
                completeRegistration();
            }
        });

        btnBack.setOnClickListener(v -> goBackToPersonalInfo());
        btn_go_to_dashboard.setOnClickListener(v -> {
            showUserList();
            clearForm();
        }
        );
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

        dropdownPosition.addTextChangedListener(textWatcher);
        dropdownDepartment.addTextChangedListener(textWatcher);
        dropdownTeachingSubjectSoftware.addTextChangedListener(textWatcher);
        dropdownTeachingSubjectDatascience.addTextChangedListener(textWatcher);
        dropdownProgram.addTextChangedListener(textWatcher);
        dropdownQualification.addTextChangedListener(textWatcher);
        etFieldOfStudy.addTextChangedListener(textWatcher);
        etUniversity.addTextChangedListener(textWatcher);
        etGraduationYear.addTextChangedListener(textWatcher);
        etExperienceYears.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
    }

    private void clearErrors() {
        if (tilPosition != null) tilPosition.setError(null);
        if (tilDepartment != null) tilDepartment.setError(null);
        if (tilTeachingSubjectSoftware != null) tilTeachingSubjectSoftware.setError(null);
        if (tilTeachingSubjectDatascience != null) tilTeachingSubjectDatascience.setError(null);
        if (tilProgramCoordinating != null) tilProgramCoordinating.setError(null);
        if (tilPassword != null) tilPassword.setError(null);
        if (tilHighestQualification != null) tilHighestQualification.setError(null);
        if (tilFieldOfStudy != null) tilFieldOfStudy.setError(null);
        if (tilUniversity != null) tilUniversity.setError(null);
        if (tilGraduationYear != null) tilGraduationYear.setError(null);
        if (tilExperienceYears != null) tilExperienceYears.setError(null);
    }

    private void setupDropdowns() {
        String[] positions = {"Lecturer", "Program Coordinator"};
        ArrayAdapter<String> positionAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, positions);
        dropdownPosition.setAdapter(positionAdapter);

        String[] departments = {"Software Engineering", "Data Science"};
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, departments);
        dropdownDepartment.setAdapter(departmentAdapter);

        String[] qualifications = {"Bachelor's Degree", "Master's Degree", "PhD", "Postdoc",
                "Professional Certificate", "Diploma"};
        ArrayAdapter<String> qualificationAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, qualifications);
        dropdownQualification.setAdapter(qualificationAdapter);

        String[] programs = {"Software Engineering", "Data Science"};
        ArrayAdapter<String> programAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, programs);
        dropdownProgram.setAdapter(programAdapter);

        String[] software = {"Introduction to Programming", "Data Structures & Algorithms", "Object-Oriented Programming", "Web Application Development", "Mobile Application development"};
        ArrayAdapter<String> softwareAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, software);
        dropdownTeachingSubjectSoftware.setAdapter(softwareAdapter);

        String[] datascience = {"Introduction to Programming for Data Science", "Mathematics for Data Science", "Data Wrangling and Exploration", "Database Systems and Big Data", "Data Visualization"};
        ArrayAdapter<String> datascienceAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, datascience);
        dropdownTeachingSubjectDatascience.setAdapter(datascienceAdapter);
    }

    private void setupPositionDependentFields() {
        dropdownPosition.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPosition = dropdownPosition.getText().toString();
            updateFieldVisibility(selectedPosition);
        });

        dropdownDepartment.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDepartment = dropdownDepartment.getText().toString();
            updateTeachingSubjectVisibility(selectedDepartment);
        });
    }

    private void updateFieldVisibility(String position) {
        tilDepartment.setVisibility(View.GONE);
        tilProgramCoordinating.setVisibility(View.GONE);
        tilTeachingSubjectSoftware.setVisibility(View.GONE);
        tilTeachingSubjectDatascience.setVisibility(View.GONE);

        if (position.toLowerCase().contains("lecturer") || position.toLowerCase().contains("professor")) {
            tilDepartment.setVisibility(View.VISIBLE);
        } else if (position.toLowerCase().contains("coordinator")) {
            tilProgramCoordinating.setVisibility(View.VISIBLE);
        }
    }

    private void updateTeachingSubjectVisibility(String department) {
        tilTeachingSubjectSoftware.setVisibility(View.GONE);
        tilTeachingSubjectDatascience.setVisibility(View.GONE);

        dropdownTeachingSubjectSoftware.setText("", false);
        dropdownTeachingSubjectDatascience.setText("", false);

        if ("Software Engineering".equals(department)) {
            tilTeachingSubjectSoftware.setVisibility(View.VISIBLE);
        } else if ("Data Science".equals(department)) {
            tilTeachingSubjectDatascience.setVisibility(View.VISIBLE);
        }
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Help")
                .setMessage("Fill in professional and educational details. Different fields will appear based on the selected position.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void completeRegistration() {
        showLoading(true);
        saveProfessionalInformation();

        new Handler().postDelayed(() -> {
            try {
                int rowsAffected = dbHelper.updateStaffProfessionalDetails(
                        currentStaff.getId(),
                        currentStaff.getPosition(),
                        currentStaff.getDepartment(),
                        currentStaff.getTeachingSubject(),
                        currentStaff.getProgramCoordinating(),
                        currentStaff.getPassword(),
                        currentStaff.getHighestQualification(),
                        currentStaff.getFieldOfStudy(),
                        currentStaff.getUniversity(),
                        currentStaff.getGraduationYear(),
                        currentStaff.getExperienceYears()
                );

                showLoading(false);

                if (rowsAffected > 0) {
                    Log.d(TAG, "Staff professional details updated successfully");
                    showToast("Staff registered successfully!");
                    navigateToCompletion();
                } else {
                    Log.e(TAG, "Failed to update staff professional details");
                    showToast("Failed to complete staff registration");
                }
            } catch (Exception e) {
                showLoading(false);
                Log.e(TAG, "Error completing registration: " + e.getMessage(), e);
                showToast("Error completing registration: " + e.getMessage());
            }
        }, 1500);
    }

    private boolean validateProfessionalInformation() {
        boolean isValid = true;

        if (dropdownPosition.getText().toString().trim().isEmpty()) {
            if (tilPosition != null) {
                tilPosition.setError("Please select a position");
            } else {
                dropdownPosition.setError("Please select a position");
            }
            dropdownPosition.requestFocus();
            isValid = false;
        }

        if (tilDepartment.getVisibility() == View.VISIBLE) {
            if (dropdownDepartment.getText().toString().trim().isEmpty()) {
                if (tilDepartment != null) {
                    tilDepartment.setError("Please select a department");
                } else {
                    dropdownDepartment.setError("Please select a department");
                }
                dropdownDepartment.requestFocus();
                isValid = false;
            }
        }

        if (tilTeachingSubjectSoftware.getVisibility() == View.VISIBLE) {
            if (dropdownTeachingSubjectSoftware.getText().toString().trim().isEmpty()) {
                if (tilTeachingSubjectSoftware != null) {
                    tilTeachingSubjectSoftware.setError("Please select a teaching subject");
                } else {
                    dropdownTeachingSubjectSoftware.setError("Please select a teaching subject");
                }
                dropdownTeachingSubjectSoftware.requestFocus();
                isValid = false;
            }
        }

        if (tilTeachingSubjectDatascience.getVisibility() == View.VISIBLE) {
            if (dropdownTeachingSubjectDatascience.getText().toString().trim().isEmpty()) {
                if (tilTeachingSubjectDatascience != null) {
                    tilTeachingSubjectDatascience.setError("Please select a teaching subject");
                } else {
                    dropdownTeachingSubjectDatascience.setError("Please select a teaching subject");
                }
                dropdownTeachingSubjectDatascience.requestFocus();
                isValid = false;
            }
        }

        if (tilProgramCoordinating.getVisibility() == View.VISIBLE) {
            if (dropdownProgram.getText().toString().trim().isEmpty()) {
                if (tilProgramCoordinating != null) {
                    tilProgramCoordinating.setError("Please select a program");
                } else {
                    dropdownProgram.setError("Please select a program");
                }
                dropdownProgram.requestFocus();
                isValid = false;
            }
        }

        if (etPassword.getText().toString().trim().isEmpty()) {
            if (tilPassword != null) {
                tilPassword.setError("Password is required");
            } else {
                etPassword.setError("Password is required");
            }
            etPassword.requestFocus();
            isValid = false;
        } else if (etPassword.getText().toString().trim().length() < 6) {
            if (tilPassword != null) {
                tilPassword.setError("Password must be at least 6 characters");
            } else {
                etPassword.setError("Password must be at least 6 characters");
            }
            etPassword.requestFocus();
            isValid = false;
        }

        if (dropdownQualification.getText().toString().trim().isEmpty()) {
            if (tilHighestQualification != null) {
                tilHighestQualification.setError("Please select highest qualification");
            } else {
                dropdownQualification.setError("Please select highest qualification");
            }
            dropdownQualification.requestFocus();
            isValid = false;
        }

        if (etFieldOfStudy.getText().toString().trim().isEmpty()) {
            if (tilFieldOfStudy != null) {
                tilFieldOfStudy.setError("Field of study is required");
            } else {
                etFieldOfStudy.setError("Field of study is required");
            }
            etFieldOfStudy.requestFocus();
            isValid = false;
        }

        if (etUniversity.getText().toString().trim().isEmpty()) {
            if (tilUniversity != null) {
                tilUniversity.setError("University/Institution is required");
            } else {
                etUniversity.setError("University/Institution is required");
            }
            etUniversity.requestFocus();
            isValid = false;
        }

        String graduationYear = etGraduationYear.getText().toString().trim();
        if (graduationYear.isEmpty()) {
            if (tilGraduationYear != null) {
                tilGraduationYear.setError("Graduation year is required");
            } else {
                etGraduationYear.setError("Graduation year is required");
            }
            etGraduationYear.requestFocus();
            isValid = false;
        } else {
            try {
                int year = Integer.parseInt(graduationYear);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (year < 1950 || year > currentYear) {
                    if (tilGraduationYear != null) {
                        tilGraduationYear.setError("Please enter a valid year");
                    } else {
                        etGraduationYear.setError("Please enter a valid year");
                    }
                    etGraduationYear.requestFocus();
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                if (tilGraduationYear != null) {
                    tilGraduationYear.setError("Please enter a valid year");
                } else {
                    etGraduationYear.setError("Please enter a valid year");
                }
                etGraduationYear.requestFocus();
                isValid = false;
            }
        }

        String experienceYears = etExperienceYears.getText().toString().trim();
        if (experienceYears.isEmpty()) {
            if (tilExperienceYears != null) {
                tilExperienceYears.setError("Years of experience is required");
            } else {
                etExperienceYears.setError("Years of experience is required");
            }
            etExperienceYears.requestFocus();
            isValid = false;
        } else {
            try {
                double experience = Double.parseDouble(experienceYears);
                if (experience < 0 || experience > 50) {
                    if (tilExperienceYears != null) {
                        tilExperienceYears.setError("Please enter valid experience years");
                    } else {
                        etExperienceYears.setError("Please enter valid experience years");
                    }
                    etExperienceYears.requestFocus();
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                if (tilExperienceYears != null) {
                    tilExperienceYears.setError("Please enter a valid number");
                } else {
                    etExperienceYears.setError("Please enter a valid number");
                }
                etExperienceYears.requestFocus();
                isValid = false;
            }
        }

        return isValid;
    }

    private void saveProfessionalInformation() {
        if (currentStaff == null) {
            currentStaff = new Staff();
        }

        currentStaff.setPosition(dropdownPosition.getText().toString().trim());

        if (tilDepartment.getVisibility() == View.VISIBLE) {
            currentStaff.setDepartment(dropdownDepartment.getText().toString().trim());
        }

        if (tilTeachingSubjectSoftware.getVisibility() == View.VISIBLE) {
            currentStaff.setTeachingSubject(dropdownTeachingSubjectSoftware.getText().toString().trim());
        } else if (tilTeachingSubjectDatascience.getVisibility() == View.VISIBLE) {
            currentStaff.setTeachingSubject(dropdownTeachingSubjectDatascience.getText().toString().trim());
        }

        if (tilProgramCoordinating.getVisibility() == View.VISIBLE) {
            currentStaff.setProgramCoordinating(dropdownProgram.getText().toString().trim());
        }

        currentStaff.setPassword(etPassword.getText().toString().trim());
        currentStaff.setHighestQualification(dropdownQualification.getText().toString().trim());
        currentStaff.setFieldOfStudy(etFieldOfStudy.getText().toString().trim());
        currentStaff.setUniversity(etUniversity.getText().toString().trim());
        currentStaff.setGraduationYear(etGraduationYear.getText().toString().trim());
        currentStaff.setExperienceYears(etExperienceYears.getText().toString().trim());

        Log.d(TAG, "Professional information saved to staff object: " + currentStaff.toString());
    }

    private void navigateToCompletion() {
        loadingOverlay.setVisibility(View.VISIBLE);

        try {
            loadingOverlay.setVisibility(View.GONE);
            successOverlay.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to completion: " + e.getMessage(), e);
            showToast("Registration completed but error in navigation");
        }
    }

    private void goBackToPersonalInfo() {
        new AlertDialog.Builder(getContext())
                .setTitle("Go Back")
                .setMessage("Do you want to go back to personal information? Any unsaved professional details will be lost.")
                .setPositiveButton("Yes, Go Back", (dialog, which) -> {
                    getParentFragmentManager().popBackStack();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showLoading(boolean show) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public Staff getCurrentStaff() {
        return currentStaff;
    }

    public void setCurrentStaff(Staff staff) {
        this.currentStaff = staff;
        populateFieldsFromStaff();
    }

    private void populateFieldsFromStaff() {
        if (currentStaff != null) {
            if (currentStaff.getPosition() != null) {
                dropdownPosition.setText(currentStaff.getPosition());
                updateFieldVisibility(currentStaff.getPosition());
            }
            if (currentStaff.getDepartment() != null) {
                dropdownDepartment.setText(currentStaff.getDepartment());
                updateTeachingSubjectVisibility(currentStaff.getDepartment());
            }
            if (currentStaff.getTeachingSubject() != null) {
                String department = currentStaff.getDepartment();
                if ("Software Engineering".equals(department)) {
                    dropdownTeachingSubjectSoftware.setText(currentStaff.getTeachingSubject());
                } else if ("Data Science".equals(department)) {
                    dropdownTeachingSubjectDatascience.setText(currentStaff.getTeachingSubject());
                }
            }
            if (currentStaff.getProgramCoordinating() != null) {
                dropdownProgram.setText(currentStaff.getProgramCoordinating());
            }
            if (currentStaff.getPassword() != null) {
                etPassword.setText(currentStaff.getPassword());
            }
            if (currentStaff.getHighestQualification() != null) {
                dropdownQualification.setText(currentStaff.getHighestQualification());
            }
            if (currentStaff.getFieldOfStudy() != null) {
                etFieldOfStudy.setText(currentStaff.getFieldOfStudy());
            }
            if (currentStaff.getUniversity() != null) {
                etUniversity.setText(currentStaff.getUniversity());
            }
            if (currentStaff.getGraduationYear() != null) {
                etGraduationYear.setText(currentStaff.getGraduationYear());
            }
            if (currentStaff.getExperienceYears() != null) {
                etExperienceYears.setText(currentStaff.getExperienceYears());
            }
            Log.d(TAG, "Fields populated from staff: " + currentStaff.toString());
        }
    }

    public void showUserList(){
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, new UserShowingFragment());
        transaction.addToBackStack(null); // Optional: allows back navigation
        transaction.commit();
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        etExperienceYears.setText("");
        etPassword.setText("");
        etFieldOfStudy.setText("");
        etUniversity.setText("");
        etGraduationYear.setText("");
        dropdownQualification.setText("");
        dropdownProgram.setText("");
        dropdownDepartment.setText("");
        dropdownPosition.setText("");
        dropdownTeachingSubjectSoftware.setText("");
        dropdownTeachingSubjectDatascience.setText("");
        clearErrors();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}