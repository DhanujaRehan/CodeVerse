package com.example.codeverse.Admin.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.codeverse.R;
import com.example.codeverse.Students.Models.Student;
import com.example.codeverse.Students.Helpers.StudentDatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class AccountDetails extends Fragment {

    private TextInputEditText etEmail, etUsername, etPassword, etConfirmPassword;
    private TextInputLayout tilEmail, tilUsername, tilPassword, tilConfirmPassword;
    private CheckBox checkboxTerms;
    private TextView tvTerms;
    private MaterialButton btnNextStep, btnCancel;
    private MaterialCardView cvBack, cvHelp;
    private FrameLayout loadingOverlay;
    private TextView tvPasswordRequirementLength, tvPasswordRequirementUppercase;
    private TextView tvPasswordRequirementNumber, tvPasswordRequirementSpecial;
    private TextView tvPasswordRequirementMatch;
    private LinearLayout cardBasicInfoIndicator, cardAcademicIndicator,
            cardAccountIndicator, cardContactIndicator;
    private StudentDatabaseHelper dbHelper;
    private long studentId = -1;
    private static final String TAG = "AccountDetailsFragment";
    private static final String ARG_STUDENT_ID = "student_id";
    private static final Pattern PASSWORD_UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern PASSWORD_NUMBER = Pattern.compile("[0-9]");
    private static final Pattern PASSWORD_SPECIAL = Pattern.compile("[^A-Za-z0-9]");

    public static AccountDetails newInstance(long studentId) {
        AccountDetails fragment = new AccountDetails();
        Bundle args = new Bundle();
        args.putLong(ARG_STUDENT_ID, studentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_details, container, false);

        if (getArguments() != null) {
            studentId = getArguments().getLong(ARG_STUDENT_ID, -1);
        }

        dbHelper = new StudentDatabaseHelper(getContext());

        initializeViews(view);
        setupClickListeners();
        setupTextChangeListeners();
        populateFieldsFromData();

        return view;
    }

    private void initializeViews(View view) {
        try {
            etEmail = view.findViewById(R.id.et_email);
            etUsername = view.findViewById(R.id.et_username);
            etPassword = view.findViewById(R.id.et_password);
            etConfirmPassword = view.findViewById(R.id.et_confirm_password);

            tilEmail = view.findViewById(R.id.til_email);
            tilUsername = view.findViewById(R.id.til_username);
            tilPassword = view.findViewById(R.id.til_password);
            tilConfirmPassword = view.findViewById(R.id.til_confirm_password);

            checkboxTerms = view.findViewById(R.id.checkbox_terms);
            tvTerms = view.findViewById(R.id.tv_terms);

            tvPasswordRequirementLength = view.findViewById(R.id.tv_password_requirement_length);
            tvPasswordRequirementUppercase = view.findViewById(R.id.tv_password_requirement_uppercase);
            tvPasswordRequirementNumber = view.findViewById(R.id.tv_password_requirement_number);
            tvPasswordRequirementSpecial = view.findViewById(R.id.tv_password_requirement_special);
            tvPasswordRequirementMatch = view.findViewById(R.id.tv_password_requirement_match);

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

    private void setupClickListeners() {
        cvBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        cvHelp.setOnClickListener(v -> showHelpDialog());

        tvTerms.setOnClickListener(v -> showTermsDialog());

        btnNextStep.setOnClickListener(v -> {
            if (validateInputs()) {
                saveAccountDetails();
            }
        });

        btnCancel.setOnClickListener(v -> {
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

        etEmail.addTextChangedListener(textWatcher);
        etUsername.addTextChangedListener(textWatcher);

        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearErrors();
                updatePasswordRequirements();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        etPassword.addTextChangedListener(passwordWatcher);
        etConfirmPassword.addTextChangedListener(passwordWatcher);
    }

    private void clearErrors() {
        tilEmail.setError(null);
        tilUsername.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    private void updatePasswordRequirements() {
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

        int successColor = ContextCompat.getColor(getContext(), android.R.color.holo_green_dark);
        int failureColor = ContextCompat.getColor(getContext(), android.R.color.darker_gray);

        boolean lengthMet = password.length() >= 8;
        tvPasswordRequirementLength.setTextColor(lengthMet ? successColor : failureColor);
        tvPasswordRequirementLength.setText(lengthMet ? "✓ At least 8 characters" : "• At least 8 characters");

        boolean uppercaseMet = PASSWORD_UPPERCASE.matcher(password).find();
        tvPasswordRequirementUppercase.setTextColor(uppercaseMet ? successColor : failureColor);
        tvPasswordRequirementUppercase.setText(uppercaseMet ? "✓ At least one uppercase letter" : "• At least one uppercase letter");

        boolean numberMet = PASSWORD_NUMBER.matcher(password).find();
        tvPasswordRequirementNumber.setTextColor(numberMet ? successColor : failureColor);
        tvPasswordRequirementNumber.setText(numberMet ? "✓ At least one number" : "• At least one number");

        boolean specialMet = PASSWORD_SPECIAL.matcher(password).find();
        tvPasswordRequirementSpecial.setTextColor(specialMet ? successColor : failureColor);
        tvPasswordRequirementSpecial.setText(specialMet ? "✓ At least one special character" : "• At least one special character");

        boolean passwordsMatch = !password.isEmpty() && password.equals(confirmPassword);
        tvPasswordRequirementMatch.setTextColor(passwordsMatch ? successColor : failureColor);
        tvPasswordRequirementMatch.setText(passwordsMatch ? "✓ Passwords match" : "• Passwords match");
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (TextUtils.isEmpty(etEmail.getText())) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            tilEmail.setError("Please enter a valid email address");
            isValid = false;
        } else {
            String email = etEmail.getText().toString().trim();
            if (dbHelper.isEmailExists(email)) {
                tilEmail.setError("This email is already registered");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(etUsername.getText())) {
            tilUsername.setError("Username is required");
            isValid = false;
        } else if (etUsername.getText().toString().length() < 5) {
            tilUsername.setError("Username must be at least 5 characters");
            isValid = false;
        } else {
            String username = etUsername.getText().toString().trim();
            if (dbHelper.isUsernameExists(username)) {
                tilUsername.setError("This username is already taken");
                isValid = false;
            }
        }

        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else {
            boolean lengthMet = password.length() >= 8;
            boolean uppercaseMet = PASSWORD_UPPERCASE.matcher(password).find();
            boolean numberMet = PASSWORD_NUMBER.matcher(password).find();
            boolean specialMet = PASSWORD_SPECIAL.matcher(password).find();

            if (!lengthMet || !uppercaseMet || !numberMet || !specialMet) {
                tilPassword.setError("Password does not meet requirements");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        if (!checkboxTerms.isChecked()) {
            showToast("You must accept the Terms of Service and Privacy Policy");
            isValid = false;
        }

        return isValid;
    }

    private void saveAccountDetails() {
        // Show loading
        loadingOverlay.setVisibility(View.VISIBLE);

        try {
            if (studentId == -1) {
                loadingOverlay.setVisibility(View.GONE);
                showToast("Error: Student ID not found");
                return;
            }

            String email = etEmail.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            boolean termsAccepted = checkboxTerms.isChecked();

            int result = dbHelper.updateStudentAccountDetails(studentId, email, username,
                    password, termsAccepted);

            new Handler().postDelayed(() -> {
                loadingOverlay.setVisibility(View.GONE);

                if (result > 0) {
                    showToast("Account details saved successfully!");

                    if (getActivity() != null) {
                        ContactDetails contactFragment = ContactDetails.newInstance(studentId);

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.framelayout, contactFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                } else {
                    showToast("Failed to save account details. Please try again.");
                }
            }, 1500);

        } catch (Exception e) {
            loadingOverlay.setVisibility(View.GONE);
            Log.e(TAG, "Error saving account details: " + e.getMessage());
            showToast("An error occurred while saving data");
        }
    }

    private void showTermsDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Terms of Service & Privacy Policy")
                .setMessage("These are the terms and conditions for using the Student Management System...\n\n" +
                        "1. Your data will be collected and stored securely.\n" +
                        "2. Your account information will only be used for academic purposes.\n" +
                        "3. You are responsible for maintaining the confidentiality of your account.\n" +
                        "4. Unauthorized access or use of another's account is prohibited.\n\n" +
                        "For the full terms and privacy policy, please visit the university website.")
                .setPositiveButton("Accept", (dialog, which) -> {
                    checkboxTerms.setChecked(true);
                })
                .setNegativeButton("Decline", (dialog, which) -> {
                    checkboxTerms.setChecked(false);
                })
                .show();
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Account Setup")
                .setMessage("This form creates your student account credentials:\n\n" +
                        "• Enter your email address (will be used for notifications)\n" +
                        "• Create a username for logging into the system\n" +
                        "• Create a secure password that meets all requirements\n" +
                        "• Accept the terms and conditions\n\n" +
                        "All fields are required for account creation.")
                .setPositiveButton("Got it", null)
                .show();
    }

    private void populateFieldsFromData() {
        if (studentId == -1) return;

        try {
            Student student = dbHelper.getStudentById(studentId);

            if (student != null) {
                if (student.getEmail() != null && !student.getEmail().isEmpty()) {
                    etEmail.setText(student.getEmail());
                }

                if (student.getUsername() != null && !student.getUsername().isEmpty()) {
                    etUsername.setText(student.getUsername());
                }

                if (student.getPassword() != null && !student.getPassword().isEmpty()) {
                    etPassword.setText(student.getPassword());
                    etConfirmPassword.setText(student.getPassword());
                }

                checkboxTerms.setChecked(student.isTermsAccepted());

                updatePasswordRequirements();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error populating fields: " + e.getMessage());
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void clearform(){
        etEmail.setText("");
        etUsername.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
        checkboxTerms.setChecked(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}