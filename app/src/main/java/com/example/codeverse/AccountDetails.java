package com.example.codeverse;

import android.content.Context;
import android.os.Bundle;
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

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class AccountDetails extends Fragment {

    // Interface for communication with parent activity
    public interface OnAccountInfoListener {
        void onAccountInfoCompleted(Student accountInfo);
        void onAccountInfoCancelled();
        void onNavigateToStep(int step);
    }

    // UI Components
    private TextInputEditText etEmail, etUsername, etPassword, etConfirmPassword;
    private TextInputLayout tilEmail, tilUsername, tilPassword, tilConfirmPassword;
    private CheckBox checkboxTerms;
    private TextView tvTerms;
    private MaterialButton btnNextStep, btnCancel;
    private MaterialCardView cvBack, cvHelp;
    private FrameLayout loadingOverlay;

    // Password requirement indicators
    private TextView tvPasswordRequirementLength, tvPasswordRequirementUppercase;
    private TextView tvPasswordRequirementNumber, tvPasswordRequirementSpecial;
    private TextView tvPasswordRequirementMatch;

    // Step indicators
    private LinearLayout cardBasicInfoIndicator, cardAcademicIndicator,
            cardAccountIndicator, cardContactIndicator;

    // Data
    private Student studentDetails;
    private OnAccountInfoListener listener;
    private StudentDatabaseHelper databaseHelper;

    // Constants
    private static final String TAG = "AccountDetailsFragment";

    // Password validation patterns
    private static final Pattern PASSWORD_UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern PASSWORD_NUMBER = Pattern.compile("[0-9]");
    private static final Pattern PASSWORD_SPECIAL = Pattern.compile("[^A-Za-z0-9]");

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAccountInfoListener) {
            listener = (OnAccountInfoListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnAccountInfoListener");
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
            studentDetails = new Student();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_details, container, false);

        // Initialize UI components
        initializeViews(view);

        // Set up click listeners
        setupClickListeners();

        // Set up text change listeners for validation
        setupTextChangeListeners();

        // Populate fields with existing data
        populateFieldsFromData();

        return view;
    }

    private void initializeViews(View view) {
        try {
            // Input fields
            etEmail = view.findViewById(R.id.et_email);
            etUsername = view.findViewById(R.id.et_username);
            etPassword = view.findViewById(R.id.et_password);
            etConfirmPassword = view.findViewById(R.id.et_confirm_password);

            // TextInputLayouts for validation
            tilEmail = view.findViewById(R.id.til_email);
            tilUsername = view.findViewById(R.id.til_username);
            tilPassword = view.findViewById(R.id.til_password);
            tilConfirmPassword = view.findViewById(R.id.til_confirm_password);

            // Checkbox and terms
            checkboxTerms = view.findViewById(R.id.checkbox_terms);
            tvTerms = view.findViewById(R.id.tv_terms);

            // Password requirement indicators
            tvPasswordRequirementLength = view.findViewById(R.id.tv_password_requirement_length);
            tvPasswordRequirementUppercase = view.findViewById(R.id.tv_password_requirement_uppercase);
            tvPasswordRequirementNumber = view.findViewById(R.id.tv_password_requirement_number);
            tvPasswordRequirementSpecial = view.findViewById(R.id.tv_password_requirement_special);
            tvPasswordRequirementMatch = view.findViewById(R.id.tv_password_requirement_match);

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

    private void setupClickListeners() {
        // Back button click listener
        cvBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Help button click listener
        cvHelp.setOnClickListener(v -> showHelpDialog());

        // Terms and conditions click listener
        tvTerms.setOnClickListener(v -> showTermsDialog());

        // Step indicators click listeners for navigation
        cardBasicInfoIndicator.setOnClickListener(v -> listener.onNavigateToStep(1));
        cardAcademicIndicator.setOnClickListener(v -> listener.onNavigateToStep(2));
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
                listener.onAccountInfoCompleted(studentDetails);
            }
        });

        // Cancel button click listener
        btnCancel.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Cancel Student Creation")
                    .setMessage("Are you sure you want to cancel? All entered information will be lost.")
                    .setPositiveButton("Yes", (dialog, which) -> listener.onAccountInfoCancelled())
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

        // Apply the TextWatcher to email and username fields
        etEmail.addTextChangedListener(textWatcher);
        etUsername.addTextChangedListener(textWatcher);

        // Password fields need special handling for requirement indicators
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear errors when text changes
                clearErrors();

                // Update password requirements
                updatePasswordRequirements();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        };

        etPassword.addTextChangedListener(passwordWatcher);
        etConfirmPassword.addTextChangedListener(passwordWatcher);
    }

    private void clearErrors() {
        // Clear all error messages
        tilEmail.setError(null);
        tilUsername.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    private void updatePasswordRequirements() {
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

        int successColor = ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark);
        int failureColor = ContextCompat.getColor(requireContext(), android.R.color.darker_gray);

        // Length requirement
        boolean lengthMet = password.length() >= 8;
        tvPasswordRequirementLength.setTextColor(lengthMet ? successColor : failureColor);
        tvPasswordRequirementLength.setText(lengthMet ? "✓ At least 8 characters" : "• At least 8 characters");

        // Uppercase requirement
        boolean uppercaseMet = PASSWORD_UPPERCASE.matcher(password).find();
        tvPasswordRequirementUppercase.setTextColor(uppercaseMet ? successColor : failureColor);
        tvPasswordRequirementUppercase.setText(uppercaseMet ? "✓ At least one uppercase letter" : "• At least one uppercase letter");

        // Number requirement
        boolean numberMet = PASSWORD_NUMBER.matcher(password).find();
        tvPasswordRequirementNumber.setTextColor(numberMet ? successColor : failureColor);
        tvPasswordRequirementNumber.setText(numberMet ? "✓ At least one number" : "• At least one number");

        // Special character requirement
        boolean specialMet = PASSWORD_SPECIAL.matcher(password).find();
        tvPasswordRequirementSpecial.setTextColor(specialMet ? successColor : failureColor);
        tvPasswordRequirementSpecial.setText(specialMet ? "✓ At least one special character" : "• At least one special character");

        // Password match requirement
        boolean passwordsMatch = !password.isEmpty() && password.equals(confirmPassword);
        tvPasswordRequirementMatch.setTextColor(passwordsMatch ? successColor : failureColor);
        tvPasswordRequirementMatch.setText(passwordsMatch ? "✓ Passwords match" : "• Passwords match");
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate email
        if (TextUtils.isEmpty(etEmail.getText())) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            tilEmail.setError("Please enter a valid email address");
            isValid = false;
        } else {
            // Check if email already exists (excluding current student)
            long excludeId = studentDetails != null ? studentDetails.getId() : -1;
            if (databaseHelper.isEmailExists(etEmail.getText().toString(), excludeId)) {
                tilEmail.setError("Email already exists");
                isValid = false;
            }
        }

        // Validate username
        if (TextUtils.isEmpty(etUsername.getText())) {
            tilUsername.setError("Username is required");
            isValid = false;
        } else if (etUsername.getText().toString().length() < 5) {
            tilUsername.setError("Username must be at least 5 characters");
            isValid = false;
        }

        // Validate password
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else {
            // Check password requirements
            boolean lengthMet = password.length() >= 8;
            boolean uppercaseMet = PASSWORD_UPPERCASE.matcher(password).find();
            boolean numberMet = PASSWORD_NUMBER.matcher(password).find();
            boolean specialMet = PASSWORD_SPECIAL.matcher(password).find();

            if (!lengthMet || !uppercaseMet || !numberMet || !specialMet) {
                tilPassword.setError("Password does not meet requirements");
                isValid = false;
            }
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        // Validate terms acceptance
        if (!checkboxTerms.isChecked()) {
            showToast("You must accept the Terms of Service and Privacy Policy");
            isValid = false;
        }

        return isValid;
    }

    private void showTermsDialog() {
        new AlertDialog.Builder(requireContext())
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
        new AlertDialog.Builder(requireContext())
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
        if (studentDetails != null) {
            if (studentDetails.getEmail() != null) {
                etEmail.setText(studentDetails.getEmail());
            }

            if (studentDetails.getUsername() != null) {
                etUsername.setText(studentDetails.getUsername());
            }

            if (studentDetails.getPassword() != null) {
                etPassword.setText(studentDetails.getPassword());
                etConfirmPassword.setText(studentDetails.getPassword());
            }

            if (studentDetails.isTermsAccepted()) {
                checkboxTerms.setChecked(true);
            }

            // Update password requirements
            updatePasswordRequirements();
        }
    }

    private void saveCurrentStepData() {
        // Save account details
        studentDetails.setEmail(etEmail.getText().toString());
        studentDetails.setUsername(etUsername.getText().toString());
        studentDetails.setPassword(etPassword.getText().toString());
        studentDetails.setTermsAccepted(checkboxTerms.isChecked());

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