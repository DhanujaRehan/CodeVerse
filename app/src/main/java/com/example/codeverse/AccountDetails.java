package com.example.codeverse;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.codeverse.R;
import com.example.codeverse.StudentDatabaseHelper;
import com.example.codeverse.Student;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountDetails extends Fragment {

    private static final String TAG = "AccountDetailsFragment";

    // Views
    private TextInputEditText etEmail, etUsername, etPassword, etConfirmPassword;
    private TextInputLayout tilEmail, tilUsername, tilPassword, tilConfirmPassword;
    private MaterialCheckBox checkboxTerms;
    private MaterialButton btnNextStep, btnCancel;
    private MaterialCardView cvBack;

    // Password requirement TextViews
    private TextView tvPasswordRequirementLength, tvPasswordRequirementUppercase,
            tvPasswordRequirementNumber, tvPasswordRequirementSpecial,
            tvPasswordRequirementMatch;

    // Data
    private Student currentStudent;
    private StudentDatabaseHelper dbHelper;
    private ExecutorService executorService;

    public interface OnStepCompleteListener {
        void onStepCompleted(Student student, int nextStep);
        void onCancel();
        void onBack();
    }

    private OnStepCompleteListener stepCompleteListener;

    public static AccountDetails newInstance() {
        return new AccountDetails();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (currentStudent == null) {
            currentStudent = new Student();
        }
        dbHelper = StudentDatabaseHelper.getInstance(getContext());
        executorService = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_details, container, false);
        initViews(view);
        setupListeners();
        setupPasswordValidation();
        return view;
    }

    private void initViews(View view) {
        // Text input fields
        etEmail = view.findViewById(R.id.et_email);
        etUsername = view.findViewById(R.id.et_username);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);

        // Text input layouts
        tilEmail = view.findViewById(R.id.til_email);
        tilUsername = view.findViewById(R.id.til_username);
        tilPassword = view.findViewById(R.id.til_password);
        tilConfirmPassword = view.findViewById(R.id.til_confirm_password);

        // Checkbox
        checkboxTerms = view.findViewById(R.id.checkbox_terms);

        // Buttons
        btnNextStep = view.findViewById(R.id.btn_next_step);
        btnCancel = view.findViewById(R.id.btn_cancel);
        cvBack = view.findViewById(R.id.cv_back);

        // Password requirement indicators
        tvPasswordRequirementLength = view.findViewById(R.id.tv_password_requirement_length);
        tvPasswordRequirementUppercase = view.findViewById(R.id.tv_password_requirement_uppercase);
        tvPasswordRequirementNumber = view.findViewById(R.id.tv_password_requirement_number);
        tvPasswordRequirementSpecial = view.findViewById(R.id.tv_password_requirement_special);
        tvPasswordRequirementMatch = view.findViewById(R.id.tv_password_requirement_match);
    }

    private void setupListeners() {
        btnNextStep.setOnClickListener(v -> {
            if (validateInput()) {
                saveAccountInfoToDatabase();
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

        // Real-time validation for email
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmailRealTime();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Real-time validation for username
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateUsernameRealTime();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupPasswordValidation() {
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePasswordRequirements();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etPassword.addTextChangedListener(passwordWatcher);
        etConfirmPassword.addTextChangedListener(passwordWatcher);
    }

    private void updatePasswordRequirements() {
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Check length requirement
        updateRequirementView(tvPasswordRequirementLength, password.length() >= 8);

        // Check uppercase requirement
        updateRequirementView(tvPasswordRequirementUppercase, password.matches(".*[A-Z].*"));

        // Check number requirement
        updateRequirementView(tvPasswordRequirementNumber, password.matches(".*[0-9].*"));

        // Check special character requirement
        updateRequirementView(tvPasswordRequirementSpecial, password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*"));

        // Check passwords match
        boolean passwordsMatch = !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)
                && password.equals(confirmPassword);
        updateRequirementView(tvPasswordRequirementMatch, passwordsMatch);
    }

    private void updateRequirementView(TextView textView, boolean isValid) {
        if (isValid) {
            textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_dark));
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
        } else {
            textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle, 0, 0, 0);
        }
    }

    private void validateEmailRealTime() {
        String email = etEmail.getText().toString().trim();
        if (!TextUtils.isEmpty(email)) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.setError("Invalid email format");
            } else if (dbHelper.isEmailExists(email, currentStudent.getId())) {
                tilEmail.setError("Email already exists");
            } else {
                tilEmail.setError(null);
            }
        } else {
            tilEmail.setError(null);
        }
    }

    private void validateUsernameRealTime() {
        String username = etUsername.getText().toString().trim();
        if (!TextUtils.isEmpty(username)) {
            if (username.length() < 3) {
                tilUsername.setError("Username must be at least 3 characters");
            } else if (!username.matches("^[a-zA-Z0-9_]+$")) {
                tilUsername.setError("Username can only contain letters, numbers, and underscores");
            } else if (dbHelper.isUsernameExists(username, currentStudent.getId())) {
                tilUsername.setError("Username already exists");
            } else {
                tilUsername.setError(null);
            }
        } else {
            tilUsername.setError(null);
        }
    }

    private boolean validateInput() {
        boolean isValid = true;

        // Reset errors
        tilEmail.setError(null);
        tilUsername.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Validate email
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid email format");
            isValid = false;
        } else if (dbHelper.isEmailExists(email, currentStudent.getId())) {
            tilEmail.setError("Email already exists");
            isValid = false;
        }

        // Validate username
        String username = etUsername.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Username is required");
            isValid = false;
        } else if (username.length() < 3) {
            tilUsername.setError("Username must be at least 3 characters");
            isValid = false;
        } else if (!username.matches("^[a-zA-Z0-9_]+$")) {
            tilUsername.setError("Username can only contain letters, numbers, and underscores");
            isValid = false;
        } else if (dbHelper.isUsernameExists(username, currentStudent.getId())) {
            tilUsername.setError("Username already exists");
            isValid = false;
        }

        // Validate password
        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (!isPasswordValid(password)) {
            tilPassword.setError("Password does not meet requirements");
            isValid = false;
        }

        // Validate confirm password
        String confirmPassword = etConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        // Validate terms acceptance
        if (!checkboxTerms.isChecked()) {
            Toast.makeText(getContext(), "Please accept the Terms of Service and Privacy Policy", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        return isValid;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[0-9].*") &&
                password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback - in production, use a proper encryption library like bcrypt
            return password;
        }
    }

    private void saveAccountInfoToDatabase() {
        // Check if student ID exists
        if (currentStudent.getId() <= 0) {
            Toast.makeText(getContext(), "Error: Student ID not found. Please restart registration.", Toast.LENGTH_LONG).show();
            return;
        }

        // Set button to loading state
        btnNextStep.setEnabled(false);
        btnNextStep.setText("Saving...");

        // Collect data from form
        currentStudent.setEmail(etEmail.getText().toString().trim());
        currentStudent.setUsername(etUsername.getText().toString().trim());
        currentStudent.setPassword(hashPassword(etPassword.getText().toString()));
        currentStudent.setTermsAccepted(checkboxTerms.isChecked());

        // Save to database in background thread
        executorService.execute(() -> {
            try {
                boolean success = dbHelper.updateAccountDetails(currentStudent.getId(), currentStudent);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        btnNextStep.setEnabled(true);
                        btnNextStep.setText("Continue to Contact Details");

                        if (success) {
                            Toast.makeText(getContext(), "Account information saved successfully", Toast.LENGTH_SHORT).show();

                            // Move to next fragment
                            if (stepCompleteListener != null) {
                                stepCompleteListener.onStepCompleted(currentStudent, 4);
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to save account information. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        btnNextStep.setEnabled(true);
                        btnNextStep.setText("Continue to Contact Details");
                        Toast.makeText(getContext(), "Error saving data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
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
            if (currentStudent.getEmail() != null) {
                etEmail.setText(currentStudent.getEmail());
            }
            if (currentStudent.getUsername() != null) {
                etUsername.setText(currentStudent.getUsername());
            }
            // Don't populate password fields for security reasons
            checkboxTerms.setChecked(currentStudent.isTermsAccepted());
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
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}