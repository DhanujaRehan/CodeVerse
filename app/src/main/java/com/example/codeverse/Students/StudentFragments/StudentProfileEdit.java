package com.example.codeverse.Students.StudentFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.example.codeverse.R;
import com.example.codeverse.Admin.Helpers.StudentDatabaseHelper;
import com.example.codeverse.Students.Models.Student;
import com.example.codeverse.Students.Utils.StudentSessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class StudentProfileEdit extends Fragment {

    private LottieAnimationView ivBack;
    private MaterialCardView cvBack;
    private FrameLayout loadingOverlay;

    private TextInputEditText etName, etStudentId, etEmail, etPhone, etFaculty, etBatch;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnCancel, btnUpdatePassword;

    private StudentDatabaseHelper databaseHelper;
    private StudentSessionManager sessionManager;
    private Student currentStudent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_profile_edit, container, false);

        initViews(view);
        initDatabase();
        loadStudentData();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        ivBack = view.findViewById(R.id.iv_back);
        cvBack = view.findViewById(R.id.cv_back);
        loadingOverlay = view.findViewById(R.id.loading_overlay);

        etName = view.findViewById(R.id.et_name);
        etStudentId = view.findViewById(R.id.et_student_id);
        etEmail = view.findViewById(R.id.et_email);
        etPhone = view.findViewById(R.id.et_phone);
        etFaculty = view.findViewById(R.id.et_faculty);
        etBatch = view.findViewById(R.id.et_batch);

        etCurrentPassword = view.findViewById(R.id.et_current_password);
        etNewPassword = view.findViewById(R.id.et_new_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);

        btnCancel = view.findViewById(R.id.btn_cancel);
        btnUpdatePassword = view.findViewById(R.id.btn_update_password);

        ivBack.setOnClickListener(v -> {

            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
                    transaction.replace(R.id.framelayout, new StudentProfile());
                    transaction.addToBackStack(null);
                    transaction.commit();


        });

    }

    private void initDatabase() {
        databaseHelper = new StudentDatabaseHelper(getContext());
        sessionManager = new StudentSessionManager(getContext());
    }

    private void loadStudentData() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(getContext(), "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        long studentId = sessionManager.getStudentId();
        if (studentId != -1) {
            currentStudent = databaseHelper.getStudentById(studentId);
            if (currentStudent != null) {
                populateFields();
            }
        } else {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);
            long fallbackStudentId = sharedPreferences.getLong("current_student_id", -1);
            if (fallbackStudentId != -1) {
                currentStudent = databaseHelper.getStudentById(fallbackStudentId);
                if (currentStudent != null) {
                    populateFields();
                }
            }
        }
    }

    private void populateFields() {
        if (currentStudent.getFullName() != null) {
            etName.setText(currentStudent.getFullName());
        }
        if (currentStudent.getUniversityId() != null) {
            etStudentId.setText(currentStudent.getUniversityId());
        }
        if (currentStudent.getEmail() != null) {
            etEmail.setText(currentStudent.getEmail());
        }
        if (currentStudent.getMobileNumber() != null) {
            etPhone.setText(currentStudent.getMobileNumber());
        }
        if (currentStudent.getFaculty() != null) {
            etFaculty.setText(currentStudent.getFaculty());
        }
        if (currentStudent.getBatch() != null) {
            etBatch.setText(currentStudent.getBatch());
        }
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });
    }

    private void updatePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (currentPassword.isEmpty()) {
            etCurrentPassword.setError("Current password is required");
            return;
        }

        if (newPassword.isEmpty()) {
            etNewPassword.setError("New password is required");
            return;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Confirm password is required");
            return;
        }

        if (!currentPassword.equals(currentStudent.getPassword())) {
            etCurrentPassword.setError("Current password is incorrect");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters");
            return;
        }

        showLoadingOverlay();

        new Handler().postDelayed(() -> {
            currentStudent.setPassword(newPassword);
            int result = databaseHelper.updateStudent(currentStudent);

            hideLoadingOverlay();

            if (result > 0) {
                sessionManager.updateStudentSession(currentStudent);
                Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                clearPasswordFields();
            } else {
                Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
            }
        }, 1500);
    }

    private void clearPasswordFields() {
        etCurrentPassword.setText("");
        etNewPassword.setText("");
        etConfirmPassword.setText("");
    }

    private void showLoadingOverlay() {
        loadingOverlay.setVisibility(View.VISIBLE);
    }

    private void hideLoadingOverlay() {
        loadingOverlay.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }


}